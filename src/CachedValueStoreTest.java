/**
 * This is a test, designed to help if needed to show the idea behind the cache.
 * You can use it if you want
 */

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CachedValueStoreTest {

    @Test
    void testCache() {
        ValueStore[] stores = new ValueStore[]{new MapValueStore()};
        CachedValueStore cache = new CachedValueStore(List.of(stores), 5);
        cache.put("a", "b");
        String a = cache.read("a");
        assertEquals("b", a);
        cache.delete("a");
        a = cache.read("a");
        assertNull(a);
    }

    @Test
    void testPut() throws IOException {
        File inputFile = Files.createTempFile("a", ".txt").toFile();
        inputFile.deleteOnExit();
        ValueStore fileStore = new FileValueStore(inputFile);
        MapValueStore mapStore = new MapValueStore();

        ValueStore[] stores = new ValueStore[]{mapStore,fileStore};

        CachedValueStore cache = new CachedValueStore(List.of(stores), 2);
        cache.put("1", "a");
        cache.put("2", "b");
        fileStore.put("9","b");
        mapStore.delete("2");
        fileStore.delete("3"); //do nothing
        cache.put("3", "c");
        cache.put("5", "b");
        cache.put("7", "b");
        cache.read("9");
        cache.put("2", "b");
        cache.put("6", "b");

        String a = cache.read("2");
        assertNotNull(a);
    }

    @Test
    void testLru() throws IOException {
        File inputFile = Files.createTempFile("a", ".txt").toFile();
        inputFile.deleteOnExit();
        ValueStore fileStore = new FileValueStore(inputFile);
        ValueStore[] stores = new ValueStore[]{fileStore};
        CachedValueStore cache = new CachedValueStore(List.of(stores), 2);
        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        fileStore.delete("1");

        String a = cache.read("1");
        assertNull(a);
    }

}