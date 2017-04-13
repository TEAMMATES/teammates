package teammates.storage.entity;

import java.lang.reflect.Field;

import javax.jdo.annotations.PrimaryKey;

import teammates.common.util.Assumption;

/**
 * Base class for all entities persisted to the Datastore.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class Entity {
    protected static String getFieldWithPrimaryKeyAnnotation(Class<?> cls) {
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                return f.getName();
            }
        }
        Assumption.fail("There should be a field annotated with @PrimaryKey");
        return null;
    }
}
