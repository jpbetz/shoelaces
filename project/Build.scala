import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "shoelaces"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.google.code.gson" % "gson" % "2.2.2",
      "org.scribe" % "scribe" % "1.3.0"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
