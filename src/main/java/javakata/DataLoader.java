// The MIT License
//
// Copyright (c) 2016 Jordan E. Terrell
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission not0ice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package javakata;

import java.io.*;
import java.util.*;
import java.util.regex.*;

final class DataLoader {
    static final Pattern ORG_LINE_PATTERN = Pattern.compile(
        "^\\s*"
      + "(?<orgId>\\d+)\\s*,\\s*"
      + "(?<parentOrgId>(\\d+|null))\\s*,\\s*"
      + "(?<name>[\\w ]*?)\\s*?\\r?(\\n|$)"
    );

    static final Pattern USER_LINE_PATTERN = Pattern.compile(
        "^\\s*"
      + "(?<userId>\\d+)\\s*,\\s*"
      + "(?<orgId>(\\d+|null))\\s*,\\s*"
      + "(?<files>(\\d+|null))\\s*,\\s*"
      + "(?<bytes>(\\d+|null))\\s*?\\r?(\\n|$)"
    );

    private DataLoader() {
        // Prevent instantiation
    }

    public static Result<OrgCollection, String> load(
        final File orgFile,
        final File userFile)
        throws FileNotFoundException {

        if (orgFile == null) {
            throw new IllegalArgumentException("orgFile argument is null");
        }

        if (userFile == null) {
            throw new IllegalArgumentException("userFile argument is null");
        }

        return load(new TextLineIterable(orgFile),
                    new TextLineIterable(userFile));
    }

    public static Result<OrgCollection, String> load(
        final Iterable<String> orgData,
        final Iterable<String> userData) {

        if (orgData == null) {
            throw new IllegalArgumentException("orgData argument is null");
        }

        if (userData == null) {
            throw new IllegalArgumentException("userData argument is null");
        }

        List<String> observations = new LinkedList<String>();
        OrgCollectionImpl orgs = new OrgCollectionImpl();

        loadOrgData(orgData, orgs, observations);
        loadUserData(userData, orgs, observations);

        return new Result<OrgCollection, String>(orgs, observations);
    }

    static void loadOrgData(final Iterable<String> orgData,
                            final OrgCollectionImpl orgs,
                            final List<String> observations) {
        int lineNumber = 0;
        for (String line : orgData) {
            lineNumber++;

            Matcher match = ORG_LINE_PATTERN.matcher(line);
            if (!match.matches()) {
                observations.add(String.format(
                    "ERROR (line: %d) - "
                  + "Organization record did not match expected format: '%s'",
                  lineNumber,
                  line
                ));
                continue;
            }

            int id = Integer.parseInt(match.group("orgId"));
            String parentIdString = match.group("parentOrgId");

            Integer parentId = !parentIdString.equals("null")
                ? Integer.parseInt(parentIdString)
                : null;

            try {
                OrgImpl org = new OrgImpl(id);
                orgs.addOrg(org, parentId);
            }
            catch (Exception e) {
                observations.add(String.format(
                    "ERROR (line: %d) - "
                  + "Organization record data could not be interpreted: '%s'\n"
                  + "  Message: %s",
                  lineNumber,
                  line,
                  e.getMessage()
                ));
            }
        }
    }

    static void loadUserData(final Iterable<String> userData,
                             final OrgCollectionImpl orgs,
                             final List<String> observations) {
        int lineNumber = 0;
        for (String line : userData) {
            lineNumber++;

            Matcher match = USER_LINE_PATTERN.matcher(line);
            if (!match.matches()) {
                observations.add(String.format(
                    "ERROR (line: %d) - "
                  + "User record did not match expected format: '%s'",
                  lineNumber,
                  line
                ));
                continue;
            }

            int orgId = Integer.parseInt(match.group("orgId"));
            int files = Integer.parseInt(match.group("files"));
            int bytes = Integer.parseInt(match.group("bytes"));

            try {
                OrgImpl org = orgs.getOrg(orgId);
                if (org == null) {
                    observations.add(String.format(
                          "ERROR (line: %d) - "
                        + "User record references unknown organization: '%s'\n",
                        lineNumber,
                        line
                    ));
                    continue;
                }

                org.addUser(files, bytes);
            }
            catch (Exception e) {
                observations.add(String.format(
                    "ERROR (line: %d) - "
                  + "User record data could not be interpreted: '%s'\n"
                  + "  Message: %s",
                  lineNumber,
                  line,
                  e.getMessage()
                ));
            }
        }
    }

    static boolean doesFileExist(final File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            return false;
        }

        return true;
    }
}
