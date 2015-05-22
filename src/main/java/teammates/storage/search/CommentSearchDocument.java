package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.gson.Gson;

/**
 * The SearchDocument object that defines how we store {@link Document} for student comments
 */
public class CommentSearchDocument extends SearchDocument {
    
    private CommentAttributes comment;
    private CourseAttributes course;
    private InstructorAttributes giverAsInstructor;
    private List<StudentAttributes> relatedStudents;
    private StringBuilder commentRecipientNameBuilder = new StringBuilder("");
    
    public CommentSearchDocument(CommentAttributes comment){
        this.comment = comment;
    }
    
    @Override
    protected void prepareData() {
        if(comment == null) return;
        
        course = logic.getCourse(comment.courseId);
        
        giverAsInstructor = logic.
                getInstructorForEmail(comment.courseId, comment.giverEmail);
        
        String delim = "";
        relatedStudents = new ArrayList<StudentAttributes>();
        switch (comment.recipientType) {
        case PERSON:
            for(String email:comment.recipients){
                StudentAttributes student = logic.getStudentForEmail(comment.courseId, email);
                if(student != null){
                    relatedStudents.add(student);
                    commentRecipientNameBuilder.append(delim).append(student.name).append(" (" + student.team + ", " + student.email + ")");
                    delim = ", ";
                } else {
                    commentRecipientNameBuilder.append(delim).append(email);
                    delim = ", ";
                }
            }
            break;
        case TEAM:
            for(String team:comment.recipients){
                List<StudentAttributes> students = logic.getStudentsForTeam(team, comment.courseId);
                if(students != null){
                    relatedStudents.addAll(students);
                }
                commentRecipientNameBuilder.append(delim).append(team);
                delim = ", ";
            }
            break;
        case SECTION:
            for(String section:comment.recipients){
                List<StudentAttributes> students = logic.getStudentsForSection(section, comment.courseId);
                if(students != null){
                    relatedStudents.addAll(students);
                }
                commentRecipientNameBuilder.append(delim).append(section);
                delim = ", ";
            }
            break;
        case COURSE:
            for(String course:comment.recipients){
                commentRecipientNameBuilder.append(delim).append("All students in Course " + course);
                delim = ", ";
            }
            break;
        default:
            break;
        }
    }

    @Override
    public Document toDocument() {
        
        //populate recipients information
        StringBuilder recipientsBuilder = new StringBuilder("");
        String delim = ",";
        int counter = 0;
        for(StudentAttributes student:relatedStudents){
            if(counter == 50) break;//in case of exceeding size limit for document
            recipientsBuilder.append(student.email).append(delim)
                .append(student.name).append(delim)
                .append(student.team).append(delim)
                .append(student.section).append(delim);
            counter++;
        }
        
        //produce searchableText for this comment document:
        //it contains
        //courseId, courseName, giverEmail, giverName, 
        //recipientEmails/Teams/Sections, and commentText
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(comment.courseId).append(delim);
        searchableTextBuilder.append(course != null? course.name: "").append(delim);
        searchableTextBuilder.append(comment.giverEmail).append(delim);
        searchableTextBuilder.append(giverAsInstructor != null? giverAsInstructor.name: "").append(delim);
        searchableTextBuilder.append(recipientsBuilder.toString()).append(delim);
        searchableTextBuilder.append(comment.commentText.getValue());
        
        Document doc = Document.newBuilder()
            //this is used to filter documents visible to certain instructor
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID).setText(comment.courseId))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.GIVER_EMAIL).setText(comment.giverEmail))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR).setText(
                    comment.isVisibleTo(CommentParticipantType.INSTRUCTOR).toString()))
            //searchableText and createdDate are used to match the query string
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT).setText(searchableTextBuilder.toString()))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.CREATED_DATE).setDate(comment.createdAt))
            //attribute field is used to convert a doc back to attribute
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_ATTRIBUTE).setText(new Gson().toJson(comment)))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_GIVER_NAME).setText(
                    new Gson().toJson(giverAsInstructor != null? giverAsInstructor.displayedName + " " + giverAsInstructor.name: comment.giverEmail)))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME).setText(
                    new Gson().toJson(commentRecipientNameBuilder.toString())))
            .setId(comment.getCommentId().toString())
            .build();
        return doc;
    }

}
