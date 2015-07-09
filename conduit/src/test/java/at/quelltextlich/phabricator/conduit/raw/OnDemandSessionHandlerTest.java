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

import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.Map;

import at.quelltextlich.phabricator.conduit.ConduitException;

public class OnDemandSessionHandlerTest extends SessionTestCase {
  public void testFillInSessionWithoutConduit() {
    final Map<String, Object> params = new HashMap<String, Object>();

    final SessionHandler sessionHandler = new OnDemandSessionHandler();

    try {
      sessionHandler.fillInSession(params);
      fail("no exception got thrown");
    } catch (final ConduitException e) {
    }

  }

  public void testFillInSessionEmptyParams() throws ConduitException {
    final ConduitModule.ConnectResult connectResult = new ConduitModule.ConnectResult(
        1, "sessionKeyFoo", "userBar");

    final ConduitModule conduitModule = createMock(ConduitModule.class);
    expect(conduitModule.connect()).andReturn(connectResult);

    final Map<String, Object> params = new HashMap<String, Object>();

    replayMocks();

    final OnDemandSessionHandler sessionHandler = new OnDemandSessionHandler();
    sessionHandler.setConduitModule(conduitModule);

    sessionHandler.fillInSession(params);

    assertHasSessionKey("sessionKeyFoo", params);

    assertLogMessageContains("Trying to start new session");
  }

  public void testFillInSessionReuseSessios() throws ConduitException {
    final ConduitModule.ConnectResult connectResult = new ConduitModule.ConnectResult(
        2, "sessionKeyFoo", "userBar");

    final ConduitModule conduitModule = createMock(ConduitModule.class);
    expect(conduitModule.connect()).andReturn(connectResult).once();

    final Map<String, Object> params1 = new HashMap<String, Object>();
    final Map<String, Object> params2 = new HashMap<String, Object>();

    replayMocks();

    final OnDemandSessionHandler sessionHandler = new OnDemandSessionHandler();
    sessionHandler.setConduitModule(conduitModule);

    sessionHandler.fillInSession(params1);
    assertHasSessionKey("sessionKeyFoo", params1);

    sessionHandler.fillInSession(params2);
    assertHasSessionKey("sessionKeyFoo", params2);

    assertLogMessageContains("Trying to start new session");
  }
}
