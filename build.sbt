scalaModuleSettings

name := "scala-jline"

libraryDependencies ++= Seq(
  "com.novocode"         % "junit-interface" % "0.11" % Test,
  "junit"                % "junit"           % "4.12" % Test,
  "org.fusesource.jansi" % "jansi"           % "1.11"
)

enablePlugins(GitVersioning)
git.useGitDescribe := true
git.gitDescribedVersion := git.gitDescribedVersion.value.map(_ drop 1) // drop the `v` from the `git describe` string, https://github.com/sbt/sbt-git/issues/67

// Otherwise the artifact has a dependency on scala-library
autoScalaLibrary := false

// Don't add `_<scala-version>` to the jar file name - it's a Java-only project, no Scala cross-versioning needed
crossPaths := false

javacOptions ++= Seq("-g", "-source", "1.5", "-target", "1.6")

// javadoc fails if we pass all of the above
javacOptions in doc := Seq("-source", "1.5")

scalaModuleOsgiSettings

OsgiKeys.exportPackage := Seq(s"scala.tools.jline.*;version=${version.value}")

// exclude some files, same patterns as in `pom.xml`
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "maven", _ @ _*) =>
    MergeStrategy.discard

  case PathList(ps @ _*) if ps.last endsWith ".txt" =>
    MergeStrategy.discard

  case PathList("org", "fusesource", "hawtjni", "runtime", p) if p.startsWith("Jni") || p.contains("Flag") || p.startsWith("T32") || p.startsWith("NativeStats") =>
    MergeStrategy.discard

  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
