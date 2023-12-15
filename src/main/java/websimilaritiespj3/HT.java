package websimilaritiespj3;

import java.io.*;
import java.util.*;

/**
 * A simple hash table implementation supporting serialization.
 * This class is used to store key-value pairs of URLs and their corresponding
 * frequency tables.
 *
 * @param <K> the type of keys maintained by this hash table
 * @param <V> the type of mapped values
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
class HT<K, V extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private transient List<Node<K, V>> table;
    private int size;

    /**
     * A node within the hash table.
     */
    static final class Node<K, V> implements Serializable {
        final K key;
        V value;
        transient Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * Constructs an empty HT with the default initial capacity (16) and the default
     * load factor (0.75).
     */
    public HT() {
        table = new ArrayList<>(Collections.nCopies(DEFAULT_INITIAL_CAPACITY, null));
    }

    /**
     * Associates the specified value with the specified key in this hash table.
     * If the hash table previously contained a mapping for the key, the old value
     * is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    void put(K key, V value) {
        int h = hash(key);
        int i = h & (table.size() - 1);
        for (Node<K, V> e = table.get(i); e != null; e = e.next) {
            if (key.equals(e.key)) {
                e.value = value;
                return;
            }
        }
        table.set(i, new Node<>(key, value, table.get(i)));
        size++;
        if ((float) size / table.size() > LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * If the specified key is not already associated with a value, associates it
     * with the given value.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void putIfAbsent(K key, V value) {
        if (!contains(key)) {
            put(key, value);
        }
    }

    /**
     * Copies all of the mappings from the specified hash table to this hash table.
     * These mappings will replace any mappings that this hash table had for any of
     * the keys currently in the specified hash table.
     *
     * @param other the hash table whose mappings are to be placed in this hash
     *              table
     */
    public void putAll(HT<K, V> other) {
        for (Node<K, V> node : other.entrySet()) {
            this.put(node.key, node.value);
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this hash
     * table contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this hash
     *         table contains no mapping for the key
     */
    V get(K key) {
        if (key == null) {
            return null; // Return null immediately if the key is null
        }
        int h = hash(key);
        int i = h & (table.size() - 1);
        for (Node<K, V> e = table.get(i); e != null; e = e.next) {
            if (key.equals(e.key)) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Checks if a key exists in the hash table.
     *
     * @param key the key to check for existence in the hash table
     * @return true if the key exists, false otherwise
     */
    boolean contains(K key) {
        int h = hash(key);
        int i = h & (table.size() - 1);
        for (Node<K, V> e = table.get(i); e != null; e = e.next) {
            if (key.equals(e.key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the hash table is empty.
     *
     * @return true if the hash table contains no key-value mappings, false
     *         otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes the mapping for the specified key from this hash table if present.
     *
     * @param key key whose mapping is to be removed from the hash table
     */
    void remove(K key) {
        int h = hash(key);
        int i = h & (table.size() - 1);
        Node<K, V> prev = null;
        for (Node<K, V> e = table.get(i); e != null; e = e.next) {
            if (key.equals(e.key)) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    table.set(i, e.next);
                }
                size--;
                return;
            }
            prev = e;
        }
    }

    /**
     * Removes all of the mappings from this hash table. The hash table will be
     * empty
     * after this call returns.
     */
    public void clear() {
        table = new ArrayList<>(Collections.nCopies(DEFAULT_INITIAL_CAPACITY, null));
        size = 0;
    }

    /**
     * Prints all key-value pairs in the hash table.
     */
    void printAll() {
        for (int i = 0; i < table.size(); i++) {
            for (Node<K, V> e = table.get(i); e != null; e = e.next) {
                System.out.println("Key: " + e.key + ", Value: " + e.value);
            }
        }
    }

    /**
     * Returns the size of the hash table.
     */
    public int size() {
        return size;
    }

    /**
     * Doubles the size of the hash table when the load factor threshold is reached.
     */
    private void resize() {
        int newCapacity = table.size() * 2;
        List<Node<K, V>> newTable = new ArrayList<>(Collections.nCopies(newCapacity, null));
        for (Node<K, V> headNode : table) {
            while (headNode != null) {
                Node<K, V> nextNode = headNode.next;
                int i = hash(headNode.key) & (newCapacity - 1);
                headNode.next = newTable.get(i);
                newTable.set(i, headNode);
                headNode = nextNode;
            }
        }
        table = newTable;
    }

    /**
     * Returns a Set view of the keys contained in this hash table.
     *
     * @return a set view of the keys contained in this hash table
     */
    Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (Node<K, V> headNode : table) {
            for (Node<K, V> e = headNode; e != null; e = e.next) {
                keySet.add(e.key);
            }
        }
        return keySet;
    }

    /**
     * Returns a Collection view of the values contained in this hash table.
     *
     * @return a collection view of the values contained in this hash table
     */
    Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        for (Node<K, V> headNode : table) {
            for (Node<K, V> e = headNode; e != null; e = e.next) {
                values.add(e.value);
            }
        }
        return values;
    }

    /**
     * Returns a Set view of the mappings contained in this hash table.
     *
     * @return a set view of the mappings contained in this hash table
     */
    public Set<Node<K, V>> entrySet() {
        Set<Node<K, V>> entrySet = new HashSet<>();
        for (Node<K, V> headNode : table) {
            for (Node<K, V> e = headNode; e != null; e = e.next) {
                entrySet.add(e);
            }
        }
        return entrySet;
    }

    /**
     * Serialization of the hashtable.
     *
     * @param oos the ObjectOutputStream to write to
     * @throws IOException if an I/O error occurs
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        int totalEntries = 0;
        for (Node<K, V> head : table) {
            for (Node<K, V> e = head; e != null; e = e.next) {
                totalEntries++;
            }
        }
        oos.writeInt(totalEntries); // Write the total number of entries
        oos.writeInt(size); // Write the actual number of elements (key-value pairs)
        for (Node<K, V> head : table) {
            for (Node<K, V> e = head; e != null; e = e.next) {
                oos.writeObject(e.key);
                oos.writeObject(e.value);
            }
        }
    }

    /**
     * Deserialization of the hashtable.
     *
     * @param ois the ObjectInputStream to read from
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be
     *                                found
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        try {
            ois.defaultReadObject();
            // Initialize an empty table regardless of the serialized data.
            table = new ArrayList<>(Collections.nCopies(DEFAULT_INITIAL_CAPACITY, null));
            size = 0; // Reset size
        } catch (OptionalDataException ode) {
            System.out.println("Deserialization failed, reinitializing the hashtable.");
            table = new ArrayList<>(Collections.nCopies(DEFAULT_INITIAL_CAPACITY, null));
            size = 0;
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }

    /**
     * Generates a hash code for a given key.
     *
     * @param key the key to hash
     * @return the hash code
     */
    private int hash(K key) {
        return (key == null) ? 0 : key.hashCode();
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if
     * this map contains no mapping for the key.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     *         {@code defaultValue} if this map contains no mapping for the key
     */
    public V getOrDefault(K key, V defaultValue) {
        if (key == null) {
            return defaultValue; // Return defaultValue if the key is null
        }
        V value = get(key);
        return (value != null) ? value : defaultValue;
    }
}
