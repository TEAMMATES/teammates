package teammates.test.util;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Method;

import org.testng.Assert;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.api.Logic;

public class TestHelper {
    
    private static final Logic logic = new Logic();

    public static void verifyEntityDoesNotExistException(String methodName,
            Class<?>[] paramTypes, Object[] params) throws Exception {

        Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

        try {
            method.setAccessible(true); // in case it is a private method
            method.invoke(logic, params);
            Assert.fail();
        } catch (Exception e) {
            assertEquals(EntityDoesNotExistException.class, e.getCause()
                    .getClass());
        }
    }
    
    @SuppressWarnings("unused")
    private void ____invoking_private_methods__() {
    }


    @SuppressWarnings("unused")
    private void ____test_object_manipulation_methods__() {
    }

}
