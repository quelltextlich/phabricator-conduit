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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.quelltextlich.phabricator.conduit.ConduitException;
import at.quelltextlich.phabricator.conduit.results.ConduitConnect;

public class OnDemandSessionHandler implements SessionHandler {
  private static final Logger log = LoggerFactory
      .getLogger(OnDemandSessionHandler.class);

  private String sessionKey;

  private ConduitModule conduitModule;

  /**
   * Creates a new instance
   * <p/>
   * Before this instance can fill in session data, it needs to have a
   * {@link ConduitModule} injected through
   * {@link #setConduitModule(ConduitModule)}.
   */
  public OnDemandSessionHandler() {
    conduitModule = null;
    resetSession();
  };

  @Override
  public void fillInSession(final Map<String, Object> params)
      throws ConduitException {
    if (conduitModule == null) {
      throw new ConduitException("Trying to fill in session with "
          + "uninitialized ConduitModule in OnDemandSessionHandler. You "
          + "need to inject a ConduitModule before before calling "
          + "fillInSession.");
    }
    if (sessionKey == null) {
      log.debug("Trying to start new session");
      final ConduitConnect conduitConnect = conduitModule.connect();
      sessionKey = conduitConnect.getSessionKey();
    }
    final Map<String, Object> conduitParams = new HashMap<String, Object>();
    conduitParams.put("sessionKey", sessionKey);
    params.put("__conduit__", conduitParams);
  }

  /**
   * Resets the current session data
   */
  private void resetSession() {
    sessionKey = null;
  }

  /**
   * Sets the ConduitModule to be used when trying to obtain a session
   *
   * @param conduitModule
   *          the {@link ConduitModule} to be used to obitan session data
   */
  public void setConduitModule(final ConduitModule conduitModule) {
    this.conduitModule = conduitModule;
  }
}
