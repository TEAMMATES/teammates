package teammates.common.datatransfer;

import java.util.List;
import java.util.Set;

import teammates.common.util.Assumption;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class CommentSearchBundle {
    private CourseAttributes course;
    private InstructorAttributes giverAsInstructor;
    private List<StudentAttributes> relatedStudents;
    private Set<String> whoCanSee;
    
    public CommentAttributes comment;
    
    public CommentSearchBundle(){}
    
    public CommentSearchBundle(CourseAttributes course, 
            InstructorAttributes giverAsInstructor, 
            List<StudentAttributes> relatedStudents,
            CommentAttributes comment,
            Set<String> whoCanSee){
        this.course = course;
        this.giverAsInstructor = giverAsInstructor;
        this.relatedStudents = relatedStudents;
        this.comment = comment;
        this.whoCanSee = whoCanSee;
    }
    
    public Document toDocument(){
        Assumption.assertNotNull(course);
        Assumption.assertNotNull(giverAsInstructor);
        Assumption.assertNotNull(relatedStudents);
        Assumption.assertNotNull(comment);
        Assumption.assertNotNull(whoCanSee);
        
        //populate recipients information
        StringBuilder recipientsBuilder = new StringBuilder("");
        String delim = ",";
        for(StudentAttributes student:relatedStudents){
            recipientsBuilder.append(student.email).append(delim)
                .append(student.name).append(delim)
                .append(student.team).append(delim)
                .append(student.section).append(delim);
        }
        
        //produce searchableText for this comment document:
        //it contains
        //courseId, courseName, giverEmail, giverName, 
        //recipientEmails/Teams/Sections, and commentText
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(this.comment.courseId).append(delim);
        searchableTextBuilder.append(course != null? course.name: "").append(delim);
        searchableTextBuilder.append(this.comment.giverEmail).append(delim);
        searchableTextBuilder.append(giverAsInstructor != null? giverAsInstructor.name: "").append(delim);
        searchableTextBuilder.append(recipientsBuilder.toString()).append(delim);
        searchableTextBuilder.append(this.comment.commentText.getValue());
        
        Document doc = Document.newBuilder()
            //whoCanSee is used to filter documents visible to certain instructor
            .addField(Field.newBuilder().setName("whoCanSee").setText(this.whoCanSee.toString()))
            //searchableText and createdDate are used to match the query string
            .addField(Field.newBuilder().setName("searchableText").setText(searchableTextBuilder.toString()))
            .addField(Field.newBuilder().setName("createdDate").setDate(this.comment.createdAt))
            //attribute field is used to convert a doc back to attribute
            .addField(Field.newBuilder().setName("attribute").setText(new Gson().toJson(this.comment)))
            .setId(this.comment.getCommentId().toString())
            .build();
        return doc;
    }
    
    public CommentSearchBundle fromDocument(ScoredDocument doc){
        CommentAttributes comment = new Gson().fromJson(doc.getOnlyField("attribute").getText(), CommentAttributes.class);
        comment.sendingState = CommentSendingState.SENT;
        this.comment = comment;
        return this;
    }
}
