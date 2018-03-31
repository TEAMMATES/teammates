package teammates.test.cases.datatransfer;

import java.lang.reflect.InvocationTargetException;

import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributesFactory;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link StudentAttributesFactory}.
 */
public class StudentAttributesFactoryTest extends BaseTestCase {

    @Test
    public void testConstructor() throws Exception {

        ______TS("Failure case: null parameter");
        try {
            new StudentAttributesFactory(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }

        ______TS("Failure case: not satisfy the minimum requirement of fields");
        String headerRow = "name \t email";
        try {
            new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED + ": <mark>Team</mark>",
                         e.getMessage());
        }

        ______TS("Failure case: missing 'Name' field");
        headerRow = "section \t team \t email";
        try {
            new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED + ": <mark>Name</mark>",
                         e.getMessage());
        }

        ______TS("Failure case: missing 'Team' field");
        headerRow = "section \t name \t email";
        try {
            new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED + ": <mark>Team</mark>",
                         e.getMessage());
        }

        ______TS("Failure case: missing 'Email' field");
        headerRow = "section \t team \t name";
        try {
            new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_MISSED + ": <mark>Email</mark>",
                         e.getMessage());
        }

        ______TS("Failure case: repeated required columns");
        headerRow = "name \t email \t team \t comments \t name";
        try {
            new StudentAttributesFactory(headerRow);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_HEADER_ROW_FIELD_REPEATED, e.getMessage());
        }

        // adding this for complete coverage
        ______TS("Failure case: repeated required columns");
        headerRow = "name \t email \t team \t comments \t section \t email \t team \t comments \t section ";
        try {
            new StudentAttributesFactory(headerRow);
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

        ______TS("Failure case: empty row");
        line = "";
        courseId = "SAFT.courseId";
        try {
            saf.makeStudent(line, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_ENROLL_LINE_EMPTY, e.getMessage());
        }

        ______TS("Failure case: too few columns");
        line = "name|email";
        try {
            saf.makeStudent(line, courseId);
            signalFailureToDetectException();
        } catch (EnrollException e) {
            assertEquals(StudentAttributesFactory.ERROR_ENROLL_LINE_TOOFEWPARTS, e.getMessage());
        }

        ______TS("Typical case: normal column order with comment");
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

        ______TS("Typical case: different column order without comment");
        saf = new StudentAttributesFactory("Name|emails|teams");
        line = "SAFT.name|SAFT@email.com|team 1";

        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.team, "team 1");
        assertEquals(studentCreated.name, "SAFT.name");
        assertEquals(studentCreated.email, "SAFT@email.com");
        assertEquals(studentCreated.comments, "");

        ______TS("Typical case: different column order, contains empty columns");
        saf = new StudentAttributesFactory("email \t name \t    \t team");
        line = "SAFT@email.com \t SAFT.name \t      \t team 1";

        studentCreated = saf.makeStudent(line, courseId);
        assertEquals(studentCreated.team, "team 1");
        assertEquals(studentCreated.name, "SAFT.name");
        assertEquals(studentCreated.email, "SAFT@email.com");
        assertEquals(studentCreated.comments, "");

        ______TS("Typical case: no header specified, assume default column order");
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

        ______TS("Typical case: not a header row");
        headerRow = "team 1|SAFT.name|SAFT@email.com";
        columnCount = locateColumnIndexes(headerRow);
        assertEquals(0, columnCount);

        ______TS("Typical case: header row contains empty columns");
        headerRow = " | team | name | | email | | comment";
        columnCount = locateColumnIndexes(headerRow);
        assertEquals(4, columnCount);
    }

    @Test
    public void testSplitLineIntoColumns() throws Exception {

        ______TS("Failure case: null parameter");
        try {
            splitLineIntoColumns(null);
            signalFailureToDetectException();
        } catch (InvocationTargetException e) {
            ignoreExpectedException();
        }

        ______TS("Typical case: line with pipe symbol as separators");
        String line = "name | email |  | team";
        String[] columns = splitLineIntoColumns(line);

        assertEquals(4, columns.length);
        assertEquals("name ", columns[0]);
        assertEquals(" email ", columns[1]);
        assertEquals("  ", columns[2]);
        assertEquals(" team", columns[3]);

        ______TS("Typical case: line with tab as separators");
        line = "team\temail\tname\t";
        columns = splitLineIntoColumns(line);

        assertEquals(4, columns.length);
        assertEquals("team", columns[0]);
        assertEquals("email", columns[1]);
        assertEquals("name", columns[2]);
        assertEquals("", columns[3]);
    }

    private int locateColumnIndexes(String line) throws Exception {
        return (int) invokeMethod(StudentAttributesFactory.class, "locateColumnIndexes",
                                  new Class<?>[] { String.class },
                                  new StudentAttributesFactory(), new Object[] { line });
    }

    private String[] splitLineIntoColumns(String line) throws Exception {
        return (String[]) invokeMethod(StudentAttributesFactory.class, "splitLineIntoColumns",
                                       new Class<?>[] { String.class },
                                       new StudentAttributesFactory(), new Object[] { line });
    }

}
