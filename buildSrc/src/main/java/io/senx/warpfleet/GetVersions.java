package io.senx.warpfleet;

import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

/**
 * The type Get versions.
 */
@SuppressWarnings("unused")
public class GetVersions extends DefaultTask {
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
  public void getVersion() {
    JSONObject json = Unirest.get("https://warpfleet.senx.io/api/{group}/{artifact}")
        .routeParam("group", this.wfGroup)
        .routeParam("artifact", this.wfArtifact)
        .header("accept", "application/json")
        .asJson()
        .getBody()
        .getObject();
    System.out.printf("- Name:            %s:%s\n",
        json.getJSONObject("latest").getString("group"),
        json.getJSONObject("latest").getString("artifact")
    );
    System.out.printf("- Description:     %s\n",json.getJSONObject("latest").getString("description"));
    System.out.printf("- Latest version:  %s\n", json.getJSONObject("latest").getString("version"));
    System.out.println("- Available:");
    json.getJSONArray("children").forEach(item -> {
      JSONObject repo = (JSONObject) item;
      System.out.printf("    - %s\n", repo.getString("name"));
    });
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
}
