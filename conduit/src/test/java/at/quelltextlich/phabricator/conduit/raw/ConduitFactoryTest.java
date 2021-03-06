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

import at.quelltextlich.phabricator.conduit.testutil.LoggingMockingTestCase;

public class ConduitFactoryTest extends LoggingMockingTestCase {
  public void testCreateConduit() {
    final Conduit conduit = ConduitFactory.createConduit("urlFoo", "userBar",
        "certBaz");

    assertNotNull("conduit module not initialized", conduit.conduit);
    assertNotNull("maniphest module not initialized", conduit.maniphest);
    assertNotNull("phid module not initialized", conduit.phid);
    assertNotNull("project module not initialized", conduit.project);
    assertNotNull("user module not initialized", conduit.user);
  }
}
