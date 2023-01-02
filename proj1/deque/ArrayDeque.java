package deque;

import java.lang.reflect.Array;

public class ArrayDeque<T> implements Deque<T> {

    private T[] arr;
    private int start;
    private int end;
    private int size;
    private double usageFactor;


    /* Constructor */
    public ArrayDeque() {
        arr = (T[]) new Object[8];
        start = arr.length/2;
        end = start-1;
        size = 0;
        usageFactor = (double)size/arr.length;
    }



    //Helper Methods
    private void doubleSize(int currentSize) {
        int newLength = currentSize*2;
        T[] tempArray = (T[]) new Object[newLength];
        int tempStart = newLength/4;
        for (int i = 0; end%arr.length != (i+start)%arr.length; i++) {
            tempArray[i+tempStart] = arr[(i+start)% arr.length];
        }
        tempArray[tempStart + arr.length-1] = arr[end];
        this.start = tempStart;
        this.end = tempStart + arr.length-1;
        this.arr = tempArray;
    }

    private void halfSize(int currentSize) {
        int newLength = currentSize/2;
        T[] tempArray = (T[]) new Object[newLength];
        int newStart = newLength/4;
        int counter = 0;
        for (int i = 0; end%arr.length != (i+start)%arr.length; i++) {
            tempArray[newStart+i] = arr[(i+start)% arr.length];
            counter++;
        }
        tempArray[newStart+counter] = arr[end];
        this.start = newStart;
        this.end = newStart + counter;
        this.arr = tempArray;
    }

    private void checkResize(){
        usageFactor = (double)size/arr.length;
        if (usageFactor < 0.25 && arr.length > 8) {
            halfSize(arr.length);
        }
        else if(usageFactor >= 1) {
            doubleSize(arr.length);
        } else {
            return;
        }
    }

    private void addFirstindex() {
        this.start = start-1;
        if (start < 0) {
            start += arr.length;
        }
    }

    private void addLastindex() {
        this.end++;
        end %= arr.length;
    }

    private void removeFirstindex() {
        this.start++;
        start%= arr.length;
    }

    private void removeLastindex() {
        this.end = end-1;
        if (this.end < 0) {
            this.end += arr.length;
        }
    }





    //Deque Methods
    @Override
    public void addFirst(T item) {
        checkResize();
        addFirstindex(); //Gets the start marker to the necessary position.
        arr[start] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        checkResize();
        addLastindex(); //Gets the end marker to the necessary position.
        arr[end] = item;
        size++;
    }

    @Override
    public int size() {
        if (size >= 0) {
            return this.size;
        }
        return 0;
    }

    @Override
    public void printDeque() {
        for (int i = 0; end%arr.length != (i+start)%arr.length; i++) {
            System.out.print((arr[start+i]).toString() + " ");
        }
        System.out.println(arr[end]);
    }

    @Override
    public T removeFirst() {
        T currentVal = arr[start];
        removeFirstindex();
        size--;
        checkResize(); // Resizes at end to make sure that the usage factor is never less than 25%.
        return currentVal; // return the deleted value.
    }

    @Override
    public T removeLast() {
        T currentVal = arr[end];
        removeLastindex();
        size--;
        checkResize(); // Resizes at end to make sure that the usage factor is never less than 25%.
        return currentVal; // return the deleted value.
    }

    @Override
    public T get(int index) {
        return arr[(index + start)%arr.length];
    }

    @Override
    public boolean equals(Object O) {
        if (!(O instanceof Deque)) {
            return false;
        }
        Deque temp = (Deque) O;
        if(temp.size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if(!get(i).equals(temp.get(i))) {
                return false;
            }
        }
        return true;
    }
}