package byog.Core;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    //private int sentinal;
    private int first;
    private int last;
    private double rate;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        first = items.length / 2;
        last = items.length / 2;
        rate = size / items.length;
    }

    public void addFirst(T item) {
        if (size == 0) {
            items[first] = item;
            size++;
        } else {
            size++;
            first--;
            if (first < 0) {
                first = first + items.length;
            }
            if (first == last) {
                first = first + 1;
                resize();
                last = last - 1;
            }
            items[first] = item;

        }
    }

    public void addLast(T item) {
        if (size == 0) {
            items[last] = item;
            size++;
        } else {
            last = (last + 1) % items.length;
            size++;

            if (last == first) {
                last = last - 1;
                resize();
                items[last] = item;
                first = first + 1;
            } else {
                items[last] = item;
            }
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (first < last) {
            for (int i = first; i <= last; i++) {
                System.out.print(items[i] + " ");
            }
        } else {
            if (size == 0) {
                System.out.println(items[first]);
            } else {
                for (int i = first; i < items.length; i++) {
                    System.out.print(items[i] + " ");
                }
                for (int i = 0; i <= last; i++) {
                    System.out.print(items[i] + " ");
                }
            }
        }
    }
    /*public void print(){
        for (int i =0; i<items.length;i++){
            System.out.print(items[i] + " ");
        }
        System.out.println("");
    }*/

    public T get(int index) {
        /*for (int i =0; i<items.length;i++){
            System.out.print(items[i] + " ");
        }*/
        return items[(first + index) % items.length];
    }

    private void resize() {
        T[] newarry = (T[]) new Object[items.length * 2];
        System.arraycopy(items, first, newarry, (items.length / 2), items.length - first);
        System.arraycopy(items, 0, newarry, (3 * items.length / 2 - first), last + 1);
        first = items.length / 2 - 1;
        last = items.length * 3 / 2;
        items = newarry;
        /*for (int i =0; i<newarry.length;i++){
            System.out.print(newarry[i] + " ");
            System.out.println("");
        }*/
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        rate = (size - 1.0) / items.length;
        T remove = items[first];
        items[first] = null;
        first = (first + 1) % items.length;
        size = size - 1;
        if (size == 0) {
            first = 4;
            last = 4;
            items = (T[]) new Object[8];
            return remove;
        }
        if (rate < 0.25 && items.length > 8) {
            if (first < last) {
                T[] newarry = (T[]) new Object[items.length / 2];
                System.arraycopy(items, first, newarry, (newarry.length / 4), last - first + 1);
                int distance = last - first;
                first = newarry.length / 4;
                last = first + distance;
                items = newarry;
            } else if (first == last) {
                T[] newarry = (T[]) new Object[8];
                items = newarry;
            } else {
                T[] newarry = (T[]) new Object[items.length / 2];
                System.arraycopy(items, first, newarry, (newarry.length / 4), items.length - first);
                int beginnum = ((newarry.length / 4) + (items.length - first) - 1);
                System.arraycopy(items, 0, newarry, beginnum, last + 1);
                first = newarry.length / 4;
                last = first + (items.length - first + last) - 1;
                items = newarry;
            }
        }
        return remove;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        rate = (size - 1.0) / items.length;
        T remove = items[last];
        items[last] = null;
        last--;
        if (last < 0) {
            last += items.length;
        }
        size = size - 1;
        if (size == 0) {
            first = 4;
            last = 4;
            items = (T[]) new Object[8];
            return remove;
        }
        if (rate < 0.25 && items.length > 8) {
            if (first < last) {
                T[] newarry = (T[]) new Object[items.length / 2];
                System.arraycopy(items, first, newarry, (newarry.length / 4), last - first + 1);
                int firstminlast = (last - first);
                first = newarry.length / 4;
                last = first + firstminlast;
                items = newarry;
            } else if (first == last) {
                T[] newarry = (T[]) new Object[8];
                items = newarry;
            } else {
                T[] newarry = (T[]) new Object[items.length / 2];
                System.arraycopy(items, first, newarry, (newarry.length / 4), items.length - first);
                int beginnum = ((newarry.length / 4) + (items.length - first));
                System.arraycopy(items, 0, newarry, beginnum, last + 1);
                int distancestvsed = (items.length - first + last);
                first = newarry.length / 4;
                last = first + (items.length - first + last);
                items = newarry;
            }
        }
        return remove;
    }
}
