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

public class UserModuleTest extends ModuleTestCase {
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
