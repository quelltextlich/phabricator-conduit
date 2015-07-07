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
package at.quelltextlich.phabricator.conduit.demo;

import java.io.PrintStream;
import java.util.Date;
import java.util.Scanner;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;

import at.quelltextlich.phabricator.conduit.raw.Conduit;
import at.quelltextlich.phabricator.conduit.raw.ConduitFactory;
import at.quelltextlich.phabricator.conduit.results.ConduitPing;
import at.quelltextlich.phabricator.conduit.results.ManiphestInfo;
import at.quelltextlich.phabricator.conduit.results.ManiphestUpdate;

/**
 * Simple demo for phabricator-conduit
 */
public class Demo {
  @Option(name = "--help", aliases = { "-help", "-h", "-?", "help" },
      help = true, usage = "print this help screen")
  private boolean help;

  @Option(name = "--url", required = true, usage = "url of the Phabricator "
      + "instance to connect to (without the trailing \"/api/\"")
  private String url;

  @Option(name = "--username", usage = "username to use when connecting to "
      + "the Phabricator instance")
  private String username = null;

  @Option(name = "--certificate", usage = "certificate to use when "
      + "connecting to the Phabricator instance")
  private String certificate = null;

  private int taskId = -1;
  @Option(name = "--task", usage = "The task id to mangle during the demo "
      + "(without the leading \"T\")")
  private void setTaskId(String str) {
    if (str.startsWith("T")) {
      str = str.substring(1);
    }
    taskId = Integer.parseInt(str);
  }

  PrintStream stdout = System.out;
  PrintStream stderr = System.err;

  private void parseArgs(final String[] args) {
    final CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
    } catch (final CmdLineException e) {
      stderr.println("Failed to parse args: " + e);
      e.printStackTrace(stderr);
      System.exit(1);
    }

    if (help) {
      // User asked for help screen
      parser.printUsage(stderr);
      stderr.println();
      stderr.print(parser.printExample(OptionHandlerFilter.REQUIRED));
      System.exit(0);
    }
  }

  private final String DASHES = "--------------------------------------"
      + "-----------------------------------------";

  private void logStart(String phase) {
    stdout.println();
    stdout.print("-- " + phase + " ");
    int printedChars = 3 + phase.length() + 1;
    if (printedChars < 80) {
      stdout.println(DASHES.substring(printedChars));
    }
  }

  private void logEnd() {
    stdout.println(DASHES);
    stdout.println("All demos ran.");
  }

  private boolean askPermission(String question) {
    stdout.println(question);
    stdout.println("(If yes, type in 'yes' (without quotes) and hit enter.)");

    Scanner scanner = new Scanner(System.in);
    String response = scanner.nextLine();
    scanner.close();

    stdout.println();

    return response != null && "yes".equals(response);
  }

  public void run(final String[] args) {
    parseArgs(args);

    try {
      // Creating a Conduit instance. This instance is the main entry point
      // for the raw bindings.
      Conduit conduit = ConduitFactory.createConduit(url, username, certificate);



      logStart("conduit.ping (anonymous)"); // --------------------------------

      ConduitPing conduitPing = conduit.getConduitModule().ping();

      // The line above looks Java-ish by using a getter.
      //
      // If you prefer a Phabricator-ish look, you could instead use
      //
      //   ConduitPing conduitPing = conduit.conduit.ping();
      //
      // which more closely resembles the called "conduit.ping" Phabricator
      // Conduit method.

      stdout.println("conduit.ping returned hostname: "
          + conduitPing.getHostname());



      // ----------------------------------------------------------------------

      if (username == null || certificate == null || taskId < 0) {
        logEnd();
        stdout.println("To run some more API calls, please provide a username, "
            + "certificate, and taskId");
        System.exit(0);
      }



      logStart("maniphest.info"); // ------------------------------------------

      stdout.println("Fetching info about task T" + taskId + "...");

      ManiphestInfo maniphestInfo = conduit.getManiphestModule().info(taskId);

      stdout.println("Here it is:");
      stdout.println("  * title: " + maniphestInfo.getTitle());
      stdout.println("  * created on: " + new Date(
          Long.parseLong(maniphestInfo.getDateCreated()) * 1000));
      stdout.println("  * modified on: " + new Date(
          Long.parseLong(maniphestInfo.getDateModified()) * 1000));
      stdout.println("  * priority: " + maniphestInfo.getPriority());
      stdout.println("  * uri: " + maniphestInfo.getUri());
      stdout.println("(Much more information is available. See the "
          + "ManiphestInfo class)");



      logStart("maniphest.update"); // ------------------------------------------

      if (askPermission("May I add a comment saying 'Test comment' to task T"
          + taskId + "?")) {
        stdout.println("Adding 'Test comment' to task T" + taskId + "...");
        ManiphestUpdate maniphestUpdate = conduit.getManiphestModule().update(taskId,
            "Test comment");
        stdout.println("Done.");

        stdout.println("Fetching fresh info about task T" + taskId + "...");
        maniphestInfo = conduit.getManiphestModule().info(taskId);
        stdout.println("New task modification date is " + new Date(
            Long.parseLong(maniphestUpdate.getDateModified()) * 1000));
      }



      logEnd(); // ------------------------------------------------------------
    } catch (Exception e) {
      stderr.println("Demo failed: " + e);
      e.printStackTrace(stderr);
      System.exit(1);
    }
  }

  public static void main(final String[] args) {
    Demo demo = new Demo();
    demo.run(args);
  }
}
