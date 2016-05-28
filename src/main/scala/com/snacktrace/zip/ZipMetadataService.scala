package com.snacktrace.zip

import java.util.zip.ZipEntry

import com.snacktrace.zip.model.ZipMetadata

import scala.concurrent.Future

/**
  * Created by ultmast on 28/05/16.
  */
trait ZipMetadataService {
  def getMetadata(url: String): Future[ZipMetadata]
}
