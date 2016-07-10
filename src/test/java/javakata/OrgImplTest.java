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

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.*;

public final class OrgImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createWithNegativeIdThrowsException() {
        thrown.expect(IllegalArgumentException.class);

        new OrgImpl(-42);
    }

    @Test
    public void createWithIdZeroThrowsException() {
        thrown.expect(IllegalArgumentException.class);

        new OrgImpl(0);
    }

    @Test
    public void addUserWithNegativeFilesThrowsException() {
        OrgImpl o = new OrgImpl(42);

        thrown.expect(IllegalArgumentException.class);

        o.addUser(-42, 42);
    }

    @Test
    public void addUserWithNegativeBytesThrowsException() {
        OrgImpl o = new OrgImpl(42);

        thrown.expect(IllegalArgumentException.class);

        o.addUser(42, -42);
    }

    @Test
    public void addUserWithZeroFilesAndNonZeroBytesThrowsException() {
        OrgImpl o = new OrgImpl(42);

        thrown.expect(IllegalArgumentException.class);

        o.addUser(0, 42);
    }

    @Test
    public void addUserPropertlyUpdatesState() {
        OrgImpl o = new OrgImpl(42);

        assertEquals(o.getNumUsers(), 0);
        assertEquals(o.getNumFiles(), 0);
        assertEquals(o.getNumBytes(), 0);

        o.addUser(3, 42);
        o.addUser(9, 84);

        assertEquals(o.getNumUsers(), 2);
        assertEquals(o.getNumFiles(), 3 + 9);
        assertEquals(o.getNumBytes(), 42 + 84);
    }
}
