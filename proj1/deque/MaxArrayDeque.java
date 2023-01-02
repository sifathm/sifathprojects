package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{

    private Comparator comparer;

    public MaxArrayDeque(Comparator<T> c){
        super();
        this.comparer = c;
    }

    public T max() {
        if (size() <= 0) {
            return null;
        }

        T max = this.get(0);
        for (int i = 1; i < this.size(); i++) {
            if (comparer.compare(max, this.get(i)) < 0) {
                max = this.get(i);
            }
        }
        return max;
    }

    public T max(Comparator<T> c) {
        if (size() <= 0) {
            return null;
        }

        T max = this.get(0);
        for (int i = 1; i < this.size(); i++) {
            if (c.compare(max, this.get(i)) < 0) {
                max = this.get(i);
            }
        }
        return max;
    }
}
