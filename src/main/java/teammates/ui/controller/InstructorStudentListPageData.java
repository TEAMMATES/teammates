package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;

public class InstructorStudentListPageData extends PageData {

    public InstructorStudentListPageData(AccountAttributes account) {
        super(account);
    }

    public HashMap<String, InstructorAttributes> instructors;
    public HashMap<String, String> numStudents;
    public List<CourseAttributes> courses;
    public String searchKey;
    public Boolean displayArchive;

}
