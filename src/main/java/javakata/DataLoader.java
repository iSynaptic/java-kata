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

import java.io.File;
import java.util.*;

final class DataLoader {
    private DataLoader() {
        // Prevent instantiation
    }

    public static OrgCollection load(final File orgFile, final File userFile) {
        if (orgFile == null || !doesFileExist(orgFile)) {
            throw new IllegalArgumentException(
                "orgFile argument is null or the file does not exist");
        }

        if (userFile == null || !doesFileExist(userFile)) {
            throw new IllegalArgumentException(
                "userFile argument is null or the file does not exist");
        }

        return null;
    }

    public static OrgCollection load(final Iterator<String> orgData,
                                     final Iterator<String> userData) {
        if (orgData == null) {
            throw new IllegalArgumentException("orgData argument is null");
        }

        if (userData == null) {
            throw new IllegalArgumentException("userData argument is null");
        }

        return null;
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
