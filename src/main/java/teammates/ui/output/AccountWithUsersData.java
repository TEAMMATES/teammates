package teammates.ui.output;

import java.util.List;

import teammates.storage.entity.Account;

public class AccountWithUsersData extends AccountData {
    private final List<InstructorData> instructors;
    private final List<StudentData> students;

    public AccountWithUsersData(Account account, List<InstructorData> instructors, List<StudentData> students) {
        super(account);
        this.instructors = instructors;
        this.students = students;
    }

    public List<InstructorData> getInstructors() {
        return instructors;
    }

    public List<StudentData> getStudents() {
        return students;
    }
}
