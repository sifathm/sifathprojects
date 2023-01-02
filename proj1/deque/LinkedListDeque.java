package deque;

public class LinkedListDeque<T> implements Deque<T>{

    private class Node<T> {
        private T first;
        private Node next;
        private Node before;
        Node(T first, Node next) {
            this.first = first;
            this.next = next;
        }
    }

    private Node sentinel;
    private Node endNode;
    private int size;



    public LinkedListDeque() {
        this.size = 0;

        sentinel = new Node(42, null);
        sentinel.next = sentinel;
        sentinel.before = sentinel;

        endNode = sentinel.next;
    }





    @Override
    public void addFirst(T item) {
        Node temp = new Node(item, sentinel.next);
        temp.before = sentinel;
        temp.next.before = temp;

        sentinel.next = temp;
        if (size == 0) {
            endNode = temp;
        }

        size++;
    }

    @Override
    public void addLast(T item) {
        Node temp = new Node(item, sentinel);
        temp.before = endNode;


        endNode.next = temp;
        sentinel.before = temp;

        endNode = endNode.next;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {

        Node parser = sentinel.next;
        if (size > 0) {
            while (parser.next != sentinel) {
                System.out.print(parser.first + " ");
                parser = parser.next;
            }
            System.out.println(parser);
        }
        return;
    }

    @Override
    public T removeFirst() {
        if (size > 0) {
            Node tempNode = sentinel.next;
            sentinel.next = sentinel.next.next;
            sentinel.next.before = sentinel;

            if (size == 1) {
                endNode = sentinel;
            }
            size--;
            return (T) tempNode.first;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if (size > 0) {
            Node tempNode = endNode;
            endNode = endNode.before;
            endNode.next = sentinel;
            sentinel.before = endNode;

            if (size == 1) {
                endNode = sentinel;
            }
            size--;
            return (T) tempNode.first;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if (size == 0 || index >= size) {
            return null;
        }
        Node parser = sentinel.next;
        while(index > 0) {
            parser = parser.next;
            index--;
        }
        return (T) parser.first;
    }


    public T getRecursive(int index) {
        return (T) getRecursiveoverload(sentinel.next, index);
    }

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


    private T getRecursiveoverload(Node parser, int count){
        if (count == 0) {
            return (T) parser.first;
        }   else {
            return (T) getRecursiveoverload(parser.next, count-1);
        }
    }




}
