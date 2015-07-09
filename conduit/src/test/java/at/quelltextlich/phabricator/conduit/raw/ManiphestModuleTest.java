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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.easymock.Capture;

import at.quelltextlich.phabricator.conduit.ConduitException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ManiphestModuleTest extends ModuleTestCase {
  public void testInfoPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject ret = new JsonObject();
    final JsonArray userArrayRet = new JsonArray();
    userArrayRet.add(new JsonPrimitive("PHID-USER-3nphm6xkw2mpyfshq4dq"));
    final JsonObject auxiliaryRet = new JsonObject();
    auxiliaryRet.add("std:maniphest:security_topic", null);
    auxiliaryRet.add("isdc:sprint:storypoints", null);
    ret.addProperty("id", 42);
    ret.addProperty("phid", "PHID-TASK-btorxi3333rmlvrqdzr7");
    ret.addProperty("authorPHID", "PHID-USER-3nphm6xkw2mpyfshq4dq");
    ret.add("ownerPHID", null);
    ret.add("ccPHIDs", userArrayRet);
    ret.addProperty("status", "open");
    ret.addProperty("statusName", "Open");
    ret.addProperty("isClosed", false);
    ret.addProperty("priority", "Needs Triage");
    ret.addProperty("priorityColor", "violet");
    ret.addProperty("title", "qchris-test-task");
    ret.addProperty("description", "foo");
    ret.add("projectPHIDs", new JsonArray());
    ret.addProperty("uri", "https://phabricator.local/T42");
    ret.add("auxiliary", auxiliaryRet);
    ret.addProperty("objectName", "T42");
    ret.addProperty("dateCreated", "1436304454");
    ret.addProperty("dateModified", "1436304469");
    ret.add("dependsOnTaskPHIDs", new JsonArray());

    expect(connection.call(eq("maniphest.info"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final ManiphestModule.InfoResult result = module.info(42);

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, params.get("task_id"));
    assertHasSessionKey(params);

    final Map<String, String> auxiliary = new HashMap<String, String>();
    auxiliary.put("std:maniphest:security_topic", null);
    auxiliary.put("isdc:sprint:storypoints", null);
    final ManiphestModule.InfoResult expected = new ManiphestModule.InfoResult(
        42, "PHID-TASK-btorxi3333rmlvrqdzr7", "PHID-USER-3nphm6xkw2mpyfshq4dq",
        null, Arrays.asList("PHID-USER-3nphm6xkw2mpyfshq4dq"), "open", "Open",
        false, "Needs Triage", "violet", "qchris-test-task", "foo",
        new ArrayList<String>(), "https://phabricator.local/T42", auxiliary,
        "T42", "1436304454", "1436304469", new ArrayList<String>());

    assertEquals("Results do not match", expected, result);
  }

  public void testInfoFailSession() throws Exception {
    final ConduitException conduitException = new ConduitException();

    sessionHandler.failNextFillingIn(conduitException);

    replayMocks();

    final ManiphestModule module = getModule();
    try {
      module.info(42);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }
  }

  public void testInfoFailCall() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject retRelevant = new JsonObject();
    retRelevant.add("id", new JsonPrimitive(42));

    final ConduitException conduitException = new ConduitException();
    expect(connection.call(eq("maniphest.info"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final ManiphestModule module = getModule();
    try {
      module.info(42);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, params.get("task_id"));
    assertHasSessionKey(params);
  }

  public void testUpdatePass() throws Exception {
    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final JsonObject retRelevant = new JsonObject();
    retRelevant.add("id", new JsonPrimitive(42));

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("maniphest.update"), capture(paramsCapture)))
        .andReturn(retRelevant).once();

    replayMocks();

    final ManiphestModule module = getModule();
    final ManiphestModule.UpdateResult updateResult = module.update(42, "foo");

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, params.get("id"));
    assertHasSessionKey(params);

    assertEquals("UpdateResult's id does not match", 42, updateResult.getId());
  }

  public void testUpdateFailSession() throws Exception {
    final ConduitException conduitException = new ConduitException();

    sessionHandler.failNextFillingIn(conduitException);

    replayMocks();

    final ManiphestModule module = getModule();
    try {
      module.update(42, "foo");
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }
  }

  public void testUpdateFailCall() throws Exception {
    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("maniphest.update"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final ManiphestModule module = getModule();
    try {
      module.update(42, "foo");
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> paramsRelevant = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", 42, paramsRelevant.get("id"));
  }

  @Override
  protected ManiphestModule getModule() {
    return new ManiphestModule(connection, sessionHandler);
  }
}
