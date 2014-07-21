package teammates.storage.search;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.gson.Gson;

public class StudentSearchDocument extends SearchDocument {

    private StudentAttributes student;
    private CourseAttributes course;
    
    public StudentSearchDocument(StudentAttributes student){
        this.student = student;
    }
    
    @Override
    protected void prepareData() {
        if(student == null) 
            return;
        
        course = logic.getCourse(student.course);
    }

    @Override
    public Document toDocument() {
        
        String delim = ",";
        
        //produce searchableText for this student document:
        //it contains
        //courseId, courseName, studentEmail, studentName 
        //studentTeam and studentSection
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(student.course).append(delim);
        searchableTextBuilder.append(course != null ? course.name : "").append(delim);
        searchableTextBuilder.append(student.email).append(delim);
        searchableTextBuilder.append(student.name).append(delim);
        searchableTextBuilder.append(student.team).append(delim);
        searchableTextBuilder.append(student.section);
        
        Document doc = Document.newBuilder()
            //this is used to filter documents visible to certain instructor
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID).setText(student.course))
            //searchableText and createdDate are used to match the query string
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT).setText(searchableTextBuilder.toString()))
            //attribute field is used to convert a doc back to attribute
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.STUDENT_ATTRIBUTE).setText(new Gson().toJson(student)))
            .setId(student.key)
            .build();
        
        return doc;
    }
}
