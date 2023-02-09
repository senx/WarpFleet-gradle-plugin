# WarpFleet-Gradle-Plugin

## Installation

```groovy
apply plugin: 'io.warp10.warpfleet-gradle-plugin'
 
buildscript{
  repositories {
      mavenCentral()
      mavenLocal()
 
      dependencies{
        classpath 'io.warp10:warpfleet-gradle-plugin:1.0-SNAPSHOT'
      }
  }
}
```


## Usage

### Available tasks

```bash
$ ./gradlew -q tasks --group WarpFleet

...

WarpFleet tasks
---------------
getArtifactInfo - Get Artifact info
getArtifacts - Get list of available artifacts
getGroups - Get list of available groups
getVersions - Get list of available artifact's versions
installArtifact - Install Artifact

...
```

### *getGroups* - Get list of available groups

```bash
$ ./gradlew -q getGroups 
- io.senx
- io.warp10
...
```

### *getArtifacts* - Get list of available artifacts

```bash
$ ./gradlew -q getArtifacts --group=io.warp10
- io.warp10:warp10-ext-arrow:2.0.3-uberjar (Conversions to and from Apache Arrow streaming format)
- io.warp10:warp10-ext-barcode:1.0.2-uberjar (WarpScript™ Barcode Extension)
- io.warp10:warp10-ext-flows:0.1.1-uberjar (FLoWS WarpScript Extension)
- io.warp10:warp10-ext-forecasting:2.0.0 (Forecast extension)
- io.warp10:warp10-ext-geotransform:0.2.3-uberjar (Extension to transform coordinates from one geographic coordinate system to another. Based on Proj4J.)
- io.warp10:warp10-ext-git:1.0.1-uberjar (WarpScript Git Extension)
...
```


### *getVersions* - Get list of available artifact's versions

```bash
$ ./gradlew -q getVersions --group=io.warp10 --artifact=warp10-plugin-warpstudio
- Name:            io.warp10:warp10-plugin-warpstudio
- Description:     WarpStudio, the WarpScript editor
- Latest version:  2.0.6
- Available:
    - 2.0.6
    - 2.0.5
    - 2.0.4
    - 2.0.3
    - 2.0.2
```

### *getArtifactInfo* - Get Artifact info

```bash
$ ./gradlew -q getArtifactInfo --group=io.warp10 --artifact=warp10-plugin-warpstudio --vers=2.0.3
- io.warp10:warp10-plugin-warpstudio:2.0.3 (WarpStudio, the WarpScript editor)
```

### *installArtifact* - Install Artifact

Single Artifact installation

    $ ./gradlew -q wfInstall --group=io.warp10 --artifact=warp10-plugin-warpstudio --vers=2.0.3 --dest=/opt/warp10
    $ ./gradlew -q wfInstall --group=io.warp10 --artifact=warp10-plugin-warpstudio --vers=latest --dest=/opt/warp10
    $ ./gradlew -q wfInstall --group=io.warp10 --artifact=warp10-plugin-warpstudio --dest=/opt/warp10

With the packages syntax

    $ ./gradlew -q wfInstall --packages=io.warp10:warp10-plugin-warpstudio:2.0.3:jar --dest=/opt/warp10
    $ ./gradlew -q wfInstall --packages=io.warp10:warp10-plugin-warpstudio:2.0.3 --dest=/opt/warp10
    $ ./gradlew -q wfInstall --packages=io.warp10:warp10-plugin-warpstudio:latest --dest=/opt/warp10
    $ ./gradlew -q wfInstall --packages=io.warp10:warp10-plugin-warpstudio --dest=/opt/warp10

Multiple artefacts

    $ ./gradlew -q wfInstall \
        --packages=io.warp10:warp10-plugin-warpstudio:latest,io.warp10:warp10-ext-barcode \
        --dest=/opt/warp10


### *wfDoc* - Generate documentation

For instance for `@path/to/macro` and `/path/to/root/folder/path/to/macro.mc2`

You should use:

    ./gradlew -q wfDoc \
        --url=https://warp10.server/api/v0/exec \ 
        --source=/path/to/root/folder \             # where your Warpscripts are located  
        --dest=./doc \                              # documentation destination
        --macroDir=path \                           # root path of your macros
        --format=json                               # json by default (could be html or md)

