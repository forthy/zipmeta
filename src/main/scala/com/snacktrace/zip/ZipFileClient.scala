package com.snacktrace.zip

import scala.concurrent.Future

/**
  * An object that can fetch a portion of a zip file, and can get the zip file's size
  */
trait ZipFileClient {
  def range(url: String, start: Long, end: Long): Future[Array[Byte]]
  def size(url: String): Future[Long]
}
