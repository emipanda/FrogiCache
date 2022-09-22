/**
 * JFrog Value Store Question:
 *
 * Let's assume we have some interface - ValueStore. This interface helps us as there are many ways to get key/value in a system.
 * ValueStore have 3 operations. read/put/delete. (see comments on methods)
 * You can check the ValueStoreExamples.java file to understand what valueStore implementations can be.
 * You can check the CachedValueStoreTest for basic test, to show functionality
**/
public interface ValueStore {
    // return associated value for a key. null if not exist
    String read(String key);
    // put a value for given key. override allowed.
    void put(String key, String value);
    // delete the value for given key. does nothing if the key is not in the store.
    void delete(String key);
}



