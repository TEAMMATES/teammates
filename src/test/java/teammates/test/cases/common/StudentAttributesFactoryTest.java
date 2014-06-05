package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.exception.EnrollException;
import teammates.test.cases.BaseTestCase;

public class StudentAttributesFactoryTest extends BaseTestCase {
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testConstructor() throws Exception {
        String headerRow = null;
        StudentAttributesFactory saf = null;
        
        ______TS("fail: null parameter");
        try {
            saf = new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        
        ______TS("fail: not satisfy the minimum requirement of fields");
        headerRow = "name \t email";
        try {
            saf = new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED, e.getMessage());
        }

        ______TS("fail: missing 'Name' field");
        headerRow = "section \t team \t email";
        try {
            saf = new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED, e.getMessage());
        }

        ______TS("fail: missing 'Team' field");
        headerRow = "section \t name \t email";
        try {
            saf = new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED, e.getMessage());
        }

        ______TS("fail: missing 'Email' field");
        headerRow = "section \t team \t name";
        try {
            saf = new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED, e.getMessage());
        }
        
        
        ______TS("fail: repeated required columns");
        headerRow = "name \t email \t team \t comments \t name";
        try {
            saf = new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_REPEATED, e.getMessage());
        }
        
        // remaining cases have been implicitly tested in testMakeStudent()
    }
        
    @Test
    public void testMakeStudent() throws EnrollException {
        StudentAttributesFactory saf = new StudentAttributesFactory();
        String line = null;
        String courseId = null;
        
        StudentAttributes studentCreated;
        
        ______TS("fail: empty row");
        line = "";
        courseId = "SAFT.courseId";
        try {
            saf.makeStudent(line, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_ENROLL_LINE_EMPTY, e.getMessage());
        }
        
        ______TS("fail: too few columns");
        line = "name|email";
        try {
            saf.makeStudent(line, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS, e.getMessage());
        }
        
        ______TS("success: normal column order with comment");
        saf = new StudentAttributesFactory("TEAMS|Names|Email|comments");
        line = "team 1|SAFT.name|SAFT@email.com|some comment...";
        
        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.team, "team 1");
        assertEquals(studentCreated.name, "SAFT.name");
        assertEquals(studentCreated.email, "SAFT@email.com");
        assertEquals(studentCreated.comments, "some comment...");
        
        line = "team 2|SAFT.name2|SAFT2@email.com";
        
        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.team, "team 2");
        assertEquals(studentCreated.name, "SAFT.name2");
        assertEquals(studentCreated.email, "SAFT2@email.com");
        assertEquals(studentCreated.comments, "");
        
        ______TS("success: different column order without comment");
        saf = new StudentAttributesFactory("Name|emails|teams");
        line = "SAFT.name|SAFT@email.com|team 1";
        
        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.team, "team 1");
        assertEquals(studentCreated.name, "SAFT.name");
        assertEquals(studentCreated.email, "SAFT@email.com");
        assertEquals(studentCreated.comments, "");
          
        ______TS("success: different column order, contains empty columns");
        saf = new StudentAttributesFactory("email \t name \t    \t team");
        line = "SAFT@email.com \t SAFT.name \t      \t team 1";
        
        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.team, "team 1");
        assertEquals(studentCreated.name, "SAFT.name");
        assertEquals(studentCreated.email, "SAFT@email.com");
        assertEquals(studentCreated.comments, "");
        
        ______TS("success: no header specified, assume default column order");
        saf = new StudentAttributesFactory();
        line = "section 1| team 1|SAFT.name|SAFT@email.com|comment";
        
        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.section, "section 1");
        assertEquals(studentCreated.team, "team 1");
        assertEquals(studentCreated.name, "SAFT.name");
        assertEquals(studentCreated.email, "SAFT@email.com");
        assertEquals(studentCreated.comments, "comment");
        
        line = "section 2| team 2|SAFT.name2|SAFT2@email.com";
        
        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.section, "section 2");
        assertEquals(studentCreated.team, "team 2");
        assertEquals(studentCreated.name, "SAFT.name2");
        assertEquals(studentCreated.email, "SAFT2@email.com");
        assertEquals(studentCreated.comments, "");
    }
    
    @Test
    public void testLocateColumnIndexes() throws Exception {
        String headerRow = null;
        int columnCount = 0;
        
        ______TS("not a header row");
        headerRow = "team 1|SAFT.name|SAFT@email.com";
        columnCount = invokeLocateColumnIndexes(headerRow);
        assertEquals(0, columnCount);
        
        ______TS("header row contains empty columns");
        headerRow = " | team | name | | email | | comment";
        columnCount = invokeLocateColumnIndexes(headerRow);
        assertEquals(4, columnCount);
        
    }
    
    @Test
    public void testSplitLineIntoColumns() throws Exception {
        String line = null;
        String[] columns = null;
        
        ______TS("fail: null parameter");
        try {
            invokeSplitLineIntoColumns(line);
            signalFailureToDetectException();
        } catch (InvocationTargetException e) {
            ignoreExpectedException();
        }
        
        ______TS("success: line with pipe symbol as separators");
        line = "name | email |  | team";
        columns = invokeSplitLineIntoColumns(line);
        
        assertEquals(4, columns.length);
        assertEquals("name ", columns[0]);
        assertEquals(" email ", columns[1]);
        assertEquals("  ", columns[2]);
        assertEquals(" team", columns[3]);
        
        ______TS("success: line with tab as separators");
        line = "team\temail\tname\t";
        columns = invokeSplitLineIntoColumns(line);
        
        assertEquals(4, columns.length);
        assertEquals("team", columns[0]);
        assertEquals("email", columns[1]);
        assertEquals("name", columns[2]);
        assertEquals("", columns[3]);
    }
    
    private int invokeLocateColumnIndexes(String line) throws Exception {
        Method privateMethod = StudentAttributesFactory.class.getDeclaredMethod("locateColumnIndexes", String.class);
        privateMethod.setAccessible(true);
        
        int retVal = (Integer) privateMethod.invoke(new StudentAttributesFactory(), line);
        return retVal;
    }
    
    private String[] invokeSplitLineIntoColumns(String line) throws Exception {
        Method privateMethod = StudentAttributesFactory.class.getDeclaredMethod("splitLineIntoColumns", String.class);
        privateMethod.setAccessible(true);
        
        String[] strs = (String[]) privateMethod.invoke(new StudentAttributesFactory(), line);
        return strs;
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
