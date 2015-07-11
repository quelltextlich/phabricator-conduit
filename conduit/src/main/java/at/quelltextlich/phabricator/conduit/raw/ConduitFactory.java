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

import at.quelltextlich.phabricator.conduit.bare.Connection;

public class ConduitFactory {
  public static Conduit createConduit(final String baseUrl,
      final String username, final String certificate) {
    final Connection connection = new Connection(baseUrl);

    final OnDemandSessionHandler sessionHandler = new OnDemandSessionHandler();

    final ConduitModule conduitModule = new ConduitModule(connection,
        sessionHandler, username, certificate);
    sessionHandler.setConduitModule(conduitModule);

    final ManiphestModule maniphestModule = new ManiphestModule(connection,
        sessionHandler);
    final PhidModule phidModule = new PhidModule(connection, sessionHandler);
    final ProjectModule projectModule = new ProjectModule(connection,
        sessionHandler);

    return new Conduit(conduitModule, maniphestModule, phidModule,
        projectModule);
  }
}
