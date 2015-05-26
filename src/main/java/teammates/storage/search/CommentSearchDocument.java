package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Document.Builder;
import com.google.gson.Gson;

/**
 * The SearchDocument object that defines how we store {@link Document} for student comments
 */
public class CommentSearchDocument extends BaseCommentSearchDocument {
    
    private CommentAttributes comment;
    private StringBuilder commentRecipientNameBuilder = new StringBuilder("");
    
    public CommentSearchDocument(CommentAttributes comment){
        this.comment = comment;
    }
    
    @Override
    protected void prepareData() {
        if(comment == null) return;

        super.prepareFieldsForDataPreparation(comment);
        
        String delim = "";
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
        StringBuilder recipientsBuilder = super.populateRelatedStudentsInformation(50);
        String delim = ",";
        
        //produce searchableText for this comment document:
        //it contains
        //courseId, courseName, giverEmail, giverName, 
        //recipientEmails/Teams/Sections, and commentText
        StringBuilder searchableTextBuilder = super
                .prepareCommonSearchableTextBuilder(comment, delim);
        searchableTextBuilder.append(recipientsBuilder.toString());
        
        Builder docBuilder = super.prepareCommonDocumentPart(comment, comment.giverEmail,
                                                             searchableTextBuilder,
                                                             comment.isVisibleTo(CommentParticipantType.INSTRUCTOR)
                                                                    .toString(),
                                                             Const.SearchDocumentField.COMMENT_ATTRIBUTE,
                                                             Const.SearchDocumentField.COMMENT_GIVER_NAME);
        Document doc = docBuilder
            //attribute field is used to convert a doc back to attribute
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME).setText(
                    new Gson().toJson(commentRecipientNameBuilder.toString())))
            .build();
        return doc;
    }

}
