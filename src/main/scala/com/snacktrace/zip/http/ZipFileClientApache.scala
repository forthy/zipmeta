package com.snacktrace.zip.http

import java.util.zip.ZipEntry

import com.snacktrace.zip.model.FileNotFoundException
import com.snacktrace.zip.{ZipFileClient, ZipMetadataService}
import org.apache.commons.io.IOUtils
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{HttpGet, HttpHead}
import org.apache.http.util.EntityUtils

import scala.concurrent.Future
import scala.io.Source

/**
  * An implementation of ZipFileClient that fetches chunks of the remote file. This implementation only supports remote
  * hosts that support byte range request headers
  */
class ZipFileClientApache(client: HttpClient) extends ZipFileClient {
  override def range(url: String, start: Long, end: Long): Array[Byte] = {
    val request = new HttpGet(url)
    request.setHeader("Range", s"bytes=${start}-${end}")
    val response = client.execute(request)
    response.getStatusLine.getStatusCode match {
      case 206 =>
        val bytes = IOUtils.toByteArray(response.getEntity.getContent)
        EntityUtils.consume(response.getEntity)
        bytes
      case 404 =>
        throw new FileNotFoundException(s"404 response from ${url}")
      case other =>
        throw new Exception(s"Unexpected status ${other} from range request ${start}-${end} to ${url}")
    }
  }

  override def size(url: String): Long = {
    val request = new HttpHead(url)
    val response = client.execute(request)
    response.getStatusLine.getStatusCode match {
      case 200 =>
        val size = response.getFirstHeader("Content-Length").getValue.toLong
        EntityUtils.consume(response.getEntity)
        size
      case 404 =>
        throw new FileNotFoundException(s"404 response from ${url}")
      case other =>
        throw new Exception(s"Unexpected status ${other} from HEAD request to ${url}")
    }
  }
}
