package ru.practicum.shareit.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientUtils {
    public static Map<String, Object> createParameters(Object... args) {
        Map<String, Object> parameters = new HashMap<>();

        Arrays.stream(args)
                .forEach(parameter -> parameters.put(parameter.toString(), parameter));

        return parameters;
    }
}
