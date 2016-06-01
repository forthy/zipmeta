package com.snacktrace.zip.impl

import java.nio.charset.Charset
import java.util.zip.Deflater

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

  "ZipMetadataServiceImpl.getEntries" should {
    "get entries" in new Fixture {
      val expected = Seq(
        "announcement.htm",
        "civility.htm",
        "con_conf.htm",
        "Dufountain.htm",
        "hwlt96-1.htm",
        "usiplogo.gif",
        "usipwall.gif",
        "wehr7492.htm"
      )
      val metadata = Await.result(service.getMetadata("http://www.colorado.edu/conflict/peace/download/peace_essay.ZIP").map {
        metadata => metadata.directoryRecords.map(_.fileName)
      }, Duration.Inf)
      metadata mustBe expected
    }
  }

  "ZipMetadataServiceImpl.getFile" should {
    "get file" in new Fixture {
      val expected = ("package com.snacktrace.zip\n\n/**\n  * An object that can fetch a portion of a zip file," +
        " and can get the zip file's size\n  */\ntrait ZipFileClient {\n  def range(url: String, start: Long, " +
        "end: Long): Array[Byte]\n  def size(url: String): Long\n}\n").getBytes(Charset.forName("UTF-8"))
      val actual = Await.result(service.getFile(
        "https://oss.sonatype.org/content/repositories/releases/com/snacktrace/zipmeta_2.11/1.0.0/zipmeta_2.11-1.0.0-sources.jar",
        "com/snacktrace/zip/ZipFileClient.scala"),
        Duration.Inf)
      actual.content.deep mustBe expected.deep
    }
  }
}
