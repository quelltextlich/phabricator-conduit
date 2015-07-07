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

import java.util.Map;

import at.quelltextlich.phabricator.conduit.testutil.LoggingMockingTestCase;

public abstract class SessionTestCase extends LoggingMockingTestCase {
  void assertHasSessionKey(final String expected,
      final Map<String, Object> params) {
    final Object conduitValue = params.get("__conduit__");
    assertNotNull(params.toString(), conduitValue);
    assertTrue("Value ot \"__conduit__\" is not a Map",
        conduitValue instanceof Map<?, ?>);
    @SuppressWarnings("unchecked")
    final Map<String, Object> __conduit__ = (Map<String, Object>) conduitValue;
    assertEquals("Filled in sessions not equal", expected,
        __conduit__.get("sessionKey"));
  }
}