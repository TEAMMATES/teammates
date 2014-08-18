package teammates.test.util;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

/**
 * By default, testng runs all methods in a test in lexical order.
 * by default, testng allows "@(priority = 1)" to order methods.
 * 
 * This class prioritizes methods based on the following: 
 *      1) Orders methods based on package name as ordered/found in testng.xml
 *      2) Orders methods based on package name in lexical order
 *      3) Orders methods by class priority e.g. Add "@Priority(1)" to class
 *      4) Orders methods by class name in lexical order
 *      5) Orders methods by priority e.g. Add "@Priority(1)" to method
 *      
 */

public class PriorityInterceptor implements IMethodInterceptor {
    static String packageOrder;
    static{
        try{
            packageOrder = FileHelper.readFile("src\\test\\testng.xml", Charset.defaultCharset());
        } catch (Exception e){
            packageOrder = FileHelper.readFile("src/test/testng.xml", Charset.defaultCharset());
        }
    }
    
    @SuppressWarnings("deprecation")
    public List<IMethodInstance> intercept(List<IMethodInstance> methods,
            ITestContext context) {
        Comparator<IMethodInstance> comparator = new Comparator<IMethodInstance>() {

            private int getMethodPriority(IMethodInstance mi) {
                int result = 0;
                Method method = mi.getMethod().getMethod();
                Priority a1 = method.getAnnotation(Priority.class);
                if (a1 != null) {
                    result = a1.value();
                }
                return result;
            }
            
            @SuppressWarnings({ "rawtypes", "unchecked" })
            private int getClassPriority(IMethodInstance mi) {
                int result = 0;
                Method method = mi.getMethod().getMethod();
                Class cls = method.getDeclaringClass();
                Priority classPriority = (Priority) cls
                        .getAnnotation(Priority.class);
                if (classPriority != null) {
                    result = classPriority.value();
                }
                return result;
            }
            
            private String getPackageName(IMethodInstance mi){
                return mi.getMethod().getMethod().getDeclaringClass().getPackage().getName();
            }
            
            private String getClassName(IMethodInstance mi){
                return mi.getMethod().getMethod().getDeclaringClass().getName();
            }
            
            private int packagePriorityOffset(String packageName){
                int index = packageOrder.indexOf(packageName);
                //Storage tests go first!
                if(packageName.contains("storage")){
                    return 1000000;
                }
                //Action tests go last. (in component tests)
                if(packageName.contains("teammates.test.cases.ui")){
                    return -1000000;
                }
                if(index != -1){
                    return -index;
                } else {
                    return 0;
                }
            }

            public int compare(IMethodInstance m1, IMethodInstance m2) {
                int val = 0;
                
                //Compare by package name
                String p1 = getPackageName(m1);
                String p2 = getPackageName(m2);
                val = p1.compareTo(p2);
                val -= packagePriorityOffset(p1);
                val += packagePriorityOffset(p2);
                if(val != 0){
                    return val;
                }
                
                //Compare by class priority
                val = getClassPriority(m1) - getClassPriority(m2);
                if(val != 0){
                    return val;
                }
                
                //Compare by class name
                val = getClassName(m1).compareTo(getClassName(m2));
                if(val != 0){
                    return val;
                }
                
                //Compare by method priority
                val = getMethodPriority(m1) - getMethodPriority(m2);
                if(val != 0){
                    return val;
                }
                
                return 0;
            }

        };
        IMethodInstance[] array = methods.toArray(new IMethodInstance[methods
                .size()]);
        Arrays.sort(array, comparator);

        return Arrays.asList(array);
    }

}