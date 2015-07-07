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
package at.quelltextlich.phabricator.conduit.results;

/**
 * Models the result for a call to conduit.connect
 * <p/>
 * JSON looks like:
 *
 * <pre>
 * {
 *   "connectionID":4,
 *   "sessionKey":"5jhpmsb3xgm1eupp7snzym7mtebndd7v4vv4ub6n",
 *   "userPHID":"PHID-USER-h4n52fq2kt2v3a2qjyqh"
 * }
 * </pre>
 *
 * @author christian
 *
 */
public class ConduitConnect {
  private int connectionID;
  private String sessionKey;
  private String userPHID;

  public int getConnectionId() {
    return connectionID;
  }

  public String getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
  }

  public String getUserPhId() {
    return userPHID;
  }
}
