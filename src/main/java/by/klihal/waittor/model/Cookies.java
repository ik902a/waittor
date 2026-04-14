package by.klihal.waittor.model;

import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private final Map<String, String> values = new HashMap<>();

    public Cookies() {
    }

    public Cookies(Map<String, String> cookies) {
        this.values.putAll(cookies);
    }

    public Map<String, String> update(Map<String, String> cookies) {
        this.values.putAll(cookies);
        return new HashMap<>(cookies);
    }

    public Map<String, String> get() {
        return new HashMap<>(values);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
