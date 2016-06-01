package com.snacktrace.zip.impl

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.nio.{ByteBuffer, ByteOrder}
import java.util.zip.{Inflater, InflaterInputStream}

import com.snacktrace.zip.model._
import com.snacktrace.zip.{ZipFileClient, ZipMetadataService}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Parses the zip metadata using a ZipFileClient to request portions of the file contents
  * Format description is on Wikipedia (https://en.wikipedia.org/wiki/Zip_(file_format))
  */
class ZipMetadataServiceImpl(client: ZipFileClient)(implicit executionContext: ExecutionContext) extends ZipMetadataService {
  val INITIAL_REQUEST_SIZE = 22
  val FIXED_LOCAL_HEADER_SIZE = 30
  val END_OF_CENTRAL_DIRECTORY = Array[Byte](0x50, 0x4b, 0x05, 0x06)
  val CENTRAL_DIRECTORY_RECORD = Array[Byte](0x50, 0x4b, 0x01, 0x02)
  val LOCAL_HEADER = Array[Byte](0x50, 0x4b, 0x03, 0x04)
  val CHARSET = Charset.forName("UTF-8")

  override def getMetadata(url: String): Future[ZipMetadata] = {
    getEndOfCentralDirectory(url).flatMap {
      eocd =>
        client.range(url, eocd.offsetOfStartOfCentralDirectory,
          eocd.offsetOfStartOfCentralDirectory + eocd.sizeOfCentralDirectory - 1).map {
          centralDirectoryBytes =>
            val centralDirectoryBuf = ByteBuffer.wrap(centralDirectoryBytes)
            centralDirectoryBuf.order(ByteOrder.LITTLE_ENDIAN)
            val records = for {
              i <- 1 to eocd.totalCentralDirectoryRecords
            } yield {
              val record = createCentralDirectoryRecord(centralDirectoryBuf)
              if (record.header.deep != CENTRAL_DIRECTORY_RECORD.deep) {
                throw new Exception("Expected directory record header but was not")
              }
              record
            }
            ZipMetadata(records, eocd)
        }
    }
  }

  private def getEndOfCentralDirectory(url: String): Future[EndOfCentralDirectory] = {
    client.size(url).flatMap {
      size =>
        client.range(url, size - INITIAL_REQUEST_SIZE, size - 1).map {
          initialChunk =>
            val buffer = ByteBuffer.wrap(initialChunk)
            val littleEndianOrdered = new Array[Byte](buffer.array.length)
            buffer.order(ByteOrder.LITTLE_ENDIAN).get(littleEndianOrdered)
            val metadataOpt = getPiecesStartingWith(littleEndianOrdered, END_OF_CENTRAL_DIRECTORY).headOption.map {
              endOfCentral =>
                createEndOfCentralDirectory(endOfCentral)
            }
            metadataOpt match {
              case Some(metadata) => metadata
              case None => throw new Exception("Could not parse")
            }
        }
    }
  }

  private def getPiecesStartingWith(searchSpace: Array[Byte], searchFor: Array[Byte]): Seq[Array[Byte]] = {
    val found = for {
      i <- 0 to searchSpace.length - searchFor.length
      if (searchSpace.slice(i, i + searchFor.length).deep == searchFor.deep)
    } yield {
      searchSpace.slice(i, searchSpace.length)
    }
    found
  }

  private def createEndOfCentralDirectory(bytes: Array[Byte]): EndOfCentralDirectory = {
    val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    // Header
    buf.getInt
    val diskNumber = buf.getShort
    val startDisk = buf.getShort
    val numRecords = buf.getShort
    val totalRecords = buf.getShort
    val recordSize = buf.getInt
    val offset = buf.getInt
    val commentLen = buf.getShort
    EndOfCentralDirectory(diskNumber, startDisk, numRecords, totalRecords, recordSize, offset, commentLen, null)
  }

  private def createCentralDirectoryRecord(buffer: ByteBuffer): CentralDirectoryRecord = {
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val header = new Array[Byte](4)
    buffer.get(header)
    val version = buffer.getShort
    val requiredVersion = buffer.getShort
    val bitFlag = buffer.getShort
    val compressionMethod = buffer.getShort
    val modTime = buffer.getShort
    val modDate = buffer.getShort
    val crc = buffer.getInt
    val compressedSize = buffer.getInt
    val decompressedSize = buffer.getInt
    val fileNameLength = buffer.getShort
    val extraFieldLength = buffer.getShort
    val commentLength = buffer.getShort
    val startDisk = buffer.getShort
    val intFileAttrs = buffer.getShort
    val extFileAttrs = buffer.getInt
    val offset = buffer.getInt
    val fileName = new Array[Byte](fileNameLength)
    buffer.get(fileName)
    val extraField = new Array[Byte](extraFieldLength)
    buffer.get(extraField)
    val comment = new Array[Byte](commentLength)
    buffer.get(comment)
    CentralDirectoryRecord(header, version, requiredVersion, bitFlag, compressionMethod, modTime, modDate, crc,
      compressedSize, decompressedSize, fileNameLength, extraFieldLength, commentLength, startDisk, intFileAttrs,
      extFileAttrs, offset, new String(fileName, CHARSET), extraField, new String(comment, CHARSET))
  }

  private def createLocalHeader(buffer: ByteBuffer): LocalHeader = {
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val header = new Array[Byte](4)
    buffer.get(header)
    val requiredVersion = buffer.getShort
    val bitFlag = buffer.getShort
    val compressionMethod = buffer.getShort
    val lastModTime = buffer.getShort
    val lastModDate = buffer.getShort
    val crc = buffer.getInt
    val compressedSize = buffer.getInt
    val uncompressedSize = buffer.getInt
    val fileNameLength = buffer.getShort
    val extraFieldLength = buffer.getShort
    val fileNameBytes = new Array[Byte](fileNameLength.toInt)
    buffer.get(fileNameBytes)
    val fileName = new String(fileNameBytes, CHARSET)
    val extraField = new Array[Byte](extraFieldLength.toInt)
    buffer.get(extraField)
    LocalHeader(header, requiredVersion, bitFlag, compressionMethod, lastModTime, lastModDate, crc, compressedSize,
      uncompressedSize, fileNameLength, extraFieldLength, fileName, extraField)
  }

  override def getFile(url: String, fileName: String): Future[Array[Byte]] = {
    getMetadata(url).flatMap {
      metadata =>
        val recordOpt = metadata.directoryRecords.filter {
          record =>
            record.fileName == fileName
        }.headOption
        recordOpt match {
          case Some(record) =>
            val sizeToGet = record.compressedSize + FIXED_LOCAL_HEADER_SIZE + record.fileNameLength +
              record.extraFieldLength
            client.range(url, record.offset, record.offset + sizeToGet).map {
              fileWithHeader =>
                val fileWithHeaderBuf = ByteBuffer.wrap(fileWithHeader)
                val localHeader = createLocalHeader(fileWithHeaderBuf)
                if(localHeader.header.deep != LOCAL_HEADER.deep) {
                  throw new Exception("Could not parse local header")
                }
                val decompresser = new Inflater(true)
                fileWithHeaderBuf.order(ByteOrder.BIG_ENDIAN)
                val restOfFile = new Array[Byte](record.compressedSize)
                val iis = new InflaterInputStream(new ByteArrayInputStream(restOfFile))
                fileWithHeaderBuf.get(restOfFile)
                decompresser.setInput(restOfFile, 0, record.compressedSize)
                val decompressed = new Array[Byte](record.decompressedSize)
                decompresser.inflate(decompressed)
                decompresser.end
                println(new String(decompressed, CHARSET))
                decompressed
            }
          case None => throw new FileNotFoundException(s"Could not find ${fileName}")
        }
    }
  }
}
