package teammates.client.scripts.scalabilitytests;

import org.kohsuke.randname.RandomNameGenerator;
import teammates.test.driver.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates test data for InstructorCourseEnrollPageScaleTest.
 */
public final class InstructorCourseEnrollPageDataGenerator {

    private static final String FILENAME = "InstructorCourseEnrollPageScaleTestData";

    private static final String HEADER = "Section | Team | Name | Email | Comments\n";

    private InstructorCourseEnrollPageDataGenerator() {
    }

    private static String generateStudents(int num) {
        RandomNameGenerator generator = new RandomNameGenerator(num);
        Set<String> studentNames = new HashSet<>();
        StringBuffer students = new StringBuffer(HEADER);
        for (int i = 0; i < num; i++) {
            String curName = generator.next();
            while (studentNames.contains(curName)) {
                curName = generator.next();
            }

            students.append(
                    String.format("Section %d | Team %d | %s | %s.tmms@gmail.tmt | %n",
                            i / 100 + 1, i / 100 + 1, curName.replace("_", " "), curName.replace("_", ".")));
        }
        return students.toString();
    }

    public static void main(String[] args) throws IOException {
        //Number of students to be added for each data set
        int[] studentGroups = {10, 20, 50, 75, 100, 150};
        String folderPath = "src/client/java/teammates/client/scripts/scalabilitytests/data/";
        new File(folderPath).mkdir();
        for (int studentGroup : studentGroups) {
            FileHelper.saveFile(
                    folderPath + FILENAME + studentGroup,
                    generateStudents(studentGroup));
        }
    }
}
