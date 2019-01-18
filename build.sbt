lazy val baseName   = "Dotterweide"
lazy val baseNameL  = baseName.toLowerCase

lazy val commonSettings = Seq(
  version            := "0.1.0-SNAPSHOT",
//  organization       := "de.sciss",
  homepage           := Some(url(s"https://github.com/dotterweide/dotterweide")),
  licenses           := Seq(lgpl2),
  scalaVersion       := "2.12.8",
  crossScalaVersions := Seq("2.12.8", "2.11.12"),
  scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
  fork in Test       := false
)

lazy val lgpl2 = "LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")

lazy val deps = new {
  val main = new {
    val scalaMeta       = "4.1.0"
    val scalariform     = "0.2.6"
    val scalaSwing      = "2.0.3"
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

lazy val root = project.in(file("."))
  .aggregate(core, lisp, toy, scalalang, ui, app)
  .settings(
    name := baseName
  )

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

lazy val scalalang = project.in(file("scalalang"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := s"$baseName - Scala language",
    libraryDependencies ++= Seq(
      "org.scala-lang"  %  "scala-compiler" % scalaVersion.value,
      "org.scalameta"   %% "scalameta"      % deps.main.scalaMeta,
      "org.scalariform" %% "scalariform"    % deps.main.scalariform,
    )
  )

lazy val ui = project.in(file("ui"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := s"$baseName - graphical user interface",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % deps.main.scalaSwing
    )
  )

lazy val app = project.in(file("app"))
  .dependsOn(ui, lisp, toy, scalalang)
  .settings(commonSettings)
  .settings(
    name := s"$baseName - demo application",
    mainClass in Compile := Some("com.pavelfatin.toyide.Application")
  )
