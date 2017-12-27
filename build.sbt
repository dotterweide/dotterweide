lazy val baseName   = "ToyIDE"
lazy val baseNameL  = baseName.toLowerCase

lazy val commonSettings = Seq(
  version            := "1.3.0-SNAPSHOT",
  organization       := "com.pavelfatin",
  homepage           := Some(url("https://pavelfatin.com/toyide")),
  licenses           := Seq("GNU General Public License v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt")),
  scalaVersion       := "2.12.4",
  crossScalaVersions := Seq("2.12.4", "2.11.12"),
  scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xfuture"),
  fork in Test       := false
)

lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    "junit"        % "junit"           % "4.12" % "test",
    "com.novocode" % "junit-interface" % "0.11" % "test"
  ),
)

lazy val root = project.in(file("."))
  .aggregate(core, lisp, toy, ui, app)

lazy val core = project.in(file("core"))
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := baseName
//    libraryDependencies ++= Seq(
//      "net.sourceforge.jasmin" % "jasmin" % "1.1",
//    ),
  )

lazy val lisp = project.in(file("lisp"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := s"$baseName - Clojure-like functional language",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "lisp"
  )

lazy val toy = project.in(file("toy"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := s"$baseName - C-like imperative language"
  )

lazy val ui = project.in(file("ui"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := s"$baseName - graphical user interface",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "2.0.1"
    )
  )

lazy val app = project.in(file("app"))
  .dependsOn(ui, lisp, toy)
  .settings(commonSettings)
  .settings(
    name := s"$baseName - demo application",
    mainClass in Compile := Some("com.pavelfatin.toyide.Application")
  )
