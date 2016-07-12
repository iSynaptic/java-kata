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
import java.io.*;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.*;

public final class DataLoaderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void loadWithNullOrgFileThrowsException() throws IOException {
            thrown.expect(IllegalArgumentException.class);
            DataLoader.load(
                null,
                new File("1a725b4c4929421b2063f1a0971ba30752ae46c9")
            );
    }

    @Test
    public void loadWithNullUserFileThrowsException() throws IOException {
            thrown.expect(IllegalArgumentException.class);
            DataLoader.load(
                new File("1a725b4c4929421b2063f1a0971ba30752ae46c9"),
                null
            );
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

    @Test
    public void loadEmptyData() {
        List<String> orgData = Arrays.asList();
        List<String> userData = Arrays.asList();

        Result<OrgCollection, String> result =
            DataLoader.load(orgData, userData);

        assertNotNull(result);

        OrgCollection col = result.getValue();
        List<Org> rootOrgs = new LinkedList<Org>();
        col.getRootOrgs().forEach(rootOrgs::add);

        assertEquals(rootOrgs.size(), 0);
    }

    @Test
    public void loadExampleData() {
        List<String> orgData = Arrays.asList(
            "1, null, Foo",
            "2, 1, Bar",
            "4, 3, Baz",
            "5, 2, Qux",
            "3, null, Xyzzy"
        );

        List<String> userData = Arrays.asList(
            "1, 1, 10, 200"
        );

        Result<OrgCollection, String> result =
            DataLoader.load(orgData, userData);

        assertNotNull(result);

        OrgCollection col = result.getValue();
        List<Org> rootOrgs = new LinkedList<Org>();
        col.getRootOrgs().forEach(rootOrgs::add);

        assertEquals(rootOrgs.size(), 2);

        Org o = col.getOrg(1);
        assertNotNull(o);
        assertEquals(o.getTotalNumUsers(), 1);
        assertEquals(o.getTotalNumFiles(), 10);
        assertEquals(o.getTotalNumBytes(), 200);
    }
}
