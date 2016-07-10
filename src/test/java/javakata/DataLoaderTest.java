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
// The above copyright notice and this permission notice shall be included in
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

import java.util.*;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.io.*;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.*;


public final class DataLoaderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void loadWithNullOrgFileThrowsException() throws IOException {
        Path emptyFilePath =
            Paths.get("1a725b4c4929421b2063f1a0971ba30752ae46c9");

        try {
            List<String> lines = Arrays.asList("");
            Files.write(emptyFilePath, lines, Charset.forName("UTF-8"));

            thrown.expect(IllegalArgumentException.class);

            DataLoader.load(null, new File(emptyFilePath.toString()));
        }
        finally {
            Files.delete(emptyFilePath);
        }
    }

    @Test
    public void loadWithNullUserFileThrowsException() throws IOException {
        Path emptyFilePath =
            Paths.get("1a725b4c4929421b2063f1a0971ba30752ae46c9");

        try {
            List<String> lines = Arrays.asList("");
            Files.write(emptyFilePath, lines, Charset.forName("UTF-8"));

            thrown.expect(IllegalArgumentException.class);
            DataLoader.load(new File(emptyFilePath.toString()), null);
        }
        finally {
            Files.delete(emptyFilePath);
        }
    }

    @Test
    public void loadWithNullOrgDataThrowsException() {
        List<String> lines = Arrays.asList();

        thrown.expect(IllegalArgumentException.class);
        DataLoader.load(null, lines);
    }

    @Test
    public void loadWithNullUserDataThrowsException() {
        List<String> lines = Arrays.asList();

        thrown.expect(IllegalArgumentException.class);
        DataLoader.load(lines, null);
    }
}
