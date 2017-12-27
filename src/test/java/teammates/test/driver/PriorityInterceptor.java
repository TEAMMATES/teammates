package teammates.test.driver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

/**
 * Allows TestNG to run tests in a specified order, based on the {@code Priority(n)} annotation.
 * By default, TestNG run all tests in lexical order.
 *
 * @see Priority
 */
public class PriorityInterceptor implements IMethodInterceptor {
    private static String packageOrder;

    static {
        try {
            packageOrder = FileHelper.readFile("src/test/testng-ci.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This class prioritizes methods based on the following:
    // 1) Orders methods based on package name as ordered/found in testng.xml
    // 2) Orders methods based on package name in lexical order
    // 3) Orders methods by class priority e.g. Add "@Priority(1)" to class
    // 4) Orders methods by class name in lexical order
    // 5) Orders methods by priority e.g. Add "@Priority(1)" to method

    @SuppressWarnings("deprecation")
    private int getMethodPriority(IMethodInstance mi) {
        int result = 0;
        Method method = mi.getMethod().getMethod();
        Priority a1 = method.getAnnotation(Priority.class);
        if (a1 != null) {
            result = a1.value();
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private int getClassPriority(IMethodInstance mi) {
        int result = 0;
        Method method = mi.getMethod().getMethod();
        Class<?> cls = method.getDeclaringClass();
        Priority classPriority = cls.getAnnotation(Priority.class);
        if (classPriority != null) {
            result = classPriority.value();
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private String getPackageName(IMethodInstance mi) {
        return mi.getMethod().getMethod().getDeclaringClass().getPackage().getName();
    }

    @SuppressWarnings("deprecation")
    private String getClassName(IMethodInstance mi) {
        return mi.getMethod().getMethod().getDeclaringClass().getName();
    }

    private int packagePriorityOffset(String packageName) {
        int index = packageOrder.indexOf(packageName);

        if (index == -1) {
            return 0;
        }
        return -index;
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods,
                                           ITestContext context) {

        //Compare by package name
        Comparator<IMethodInstance> compareByPackage = (IMethodInstance m1, IMethodInstance m2) -> {
            int val = 0;

            String p1 = getPackageName(m1);
            String p2 = getPackageName(m2);
            val = p1.compareTo(p2);
            val -= packagePriorityOffset(p1);
            val += packagePriorityOffset(p2);

            return val;
        };

        //Compare by class priority
        Comparator<IMethodInstance> compareByClassPriority = Comparator.comparing((IMethodInstance m) ->
                getClassPriority(m));

        //Compare by class name
        Comparator<IMethodInstance> compareByClassName = Comparator.comparing((IMethodInstance m) ->
                getClassName(m));

        //Compare by class name
        Comparator<IMethodInstance> compareByMethodPriority = Comparator.comparing((IMethodInstance m) ->
                getMethodPriority(m));

        //Overall Comparator sorts in the order of package name -> class priority -> class name -> method priority
        Comparator<IMethodInstance> comparator = compareByPackage.thenComparing(compareByClassPriority)
                .thenComparing(compareByClassName)
                .thenComparing(compareByMethodPriority);

        IMethodInstance[] array = methods.toArray(new IMethodInstance[methods
                .size()]);
        Arrays.sort(array, comparator);

        return Arrays.asList(array);
    }

}
