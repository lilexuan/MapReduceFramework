package user;

import mr.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类由用户自己编写
 */
public class UserMR {
    public static String fileDir = "demo1";
    public static int nWorker = 8;
    public static int nReduce = 10;

    public static List<KeyValue> mapf(String key, String value) {
        List<KeyValue> kva = new ArrayList<>();
        value = value.replaceAll("[^a-zA-Z]", " ");
        value = value.replaceAll(" {2,}", " ");
        String[] words = value.split(" ");
        for (String s : words) {
            if (s != null && s.length() > 0) {
                kva.add(new KeyValue(s.toLowerCase(), "1"));
            }
        }
        return kva;
    }

    public static KeyValue reducef(String key, List<String> values) {
        return new KeyValue(key, String.valueOf(values.size()));
    }
}
