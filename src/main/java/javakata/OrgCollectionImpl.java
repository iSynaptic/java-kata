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

    private Map<Integer, List<OrgImpl>> toResolve;

    public OrgCollectionImpl() {
        this.orgs = new HashMap<Integer, OrgImpl>();
        this.rootOrgs = new LinkedList<OrgImpl>();
    }

    public void addOrg(final OrgImpl org, final Integer parentOrgId) {
        if (org == null) {
            throw new IllegalArgumentException("org argument is null");
        }

        // Step 1: Verify org is not duplicate and ensure correct org exists
        //         in the hashmap

        // just put the org in the collection and optimize for
        // the case when there is usually no existing org
        OrgImpl orig = this.orgs.put(org.getId(), org);
        if (orig != null && orig != org) {
            // should be edge case, put original org back in hashmap
            this.orgs.put(org.getId(), orig);

            throw new IllegalArgumentException(
                "Duplicate organization detected. Id: "
              + Integer.toString(org.getId())
            );
        }

        // Step 2: Look up any children associated with org and
        //         add the children to the org
        List<OrgImpl> deferred = null;
        if (this.toResolve != null) {
            deferred = this.toResolve.get(org.getId());
        }

        if (deferred != null) {
            for (OrgImpl child : deferred) {
                org.addChildOrg(child);
            }

            this.toResolve.remove(org.getId());
        }

        // Step 3: Essure this org is associated with its parent or
        //         register it for future resolution
        OrgImpl parentOrg = null;
        if (parentOrgId != null) {
            parentOrg = this.orgs.get(parentOrgId);
        }

        if (parentOrg != null) {
            parentOrg.addChildOrg(org);
        }
        else if (parentOrgId != null) {
            this.addForResolution(org, parentOrgId);
        }
        else {
            this.rootOrgs.add(org);
        }
    }

    private void addForResolution(final OrgImpl org,
                                  final Integer parentOrgId) {
        if (this.toResolve == null) {
            this.toResolve = new HashMap<Integer, List<OrgImpl>>();
        }

        List<OrgImpl> list = this.toResolve.get(parentOrgId);
        if (list == null) {
            list = new LinkedList<OrgImpl>();
            this.toResolve.put(parentOrgId, list);
        }

        list.add(org);
    }

    public Iterable<Org> getRootOrgs() {
        return (Iterable) this.rootOrgs;
    }

    public OrgImpl getOrg(final int orgId) {
        return this.orgs.get(orgId);
    }

    public List<Org> getOrgTree(final int orgId, final boolean inclusive) {
        List<Org> list = new LinkedList<Org>();

        OrgImpl org = this.orgs.get(orgId);
        if (org == null) {
            return list;
        }

        if (inclusive) {
            list.add(org);
        }

        this.addChildren(org, list);
        return list;
    }

    private void addChildren(final Org org, final List<Org> list) {
        List<Org> children = org.getChildOrgs();

        list.addAll(children);
        for (Org child : children) {
            this.addChildren(child, list);
        }
    }
}
