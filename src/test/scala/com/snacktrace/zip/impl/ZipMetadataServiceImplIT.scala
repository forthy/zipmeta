package com.snacktrace.zip.impl

import com.snacktrace.zip.http.ZipFileClientApache
import org.apache.http.impl.client.DefaultHttpClient
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by ultmast on 28/05/16.
  */
class ZipMetadataServiceImplIT extends WordSpec with MustMatchers {
  trait Fixture {
    val client = new ZipFileClientApache(new DefaultHttpClient())
    val service = new ZipMetadataServiceImpl(client)
  }

  "ZipEntryServiceImplIT.getEntries" should {
    "get entries" in new Fixture {
      val metadata = Await.result(service.getMetadata(
        "https://oss.sonatype.org/content/repositories/releases/abbot/abbot/1.4.0/abbot-1.4.0.jar"), Duration.Inf)
      metadata.directoryRecords.map {
        record =>
          println(record.fileName)
      }
    }
  }
}
