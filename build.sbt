lazy val baseName   = "Dotterweide"
lazy val baseNameL  = baseName.toLowerCase

lazy val commonSettings = Seq(
  version                   := "0.1.0-SNAPSHOT",
  organization              := "de.sciss",  // for now, so we can publish artifacts
  homepage                  := Some(url(s"https://github.com/dotterweide/dotterweide")),
  licenses                  := Seq(lgpl2),
  scalaVersion              := "2.12.8",
  crossScalaVersions        := Seq("2.13.0-M5", "2.12.8", "2.11.12"),
  scalacOptions            ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
  fork in Test              := false,
  fork in (Compile, run)    := true,
  parallelExecution in Test := false
)

lazy val lgpl2 = "LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")

lazy val deps = new {
  val main = new {
    val akka            = "2.5.19"
    val scalariform     = "0.2.6"
    val scalaSwing      = "2.1.0"
  }
  val demo = new {
    val scopt           = "3.7.1"
  }
  val test = new {
    val junit           = "4.12"
    val junitInterface  = "0.11"
  }
}

lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    "junit"        % "junit"           % deps.test.junit          % Test,
    "com.novocode" % "junit-interface" % deps.test.junitInterface % Test
  ),
)

lazy val root = project.withId(baseNameL).in(file("."))
  .aggregate(core, lispLang, toyLang, scalaLang, ui, demo)
  .settings(commonSettings)
  .settings(
    name        := baseName,
    description := s"$baseName - Embeddable mini-IDE"
  )

lazy val core = project.withId(s"$baseNameL-core").in(file("core"))
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Core",
    description := s"$baseName - Core API"
//    libraryDependencies ++= Seq(
//      "net.sourceforge.jasmin" % "jasmin" % "1.1",
//    ),
  )

lazy val lispLang = project.withId(s"$baseNameL-lisp").in(file("lisp"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Lisp",
    description := s"$baseName - Clojure-like functional language",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "lisp"
  )

lazy val toyLang = project.withId(s"$baseNameL-toy").in(file("toy"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Toy",
    description := s"$baseName - C-like imperative language"
  )

lazy val scalaLang = project.withId(s"$baseNameL-scala").in(file("scala"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Scala",
    description := s"$baseName - Scala language",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor"     % deps.main.akka,
      "org.scala-lang"    %  "scala-compiler" % scalaVersion.value
    ),
    libraryDependencies += {
      val v = if (scalaVersion.value == "2.13.0-M5") "0.2.7-SNAPSHOT" else deps.main.scalariform
      "org.scalariform"   %% "scalariform"    % v
    }
  )

lazy val ui = project.withId(s"$baseNameL-ui").in(file("ui"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-UI",
    description := s"$baseName - graphical user interface",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % deps.main.scalaSwing
    )
  )

lazy val demo = project.withId(s"$baseNameL-demo").in(file("demo"))
  .dependsOn(ui, lispLang, toyLang, scalaLang)
  .settings(commonSettings)
  .settings(
    name        := s"$baseName-Demo",
    description := s"$baseName - demo application",
    mainClass in Compile := Some("dotterweide.Demo"),
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % deps.demo.scopt
    )
  )
