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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.quelltextlich.phabricator.conduit.results.ConduitConnect;

/**
 * Bindings for Phabricator's Conduit API
 * <p/>
 * This class is not thread-safe.
 */
public class Conduit implements SessionHandler {

  private static final Logger log = LoggerFactory.getLogger(Conduit.class);

  private final Connection connection;

  private String sessionKey;

  public final ConduitModule conduit;
  public final ManiphestModule maniphest;

  public Conduit(final String baseUrl, final String username,
      final String certificate) {
    connection = new Connection(baseUrl);
    resetSession();

    conduit = new ConduitModule(connection, this, username, certificate);
    maniphest = new ManiphestModule(connection, this);
  }

  private void resetSession() {
    sessionKey = null;
  }

  /**
   * Adds session parameters to a Map of parameters
   * <p/>
   * If there is no active session, a new one is opened
   * <p/>
   * This method overrides the params' __conduit__ value.
   *
   * @param params
   *          The Map to add session paramaters to
   */
  @Override
  public void fillInSession(final Map<String, Object> params)
      throws ConduitException {
    if (sessionKey == null) {
      log.debug("Trying to start new session");
      final ConduitConnect conduitConnect = conduit.connect();
      sessionKey = conduitConnect.getSessionKey();
    }
    final Map<String, Object> conduitParams = new HashMap<String, Object>();
    conduitParams.put("sessionKey", sessionKey);
    params.put("__conduit__", conduitParams);
  }

  /**
   * Gets the current ConduitModule
   *
   * @return Gets the current ConduitModule
   */
  public ConduitModule getConduitModule() {
    return conduit;
  }

  /**
   * Gets the current ManiphestModule
   *
   * @return Gets the current ManiphestModule
   */
  public ManiphestModule getManiphestModule() {
    return maniphest;
  }
}
