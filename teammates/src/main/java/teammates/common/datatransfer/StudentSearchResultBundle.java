package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The search result bundle for {@link StudentAttributes}.
 */
public class StudentSearchResultBundle extends SearchResultBundle {

    public List<StudentAttributes> studentList = new ArrayList<>();
    public Map<String, InstructorAttributes> courseIdInstructorMap = new HashMap<>();

}
