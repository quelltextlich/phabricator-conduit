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
package at.quelltextlich.phabricator.conduit;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.resetToStrict;
import static org.powermock.api.easymock.PowerMock.expectNew;

import java.util.Map;

import org.easymock.Capture;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import at.quelltextlich.phabricator.conduit.results.ConduitConnect;
import at.quelltextlich.phabricator.conduit.results.ConduitPing;
import at.quelltextlich.phabricator.conduit.results.ManiphestInfo;
import at.quelltextlich.phabricator.conduit.results.ManiphestUpdate;
import at.quelltextlich.phabricator.conduit.testutil.LoggingMockingTestCase;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Conduit.class)
public class ConduitTest extends LoggingMockingTestCase {
  private final static String URL = "urlFoo";
  private final static String USERNAME = "usernameFoo";
  private final static String CERTIFICATE = "certificateFoo";

  private Connection connection;

  public void testConduitPingPass() throws Exception {
    mockConnection();

    expect(connection.call("conduit.ping")).andReturn(new JsonPrimitive("foo"))
        .once();

    replayMocks();

    final Conduit conduit = new Conduit(URL);

    final ConduitPing actual = conduit.conduitPing();

    assertEquals("Hostname does not match", "foo", actual.getHostname());
  }

  public void testConduitPingConnectionFail() throws Exception {
    mockConnection();

    final ConduitException conduitException = new ConduitException();

    expect(connection.call("conduit.ping")).andThrow(conduitException).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL);

