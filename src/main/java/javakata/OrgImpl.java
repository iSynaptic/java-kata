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

final class OrgImpl implements Org {
    private final int id;
    private final LinkedList<OrgImpl> children;

    private int users;
    private int files;
    private long bytes;

    public OrgImpl(final int id) {
        if (id < 1) {
            throw new IllegalArgumentException(
                "Org identifiers must be greater than or equal to one."
            );
        }

        this.id = id;
        this.children = new LinkedList<OrgImpl>();
    }

    public void addUser(final int files, final long bytes) {
        if (files < 0) {
            throw new IllegalArgumentException(
                "Users cannot have a negative number of files."
            );
        }

        if (bytes < 0) {
            throw new IllegalArgumentException(
                "Users cannot have a negative number of bytes."
            );
        }

        if (files == 0 && bytes > 0) {
            throw new IllegalArgumentException(
                "Users cannot have a non-zero number of bytes with zero files."
            );
        }

        // zero-length files are permitted
        this.users++;
        this.files += files;
        this.bytes += bytes;
    }

    public void addChildOrg(final OrgImpl child) {
        if (child == null) {
            throw new IllegalArgumentException("child argument is null");
        }

        this.children.add(child);
    }

    public int getId() {
        return this.id;
    }

    public int getNumUsers() {
        return this.users;
    }

    public int getNumFiles() {
        return this.files;
    }

    public long getNumBytes() {
        return this.bytes;
    }

    public int getTotalNumUsers() {
        int sum = this.getNumUsers();

        for (OrgImpl child : this.children) {
            sum += child.getTotalNumUsers();
        }

        return sum;
    }

    public int getTotalNumFiles() {
        int sum = this.getNumFiles();

        for (OrgImpl child : this.children) {
            sum += child.getTotalNumFiles();
        }

        return sum;
    }

    public long getTotalNumBytes() {
        long sum = this.getNumBytes();

        for (OrgImpl child : this.children) {
            sum += child.getTotalNumBytes();
        }

        return sum;
    }
}
