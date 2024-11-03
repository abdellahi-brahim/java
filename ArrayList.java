import java.util.*;

/**
 * Custom implementation of a dynamically resizing ArrayList.
 *
 * @param <T> The type of elements in this list
 */
public class ArrayList<T> implements List<T>, Iterable<T> {
    private static final int INITIAL_CAPACITY = 4;
    private static final double GROWTH_FACTOR = 1.618; // Golden ratio for balanced growth
    private int size = 0; // Number of elements in the list
    private T[] data; // Internal array to store elements
    private int modCount = 0; // Used to track modifications for fail-fast iterators

    /**
     * Default constructor initializes the internal array with the initial capacity.
     */
    @SuppressWarnings("unchecked")
    public ArrayList() {
        data = (T[]) new Object[INITIAL_CAPACITY];
    }

    /**
     * Constructor that initializes the list with elements from a collection.
     * This ensures the internal array has enough capacity to hold all elements.
     *
     * @param collection the collection of elements to add
     */
    @SuppressWarnings("unchecked")
    public ArrayList(Collection<? extends T> collection) {
        data = (T[]) new Object[Math.max(INITIAL_CAPACITY, collection.size())];
        addAll(collection);
    }

    /**
     * Ensures the internal array has enough capacity for at least `minCapacity` elements.
     *
     * @param minCapacity the minimum capacity required
     */
    private void ensureCapacity(int minCapacity) {
        if (data.length < minCapacity) {
            int newCapacity = Math.max((int) (data.length * GROWTH_FACTOR), minCapacity + 2);
            data = Arrays.copyOf(data, newCapacity);
            modCount++;
        }
    }

    /**
     * Adds a new element to the end of the list.
     *
     * @param value the element to be added
     * @return true if the element was added
     * @throws NullPointerException if the value is null
     */
    @Override
    public boolean add(T value) {
        Objects.requireNonNull(value, "Cannot add null to the list");
        ensureCapacity(size + 1); // Ensure capacity before adding
        data[size++] = value; // Place the value and increment size
        modCount++;
        return true;
    }

    /**
     * Inserts an element at a specified index.
     *
     * @param index   the index where the element should be inserted
     * @param element the element to insert
     * @throws NullPointerException      if the element is null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    public void add(int index, T element) {
        checkBoundsForAdd(index);
        Objects.requireNonNull(element, "Cannot add null to the list");
        ensureCapacity(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
        modCount++;
    }

    /**
     * Removes and returns the element at a specified index.
     *
     * @param index the index of the element to remove
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    public T remove(int index) {
        checkBounds(index);
        T removedValue = data[index];
        int elementsToShift = size - index - 1;
        if (elementsToShift > 0) {
            System.arraycopy(data, index + 1, data, index, elementsToShift);
        }
        data[--size] = null; // Clear the last element to prevent memory leaks
        if (size > 0 && size <= data.length / 4) {
            shrinkIfNecessary(); // Shrink array if too much unused space
        }
        modCount++;
        return removedValue;
    }

    /**
     * Removes the first occurrence of the specified element from the list, if it is present.
     *
     * @param o the element to be removed
     * @return true if the list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(data[i], o)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the element at a specified index.
     *
     * @param index the index of the element to get
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    public T get(int index) {
        checkBounds(index);
        return data[index];
    }

    /**
     * Replaces the element at the specified position with the specified element.
     *
     * @param index   the index of the element to replace
     * @param element the new element to store at the specified position
     * @return the element previously at the specified position
     * @throws NullPointerException      if the element is null
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    public T set(int index, T element) {
        checkBounds(index);
        Objects.requireNonNull(element, "Cannot set null to the list");
        T oldValue = data[index];
        data[index] = element;
        return oldValue;
    }

    /**
     * Checks if the index is within the bounds of the list for access methods.
     *
     * @param index the index to check
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    private void checkBounds(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Checks if the index is within bounds for add operations.
     *
     * @param index the index to check
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    private void checkBoundsForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Shrinks the internal array if too much unused space exists.
     */
    private void shrinkIfNecessary() {
        int newCapacity = Math.max(INITIAL_CAPACITY, (int) (data.length / GROWTH_FACTOR));
        if (newCapacity < data.length) {
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    /**
     * Returns the current size of the list.
     *
     * @return the number of elements in the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Checks if the list is empty.
     *
     * @return true if the list is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears the list, removing all elements and resetting the size.
     */
    @Override
    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
        modCount++;
    }

    /**
     * Checks if the list contains the specified element.
     *
     * @param o the element to check for
     * @return true if the list contains the specified element, otherwise false
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element, or -1 if not found.
     *
     * @param o the element to search for
     * @return the index of the element, or -1 if not found
     */
    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(data[i], o)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Provides an iterator to traverse the list.
     *
     * @return an iterator for this list
     */
    @Override
    public Iterator<T> iterator() {
        return new ArrayListIterator();
    }

    private class ArrayListIterator implements Iterator<T> {
        private int cursor = 0;
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public T next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException("ArrayList was modified during iteration");
            }
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the list");
            }
            return data[cursor++];
        }
    }

    /**
     * Adds all elements from the given collection to the list.
     *
     * @param collection the collection containing elements to add
     * @return true if the list was changed as a result of the call
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        Objects.requireNonNull(collection, "Collection cannot be null");
        ensureCapacity(size + collection.size());
        for (T element : collection) {
            add(element);
        }
        return !collection.isEmpty();
    }

    /**
     * Provides a string representation of the list.
     *
     * @return the string representation of the list
     */
    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(data, size));
    }
}
