import java.util.HashMap;
import java.util.Map;

// in memory based value store
public class MapValueStore implements ValueStore {

    private final Map<String, String> map = new HashMap<String, String>();

    @Override
    public String read(String key) {
        return map.get(key);
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void delete(String key) {
        map.remove(key);
    }
}
