package teammates.test.cases.testdriver;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.ITestNGMethod;
import org.testng.SuiteRunner;
import org.testng.TestNG;
import org.testng.TestRunner;
import org.testng.annotations.Test;
import org.testng.internal.Configuration;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import teammates.test.cases.BaseTestCase;

/**
 * Verifies that the TestNG suite files contains all the test cases in the project.
 */
public class TestNgTest extends BaseTestCase {

    @Test
    public void allTestsIncludedInSuites() throws IOException, ParserConfigurationException, SAXException {
        final List<String> fullyQualifiedMethodNamesWithTestAnnotation =
                getFullyQualifiedMethodNamesWithAnnotation(Test.class);

        // `testng-all.xml` is expected to contain all tests used in the project
        String suitePath = "src/test/testng-all.xml";
        final List<String> fullyQualifiedMethodNamesInSuites = getFullyQualifiedMethodNamesFromSuitePath(suitePath);

        Collections.sort(fullyQualifiedMethodNamesWithTestAnnotation);
        Collections.sort(fullyQualifiedMethodNamesInSuites);

        assertEquals(fullyQualifiedMethodNamesWithTestAnnotation, fullyQualifiedMethodNamesInSuites);
    }

    private List<String> getFullyQualifiedMethodNamesWithAnnotation(Class<? extends Annotation> annotationClass)
            throws IOException {
        List<String> fullyQualifiedMethodNamesWithAnnotation = new ArrayList<>();

        final ClassPath classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
        final ImmutableSet<ClassPath.ClassInfo> classInfos = classPath.getTopLevelClassesRecursive(
                "teammates.test.cases");
        for (ClassPath.ClassInfo classInfo : classInfos) {
            // GodModeTest needs to only run when there are changes made to GodMode
            if (classInfo.getName().equals("teammates.test.cases.browsertests.GodModeTest")) {
                continue;
            }

            List<Method> methodsWithTestAnnotation = getMethodsWithAnnotation(classInfo.load(), annotationClass);
            for (Method method : methodsWithTestAnnotation) {
                fullyQualifiedMethodNamesWithAnnotation.add(
                        getFullyQualifiedMethodName(classInfo.getName(), method.getName()));
            }
        }

        return fullyQualifiedMethodNamesWithAnnotation;
    }

    private static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        final List<Method> methods = new ArrayList<>();

        final Method[] classMethods = clazz.getDeclaredMethods();
        for (final Method method : classMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private String getFullyQualifiedMethodName(String parentClassName, String methodName) {
        return parentClassName + "#" + methodName;
    }

    private List<String> getFullyQualifiedMethodNamesFromSuitePath(String suitePath)
            throws IOException, SAXException, ParserConfigurationException {
        Collection<XmlSuite> xmlSuites = new Parser(suitePath).parse();
        return getFullyQualifiedMethodNamesFromXmlSuites(xmlSuites);
    }

    private List<String> getFullyQualifiedMethodNamesFromXmlSuites(Collection<XmlSuite> xmlSuites) {
        List<String> methodNames = new ArrayList<>();
        for (XmlSuite xmlSuite : xmlSuites) {
            // we add the method names from the child suites before adding the method names from the current suite

            methodNames.addAll(getFullyQualifiedMethodNamesFromXmlSuites(xmlSuite.getChildSuites()));
            methodNames.addAll(getMethodNamesFromXmlSuite(xmlSuite));
        }
        return methodNames;
    }

    private List<String> getMethodNamesFromXmlSuite(XmlSuite xmlSuite) {
        List<String> methodNames = new ArrayList<>();

        final Configuration configuration = new Configuration();
        SuiteRunner suiteRunner = new SuiteRunner(configuration, xmlSuite, TestNG.DEFAULT_OUTPUTDIR);
        for (XmlTest test : xmlSuite.getTests()) {
            TestRunner testRunner = new TestRunner(configuration, suiteRunner, test, false,
                    null);
            methodNames.addAll(getMethodNamesFromTestRunner(testRunner));
        }

        return methodNames;
    }

    private List<String> getMethodNamesFromTestRunner(TestRunner testRunner) {
        List<String> methodNames = new ArrayList<>();
        for (ITestNGMethod testMethod : testRunner.getAllTestMethods()) {
            final String className = testMethod.getConstructorOrMethod().getDeclaringClass().getName();
            final String methodName = testMethod.getMethodName();
            final String fullyQualifiedMethodName = getFullyQualifiedMethodName(className, methodName);

            methodNames.add(fullyQualifiedMethodName);
        }
        return methodNames;
    }
}
