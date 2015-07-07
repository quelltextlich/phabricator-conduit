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

import java.util.HashMap;
import java.util.Map;

public abstract class ModuleTestCase extends SessionTestCase {
  public Connection connection;
  public SessionHandlerStub sessionHandler;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    connection = createMock(Connection.class);
    sessionHandler = new SessionHandlerStub();
  }

  void assertHasSessionKey(final Map<String, Object> params) {
    assertHasSessionKey(sessionHandler.sessionKey, params);
  }

  protected abstract Module getModule();

  class SessionHandlerStub implements SessionHandler {
    private final String sessionKey = "sessionKeyFoo";

    private ConduitException nextException = null;

    public void failNextFillingIn(final ConduitException e) {
      nextException = e;
    }

    @Override
    public void fillInSession(final Map<String, Object> params)
        throws ConduitException {
      assertNotNull("The passed parameters are uninitialized", params);

      if (nextException == null) {
        final Object conduitParamsObj = params.get("__conduit__");
        if (conduitParamsObj == null) {
          final Map<String, Object> conduitParams = new HashMap<String, Object>();
          conduitParams.put("sessionKey", sessionKey);
          params.put("__conduit__", conduitParams);
        } else {
          if (conduitParamsObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> conduitParams = (Map<String, Object>) conduitParamsObj;
            conduitParams.put("sessionKey", sessionKey);
          }
        }
      } else {
        throw nextException;
      }
    }
  }
}