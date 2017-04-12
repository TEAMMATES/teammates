package teammates.storage.entity;

import java.lang.reflect.Field;

import javax.jdo.annotations.PrimaryKey;

final class Entity {
    private Entity() {
    }

    static String getFieldWithPrimaryKeyAnnotation(Class<?> cls) {
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                return f.getName();
            }
        }
        return "";
    }
}
