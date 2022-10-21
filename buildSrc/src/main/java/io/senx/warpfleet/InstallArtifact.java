/*
 *   Copyright 2022  SenX S.A.S.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.senx.warpfleet;

import io.senx.warpfleet.utils.Constants;
import io.senx.warpfleet.utils.Helper;
import io.senx.warpfleet.utils.PackageInfo;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * The type Install artifact.
 */
@SuppressWarnings("unused")
public class InstallArtifact extends DefaultTask {
  /**
   * The Wf group.
   */
  @Input
  @Optional
  String wfGroup;
  /**
   * The Wf artifact.
   */
  @Input
  @Optional
  String wfArtifact;
  /**
   * The Wf version.
   */
  @Input
  @Optional
  String wfVersion;
  /**
   * The Wf classifier.
   */
  @Input
  @Optional
  String wfClassifier;
  /**
   * The Wf packages.
   */
  @Input
  @Optional
  String wfPackages;
  /**
   * The Warp 10 dir.
   */
  @Input
  String warp10Dir;

  /**
   * Instantiates a new Install artifact.
   */
  public InstallArtifact() {
    this.setDescription("Install Artifact");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Install artifact.
   */
  @TaskAction
  public void installArtifact() {
    List<PackageInfo> packages = new ArrayList<>();
    try {
      // Test inputs
      if (!StringUtils.isBlank(wfPackages) && !"unspecified".equals(this.wfPackages)) {
        packages = Arrays.stream(wfPackages.split(",")).map(p -> {
          String[] parts = p.split(":");
          if (parts.length < 2) {
            Helper.messageError("Wrong syntax for :" + p);
            throw new RuntimeException("Bad syntax");
          }
          PackageInfo packageInfo = new PackageInfo();
          packageInfo.setGroup(parts[0]);
          packageInfo.setName(parts[1]);
          if (parts.length > 2) {
            String version = parts[2];
            if (StringUtils.isBlank(version) || "latest".equals(version)) {
              version = Helper.getLatest(parts[0], parts[1]).getJSONObject("latest").getString("version");
            }
            packageInfo.setVersion(version);
          }
          if (parts.length > 3) {
            if (!StringUtils.isBlank(parts[3])) {
              packageInfo.setClassifier(parts[3]);
            }
          }
          return packageInfo;
        }).collect(Collectors.toList());
      } else {
        if (StringUtils.isBlank(this.wfVersion) || "unspecified".equals(this.wfVersion) || "latest".equals(this.wfVersion)) {
          this.wfVersion = Helper.getLatest(this.wfGroup, this.wfArtifact).getJSONObject("latest").getString("version");
        }
        if (StringUtils.isBlank(this.wfGroup) || "unspecified".equals(this.wfGroup)) {
          Helper.messageError("Artifact's group is mandatory");
          return;
        }
        if (StringUtils.isBlank(this.wfArtifact) || "unspecified".equals(this.wfArtifact)) {
          Helper.messageError("Artifact's name is mandatory");
          return;
        }
        if (StringUtils.isBlank(this.warp10Dir) || "unspecified".equals(this.warp10Dir)) {
          Helper.messageError("Warp 10 root directory is mandatory");
          return;
        }
        if (StringUtils.isBlank(this.wfClassifier) || "unspecified".equals(this.wfClassifier)) {
          this.wfClassifier = null;
        }
        packages.add(new PackageInfo(this.wfGroup, this.wfArtifact, this.wfVersion, this.wfClassifier));
      }
      // process packages
      for (PackageInfo pi: packages) {
        Helper.messageInfo("Retrieving " + pi + " info");
        JSONObject info = Helper.getArtifactInfo(pi);
        Helper.messageInfo("Installing " + pi + " into: " + this.warp10Dir);
        // TODO get macro

        JSONArray dependencies = info.optJSONArray("dependencies");
        if (null == dependencies) {
          dependencies = new JSONArray();
        }

        // Prepare tmp directory
        File workDir = Helper.filePath(FileUtils.getTempDirectoryPath(), ".wf");
        if (workDir.exists()) {
          FileUtils.deleteDirectory(workDir);
        }
        FileUtils.forceMkdir(workDir);
        File depsDir = Helper.filePath(workDir.getAbsolutePath(), "deps");
        FileUtils.forceMkdir(depsDir);

        // Retrieve jar
        String jar = info.getString("jar");
        String fileName = pi.getGroup() + "-" + pi.getName() + "-" + pi.getVersion() + ".jar";
        Helper.messageInfo("Retrieving: " + fileName);
        Unirest.get(jar)
            .asFile(Helper.path(depsDir.getAbsolutePath(), fileName))
            .ifFailure(Helper::processHTTPError)
            .getBody();
        Helper.messageSusccess(fileName + " retrieved");

        // Building custom gradle file
        Helper.messageInfo("Calculating dependencies");
        StringBuilder gradle = new StringBuilder()
            .append("plugins { id 'java' }\n")
            .append("sourceCompatibility = '1.8'\n")
            .append("targetCompatibility = '1.8'\n")
            .append("repositories {\n")
            .append(" maven { url '").append(info.getString("repoUrl")).append("' }\n")
            .append(" mavenCentral()\n")
            .append("}\n")
            .append("dependencies {\n");
        dependencies.forEach(d -> {
          JSONObject dep = (JSONObject) d;
          gradle
              .append(" implementation '")
              .append(dep.getString("group")).append(":")
              .append(dep.getString("artifact")).append(":")
              .append(dep.getString("version"));
          String classifier = dep.optString("classifier", "");
          if (!"".equals(classifier)) {
            gradle.append(":").append(classifier);
          }
          gradle.append("'\n");
        });
        gradle.append("}\n")
            .append("task getDeps(type: Copy) {\n")
            .append(" from sourceSets.main.runtimeClasspath\n")
            .append(" into '").append(depsDir.getAbsolutePath()).append("'\n")
            .append("}");
        FileUtils.write(Helper.filePath(workDir.getAbsolutePath(), "build.gradle"), gradle.toString(), StandardCharsets.UTF_8);
        FileUtils.write(Helper.filePath(workDir.getAbsolutePath(), "settings.gradle"), "rootProject.name = 'newProjectName'", StandardCharsets.UTF_8);
        Helper.exec("./gradlew getDeps -q -b " + Helper.path(workDir.getAbsolutePath(), "build.gradle"));

        // Copy all jars into Warp 10 lib folder
        Helper.messageInfo("Installing dependencies:");
        List<File> jarList = Arrays.stream(Objects.requireNonNull(depsDir.listFiles()))
            .filter(f -> f.getName().endsWith(".jar"))
            .collect(Collectors.toList());
        for (File f: jarList) {
          FileUtils.copyFile(f, Helper.filePath(this.warp10Dir, "lib", f.getName()));
          Helper.messageSusccess("Dependency: " + f.getName() + " successfully deployed");
        }

        // Process conf entries
        Helper.messageInfo("Calculating properties");
        File propertyFile = Helper.filePath(this.warp10Dir, "etc", "conf.d", "99-" + pi.getGroup() + "-" + pi.getName() + ".conf");
        Properties props = new Properties();
        if (propertyFile.exists()) {
          Helper.messageWarning(propertyFile.getAbsolutePath() + " already exists, will backup it");
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
          String backupName = "_" + dateFormat.format(new Date());
          FileUtils.copyFile(propertyFile, new File(propertyFile.getAbsolutePath() + backupName));
        }
        StringBuilder properties = new StringBuilder();
        properties
            .append("// -------------------------------------------------------------------------------------\n")
            .append("// ").append(pi).append('\n')
            .append("// -------------------------------------------------------------------------------------\n\n");

        // Read conf Array
        if (info.has("conf")) {
          info.getJSONArray("conf").forEach(p -> {
            if (((String) p).startsWith("//")) {
              properties.append(p);
            } else {
              String[] currentProp = Arrays.stream(((String) p).split("=")).map(String::trim).toArray(String[]::new);
              if (props.containsKey(currentProp[0])) {
                Helper.messageWarning(currentProp[0] + " already exists, bypassing");
              } else {
                props.put(currentProp[0], currentProp[1]);
              }
            }
          });
          props.forEach((k, v) -> properties.append(k).append("=").append(v).append('\n'));
        } else {
          if ("plugin".equals(info.getString("type"))) {
            Helper.messageWarning("No configuration found, do not forget to add 'warpscript.plugins.xxx = package.class'");
          }
          if ("ext".equals(info.getString("type"))) {
            Helper.messageWarning("No configuration found, do not forget to add 'warpscript.extension.xxx = package.class'");
          }
        }
        FileUtils.write(propertyFile, properties.toString(), StandardCharsets.UTF_8, false);

        if (workDir.exists()) {
          FileUtils.deleteDirectory(workDir);
        }
      }
    } catch (Throwable e) {
      Helper.messageError(e.getMessage());
    }
  }

  /**
   * Gets wf group.
   *
   * @return the wf group
   */
  public String getWfGroup() {
    return wfGroup;
  }

  /**
   * Sets wf group.
   *
   * @param wfGroup the wf group
   */
  @Option(option = "group", description = "Artifact's group, ie: io.warp10")
  public void setWfGroup(String wfGroup) {
    this.wfGroup = wfGroup;
  }

  /**
   * Gets wf artifact.
   *
   * @return the wf artifact
   */
  public String getWfArtifact() {
    return wfArtifact;
  }

  /**
   * Sets wf artifact.
   *
   * @param wfArtifact the wf artifact
   */
  @Option(option = "artifact", description = "Artifact's name, ie: warp10-plugin-mqtt")
  public void setWfArtifact(String wfArtifact) {
    this.wfArtifact = wfArtifact;
  }

  /**
   * Gets wf version.
   *
   * @return the wf version
   */
  public String getWfVersion() {
    return wfVersion;
  }

  /**
   * Sets wf version.
   *
   * @param wfVersion the wf version
   */
  @Option(option = "version", description = "Artifact's version, ie: 0.0.3")
  public void setWfVersion(String wfVersion) {
    this.wfVersion = wfVersion;
  }

  /**
   * Gets warp 10 dir.
   *
   * @return the warp 10 dir
   */
  public String getWarp10Dir() {
    return warp10Dir;
  }

  /**
   * Sets warp 10 dir.
   *
   * @param warp10Dir the warp 10 dir
   */
  @Option(option = "dest", description = "Warp 10 root installation directory, ie: /opt/warp10")
  public void setWarp10Dir(String warp10Dir) {
    this.warp10Dir = warp10Dir;
  }

  /**
   * Gets wf packages.
   *
   * @return the wf packages
   */
  public String getWfPackages() {
    return wfPackages;
  }

  /**
   * Sets wf packages.
   *
   * @param wfPackages the wf packages
   */
  @Option(option = "packages", description = "Coma separated list of packages to install")
  public void setWfPackages(String wfPackages) {
    this.wfPackages = wfPackages;
  }

  /**
   * Gets wf classifier.
   *
   * @return the wf classifier
   */
  public String getWfClassifier() {
    return wfClassifier;
  }

  /**
   * Sets wf classifier.
   *
   * @param wfClassifier the wf classifier
   */
  @Option(option = "classifier", description = "Artifact's classifier, ie: uberjar")
  public void setWfClassifier(String wfClassifier) {
    this.wfClassifier = wfClassifier;
  }
}
