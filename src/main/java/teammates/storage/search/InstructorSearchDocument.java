package teammates.storage.search;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

public class InstructorSearchDocument extends SearchDocument {
    
    private InstructorAttributes instructor;
    private CourseAttributes course;
    
    public InstructorSearchDocument(InstructorAttributes instructor) {
        this.instructor = instructor;
    }
    
    @Override
    protected void prepareData() {
        if (instructor == null) {
            return;
        }
        
        course = coursesDb.getCourse(instructor.courseId);
    }

    @Override
    protected Document toDocument() {
        
        String delim = ",";
        
        //produce searchableText for this instructor document:
        //it contains
        //courseId, courseName, instructorName, instructorEmail,
        // instructorGoogleId, instructorRole
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(instructor.courseId).append(delim)
                             .append(course == null ? "" : course.getName()).append(delim)
                             .append(instructor.name).append(delim)
                             .append(instructor.email).append(delim)
                             .append(instructor.googleId == null ? "" : instructor.googleId).append(delim)
                             .append(instructor.role).append(delim)
                             .append(instructor.displayedName).append(delim);
        
        Document doc = Document.newBuilder()
                       //searchableText is used to match the query string
                       .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                                   .setText(searchableTextBuilder.toString()))
                       //attribute field is used to convert a doc back to attribute
                       .addField(Field.newBuilder().setName(Const.SearchDocumentField.INSTRUCTOR_ATTRIBUTE)
                                                   .setText(JsonUtils.toJson(instructor)))
                       .setId(StringHelper.encrypt(instructor.key))
                       .build();
                
        return doc;
    }

}
