name := "si"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx"        % "1.0.0-R8",
  "org.scalafx" %% "scalafxml-core" % "0.1"
)

// Add dependency on JavaFX library based on JAVA_HOME variable
//unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

