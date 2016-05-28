package com.snacktrace.zip

/**
  * An object that can fetch a portion of a zip file, and can get the zip file's size
  */
trait ZipFileClient {
  def range(url: String, start: Long, end: Long): Array[Byte]
  def size(url: String): Long
}
