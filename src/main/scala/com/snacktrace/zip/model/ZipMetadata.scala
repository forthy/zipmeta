package com.snacktrace.zip.model

/**
  * Created by ultmast on 28/05/16.
  */
final case class ZipMetadata(directoryRecords: Seq[CentralDirectoryRecord], endOfDirectory: EndOfCentralDirectory)
