package teammates.client.scripts;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.Logic;

public class ModifyInstituteOfStudentsInCourse extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        ModifyInstituteOfStudentsInCourse modifyInstituteOfStudentsInCourse = new ModifyInstituteOfStudentsInCourse();
        modifyInstituteOfStudentsInCourse.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Logic logic = new Logic();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter course to edit: ");
        String courseId = scanner.nextLine();
        System.out.println("Enter new institute name: ");
        String institute = scanner.nextLine();

        try {
            List<StudentAttributes> students = logic.getStudentsForCourse(courseId);

            for (StudentAttributes student : students) {

                //Account might be null if student was enrolled but not joined yet
                if (student.googleId == null || student.googleId.isEmpty()) {
                    continue;
                }

                AccountAttributes account = logic.getAccount(student.googleId);

                System.out.println("changed for " + account.email + " from " + account.institute + " to " + institute);
                account.institute = institute;
                logic.updateAccount(account);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        scanner.close();
    }

}
