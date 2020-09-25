package teammates.e2e.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import teammates.test.FileHelper;

/**
 * Allows TestNG to run tests in a specified order, based on the {@code Priority(n)} annotation.
 * By default, TestNG run all tests in lexical order.
 *
 * @see Priority
 */
public class PriorityInterceptor implements IMethodInterceptor {

    // This class prioritizes methods based on the following:
    // 1) Orders methods based on package name as ordered/found in testng.xml
    // 2) Orders methods based on package name in lexical order
    // 3) Orders methods by class priority e.g. Add "@Priority(1)" to class
    // 4) Orders methods by class name in lexical order
    // 5) Orders methods by priority e.g. Add "@Priority(1)" to method

    private static final Comparator<IMethodInstance> COMPARE_BY_PACKAGE = (m1, m2) -> {
        String p1 = getPackageName(m1);
        String p2 = getPackageName(m2);

        return p1.compareTo(p2) - getPackagePriority(p1) + getPackagePriority(p2);
    };

    private static final Comparator<IMethodInstance> COMPARATOR = COMPARE_BY_PACKAGE
            .thenComparing(m -> getClassPriority(m))
            .thenComparing(m -> getClassName(m))
            .thenComparing(m -> getMethodPriority(m));

    private static final String PACKAGE_ORDER;

    static {
        try {
            PACKAGE_ORDER = FileHelper.readFile("src/e2e/resources/testng-e2e.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getMethod(IMethodInstance mi) {
        return mi.getMethod().getConstructorOrMethod().getMethod();
    }

    private static Class<?> getDeclaringClassOfMethod(IMethodInstance mi) {
        return mi.getMethod().getRealClass();
    }

    private static int getMethodPriority(IMethodInstance mi) {
        Method method = getMethod(mi);
        Priority a1 = method.getAnnotation(Priority.class);
        if (a1 != null) {
            return a1.value();
        }
        return 0;
    }

    private static int getClassPriority(IMethodInstance mi) {
        Class<?> cls = getDeclaringClassOfMethod(mi);
        Priority classPriority = cls.getAnnotation(Priority.class);
        if (classPriority != null) {
            return classPriority.value();
        }
        return 0;
    }

    private static String getPackageName(IMethodInstance mi) {
        return getDeclaringClassOfMethod(mi).getPackage().getName();
    }

    private static String getClassName(IMethodInstance mi) {
        return getDeclaringClassOfMethod(mi).getName();
    }

    private static int getPackagePriority(String packageName) {
        int index = PACKAGE_ORDER.indexOf(packageName);

        if (index == -1) {
            return 0;
        }
        return -index;
    }

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        methods.sort(COMPARATOR);
        return methods;
    }

}
