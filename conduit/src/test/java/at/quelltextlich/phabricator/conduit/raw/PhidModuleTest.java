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

import at.quelltextlich.phabricator.conduit.raw.PhidModule.SinglePhidResult;

import com.google.gson.JsonObject;

public class PhidModuleTest extends ModuleTestCase {
  public void testLookupPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject retT84 = new JsonObject();
    retT84.addProperty("phid", "PHID-TASK-bto8xi3333rmlvrqdzr7");
    retT84.addProperty("uri", "https://phab.local/T84");
    retT84.addProperty("typeName", "typeName-T84");
    retT84.addProperty("type", "type-T84");
    retT84.addProperty("name", "T84");
    retT84.addProperty("fullName", "T84: test Task");
    retT84.addProperty("status", "open");

    final JsonObject retT85 = new JsonObject();
    retT85.addProperty("phid", "PHID-TASK-jpnuseiiujvw6f7vvnfp");
    retT85.addProperty("uri", "https://phab.local/T85");
    retT85.addProperty("typeName", "typeName-T85");
    retT85.addProperty("type", "type-T85");
    retT85.addProperty("name", "T85");
    retT85.addProperty("fullName", "T85: test Task");
    retT85.addProperty("status", "closed");

    final JsonObject ret = new JsonObject();
    ret.add("T84", retT84);
    ret.add("T85", retT85);

    expect(connection.call(eq("phid.lookup"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final PhidModule module = getModule();
    final PhidModule.LookupResult result = module.lookup(Arrays.asList("T84",
        "T85"));

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("TaskResult id is not set", Arrays.asList("T84", "T85"),
        params.get("names"));
    assertHasSessionKey(params);

    final PhidModule.LookupResult expected = new PhidModule.LookupResult();

    final SinglePhidResult T84 = new SinglePhidResult(
        "PHID-TASK-bto8xi3333rmlvrqdzr7", "https://phab.local/T84",
        "typeName-T84", "type-T84", "T84", "T84: test Task", "open");
    expected.put("T84", T84);

    final SinglePhidResult T85 = new SinglePhidResult(
        "PHID-TASK-jpnuseiiujvw6f7vvnfp", "https://phab.local/T85",
        "typeName-T85", "type-T85", "T85", "T85: test Task", "closed");
    expected.put("T85", T85);

    assertEquals("Call results do not match", expected, result);
  }

  @Override
  protected PhidModule getModule() {
    return new PhidModule(connection, sessionHandler);
  }
}
