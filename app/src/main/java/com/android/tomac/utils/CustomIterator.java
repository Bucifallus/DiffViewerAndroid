package com.android.tomac.utils;

import java.util.ListIterator;

/**
 * Created by TomaC on 20.02.2018.
 */

public class CustomIterator<T> {

    private final ListIterator<T> listIterator;

    private boolean nextWasCalled = false;
    private boolean previousWasCalled = false;

    public CustomIterator(ListIterator<T> listIterator) {
        this.listIterator = listIterator;
    }

    public T next() {
        nextWasCalled = true;
        if (previousWasCalled) {
            previousWasCalled = false;
            listIterator.next ();
        }
        return listIterator.next ();
    }

    public T previous() {
        if (nextWasCalled) {
            listIterator.previous();
            nextWasCalled = false;
        }
        previousWasCalled = true;
        return listIterator.previous();
    }

    public boolean hasPrevious() {
        if (nextWasCalled) {
            listIterator.previous();
        }

        boolean toReturn = listIterator.hasPrevious();

        if (nextWasCalled) {
            listIterator.next();
        }
        return toReturn;
    }

    public boolean hasNext() {
        if (previousWasCalled) {
            listIterator.next();
        }

        boolean toReturn = listIterator.hasNext();

        if (previousWasCalled) {
            listIterator.previous();
        }
        return listIterator.hasNext();
    }

}
