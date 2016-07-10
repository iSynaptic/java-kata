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

import java.io.*;
import java.util.*;

final class TextLineIterable implements Iterable<String> {

    private BufferedReader reader;
    private Iterator<String> iterator;

    public TextLineIterable(final BufferedReader br) {
        this.iterator = new TextLineIterator(br, false);
    }

    public TextLineIterable(final File f) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(f));
        this.iterator = new TextLineIterator(this.reader, true);
    }

    public Iterator<String> iterator() {
        return this.iterator;
    }

    private class TextLineIterator implements Iterator<String> {
        private boolean ownReader;
        private BufferedReader reader;
        private String line;

        public TextLineIterator(final BufferedReader br,
                                final boolean ownReader) {
            this.reader = br;
            this.ownReader = ownReader;
            this.advance();
        }

        public boolean hasNext() {
            return this.line != null;
        }

        public String next() {
            String retval = this.line;
            this.advance();
            return retval;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }

        private void advance() {
            BufferedReader temp = this.reader;
            this.line = null;
            if (temp != null) {
                try {
                    this.line = temp.readLine();
                    if (this.line == null) {
                        this.close();
                    }
                }
                catch (IOException e) {
                    this.close();
                }
            }
        }

        private void close() {
            BufferedReader temp = this.reader;
            this.reader = null;
            this.line = null;
            if (this.ownReader && temp != null) {
                try {
                    temp.close();
                }
                catch (IOException e) {
                    // we tried / perhaps some logging is needed here
                    return;
                }
            }
        }
    }
}
