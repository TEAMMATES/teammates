package teammates.test.driver;

import java.io.IOException;
import java.lang.reflect.Method;
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

    private Method getMethod(IMethodInstance mi) {
        return mi.getMethod().getConstructorOrMethod().getMethod();
    }

    private Class<?> getDeclaringClassOfMethod(IMethodInstance mi) {
        return mi.getMethod().getRealClass();
    }

    private int getMethodPriority(IMethodInstance mi) {
        Method method = getMethod(mi);
        Priority a1 = method.getAnnotation(Priority.class);
        if (a1 != null) {
            return a1.value();
        }
        return 0;
    }

    private int getClassPriority(IMethodInstance mi) {
        Class<?> cls = getDeclaringClassOfMethod(mi);
        Priority classPriority = cls.getAnnotation(Priority.class);
        if (classPriority != null) {
            return classPriority.value();
        }
        return 0;
    }

    private String getPackageName(IMethodInstance mi) {
        return getDeclaringClassOfMethod(mi).getPackage().getName();
    }

    private String getClassName(IMethodInstance mi) {
        return getDeclaringClassOfMethod(mi).getName();
    }

    private int getPackagePriority(String packageName) {
        int index = packageOrder.indexOf(packageName);

        if (index == -1) {
            return 0;
        }
        return -index;
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {

        //Package Name -> Package priority
        Comparator<IMethodInstance> compareByPackage = (m1, m2) -> {
            String p1 = getPackageName(m1);
            String p2 = getPackageName(m2);

            return p1.compareTo(p2) - getPackagePriority(p1) + getPackagePriority(p2);
        };

        //(Package name -> package priority) -> class priority -> class name -> method priority
        Comparator<IMethodInstance> comparator = compareByPackage
                .thenComparing(m -> getClassPriority(m))
                .thenComparing(m -> getClassName(m))
                .thenComparing(m -> getMethodPriority(m));

        methods.sort(comparator);

        return methods;
    }

}
