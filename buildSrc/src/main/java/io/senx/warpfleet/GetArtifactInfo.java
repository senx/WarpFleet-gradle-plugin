package io.senx.warpfleet;

import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * The type Get artifact info.
 */
@SuppressWarnings("unused")
public class GetArtifactInfo extends DefaultTask {
  /**
   * The Description.
   */
  @Internal
  String description;
  /**
   * The Group.
   */
  @Internal
  String group;
  /**
   * The wfGroup.
   */
  @Input
  String wfGroup;
  /**
   * The Wf artifact.
   */
  @Input
  String wfArtifact;
  /**
   * The Wf version.
   */
  @Input
  @Optional
  String wfVersion;

  @Override
  public String getGroup() {
    return "WarpFleet";
  }

  @Override
  public String getDescription() {
    return "Get list of available artifact's versions";
  }

  /**
   * Java task.
   */
  @TaskAction
  public void getArtifactInfo() {
    if (null == this.wfVersion || "latest".equals(this.wfVersion)) {
      this.wfVersion = Unirest.get("https://warpfleet.senx.io/api/{group}/{artifact}")
          .routeParam("group", this.wfGroup)
          .routeParam("artifact", this.wfArtifact)
          .header("accept", "application/json")
          .asJson()
          .getBody()
          .getObject()
          .getJSONObject("latest").getString("version");
    }
    JSONObject info = Unirest.get("https://warpfleet.senx.io/api/{group}/{artifact}/{version}")
        .routeParam("group", this.wfGroup)
        .routeParam("artifact", this.wfArtifact)
        .routeParam("version", this.wfVersion)
        .header("accept", "application/json")
        .asJson()
        .getBody()
        .getObject();
    System.out.printf("- Name: %s", info);
  }

  /**
   * Gets wfGroup.
   *
   * @return the wfGroup
   */
  public String getWfGroup() {
    return wfGroup;
  }

  /**
   * Sets wfGroup.
   *
   * @param wfGroup the wfGroup
   */
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
  public void setWfVersion(String wfVersion) {
    this.wfVersion = wfVersion;
  }
}
