package com.snacktrace.zip.model

/**
  * Created by ultmast on 1/06/16.
  */
final case class ZipFile(localHeader: LocalHeader, content: Array[Byte])
