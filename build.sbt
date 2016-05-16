import Dependencies._

name := """emju-mylist"""
resolvers += "EMJU repository" at "http://albertsons-binrepo.westus.cloudapp.azure.com/artifactory/libs-release-local/"
scalaVersion := "2.11.7" 

//*********CI build and release process start********************
//customize release process using release plugin:
//  commented out Git related steps
//  comment out setting next version number
//note: version number is maintained the version.sbt file


releaseProcess := {
    Seq[ReleaseStep](
        ReleaseTransformations.checkSnapshotDependencies,
        ReleaseTransformations.inquireVersions,
        ReleaseTransformations.runClean,
        ReleaseTransformations.runTest,
        ReleaseTransformations.setReleaseVersion,
//        ReleaseTransformations.commitReleaseVersion,
//        ReleaseTransformations.tagRelease,
        ReleaseTransformations.publishArtifacts   //note ending comma removed, required if using all default steps
//        ReleaseTransformations.setNextVersion       
//        ReleaseTransformations.commitNextVersion,
//        ReleaseTransformations.pushChanges
    )
}

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