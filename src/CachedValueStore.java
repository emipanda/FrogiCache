import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
In order to maintain get, put and delete complexity as O(1) we're using HashMap pointing to the Nodes in the Doubly
Linked List of key, value pairs.
Having bidirectional connection between the Nodes allows us to remove elements from any position in the List in O(1).
In addition to that, having key to Node relation in map allows us to access/modify any element in O(1).
As read and put operations are in fact composition of previously described actions, we can perform both in O(1).
 */

public class CachedValueStore implements ValueStore {
    // Dummy head and tail. In empty Cache head points to tail and vice versa
    private final Node head = new Node("", "");
    private final Node tail = new Node("", "");

    // A list of all available value stores. list order should be kept.
     private List<ValueStore> valueStores;
     private int maxCachedItems;
    private final Map<String, Node> cachedDataMap = new HashMap<>();


    /**
     * Constructor for the cache value store.
     * @param valueStore        list of ordered value stores to operate on
     * @param maxCachedItems    number of maximum items the cache can hold
     */
    public CachedValueStore(List<ValueStore> valueStore, int maxCachedItems) {
        this.valueStores = valueStore;
        this.maxCachedItems = maxCachedItems;
        // Empty Cache in the beginning
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    /**
     * Iterate on valueStores and find the first key that matches. order is important.
     */
    public String read(String key) {
        // Found in cache, moving to the beginning of the linked list.
        if (cachedDataMap.containsKey(key)) {
            Node node = cachedDataMap.get(key);
            remove(node);
            insertFirst(node);
            return node.value;
        } else { // Found in valueStore, copying to cache
            for (ValueStore valueStore : valueStores) {
                String result = valueStore.read(key);
                if (result == null)
                    continue;

                //update cache key list, move value to top and check cache capacity
                sustainCacheCapacity();
                insertFirst(new Node(key, result));

                return result;
            }
        }
        // Not found both in cache and in value store
        return null;
    }

    /**
     * Put <key, value> in first valueStores only. order is important.
     */
    public void put(String key, String value) {
        // Moving to the top afterwards even if it's already in the list
        if (cachedDataMap.containsKey(key)) {
            remove(cachedDataMap.get(key));
        }

        // Sustaining max capacity
        sustainCacheCapacity();
        insertFirst(new Node(key, value));

        // Add value to the first valueStore
        if (!valueStores.isEmpty()){
            valueStores.get(0).put(key, value);
        }
        else
            throw new IllegalStateException("valueStores is empty");
    }

    /**
     * Iterate on valueStores and delete the key from all of them.
     */
    public void delete(String key) {
        if (cachedDataMap.containsKey(key)) {
            Node node = cachedDataMap.get(key);
            remove(node);
        }
        valueStores.forEach(valueStore -> valueStore.delete(key));
    }

    private void sustainCacheCapacity() {
        if (cachedDataMap.size() == maxCachedItems) {
            remove(tail.prev);
        }
    }

    // Doubly Linked List removal
    private void remove(Node node) {
        cachedDataMap.remove(node.key);
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Doubly linked list insertion to the head
    private void insertFirst(Node node) {
        cachedDataMap.put(node.key, node);
        node.next = head.next;
        node.next.prev = node;
        head.next = node;
        node.prev = head;
    }

    public List<ValueStore> getValueStores() {
        return valueStores;
    }

    public void setValueStores(List<ValueStore> valueStores) {
        this.valueStores = valueStores;
    }

    public int getMaxCachedItems() {
        return maxCachedItems;
    }

    public void setMaxCachedItems(int maxCachedItems) {
        this.maxCachedItems = maxCachedItems;
    }
}

class Node {
    Node prev, next;
    String key, value;
    Node(String _key, String _value) {
        key = _key;
        value = _value;
    }
}