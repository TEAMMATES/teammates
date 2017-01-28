package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentSearchResultBundle extends SearchResultBundle {

    public List<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
    public Map<String, InstructorAttributes> courseIdInstructorMap = new HashMap<String, InstructorAttributes>();
    
}
