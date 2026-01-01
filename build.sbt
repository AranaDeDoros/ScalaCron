import scala.collection.immutable.Seq

ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "3.3.6"


Compile / doc / scalacOptions ++= Seq(
  "-skip-packages", "main"
)

Compile / packageBin / mappings := {
  val original = (Compile / packageBin / mappings).value
  original.filterNot { case (_, pathInJar) =>
    pathInJar.startsWith("main/")
  }
}

lazy val root = (project in file("."))
  .settings(
    name := "ScalaCron",
    idePackagePrefix := Some("org.anaradedoros.scalacron")
  )
