package dotterweide.ide

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileOutputStream}
import java.net.URL

import dispatch.{Http, as, url}
import dotterweide.build.{Module, Version}
import dotterweide.io.{FileDownload, FileDownloadImpl, JarUtil}

import scala.collection.immutable.{Seq => ISeq}
import scala.concurrent.{ExecutionContext, Future, blocking}

object DocUtil {
  /** Copies the contents of a resource into a given file.
    *
    * @param path   the resource path (absolute)
    * @param out    the target file to write to
    */
  def copyResource(path: String, out: File): Unit = {
    val is = new BufferedInputStream(getClass.getClassLoader.getResourceAsStream(path))
    try {
      val os = new BufferedOutputStream(new FileOutputStream(out))
      try {
        val arr = new Array[Byte](1024)
        while ({
          val sz = is.read(arr )
          sz > 0 && { os.write(arr, 0, sz); true }
        }) ()
      } finally  {
        os.close()
      }
    } finally {
      is.close()
    }
  }

  /** Sets the scala-doc CSS files to either dark or light skin.
    *
    * @param dark     `true` for dark look, `false` for light look
    * @param baseDir  base directory of extracted java-docs.
    *                 The CSS files will be placed inside the `lib`
    *                 directory in `baseDir`
    */
  def setScalaCssStyle(dark: Boolean, baseDir: File): Unit = {
    val tpe       = if (dark) "dark" else "light"
    val styleDir  = new File(baseDir, "lib")
    copyResource(s"dotterweide/index-$tpe.css"    , new File(styleDir, "index.css"    ))
    copyResource(s"dotterweide/template-$tpe.css" , new File(styleDir, "template.css" ))
  }

  def mavenCentral: URL =
    new URL("https://repo1.maven.org/maven2")

  private def appendToURL(in: URL, child: String): URL = {
    val uri     = in.toURI
    val newPath = uri.getPath + child
    val newUri  = uri.resolve(newPath)
    newUri.toURL
  }

  def mkJavadocDownloadUrl(docModule: Module, repoBase: URL = mavenCentral): URL = {
    val version = docModule.version.toString
    import docModule.{artifactId, groupId}
    val child = s"/${groupId.replace('.', '/')}/$artifactId/$version/$artifactId-$version-javadoc.jar"
    appendToURL(repoBase, child)
  }

  def mkJavadocMetaDataUrl(docModule: Module, repoBase: URL = mavenCentral): URL = {
    import docModule.{artifactId, groupId}
    val child = s"/${groupId.replace('.', '/')}/$artifactId/maven-metadata.xml"
    appendToURL(repoBase, child)
  }

  def downloadAndExtract(docModule: Module, target: File, darkCss: Boolean = false,
                         repoBase: URL = mavenCentral, deleteOnExit: Boolean = false)
                        (implicit exec: ExecutionContext): (FileDownload, Future[Unit]) = {
    val jarFile         = File.createTempFile("javadoc", ".jar")
    val docUrl          = mkJavadocDownloadUrl(docModule, repoBase = repoBase)
    val req             = url(docUrl.toString)
    val dl              = new FileDownloadImpl(req = req, out = jarFile)
    val futRes          = dl.status.map { _ =>
      blocking {
        target.mkdirs()
        val map = JarUtil.unpackFiles(jar = jarFile, target = target)
        if (deleteOnExit) map.valuesIterator.foreach(_.deleteOnExit())
        jarFile.delete()
        DocUtil.setScalaCssStyle(dark = darkCss, baseDir = target)
      }
      ()
    }

    (dl, futRes)
  }

  def defaultUnpackDir(baseDir: File, docModule: Module): File =
    new File(new File(new File(baseDir, docModule.groupId), docModule.artifactId), docModule.version.toString)

  case class Metadata(lastUpdated: Long, latestVersion: Version, versions: ISeq[Version])

  /** Reads the maven meta data for a documentation module. It only uses the
    * `groupId` and `artifactId` and ignores the version, instead returning all
    * found versions, sorted from newest to oldest.
    */
  def findModuleVersions(docModule: Module, repoBase: URL = mavenCentral)
                        (implicit exec: ExecutionContext): Future[Metadata] = {
    val metaDataURL     = mkJavadocMetaDataUrl(docModule, repoBase)
    // println("Resolving artifact...")
    val metaDataReq     = url(metaDataURL.toString)
    val metaDataFut: Future[xml.Elem] = Http.default(metaDataReq OK as.xml.Elem)

    metaDataFut.map { metaData =>
      // val metaGroupId     = (metaData \ "groupId"    ).text.trim
      // val metaArtifactId  = (metaData \ "artifactId" ).text.trim
      val metaVersioning  =  metaData \ "versioning"
      val lastUpdated     = (metaVersioning \ "lastUpdated" ).text.trim.toLong
      val latestVersion   = Version.parse((metaVersioning \ "latest").text).get
      val metaVersions    = (metaVersioning \ "versions" \ "version").flatMap(n => Version.parse(n.text).toOption)
      val res = metaVersions.sorted(Version.ordering.reverse)
      Metadata(lastUpdated = lastUpdated, latestVersion = latestVersion, versions = res)
    }
  }
}
