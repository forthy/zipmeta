package com.snacktrace.zip

import com.snacktrace.zip.model.{ZipFile, ZipMetadata}

import scala.concurrent.Future

/**
  * An object that can get metadata of a zip format file
  */
trait ZipMetadataService {
  /**
    * Fetches the metadata of the zip file at the given URL
    * @param url The URL of the zip file
    * @return The zip metadata
    */
  def getMetadata(url: String): Future[ZipMetadata]

  /**
    * Reads the single file from the zip file at the given URL
    * @param url The URL of the zip file
    * @param fileName The name of the file to extract
    * @return The file contents, decompressed
    */
  def getFile(url: String, fileName: String): Future[ZipFile]
}
