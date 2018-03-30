name := "Push On Bintray Example"

version := "0.1"

scalaVersion := "2.11.8"

organization := "clustering4ever"

//bintrayRepository := "Clustering4Ever"

bintrayOrganization := Some("clustering4ever")

val sparkVersion = "2.2.0"

libraryDependencies ++= Seq(
	"org.apache.spark" %% "spark-core" % sparkVersion % "provided",
	"org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"
)

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

credentials += Credentials(Path.userHome / ".bintray" / ".credentials")