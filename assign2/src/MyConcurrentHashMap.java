import java.util.ArrayList;
import java.util.List;

public class MyConcurrentHashMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private List<Node<K, V>>[] buckets;
    private int size;
    private int threshold;
    private float loadFactor;
    private List<Object> locks;

    public MyConcurrentHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyConcurrentHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public MyConcurrentHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.buckets = new ArrayList[initialCapacity];
        for (int i = 0; i < initialCapacity; i++) {
            buckets[i] = new ArrayList<Node<K, V>>();
        }
        this.size = 0;
        this.loadFactor = loadFactor;
        this.threshold = (int) (initialCapacity * loadFactor);
        this.locks = new ArrayList<Object>();
        for (int i = 0; i < initialCapacity; i++) {
            locks.add(new Object());
        }
    }

    public V put(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        Object lock = locks.get(bucketIndex);
        synchronized (lock) {
            List<Node<K, V>> bucket = buckets[bucketIndex];
            for (Node<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    V oldValue = node.value;
                    node.value = value;
                    return oldValue;
                }
            }
            bucket.add(new Node<K, V>(key, value));
            size++;
            if (size > threshold) {
                resize();
            }
            return null;
        }
    }

    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        Object lock = locks.get(bucketIndex);
        synchronized (lock) {
            List<Node<K, V>> bucket = buckets[bucketIndex];
            for (Node<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    return node.value;
                }
            }
            return null;
        }
    }

    public V remove(K key) {
        int bucketIndex = getBucketIndex(key);
        Object lock = locks.get(bucketIndex);
        synchronized (lock) {
            List<Node<K, V>> bucket = buckets[bucketIndex];
            for (int i = 0; i < bucket.size(); i++) {
                Node<K, V> node = bucket.get(i);
                if (node.key.equals(key)) {
                    bucket.remove(i);
                    size--;
                    return node.value;
                }
            }
            return null;
        }
    }

    public boolean containsKey(K key) {
        int bucketIndex = getBucketIndex(key);
        Object lock = locks.get(bucketIndex);
        synchronized (lock) {
            List<Node<K, V>> bucket = buckets[bucketIndex];
            for (Node<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int size() {
        return size;
    }

    private int getBucketIndex(K key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode % buckets.length);
    }

    private void resize() {
        int newCapacity = buckets.length * 2;
        List<Node<K, V>>[] newBuckets = new ArrayList[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            newBuckets[i] = new ArrayList<Node<K, V>>();
        }
        List<Object> newLocks = new ArrayList<Object>();
        for (int i = 0; i < newCapacity; i++) {
            newLocks.add(new Object());
        }
        for (List<Node<K, V>> bucket : buckets) {
            for (Node<K, V> node : bucket) {
                int bucketIndex = getBucketIndex(node.key);
                Object lock = newLocks.get(bucketIndex);
                synchronized (lock) {
                    newBuckets[bucketIndex].add(node);
                }
            }
        }
        this.buckets = newBuckets;
        this.threshold = (int) (newCapacity * loadFactor);
        this.locks = newLocks;
    }

    public List<K> getKeysByValue(V value) {
        List<K> keys = new ArrayList<>();

        for (List<Node<K, V>> bucket : buckets) {
            for (Node<K, V> node : bucket) {
                if (node.value.equals(value)) {
                    keys.add(node.key);
                }
            }
        }

        return keys;
    }

    public List<Node<K, V>> getAllData() {
        List<Node<K, V>> allData = new ArrayList<>();

        for (List<Node<K, V>> bucket : buckets) {
            synchronized (bucket) {
                allData.addAll(bucket);
            }
        }

        return allData;
    }

    public static class Node<K, V> {
        public final K key;
        public V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
