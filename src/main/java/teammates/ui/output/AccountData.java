package teammates.ui.output;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.storage.entity.Account;

/**
 * Output format of account data.
 */
public class AccountData implements ApiOutput {

    private final UUID accountId;
    private final String email;
    private final List<InstructorData> instructors;
    private final List<StudentData> students;

    @JsonCreator
    private AccountData(
            UUID accountId, String email,
            List<InstructorData> instructors, List<StudentData> students) {
        this.accountId = accountId;
        this.email = email;
        this.instructors = instructors;
        this.students = students;
    }

    public AccountData(Account account) {
        this.accountId = account.getId();
        this.email = account.getEmail();
        this.instructors = account.getInstructors().stream().map(InstructorData::new).toList();
        this.students = account.getStudents().stream().map(StudentData::new).toList();
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public List<InstructorData> getInstructors() {
        return instructors;
    }

    public List<StudentData> getStudents() {
        return students;
    }
}
