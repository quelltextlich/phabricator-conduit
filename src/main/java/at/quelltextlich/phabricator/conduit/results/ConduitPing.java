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
 * Models the result for a call to conduit.ping
 * <p/>
 * JSON is just the hostname of the instance. We wrap it in a proper object to
 * make it a nicer Java citizen.
 */
public class ConduitPing {
  private String hostname;

  public String getHostname() {
    return hostname;
  }
}
