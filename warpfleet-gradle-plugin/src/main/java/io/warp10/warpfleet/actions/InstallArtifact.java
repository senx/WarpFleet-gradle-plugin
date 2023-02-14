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

package io.warp10.warpfleet.actions;

import io.warp10.warpfleet.utils.Constants;
import io.warp10.warpfleet.utils.Helper;
import io.warp10.warpfleet.utils.Logger;
import io.warp10.warpfleet.utils.PackageInfo;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
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
abstract public class InstallArtifact extends DefaultTask {
  /**
   * Gets wf group.
   *
   * @return the wf group
   */
  @Input
  @Optional
  @Option(option = "group", description = "Artifact's group, ie: io.warp10")
  abstract public Property<String> getWfGroup();

  /**
   * Gets wf artifact.
   *
   * @return the wf artifact
   */
  @Input
  @Optional
  @Option(option = "artifact", description = "Artifact's name, ie: warp10-plugin-mqtt")
  abstract public Property<String> getWfArtifact();

  /**
   * Gets wf version.
   *
   * @return the wf version
   */
  @Input
  @Optional
  @Option(option = "vers", description = "Artifact's version, ie: 0.0.3")
  abstract public Property<String> getWfVersion();

  /**
   * Gets warp 10 dir.
   *
   * @return the warp 10 dir
   */
  @Input
  @Option(option = "dest", description = "Warp 10 root installation directory, ie: /opt/warp10")
  abstract public Property<String> getWarp10Dir();

  /**
   * Gets wf packages.
   *
   * @return the wf packages
   */
  @Input
  @Optional
  @Option(option = "packages", description = "Coma separated list of packages to install")
  abstract public Property<String> getWfPackages();


  /**
   * Gets wf classifier.
   *
   * @return the wf classifier
   */
  @Input
  @Optional
  @Option(option = "classifier", description = "Artifact's classifier, ie: uberjar")
  abstract public Property<String> getWfClassifier();

