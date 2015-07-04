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

package at.quelltextlich.phabricator.conduit.testutil;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;

import at.quelltextlich.phabricator.conduit.testutil.log.LogUtil;

public abstract class LoggingMockingTestCase extends MockingTestCase {

  private java.util.Collection<LoggingEvent> loggedEvents;

  protected final void assertLogMessageContains(final String needle,
      final Level level) {
    LoggingEvent hit = null;
    final Iterator<LoggingEvent> iter = loggedEvents.iterator();
    while (hit == null && iter.hasNext()) {
      final LoggingEvent event = iter.next();
      if (event.getRenderedMessage().contains(needle)) {
        if (level == null || level.equals(event.getLevel())) {
          hit = event;
        }
      }
    }
    assertNotNull("Could not find log message containing '" + needle + "'", hit);
    assertTrue("Could not remove log message containing '" + needle + "'",
        loggedEvents.remove(hit));
  }

  protected final void assertLogMessageContains(final String needle) {
    assertLogMessageContains(needle, null);
  }

  protected final void assertLogThrowableMessageContains(final String needle) {
    LoggingEvent hit = null;
    final Iterator<LoggingEvent> iter = loggedEvents.iterator();
    while (hit == null && iter.hasNext()) {
      final LoggingEvent event = iter.next();

      if (event.getThrowableInformation().getThrowable().toString()
          .contains(needle)) {
        hit = event;
      }
    }
    assertNotNull("Could not find log message with a Throwable containing '"
        + needle + "'", hit);
    assertTrue("Could not remove log message with a Throwable containing '"
        + needle + "'", loggedEvents.remove(hit));
  }

  // As the PowerMock runner does not pass through runTest, we inject log
  // verification through @After
  @After
  public final void assertNoUnassertedLogEvents() {
    if (loggedEvents.size() > 0) {
      final LoggingEvent event = loggedEvents.iterator().next();
      String msg = "Found untreated logged events. First one is:\n";
      msg += event.getRenderedMessage();
      if (event.getThrowableInformation() != null) {
        msg += "\n" + event.getThrowableInformation().getThrowable();
      }
      fail(msg);
    }
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    loggedEvents = new LinkedList<LoggingEvent>();

    // The logger we're interested is class name without the trailing "Test".
    // While this is not the most general approach it is sufficient for now,
    // and we can improve later to allow tests to specify which loggers are
    // to check.
    String logName = this.getClass().getCanonicalName();
    logName = logName.substring(0, logName.length() - 4);
    LogUtil.logToCollection(logName, loggedEvents);
  }

  @Override
  protected void runTest() throws Throwable {
    super.runTest();
    // Plain JUnit runner does not pick up @After, so we add it here
    // explicitly. Note, that we cannot put this into tearDown, as failure
    // to verify mocks would bail out and might leave open resources from
    // subclasses open.
    assertNoUnassertedLogEvents();
  }
}
