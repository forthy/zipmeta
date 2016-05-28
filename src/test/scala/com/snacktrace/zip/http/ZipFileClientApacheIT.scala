package com.snacktrace.zip.http

import org.apache.http.impl.client.DefaultHttpClient
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Created by ultmast on 28/05/16.
  */
class ZipFileClientApacheIT extends WordSpec with MustMatchers {

  trait Fixture {
    val http = new DefaultHttpClient()
    val client = new ZipFileClientApache(http)
  }

  "ZipFileClientApache.size" should {
    "return size of file" in new Fixture {
      client.size("https://oss.sonatype.org/content/repositories/releases/abbot/abbot/1.4.0/abbot-1.4.0.jar") mustBe 687192l
    }
  }

  "ZipFileClientApache.range" should {
    "return given range of bytes from file" in new Fixture {
      val firstBytes = client.range(
        "https://oss.sonatype.org/content/repositories/releases/abbot/abbot/1.4.0/abbot-1.4.0.jar", 0, 10)
      firstBytes mustBe Array[Byte](80, 75, 3, 4, 10, 0, 0, 8, 8, 0, 19)
    }
  }
}
