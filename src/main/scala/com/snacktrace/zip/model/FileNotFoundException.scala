package com.snacktrace.zip.model

/**
  * Created by ultmast on 31/05/16.
  */
class FileNotFoundException(message: String, cause: Option[Throwable] = None) extends Exception(message, cause.orNull)
