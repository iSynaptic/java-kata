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

final class Main {
    private Main() {
        // Prevent instantiation
    }

    public static void main(final String[] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }

        File orgFile = new File(args[0]);
        File userFile = new File(args[1]);

        Result<OrgCollection, String> result = null;

        try {
            result = DataLoader.load(orgFile, userFile);

            for (String observation : result.getObservations()) {
               System.err.println(observation);
            }
        }
        catch (Exception e) {
            System.err.println("Unexpected error in processing input data:");
            e.printStackTrace(System.err);
            
            System.exit(1);
            return;
        }

        DataTextReporter.writeReport(result.getValue(), System.out);
    }

    private static void printUsage() {
        System.out.println(
            "You must provide two arguments: "
            + "{path-to-org-file} {path-to-user-file}"
        );
    }
}
