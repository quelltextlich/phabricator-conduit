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

  @Override
  protected ProjectModule getModule() {
    return new ProjectModule(connection, sessionHandler);
  }
}