  /**
   * Gets wf repo url.
   *
   * @return the wf repo url
   */
  @Input
  @Optional
  @Option(option = "repoURL", description = "Artifact's maven like repo url")
  abstract public Property<String> getWfRepoURL();

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
      String vers = this.getWfVersion().getOrNull();
      // Test inputs
      if (!StringUtils.isBlank(this.getWfPackages().getOrNull()) && !"unspecified".equals(this.getWfPackages().getOrNull())) {
        packages = Arrays.stream(this.getWfPackages().get().split(",")).map(p -> {
          String[] parts = p.split(":");
          if (parts.length < 2) {
            Logger.messageError("Wrong syntax for :" + p);
            throw new RuntimeException("Bad syntax");
          }
          PackageInfo packageInfo = new PackageInfo();
          packageInfo.setGroup(parts[0]);
          packageInfo.setName(parts[1]);

          if (parts.length == 2) {
            String version = Helper.getLatest(parts[0], parts[1]).getJSONObject("latest").getString("version");
            packageInfo.setVersion(version);
          }
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
        if (StringUtils.isBlank(vers) || "unspecified".equals(vers) || "latest".equals(vers)) {
          vers = Helper.getLatest(this.getWfGroup().get(), this.getWfArtifact().get()).getJSONObject("latest").getString("version");
        }
        if (StringUtils.isBlank(this.getWfGroup().getOrNull()) || "unspecified".equals(this.getWfGroup().getOrNull())) {
          Logger.messageError("Artifact's group is mandatory");
          return;
        }
        if (StringUtils.isBlank(this.getWfArtifact().getOrNull()) || "unspecified".equals(this.getWfArtifact().getOrNull())) {
          Logger.messageError("Artifact's name is mandatory");
          return;
        }
        if (StringUtils.isBlank(this.getWarp10Dir().getOrNull()) || "unspecified".equals(this.getWarp10Dir().getOrNull())) {
          Logger.messageError("Warp 10 root directory is mandatory");
          return;
        }
        String classifier = this.getWfClassifier().getOrNull();
        if (StringUtils.isBlank(classifier) || "unspecified".equals(classifier)) {
          classifier = null;
        }
        packages.add(new PackageInfo(this.getWfGroup().get(), this.getWfArtifact().get(), vers, classifier));
      }
      // process packages
      for (PackageInfo pi : packages) {
        Logger.messageInfo("Retrieving " + pi + " info");
        JSONObject info = Helper.getArtifactInfo(pi);
        Logger.messageInfo("Installing " + pi + " into: " + this.getWarp10Dir().get());

        if (null != info.getJSONArray("macros")) {
          for (Object m : info.getJSONArray("macros")) {
            Helper.getMacro(this.getWfGroup().get(), this.getWfArtifact().get(), vers, (JSONObject) m, this.getWarp10Dir().get());
          }
        }
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
        Logger.messageInfo("Retrieving: " + fileName);
        Unirest.get(jar)
          .asFile(Helper.path(depsDir.getAbsolutePath(), fileName))
          .ifFailure(Helper::processHTTPError)
          .getBody();
        Logger.messageSusccess(fileName + " retrieved");

        // Building custom gradle file
        Logger.messageInfo("Calculating dependencies");
        String repoURL = info.getString("repoUrl");
        if (!StringUtils.isBlank(this.getWfRepoURL().getOrNull()) && !"unspecified".equals(this.getWfRepoURL().getOrNull())) {
          repoURL = this.getWfRepoURL().get();
        }
        StringBuilder gradle = new StringBuilder()
          .append("plugins { id 'java' }\n")
          .append("sourceCompatibility = '1.8'\n")
          .append("targetCompatibility = '1.8'\n")
          .append("repositories {\n")
          .append(" maven { url '").append(repoURL).append("' }\n")
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
        Helper.exec(("./gradlew getDeps -q -b " + Helper.path(workDir.getAbsolutePath(), "build.gradle")).split(" "));

        // Copy all jars into Warp 10 lib folder
        Logger.messageInfo("Installing dependencies:");
        List<File> jarList = Arrays.stream(Objects.requireNonNull(depsDir.listFiles()))
          .filter(f -> f.getName().endsWith(".jar")).collect(Collectors.toList());
        for (File f : jarList) {
          FileUtils.copyFile(f, Helper.filePath(this.getWarp10Dir().get(), "lib", f.getName()));
          Logger.messageSusccess("Dependency: " + f.getName() + " successfully deployed");
        }

        // Process conf entries
        Logger.messageInfo("Calculating properties");
        File propertyFile = Helper.filePath(this.getWarp10Dir().get(), "etc", "conf.d", "99-" + pi.getGroup() + "-" + pi.getName() + ".conf");
        Properties props = new Properties();
        if (propertyFile.exists()) {
          Logger.messageWarning(propertyFile.getAbsolutePath() + " already exists, will backup it");
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
            if (((String) p).startsWith("//") || ((String) p).startsWith("#")) {
              properties.append(p);
            } else {
              String[] currentProp = Arrays.stream(((String) p).split("=")).map(String::trim).toArray(String[]::new);
              if (props.containsKey(currentProp[0])) {
                Logger.messageWarning(currentProp[0] + " already exists, bypassing");
              } else {
                props.put(currentProp[0], currentProp.length > 1 ? currentProp[1] : "");
              }
            }
          });
          props.forEach((k, v) -> properties.append(k).append("=").append(v).append('\n'));
        } else {
          if ("plugin".equals(info.getString("type"))) {
            Logger.messageWarning("No configuration found, do not forget to add 'warpscript.plugins.xxx = package.class'");
          }
          if ("ext".equals(info.getString("type"))) {
            Logger.messageWarning("No configuration found, do not forget to add 'warpscript.extension.xxx = package.class'");
          }
        }
        FileUtils.write(propertyFile, properties.toString(), StandardCharsets.UTF_8, false);
        Logger.messageSusccess(pi + " installed successfully.\nDo not forget to check the configuration file: " + propertyFile.getAbsolutePath());

        if (workDir.exists()) {
          FileUtils.deleteDirectory(workDir);
        }
      }
    } catch (Throwable e) {
      Logger.messageError(e.getMessage());
    }
  }
}
