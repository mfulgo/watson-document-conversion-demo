name := "document-conversion-example"

version := "1.0"

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

libraryDependencies ++= Seq(
  "com.ibm.watson.developer_cloud" % "java-sdk" % "2.1.0",
  "com.typesafe" % "config" % "1.3.0"
)
