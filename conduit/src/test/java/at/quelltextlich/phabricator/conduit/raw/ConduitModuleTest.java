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

import at.quelltextlich.phabricator.conduit.ConduitException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConduitModuleTest extends ModuleTestCase {
  public void testPingPass() throws Exception {
    expect(connection.call("conduit.ping")).andReturn(new JsonPrimitive("foo"))
        .once();

    replayMocks();

    final ConduitModule module = getModule();

    final ConduitModule.PingResult result = module.ping();

    final ConduitModule.PingResult expected = new ConduitModule.PingResult(
        "foo");
    assertEquals("Hostname does not match", expected, result);
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
    ret.addProperty("connectionID", 42);
    ret.addProperty("sessionKey", "sessionKeyFoo");
    ret.addProperty("userPHID", "userFoo");

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.ConnectResult result = module.connect();

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", "userFoo", params.get("user"));

    final ConduitModule.ConnectResult expected = new ConduitModule.ConnectResult(
        42, "sessionKeyFoo", "userFoo");
    assertEquals("Results do not match", expected, result);
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

    final JsonObject ret = new JsonObject();
    final JsonArray outputs = new JsonArray();
    outputs.add(new JsonPrimitive("foo"));
    outputs.add(new JsonPrimitive("bar"));
    ret.add("output", outputs);

    final JsonArray inputs = new JsonArray();
    inputs.add(new JsonPrimitive("baz"));
    ret.add("input", inputs);

    expect(
        connection.call(eq("conduit.getcapabilities"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.GetCapabilitiesResult result = module.getCapabilities();

    final ConduitModule.GetCapabilitiesResult expected = new ConduitModule.GetCapabilitiesResult();
    expected.put("output", Arrays.asList("foo", "bar"));
    expected.put("input", Arrays.asList("baz"));
    assertEquals("Results do not match", expected, result);
  }

  public void testGetCertificatePass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject ret = new JsonObject();
    ret.addProperty("username", "userFoo");
    ret.addProperty("certificate", "certBar");

    expect(
        connection.call(eq("conduit.getcertificate"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.GetCertificateResult result = module.getCertificate(
        "tokenFoo", "hostBar");

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Tokens do not match", "tokenFoo", params.get("token"));
    assertEquals("Hostnames do not match", "hostBar", params.get("host"));

    final ConduitModule.GetCertificateResult expected = new ConduitModule.GetCertificateResult(
        "userFoo", "certBar");
    assertEquals("Results do not match", expected, result);
  }

  public void testQueryPass() throws Exception {
    final Capture<Map<String, Object>> paramsCapture = createCapture();

    final JsonObject ret = new JsonObject();

    final JsonObject retFoo = new JsonObject();
    retFoo.addProperty("description", "service Foo");
    final JsonObject paramsFoo = new JsonObject();
    paramsFoo.addProperty("paramFooA", "valueA");
    paramsFoo.addProperty("paramFooB", "valueB");
    retFoo.add("params", paramsFoo);
    retFoo.addProperty("return", "return foo");
    ret.add("foo", retFoo);

    final JsonObject retBar = new JsonObject();
    retBar.addProperty("description", "service Bar");
    retBar.add("params", new JsonObject());
    retBar.addProperty("return", "return bar");
    ret.add("bar", retBar);

    expect(connection.call(eq("conduit.query"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final ConduitModule module = getModule();
    final ConduitModule.QueryResult result = module.query();

    final Map<String, Object> params = paramsCapture.getValue();
    assertHasSessionKey(params);

    final ConduitModule.QueryResult expected = new ConduitModule.QueryResult();
    final Map<String, String> expectedFooParams = new HashMap<String, String>();
    expectedFooParams.put("paramFooA", "valueA");
    expectedFooParams.put("paramFooB", "valueB");
    expected.put("foo", new ConduitModule.SingleQueryResult("service Foo",
        expectedFooParams, "return foo"));
    expected.put("bar", new ConduitModule.SingleQueryResult("service Bar",
        new HashMap<String, String>(), "return bar"));
    assertEquals("Results do not match", expected, result);
  }

  @Override
  protected ConduitModule getModule() {
    return new ConduitModule(connection, sessionHandler, "userFoo", "certBar");
  }
}
