// Copyright (C) 2015 quelltextlich e.U.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package at.quelltextlich.phabricator.conduit.raw;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.easymock.Capture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ProjectModuleTest extends ModuleTestCase {
  public void testCreatePass() throws Exception {
    final JsonObject ret = new JsonObject();
    final JsonArray membersRet = new JsonArray();
    membersRet.add(new JsonPrimitive("user1"));
    membersRet.add(new JsonPrimitive("user2"));
    final JsonArray slugsRet = new JsonArray();
    slugsRet.add(new JsonPrimitive("slug-foo"));
    slugsRet.add(new JsonPrimitive("slug-bar"));
    ret.addProperty("id", 21);
    ret.addProperty("phid", "PHID-TASK-betonxi3333rmlvrqdzr7");
    ret.addProperty("name", "nameFoo");
    ret.addProperty("profileImagePHID", "PHID-PROFILE-FOO");
    ret.addProperty("icon", "rocket");
    ret.addProperty("color", "red");
    ret.add("members", membersRet);
    ret.add("slugs", slugsRet);
    ret.addProperty("dateCreated", "1436304454");
    ret.addProperty("dateModified", "1436304469");

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("project.create"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ProjectModule module = getModule();
    final ProjectModule.CreateResult result = module.create("nameFoo",
        Arrays.asList("user1", "user2"), "rocket", "red",
        Arrays.asList("tagBar", "tagBaz"));

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'name' does not match in params", "nameFoo",
        params.get("name"));
    assertEquals("'members' does not match in params",
        Arrays.asList("user1", "user2"), params.get("members"));
    assertEquals("'icon' does not match in params", "rocket",
        params.get("icon"));
    assertEquals("'color' does not match in params", "red", params.get("color"));
    assertEquals("'tags' does not match in params",
        Arrays.asList("tagBar", "tagBaz"), params.get("tags"));

    final ProjectModule.CreateResult expected = new ProjectModule.CreateResult(
        21, "PHID-TASK-betonxi3333rmlvrqdzr7", "nameFoo", "PHID-PROFILE-FOO",
        "rocket", "red", Arrays.asList("user1", "user2"), Arrays.asList(
            "slug-foo", "slug-bar"), "1436304454", "1436304469");

    assertEquals("Results do not match", expected, result);
  }

  public void testQueryPass() throws Exception {
    final JsonObject cursor = new JsonObject();
    cursor.addProperty("limit", 3);
    cursor.addProperty("after", "18");
    cursor.add("before", null);

    final JsonObject data = new JsonObject();

    JsonObject dataElement = new JsonObject();
    JsonArray membersRet = new JsonArray();
    membersRet.add(new JsonPrimitive("user1"));
    membersRet.add(new JsonPrimitive("user2"));
    JsonArray slugsRet = new JsonArray();
    slugsRet.add(new JsonPrimitive("slug-foo"));
    slugsRet.add(new JsonPrimitive("slug-bar"));
    dataElement.addProperty("id", 21);
    dataElement.addProperty("phid", "PHID-TASK-betonxi3333rmlvrqdzr7");
    dataElement.addProperty("name", "nameFoo");
    dataElement.addProperty("profileImagePHID", "PHID-PROFILE-FOO");
    dataElement.addProperty("icon", "rocket");
    dataElement.addProperty("color", "red");
    dataElement.add("members", membersRet);
    dataElement.add("slugs", slugsRet);
    dataElement.addProperty("dateCreated", "1436304454");
    dataElement.addProperty("dateModified", "1436304469");
    data.add("PHID-TASK-betonxi3333rmlvrqdzr7", dataElement);

    dataElement = new JsonObject();
    membersRet = new JsonArray();
    membersRet.add(new JsonPrimitive("user3"));
    slugsRet = new JsonArray();
    slugsRet.add(new JsonPrimitive("baz"));
    dataElement.addProperty("id", 22);
    dataElement.addProperty("phid", "PHID-TASK-betonxi3333rmlvrqdzr8");
    dataElement.addProperty("name", "nameBar");
    dataElement.add("profileImagePHID", null);
    dataElement.addProperty("icon", "bike");
    dataElement.addProperty("color", "yellow");
    dataElement.add("members", membersRet);
    dataElement.add("slugs", slugsRet);
    dataElement.addProperty("dateCreated", "1436304455");
    dataElement.addProperty("dateModified", "1436304470");
    data.add("PHID-TASK-betonxi3333rmlvrqdzr8", dataElement);

    final JsonObject ret = new JsonObject();
    ret.add("data", data);
    ret.add("slugMap", new JsonArray());
    ret.add("cursor", cursor);

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("project.query"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ProjectModule module = getModule();
    final ProjectModule.QueryResult result = module.query(
        Arrays.asList(21, 22), Arrays.asList("nameFoo", "nameBar"), Arrays
            .asList("PHID-TASK-betonxi3333rmlvrqdzr7",
                "PHID-TASK-betonxi3333rmlvrqdzr8"), Arrays.asList("slug-foo",
            "bar"), Arrays.asList("rocket", "bike"), Arrays.asList("yellow",
            "red"), "status-open", Arrays.asList("user1", "user3"), 3, 5);

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'ids' does not match in params", Arrays.asList(21, 22),
        params.get("ids"));
    assertEquals("'names' does not match in params",
        Arrays.asList("nameFoo", "nameBar"), params.get("names"));
    assertEquals("'phids' does not match in params", Arrays.asList(
        "PHID-TASK-betonxi3333rmlvrqdzr7", "PHID-TASK-betonxi3333rmlvrqdzr8"),
        params.get("phids"));
    assertEquals("'slugs' does not match in params",
        Arrays.asList("slug-foo", "bar"), params.get("slugs"));
    assertEquals("'icons' does not match in params",
        Arrays.asList("rocket", "bike"), params.get("icons"));
    assertEquals("'colors' does not match in params",
        Arrays.asList("yellow", "red"), params.get("colors"));
    assertEquals("'status' does not match in params", "status-open",
        params.get("status"));
    assertEquals("'members' does not match in params",
        Arrays.asList("user1", "user3"), params.get("members"));
    assertEquals("'limit' does not match in params", 3, params.get("limit"));
    assertEquals("'offset' does not match in params", 5, params.get("offset"));

    final Map<String, ProjectModule.ProjectResult> expData = new HashMap<String, ProjectModule.ProjectResult>();
    expData.put(
        "PHID-TASK-betonxi3333rmlvrqdzr7",
        new ProjectModule.ProjectResult(21, "PHID-TASK-betonxi3333rmlvrqdzr7",
            "nameFoo", "PHID-PROFILE-FOO", "rocket", "red", Arrays.asList(
                "user1", "user2"), Arrays.asList("slug-foo", "slug-bar"),
            "1436304454", "1436304469"));
    expData.put(
        "PHID-TASK-betonxi3333rmlvrqdzr8",
        new ProjectModule.ProjectResult(22, "PHID-TASK-betonxi3333rmlvrqdzr8",
            "nameBar", null, "bike", "yellow", Arrays.asList("user3"), Arrays
                .asList("baz"), "1436304455", "1436304470"));
    final ProjectModule.Cursor expCursor = new ProjectModule.Cursor(3, "18",
        null);
    final ProjectModule.QueryResult expected = new ProjectModule.QueryResult(
        expData, new HashMap<String, String>(), expCursor);

    assertEquals("Project 21 does not match", expected.getData().get("21"),
        result.getData().get("21"));
    assertEquals("Project 22 does not match", expected.getData().get("22"),
        result.getData().get("22"));
    assertEquals("Cursor does not match", expected.getCursor(),
        result.getCursor());
    assertEquals("Results do not match", expected, result);
  }

  @Override
  protected ProjectModule getModule() {
    return new ProjectModule(connection, sessionHandler);
  }
}
