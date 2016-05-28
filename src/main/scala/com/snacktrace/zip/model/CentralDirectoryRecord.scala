package com.snacktrace.zip.model

final case class CentralDirectoryRecord(header: Array[Byte], version: Short, versionRequired: Short, bitFlag: Short,
  compressionMethod: Short, fileLastModTime: Short, fileLastModDate: Short, crc: Int, compressedSize: Int,
  decompressedSize: Int, fileNameLength: Short, extraFieldLength: Short, fileCommentLength: Short,
  diskNumFileStart: Short, internalFileAttributes: Short, externalFileAttributes: Int, offset: Int, fileName: String,
  extraField: Array[Byte], comment: String)
