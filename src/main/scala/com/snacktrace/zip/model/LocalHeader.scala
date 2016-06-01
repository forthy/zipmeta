package com.snacktrace.zip.model

/**
  * Created by ultmast on 1/06/16.
  */
final case class LocalHeader(header: Array[Byte], requiredVersion: Short, bitFlag: Short, compressionMethod: Short,
  modTime: Short, modDate: Short, crc: Int, compressedSize: Int, uncompressedSize: Int, fileNameLength: Short,
  extraFieldLength: Short, fileName: String, extraField: Array[Byte])
