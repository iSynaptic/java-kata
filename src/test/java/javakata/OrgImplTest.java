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
        o.addUser(9, 6 * 9);

        assertEquals(o.getNumUsers(), 2);
        assertEquals(o.getNumFiles(), 3 + 9);
        assertEquals(o.getNumBytes(), 42 + (6 * 9));
    }

    @Test
    public void recursiveComputationsAreCorrect() {
        // This test is a bit large and convoluted,
        // but since this is an exercise, I'll have
        // a little fun with it.

        OrgCollectionImpl col = new OrgCollectionImpl();

        OrgImpl root = new OrgImpl(4);
        OrgImpl org1 = new OrgImpl(8);
        OrgImpl org1_1 = new OrgImpl(15);
        OrgImpl org1_2 = new OrgImpl(16);
        OrgImpl org2 = new OrgImpl(23);
        OrgImpl org2_1 = new OrgImpl(42);
        OrgImpl org2_2 = new OrgImpl(108);

        col.addOrg(root, null);
        col.addOrg(org1, 4);
        col.addOrg(org1_1, 8);
        col.addOrg(org1_2, 8);
        col.addOrg(org2, 4);
        col.addOrg(org2_1, 23);
        col.addOrg(org2_2, 23);

        // no user data added yet
        assertEquals(root.getTotalNumUsers(), 0);
        assertEquals(root.getTotalNumFiles(), 0);
        assertEquals(root.getTotalNumBytes(), 0);

        // add user to leaf org
        org2_2.addUser(84, 75);

        assertEquals(root.getTotalNumUsers(), 1);
        assertEquals(root.getTotalNumFiles(), 84);
        assertEquals(root.getTotalNumBytes(), 75);

        assertEquals(org2.getTotalNumUsers(), 1);
        assertEquals(org2.getTotalNumFiles(), 84);
        assertEquals(org2.getTotalNumBytes(), 75);

        // other branch org not affected
        assertEquals(org1.getTotalNumUsers(), 0);
        assertEquals(org1.getTotalNumFiles(), 0);
        assertEquals(org1.getTotalNumBytes(), 0);

        // add user to branch org
        org1.addUser(4, 21);

        // everthing roles up
        assertEquals(root.getTotalNumUsers(), 2);
        assertEquals(root.getTotalNumFiles(), 88);
        assertEquals(root.getTotalNumBytes(), 96);

        // other branch org not affected
        assertEquals(org2.getTotalNumUsers(), 1);
        assertEquals(org2.getTotalNumFiles(), 84);
        assertEquals(org2.getTotalNumBytes(), 75);
    }
}
