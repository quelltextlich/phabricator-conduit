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

import java.util.Map;

import org.easymock.Capture;

import at.quelltextlich.phabricator.conduit.ConduitException;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConduitModuleTest extends ModuleTestCase {
  public void testPingPass() throws Exception {
    expect(connection.call("conduit.ping")).andReturn(new JsonPrimitive("foo"))
        .once();

    replayMocks();

    final ConduitModule module = getModule();

    final ConduitModule.PingResult actual = module.ping();

    assertEquals("Hostname does not match", "foo", actual.getHostname());
  }

  public void testPingConnectionFail() throws Exception {
    final ConduitException conduitException = new ConduitException();

    expect(connection.call("conduit.ping")).andThrow(conduitException).once();

    replayMocks();

    final ConduitModule module = getModule();

    try {
      module.ping();
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }
  }

  public void testConnectPass() throws Exception {
    final JsonObject ret = new JsonObject();
    ret.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.ConnectResult connectResult = module.connect();

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", "userFoo", params.get("user"));

    assertEquals("Session keys don't match", "KeyFoo",
        connectResult.getSessionKey());
  }

  public void testConduitConnectConnectionFail() throws Exception {
    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final ConduitModule module = getModule();

    try {
      module.connect();
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", "userFoo", params.get("user"));
  }

  public void testGetCapabilitiesPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject retRelevant = new JsonObject();

    expect(
        connection.call(eq("conduit.getcapabilities"), capture(paramsCapture)))
        .andReturn(retRelevant).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.GetCapabilitiesResult getCapabilitiesResult = module
        .getCapabilities();
    assertNotNull("Result is null", getCapabilitiesResult);
  }

  public void testGetCertificatePass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject retRelevant = new JsonObject();

    expect(
        connection.call(eq("conduit.getcertificate"), capture(paramsCapture)))
        .andReturn(retRelevant).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.GetCertificateResult result = module.getCertificate(
        "tokenFoo", "hostBar");

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Tokens do not match", "tokenFoo", params.get("token"));
    assertEquals("Hostnames do not match", "hostBar", params.get("host"));

    assertNotNull("Result is null", result);
  }

  public void testQueryPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject retRelevant = new JsonObject();

    expect(connection.call(eq("conduit.query"), capture(paramsCapture)))
        .andReturn(retRelevant).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.QueryResult result = module.query();

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);

    assertNotNull("Result is null", result);
  }

  @Override
  protected ConduitModule getModule() {
    return new ConduitModule(connection, sessionHandler, "userFoo", "certBar");
  }
}
