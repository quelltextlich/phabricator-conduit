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
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class UserModuleTest extends ModuleTestCase {
  public void testDisablePass() throws Exception {
    final JsonNull ret = JsonNull.INSTANCE;

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("user.disable"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final UserModule module = getModule();
    module.disable(Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq",
        "PHID-USER-3nphm6xkw2mpyfshq4dr"));

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'phids' does not match in params", Arrays.asList(
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "PHID-USER-3nphm6xkw2mpyfshq4dr"),
        params.get("phids"));
  }

  public void testEnablePass() throws Exception {
    final JsonNull ret = JsonNull.INSTANCE;

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("user.enable"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final UserModule module = getModule();
    module.enable(Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq",
        "PHID-USER-3nphm6xkw2mpyfshq4dr"));

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'phids' does not match in params", Arrays.asList(
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "PHID-USER-3nphm6xkw2mpyfshq4dr"),
        params.get("phids"));
  }

  public void testQueryPass() throws Exception {
    final JsonArray ret = new JsonArray();

    JsonObject userRet = new JsonObject();
    userRet.addProperty("phid", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    userRet.addProperty("userName", "qchris");
    userRet.addProperty("realName", "John Doe");
    userRet.addProperty("image", "http://www.example.com/image.png");
    userRet.addProperty("uri", "http://www.example.com/");
    JsonArray rolesRet = new JsonArray();
    rolesRet.add(new JsonPrimitive("approved"));
    rolesRet.add(new JsonPrimitive("activated"));
    userRet.add("roles", rolesRet);
    ret.add(userRet);

    userRet = new JsonObject();
    userRet.addProperty("phid", "PHID-USER-3nphm6xkw2mpyfshq4dr");
    userRet.addProperty("userName", "frank.the.tank");
    userRet.addProperty("realName", "Frank Tank");
    userRet.addProperty("image", "http://www.example.com/image-tank.png");
    userRet.addProperty("uri", "http://www.example.com/tank");
    rolesRet = new JsonArray();
    rolesRet.add(new JsonPrimitive("tankified"));
    userRet.add("roles", rolesRet);
    ret.add(userRet);

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("user.query"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final UserModule module = getModule();
    final UserModule.QueryResult result = module.query(Arrays.asList("qchris",
        "frank.the.tank"), Arrays.asList("foo@example.org", "bar@example.org"),
        Arrays.asList("John Doe", "Frank Tank"),
        Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq",
            "PHID-USER-3nphm6xkw2mpyfshq4dr"), Arrays.asList(42, 43), 3, 5);

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);
    assertEquals("'usernames' does not match in params",
        Arrays.asList("qchris", "frank.the.tank"), params.get("usernames"));
    assertEquals("'emails' does not match in params",
        Arrays.asList("foo@example.org", "bar@example.org"),
        params.get("emails"));
    assertEquals("'realnames' does not match in params",
        Arrays.asList("John Doe", "Frank Tank"), params.get("realnames"));
    assertEquals("'phids' does not match in params", Arrays.asList(
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "PHID-USER-3nphm6xkw2mpyfshq4dr"),
        params.get("phids"));
    assertEquals("'ids' does not match in params", Arrays.asList(42, 43),
        params.get("ids"));
    assertEquals("'offset' does not match in params", 3, params.get("offset"));
    assertEquals("'limit' does not match in params", 5, params.get("limit"));

    final UserModule.QueryResult expected = new UserModule.QueryResult();
    expected.add(new UserModule.UserResult("PHID-USER-3nphm6xkw2mpyfshq4dq",
        "qchris", "John Doe", "http://www.example.com/image.png",
        "http://www.example.com/", Arrays.asList("approved", "activated")));
    expected.add(new UserModule.UserResult("PHID-USER-3nphm6xkw2mpyfshq4dr",
        "frank.the.tank", "Frank Tank",
        "http://www.example.com/image-tank.png", "http://www.example.com/tank",
        Arrays.asList("tankified")));
    assertEquals("Results do not match", expected, result);
  }

  public void testWhoAmIPass() throws Exception {
    final JsonObject ret = new JsonObject();
    ret.addProperty("phid", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    ret.addProperty("userName", "qchris");
    ret.addProperty("realName", "John Doe");
    ret.addProperty("image", "http://www.example.com/image.png");
    ret.addProperty("uri", "http://www.example.com/");
    final JsonArray rolesRet = new JsonArray();
    rolesRet.add(new JsonPrimitive("approved"));
    rolesRet.add(new JsonPrimitive("activated"));
    ret.add("roles", rolesRet);

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("user.whoami"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final UserModule module = getModule();
    final UserModule.WhoAmIResult result = module.whoAmI();

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);

    final UserModule.WhoAmIResult expected = new UserModule.WhoAmIResult(
        "PHID-USER-3nphm6xkw2mpyfshq4dq", "qchris", "John Doe",
        "http://www.example.com/image.png", "http://www.example.com/",
        Arrays.asList("approved", "activated"));

    assertEquals("Results do not match", expected, result);
  }

  @Override
  protected UserModule getModule() {
    return new UserModule(connection, sessionHandler);
  }
}
