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
}
