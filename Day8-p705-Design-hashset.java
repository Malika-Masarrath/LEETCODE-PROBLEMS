class MyHashSet {
    private Bucket[] buckets;
    private int keyRange;

    public MyHashSet() {
        keyRange = 769; // prime number for better distribution
        buckets = new Bucket[keyRange];
        for (int i = 0; i < keyRange; i++) {
            buckets[i] = new Bucket();
        }
    }

    private int hash(int key) {
        return key % keyRange;
    }

    public void add(int key) {
        int index = hash(key);
        buckets[index].insert(key);
    }

    public void remove(int key) {
        int index = hash(key);
        buckets[index].delete(key);
    }

    public boolean contains(int key) {
        int index = hash(key);
        return buckets[index].exists(key);
    }
}

class Bucket {
    private LinkedList<Integer> list;

    public Bucket() {
        list = new LinkedList<>();
    }

    public void insert(int key) {
        if (!list.contains(key)) {
            list.addFirst(key);
        }
    }

    public void delete(int key) {
        list.remove((Integer) key);
    }

    public boolean exists(int key) {
        return list.contains(key);
    }
}
