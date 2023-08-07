package trainingback.util;


import java.lang.reflect.Field;

public class NullChecker {
    public static <T> boolean almostOneFieldIsNull(T obj) throws IllegalAccessException {
        if (obj==null){
            return true;
        }
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.get(obj) == null) {
                return true;
            }
        }
        return false;
    }
}
