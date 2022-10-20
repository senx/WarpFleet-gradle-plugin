package io.senx.warpfleet;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

/**
 * The type Get groups.
 */
@SuppressWarnings("unused")
public class GetGroups extends DefaultTask  {
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

  @Override
  public String getGroup() {
    return "WarpFleet";
  }

  @Override
  public String getDescription() {
    return "Get list of available groups";
  }

  /**
   * Java task.
   */
  @TaskAction
  public void getGroups() {
    Unirest.get("https://warpfleet.senx.io/api/")
        .header("accept", "application/json")
        .asJson()
        .getBody()
        .getObject()
        .getJSONArray("children").forEach(item -> {
          JSONObject repo = (JSONObject) item;
          System.out.printf("- %s\n", repo.getString("name"));
        });
  }
}