    try {
      conduit.conduitPing();
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }
  }

  public void testConduitConnectPass() throws Exception {
    mockConnection();

    final JsonObject ret = new JsonObject();
    ret.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andReturn(ret).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    final ConduitConnect conduitConnect = conduit.conduitConnect();

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", USERNAME, params.get("user"));

    assertEquals("Session keys don't match", "KeyFoo",
        conduitConnect.getSessionKey());
  }

  public void testConduitConnectConnectionFail() throws Exception {
    mockConnection();

    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    try {
      conduit.conduitConnect();
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", USERNAME, params.get("user"));
  }

  public void testManiphestInfoPass() throws Exception {
    mockConnection();

    resetToStrict(connection);

    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCaptureConnect = createCapture();

    expect(
        connection.call(eq("conduit.connect"), capture(paramsCaptureConnect)))
        .andReturn(retConnect).once();

    final JsonObject retRelevant = new JsonObject();
    retRelevant.add("id", new JsonPrimitive(42));

    final Capture<Map<String, Object>> paramsCaptureRelevant = createCapture();

    expect(
        connection.call(eq("maniphest.info"), capture(paramsCaptureRelevant)))
        .andReturn(retRelevant).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    final ManiphestInfo maniphestInfo = conduit.maniphestInfo(42);

    final Map<String, Object> paramsConnect = paramsCaptureConnect.getValue();
    assertEquals("Usernames do not match", USERNAME, paramsConnect.get("user"));

    final Map<String, Object> paramsRelevant = paramsCaptureRelevant.getValue();
    assertEquals("Task id is not set", 42, paramsRelevant.get("task_id"));

    assertEquals("ManiphestInfo's id does not match", 42, maniphestInfo.getId());

    assertLogMessageContains("Trying to start new session");
  }

  public void testManiphestInfoFailConnect() throws Exception {
    mockConnection();

    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    try {
      conduit.maniphestInfo(42);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", USERNAME, params.get("user"));

    assertLogMessageContains("Trying to start new session");
  }

  public void testManiphestInfoFailRelevant() throws Exception {
    mockConnection();

    resetToStrict(connection);

    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCaptureConnect = createCapture();

    expect(
        connection.call(eq("conduit.connect"), capture(paramsCaptureConnect)))
        .andReturn(retConnect).once();

    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCaptureRelevant = createCapture();

    expect(
        connection.call(eq("maniphest.info"), capture(paramsCaptureRelevant)))
        .andThrow(conduitException).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    try {
      conduit.maniphestInfo(42);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> paramsConnect = paramsCaptureConnect.getValue();
    assertEquals("Usernames do not match", USERNAME, paramsConnect.get("user"));

    final Map<String, Object> paramsRelevant = paramsCaptureRelevant.getValue();
    assertEquals("Task id is not set", 42, paramsRelevant.get("task_id"));

    assertLogMessageContains("Trying to start new session");
  }

  public void testManiphestUpdatePass() throws Exception {
    mockConnection();

    resetToStrict(connection);

    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCaptureConnect = createCapture();

    expect(
        connection.call(eq("conduit.connect"), capture(paramsCaptureConnect)))
        .andReturn(retConnect).once();

    final JsonObject retRelevant = new JsonObject();
    retRelevant.add("id", new JsonPrimitive(42));

    final Capture<Map<String, Object>> paramsCaptureRelevant = createCapture();

    expect(
        connection.call(eq("maniphest.update"), capture(paramsCaptureRelevant)))
        .andReturn(retRelevant).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    final ManiphestUpdate maniphestUpdate = conduit.maniphestUpdate(42, "foo");

    final Map<String, Object> paramsConnect = paramsCaptureConnect.getValue();
    assertEquals("Usernames do not match", USERNAME, paramsConnect.get("user"));

    final Map<String, Object> paramsRelevant = paramsCaptureRelevant.getValue();
    assertEquals("Task id is not set", 42, paramsRelevant.get("id"));

    assertEquals("ManiphestInfo's id does not match", 42,
        maniphestUpdate.getId());

    assertLogMessageContains("Trying to start new session");
  }

  public void testManiphestUpdateFailConnect() throws Exception {
    mockConnection();

    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCapture = createCapture();

    expect(connection.call(eq("conduit.connect"), capture(paramsCapture)))
        .andThrow(conduitException).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    try {
      conduit.maniphestUpdate(42, "foo");
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> params = paramsCapture.getValue();
    assertEquals("Usernames do not match", USERNAME, params.get("user"));

    assertLogMessageContains("Trying to start new session");
  }

  public void testManiphestUpdateFailRelevant() throws Exception {
    mockConnection();

    resetToStrict(connection);

    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCaptureConnect = createCapture();

    expect(
        connection.call(eq("conduit.connect"), capture(paramsCaptureConnect)))
        .andReturn(retConnect).once();

    final ConduitException conduitException = new ConduitException();

    final Capture<Map<String, Object>> paramsCaptureRelevant = createCapture();

    expect(
        connection.call(eq("maniphest.update"), capture(paramsCaptureRelevant)))
        .andThrow(conduitException).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    try {
      conduit.maniphestUpdate(42, "foo");
      fail("no exception got thrown");
    } catch (final ConduitException e) {
      assertSame(conduitException, e);
    }

    final Map<String, Object> paramsConnect = paramsCaptureConnect.getValue();
    assertEquals("Usernames do not match", USERNAME, paramsConnect.get("user"));

    final Map<String, Object> paramsRelevant = paramsCaptureRelevant.getValue();
    assertEquals("Task id is not set", 42, paramsRelevant.get("id"));

    assertLogMessageContains("Trying to start new session");
  }

  public void testConnectionReuse() throws Exception {
    mockConnection();

    resetToStrict(connection);

    final JsonObject retConnect = new JsonObject();
    retConnect.add("sessionKey", new JsonPrimitive("KeyFoo"));

    final Capture<Map<String, Object>> paramsCaptureConnect = createCapture();

    expect(
        connection.call(eq("conduit.connect"), capture(paramsCaptureConnect)))
        .andReturn(retConnect).once();

    final JsonObject retRelevant = new JsonObject();
    retRelevant.add("id", new JsonPrimitive(42));

    final Capture<Map<String, Object>> paramsCaptureRelevant = createCapture();

    expect(
        connection.call(eq("maniphest.info"), capture(paramsCaptureRelevant)))
        .andReturn(retRelevant).once();

    replayMocks();

    final Conduit conduit = new Conduit(URL, USERNAME, CERTIFICATE);

    final ConduitConnect conduitConnect = conduit.conduitConnect();
    final ManiphestInfo maniphestInfo = conduit.maniphestInfo(42);

    final Map<String, Object> paramsConnect = paramsCaptureConnect.getValue();
    assertEquals("Usernames do not match", USERNAME, paramsConnect.get("user"));

    final Map<String, Object> paramsRelevant = paramsCaptureRelevant.getValue();
    assertEquals("Task id is not set", 42, paramsRelevant.get("task_id"));

    assertEquals("Session keys don't match", "KeyFoo",
        conduitConnect.getSessionKey());

    assertEquals("ManiphestInfo's id does not match", 42, maniphestInfo.getId());
  }

  private void mockConnection() throws Exception {
    connection = createMock(Connection.class);
    ;
    expectNew(Connection.class, URL).andReturn(connection).once();
  }
}