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

public final class OrgCollectionImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void newCollectionHasCorrectInitialState() {

        OrgCollectionImpl col = new OrgCollectionImpl();

        // should not get null collection
        assertNotNull(col.getRootOrgs());

        // root orgs should be empty
        int count = 0;
        for (Org o : col.getRootOrgs()) {
            count++;
        }

        assertEquals(count, 0);

        // no exception, null return for getOrg
        assertNull(col.getOrg(42));

        // no exception, empty list for getOrgTree
        // not going to test inclusive == false condition
        List<Org> list = col.getOrgTree(42, true);
        assertNotNull(list);
        assertEquals(list.size(), 0);
    }

    @Test
    public void addOrgWithRootOrg() {
        OrgCollectionImpl col = new OrgCollectionImpl();
        OrgImpl org = new OrgImpl(42);

        col.addOrg(org, null);

        assertEquals(col.getOrg(42), org);

        List<Org> rootOrgs = new LinkedList<Org>();
        col.getOrgTree(42, true).forEach(rootOrgs::add);

        assertEquals(rootOrgs.size(), 1);
        assertEquals(rootOrgs.get(0), org);

        List<Org> orgTree = col.getOrgTree(42, true);
        assertEquals(orgTree.size(), 1);
        assertEquals(orgTree.get(0), org);
    }

    /*@Test
    public void addMultipleOrgs() {
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
    }*/
}
