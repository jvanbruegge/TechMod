package com.jvanbruegge.techmod.misc;

import java.util.Iterator;
import java.util.List;

public class ZipList3Iterable<A, B, C> implements Iterable<Tuple3<A, B, C>> {

    private final List<A> a;
    private final List<B> b;
    private final List<C> c;
    public ZipList3Iterable(List<A> a, List<B> b, List<C> c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public Iterator<Tuple3<A, B, C>> iterator() {
        return new Iterator<Tuple3<A, B, C>>() {
            private Iterator<A> itA = a.iterator();
            private Iterator<B> itB = b.iterator();
            private Iterator<C> itC = c.iterator();

            @Override
            public boolean hasNext() {
                return itA.hasNext() && itB.hasNext() && itC.hasNext();
            }

            @Override
            public Tuple3<A, B, C> next() {
                return new Tuple3<>(itA.next(), itB.next(), itC.next());
            }
        };
    }
}
