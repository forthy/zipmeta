package com.snacktrace.zip.model

final case class EndOfCentralDirectory(diskNumber: Short, centralDirectoryStartDiskNumber: Short,
  countCentralDirectoryRecordsOnDisk: Short, totalCentralDirectoryRecords: Short, sizeOfCentralDirectory: Int,
  offsetOfStartOfCentralDirectory: Int, commentLength: Short, comment: String)
