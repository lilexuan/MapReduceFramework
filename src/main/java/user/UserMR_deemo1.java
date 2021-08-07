package user;

import mr.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class UserMR_deemo1 {
    public static List<KeyValue> mapf(String key, String value) {
        List<KeyValue> kva = new ArrayList<>();
        value = value.replaceAll("\n", " ");
        value = value.replaceAll("\t", " ");
        String[] words = value.split(" ");
        for (String s : words) {
            kva.add(new KeyValue(s, "1"));
        }
        return kva;
    }

    public static KeyValue reducef(String key, List<String> values) {
        return new KeyValue(key, String.valueOf(values.size()));
    }
}
