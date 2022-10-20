package io.senx.warpfleet;

import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

/**
 * The type Get artifacts.
 */
@SuppressWarnings("unused")
public class GetArtifacts extends DefaultTask {
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

  @Override
  public String getGroup() {
    return "WarpFleet";
  }

  @Override
  public String getDescription() {
    return "Get list of available artifacts";
  }

  /**
   * Java task.
   */
  @TaskAction
  public void getArtifacts() {
    Unirest.get("https://warpfleet.senx.io/api/{artifact}")
        .routeParam("artifact", this.wfGroup)
        .header("accept", "application/json")
        .asJson()
        .getBody()
        .getObject()
        .getJSONArray("children").forEach(item -> {
          JSONObject repo = (JSONObject) item;
          System.out.printf("- %s:%s:%s (%s)\n",
              repo.getJSONObject("latest").getString("group"),
              repo.getJSONObject("latest").getString("artifact"),
              repo.getJSONObject("latest").getString("version"),
              repo.getJSONObject("latest").getString("description"));
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
}
