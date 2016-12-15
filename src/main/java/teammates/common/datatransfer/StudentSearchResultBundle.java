package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

public class StudentSearchResultBundle extends SearchResultBundle {

    public List<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
    public Map<String, InstructorAttributes> courseIdInstructorMap = new HashMap<String, InstructorAttributes>();
    public Cursor cursor;
    private int numberOfResults;
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    
    /**
     * Produce a StudentSearchResultBundle from the Results<ScoredDocument> collection.
     * The list of InstructorAttributes is used to filter out the search result.
     */
    public StudentSearchResultBundle fromResults(Results<ScoredDocument> results,
                                                 List<InstructorAttributes> instructors) {
        if (results == null) {
            return this;
        }
        
        cursor = results.getCursor();
        List<String> giverEmailList = new ArrayList<String>();
        for (InstructorAttributes ins : instructors) {
            giverEmailList.add(ins.email);
            courseIdInstructorMap.put(ins.courseId, ins);
        }
        
        List<ScoredDocument> filteredResults = filterOutCourseId(results, instructors);
        for (ScoredDocument doc : filteredResults) {
            StudentAttributes student = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.STUDENT_ATTRIBUTE).getText(),
                    StudentAttributes.class);
            if (student.key == null) {
                studentsLogic.deleteDocument(student);
                continue;
            }
            if (studentsLogic.getStudentForRegistrationKey(StringHelper.encrypt(student.key)) == null) {
                studentsLogic.deleteDocument(student);
                continue;
            }
            
            studentList.add(student);
            numberOfResults++;
        }
        
        sortStudentResultList();
        
        return this;
    }

    /**
     * This method should be used by admin only since the previous searching does not restrict the
     * visibility according to the logged-in user's google ID. Therefore,This fromResults method
     * does not require a googleID as a parameter. Returned results bundle will contain information
     * related to matched students only.
     * @param results
     * @return studentResultBundle containing information related to matched students only.
     */
    public StudentSearchResultBundle getStudentsfromResults(Results<ScoredDocument> results) {
        if (results == null) {
            return this;
        }
        
        cursor = results.getCursor();
        
        for (ScoredDocument doc : results) {
            StudentAttributes student =
                    JsonUtils.fromJson(doc.getOnlyField(Const.SearchDocumentField.STUDENT_ATTRIBUTE).getText(),
                                                         StudentAttributes.class);
            
            if (studentsLogic.getStudentForRegistrationKey(StringHelper.encrypt(student.key)) == null) {
                studentsLogic.deleteDocument(student);
                continue;
            }
            
            studentList.add(student);
            numberOfResults++;
        }
        
        sortStudentResultList();
        
        return this;
    }

    private void sortStudentResultList() {
        
        Collections.sort(studentList, new Comparator<StudentAttributes>() {
            @Override
            public int compare(StudentAttributes s1, StudentAttributes s2) {
                int compareResult = s1.course.compareTo(s2.course);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = s1.section.compareTo(s2.section);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = s1.team.compareTo(s2.team);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = s1.name.compareTo(s2.name);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                return s1.email.compareTo(s2.email);
            }
        });
    }

    @Override
    public int getResultSize() {
        return numberOfResults;
    }
}
