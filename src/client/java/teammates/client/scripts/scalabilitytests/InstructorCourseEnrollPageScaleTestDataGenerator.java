package teammates.client.scripts.scalabilitytests;

import org.kohsuke.randname.RandomNameGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

/**
 * Generates test data for InstructorCourseEnrollPageScaleTest.
 */
public final class InstructorCourseEnrollPageScaleTestDataGenerator {

    private static final String FILENAME = "InstructorCourseEnrollPageScaleTestData";

    private static final String HEADER = "Section | Team | Name | Email | Comments\n";

    private static final int[] studentGroups = {10, 20, 50, 75, 100, 150};

    private InstructorCourseEnrollPageScaleTestDataGenerator() {
    }

    private static String generateStudents(int num) {
        RandomNameGenerator generator = new RandomNameGenerator(num);
        HashSet<String> studentNames = new HashSet<>();
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
        for (int studentGroup : studentGroups) {
            File file = new File("src/client/java/teammates/client/scripts/scalabilitytests/" + FILENAME + studentGroup);
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(generateStudents(studentGroup));
                out.close();
            }
        }
    }
}
