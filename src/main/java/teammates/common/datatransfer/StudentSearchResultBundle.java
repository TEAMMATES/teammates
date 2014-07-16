package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class StudentSearchResultBundle extends SearchResultBundle {

    public Map<String, List<StudentAttributes>> studentsTable = new HashMap<String, List<StudentAttributes>>();
    public Cursor cursor = null;
    private int numberOfResults = 0;
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    
    public StudentSearchResultBundle(){}
    
    public StudentSearchResultBundle fromResults(Results<ScoredDocument> results, String googleId){
        if(results == null) 
            return this;
        
        cursor = results.getCursor();
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        List<String> giverEmailList = new ArrayList<String>();
        for(InstructorAttributes ins:instructorRoles){
            giverEmailList.add(ins.email);
        }
        
        for(ScoredDocument doc:results){
            StudentAttributes student = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.STUDENT_ATTRIBUTE).getText(), 
                    StudentAttributes.class);
            if(studentsLogic.getStudentForRegistrationKey(student.key) == null){
                studentsLogic.deleteDocument(student);
                continue;
            }
            
            List<StudentAttributes> studentsList = studentsTable.get(student.course);
            if(studentsList == null){
                studentsList = new ArrayList<StudentAttributes>();
                studentsTable.put(student.course, studentsList);
            }
            studentsList.add(student);
            numberOfResults++;
        }
        return this;
    }

    @Override
    public int getResultSize() {
        return numberOfResults;
    }
}
