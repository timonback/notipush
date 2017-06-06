package de.timonback.notipush.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {


    public static Object instanceClass(String className, Object... args)
            throws ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        List<Object> argsList = Arrays.asList(args);
        List<Class<?>> params = new ArrayList<>();
        for (Object obj : args) {
            params.add(obj.getClass());
        }
        Class<?>[] paramsArray = arrayListToArray(params);
        Object obj = Class.forName(className).getConstructor(paramsArray).newInstance(args);
        return obj;
        //Class<?> clazz = obj.getClass();
        //return (T) clazz.cast(obj);
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] arrayListToArray(List<E> list) {
        int s;
        if (list == null || (s = list.size()) < 1)
            return null;
        E[] temp;
        E typeHelper = list.get(0);

        try {
            Object o = Array.newInstance(typeHelper.getClass(), s);
            temp = (E[]) o;

            for (int i = 0; i < list.size(); i++)
                Array.set(temp, i, list.get(i));
        } catch (Exception e) {
            return null;
        }

        return temp;
    }


    public static <T> T instanceClass(Class<T> clazz)
            throws ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        return clazz.newInstance();
    }


    public static <V, K> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return defaultValue;
    }
}
