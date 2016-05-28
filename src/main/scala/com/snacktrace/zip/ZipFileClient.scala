package com.snacktrace.zip

/**
  * Created by ultmast on 28/05/16.
  */
trait ZipFileClient {
  def range(url: String, start: Long, end: Long): Array[Byte]
  def size(url: String): Long
}
