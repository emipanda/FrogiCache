import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * These are just examples for implementations or ideas for value stores.
 * NOTE! there is no need to really understand or read this class. it's just a reference to help understand usage.
**/

// File based value store
public class FileValueStore implements ValueStore{

    private final Properties properties;

    public FileValueStore(File inputFile) {
        properties = new Properties();
        try (FileInputStream inStream = new FileInputStream(inputFile)) {
            properties.load(inStream);
        } catch (IOException e) {
            throw new RuntimeException("...", e);
        }
    }

    @Override
    public String read(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void put(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public void delete(String key) {
        properties.remove(key);
    }
}

