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

package at.quelltextlich.phabricator.conduit.testutil.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Log4j appender that logs into a list
 */
public class CollectionAppender extends AppenderSkeleton {
  private Collection<LoggingEvent> events;

  public CollectionAppender() {
    events = new LinkedList<LoggingEvent>();
  }

  public CollectionAppender(Collection<LoggingEvent> events) {
    this.events = events;
  }

  @Override
  public boolean requiresLayout() {
    return false;
  }

  @Override
  protected void append(LoggingEvent event) {
    if (! events.add(event)) {
      throw new RuntimeException("Could not append event " + event);
    }
  }

  @Override
  public void close() {
  }

  public Collection<LoggingEvent> getLoggedEvents() {
    return new LinkedList<LoggingEvent>(events);
  }
}
