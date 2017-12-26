name               := "ToyIDE"
version            := "1.2.4-SNAPSHOT"
organization       := "com.pavelfatin"
homepage           := Some(url("https://pavelfatin.com/toyide"))
scalaVersion       := "2.12.4"
crossScalaVersions := Seq("2.12.4", "2.11.12")
scalacOptions     ++= Seq("-deprecation")

fork in Test := false

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing"     % "2.0.1",
  // "net.sourceforge.jasmin" %  "jasmin"          % "1.1",
  "junit"                  %  "junit"           % "4.12" % "test",
  "com.novocode"           %  "junit-interface" % "0.11" % "test"
)

mainClass in Compile := Some("com.pavelfatin.toyide.Application")

unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "lisp"
