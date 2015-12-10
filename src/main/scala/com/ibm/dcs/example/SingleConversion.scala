package com.ibm.dcs.example

import java.io.File

import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion

object SingleConversion extends App {

  val book = new File("alices_adventures_in_wonderland.pdf")

  val service = new DocumentConversion()
  service.setUsernameAndPassword("<username>", "<password>")

  val text = service.convertDocumentToText(book)

  println(text.take(1024*10))

}
