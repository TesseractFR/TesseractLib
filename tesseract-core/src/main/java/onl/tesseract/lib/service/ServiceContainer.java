package onl.tesseract.lib.service;

import java.util.HashMap;

public class ServiceContainer {

    private static final ServiceContainer INSTANCE = new ServiceContainer();

    public static ServiceContainer getInstance() {
        return INSTANCE;
    }

    public static <T> T get(Class<T> type){
        return INSTANCE.getService(type);
    }

    private final HashMap<Class<?>, Object> services = new HashMap<>();

    public <T> T registerService(Class<T> type, T service){
        services.put(type, service);
        return service;
    }
    public <T> T getService(Class<T> type){
        if(!services.containsKey(type)) {
            throw new IllegalArgumentException(type.getName() + " not found");
        }
        Object service = services.get(type);
        return type.cast(service);
    }
}