import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CachedValueStore implements ValueStore {

    // A list of all available value stores. list order should be kept.
     private List<ValueStore> valueStores;
     private int maxCachedItems;
    private final Map<String, String> cachedDataMap = new HashMap<>();
    private final LinkedList<String> cacheOrder = new LinkedList<>();


    /**
     * Constructor for the cache value store.
     * @param valueStore        list of ordered value stores to operate on
     * @param maxCachedItems    number of maximum items the cache can hold
     */
    public CachedValueStore(List<ValueStore> valueStore, int maxCachedItems) {
        this.valueStores = valueStore;
        this.maxCachedItems = maxCachedItems;
    }

    /**
     * Iterate on valueStores and find the first key that matches. order is important.
     */
    public String read(String key) {
        String cacheResult = cachedDataMap.get(key);
        if (cacheResult == null) {
            for (ValueStore valueStore : valueStores) {
                String result = valueStore.read(key);
                if (result == null)
                    continue;

                //update cache key list, move value to top and check cache capacity
                sustainCacheCapacity();
                cachedDataMap.put(key, result);
                cacheOrder.addFirst(key);

                return result;
            }
        } else {
            // Update cache key list order, move value to top
            // Complexity is O(N), can be improved by implementing Map to Doubly Linked List Node Pointer
            cacheOrder.remove(key);
            cacheOrder.addFirst(key);
        }

        return cacheResult;

    }

    /**
     * Put <key, value> in first valueStores only. order is important.
     */
    public void put(String key, String value) {
        sustainCacheCapacity();
        // add new cache on top of the list
        cacheOrder.addFirst(key);
        cachedDataMap.put(key, value);

        //add value to first valueStore
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
        cacheOrder.remove(key);
        cachedDataMap.remove(key);
        valueStores.forEach(valueStore -> valueStore.delete(key));
    }

    private void sustainCacheCapacity(){
        if(cacheOrder.size() == maxCachedItems){
            //if cache is full, remove last element from linkedList and from HashMap
            String keyRemoved = cacheOrder.removeLast();
            cachedDataMap.remove(keyRemoved);
        }
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