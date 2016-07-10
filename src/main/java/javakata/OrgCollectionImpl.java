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

import java.util.*;

final class OrgCollectionImpl implements OrgCollection {
    private final HashMap<Integer, OrgImpl> orgs;
    private final LinkedList<OrgImpl> rootOrgs;

    public OrgCollectionImpl() {
        this.orgs = new HashMap<Integer, OrgImpl>();
        this.rootOrgs = new LinkedList<OrgImpl>();
    }

    public void addOrg(final OrgImpl org, final Integer parentOrgId) {
        if (org == null) {
            throw new IllegalArgumentException("org argument is null");
        }

        OrgImpl parentOrg = null;
        if (parentOrgId != null) {
            parentOrg = this.orgs.get(parentOrgId);

            // TODO: add second pass logic
            if (parentOrg == null) {
                throw new IllegalArgumentException(
                    "parentOrgId references an organization that does not exist"
                );
            }
        }

        // just put the org in the collection and optimize for
        // the case when there is usually no existing org
        OrgImpl old = this.orgs.put(org.getId(), org);
        if (old != null) {
            this.orgs.put(org.getId(), old);

            throw new IllegalArgumentException(
                "Duplicate organization detected. Id: "
              + Integer.toString(org.getId())
            );
        }

        if (parentOrg != null) {
            parentOrg.addChildOrg(org);
        }
        else {
            this.rootOrgs.add(org);
        }
    }

    public Iterable<Org> getRootOrgs() {
        return (Iterable) this.rootOrgs;
    }

    public Org getOrg(final int orgId) {
        return null;
    }

    public List<Org> getOrgTree(final int orgId, final boolean inclusive) {
        return null;
    }
}
