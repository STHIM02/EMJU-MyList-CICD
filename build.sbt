import Dependencies._
import ReleaseTransformations._
import de.johoop.findbugs4sbt.FindBugs._
import de.johoop.findbugs4sbt._

name := """emju-mylist"""
resolvers += "EMJU repository" at "http://albertsons-binrepo.westus.cloudapp.azure.com/artifactory/libs-release-local/"
scalaVersion := "2.11.7" 

//Code Coverage via JaCoCo 
jacoco.settings

//Code Analysis via FindBugs
findbugsSettings
//findbugsClasspath := IO.listFiles(file(<\/project\/plugins.sbt>)).filter(_.getName.endsWith("jar")).toSeq.classpath


//*********CI build and release process start********************
//customize release process using release plugin:
//  commented out Git related steps
//  comment out setting next version number
//note: version number is maintained the version.sbt file


releaseProcess := {
    Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
//        commitReleaseVersion,
//        tagRelease,
        publishArtifacts   //note ending comma removed, required if using all default steps
//        setNextVersion       
//        commitNextVersion,
//        pushChanges
    )
}

//make sure that parallelExecution is off during test execution
parallelExecution in jacoco.Config := false

//Disable commit check for no untracked files in working directory (requires release plug in version 1.0.3)
releaseIgnoreUntrackedFiles := true

//let build override previous versions
isSnapshot := true

//include universal zip archive when publishing
deploymentSettings
publish <<= publish.dependsOn(publish in config("universal"))

//set target repo by passing argument -DrepoTarget=value at command line
val repoTarget = settingKey[String]("repoTarget") 
repoTarget := sys.props.getOrElse("repoTarget", default = "libs-snapshot-local")

//publish to binary repo manager
publishTo <<= (version, repoTarget) { (v: String, s: String) =>
    val artifactory = "http://albertsons-binrepo.westus.cloudapp.azure.com/artifactory/"
    if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at artifactory + s)
    else
        Some("releases"  at artifactory + s)
}
//binary repo credentials
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

//if publishing outside CI process environment, uncomment line below and comment out publishTo lines above
//publishTo := Some(Resolver.file("file",  new File( "../repo" )) )
//*********CI build and release process end***********************

lazy val root = (
  project.in(file("."))
  enablePlugins(PlayJava)
    settings = Defaults.defaultSettings //added
      jacoco.settings ++ //this is the important part. 
)

libraryDependencies ++= webDependencies

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java           // Java project. Don't expect Scala IDE
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)  // Use .class files instead of generated .scala files for views and routes 
EclipseKeys.preTasks := Seq(compile in Compile)                  // Compile the project before generating Eclipse files, so that .class files for views and routes are present
EclipseKeys.skipParents in ThisBuild := false

doc in Compile <<= target.map(_ / "none")

// disable Akka checker in Dev mode
PlayKeys.devSettings := Seq("akka.diagnostics.checker.enabled" -> "off")
//commit for test-3
//commit for test-4 dated 0719hrs 19-Jun 2017
//commit for test-5 dated 0930hrs 19-Jun 2017
//commit for test-5 dated 1530hrs 19-Jun 2017
//commit for test-5 dated 1545hrs 19-Jun 2017
//commit for test 6 fro findbugs
