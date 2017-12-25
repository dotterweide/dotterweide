name         := "ToyIDE"
version      := "1.2.1"
organization := "com.pavelfatin"
homepage     := Some(url("https://pavelfatin.com/toyide"))
scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing"     % "1.0.2",
  // "net.sourceforge.jasmin" %  "jasmin"          % "1.1",
  "junit"                  %  "junit"           % "4.12" % "test",
  "com.novocode"           %  "junit-interface" % "0.11" % "test"
)

mainClass in Compile := Some("com.pavelfatin.toyide.Application")

unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "lisp"
