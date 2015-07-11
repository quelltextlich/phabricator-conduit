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

/**
 * Bindings for Phabricator's Conduit API
 * <p/>
 * To easily create instances from only connection strings, see the
 * {@link ConduitFactory#createConduit(String, String, String)} method.
 * <p/>
 * This class is not thread-safe.
 */
public class Conduit {
  public final ConduitModule conduit;
  public final ManiphestModule maniphest;
  public final PhidModule phid;
  public final ProjectModule project;
  public final UserModule user;

  public Conduit(final ConduitModule conduitModule,
      final ManiphestModule maniphestModule, final PhidModule phidModule,
      final ProjectModule projectModule, final UserModule userModule) {
    conduit = conduitModule;
    maniphest = maniphestModule;
    phid = phidModule;
    project = projectModule;
    user = userModule;
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

  /**
   * Gets the current PhidModule
   *
   * @return Gets the current PhidModule
   */
  public PhidModule getPhidModule() {
    return phid;
  }

  /**
   * Gets the current ProjectModule
   *
   * @return Gets the current ProjectModule
   */
  public ProjectModule getProjectModule() {
    return project;
  }

  /**
   * Gets the current UserModule
   *
   * @return Gets the current UserModule
   */
  public UserModule getUserModule() {
    return user;
  }
}
