package com.snacktrace.zip.http

import java.util.zip.ZipEntry

import com.snacktrace.zip.{ZipMetadataService, ZipFileClient}
import org.apache.commons.io.IOUtils
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{HttpGet, HttpHead}

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
    IOUtils.toByteArray(response.getEntity.getContent)
  }

  override def size(url: String): Long = {
    val request = new HttpHead(url)
    val response = client.execute(request)
    response.getFirstHeader("Content-Length").getValue.toLong
  }
}
