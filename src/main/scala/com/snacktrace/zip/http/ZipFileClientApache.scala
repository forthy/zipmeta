package com.snacktrace.zip.http

import java.util.zip.ZipEntry

import com.snacktrace.zip.{ZipMetadataService, ZipFileClient}
import org.apache.commons.io.IOUtils
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{HttpGet, HttpHead}

import scala.concurrent.Future
import scala.io.Source

/**
  * Created by ultmast on 28/05/16.
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
