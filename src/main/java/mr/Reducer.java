package mr;

import java.util.List;

@FunctionalInterface
public interface Reducer {
    KeyValue reducef(String key, List<String> values);
}
