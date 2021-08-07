package mr;

import java.util.List;

@FunctionalInterface
public interface Mapper {
    List<KeyValue> mapf(String key, String value) throws ClassNotFoundException;
}
