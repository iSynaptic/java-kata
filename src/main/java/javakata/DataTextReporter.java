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

final class DataTextReporter {
    private DataTextReporter() {
        // Prevent instantiation
    }

    public static void writeReport(final OrgCollection col,
                                   final PrintStream dest) {

        for (Org org : col.getRootOrgs()) {
            writeOrg(org, dest, 0);
        }
    }

    private static void writeOrg(final Org org,
                                 final PrintStream dest,
                                 final int level) {

        dest.print(String.join("", Collections.nCopies(level, "  ")));
        dest.println(String.format("- %d, %d, %d, %d",
            org.getId(),
            org.getTotalNumUsers(),
            org.getTotalNumFiles(),
            org.getTotalNumBytes()
        ));

        for (Org child : org.getChildOrgs()) {
            writeOrg(child, dest, level + 1);
        }
    }
}
