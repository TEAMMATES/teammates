package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.Comment;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class CommentAttributes extends EntityAttributes 
    implements Comparable<CommentAttributes>{

    private Long commentId = null;
    public String courseId;
    public String giverEmail;
    public CommentRecipientType recipientType = CommentRecipientType.PERSON;
    public Set<String> recipients;
    public CommentStatus status = CommentStatus.FINAL;
    public CommentSendingState sendingState = CommentSendingState.SENT;
    public List<CommentRecipientType> showCommentTo;
    public List<CommentRecipientType> showGiverNameTo;
    public List<CommentRecipientType> showRecipientNameTo;
    public Text commentText;
    public Date createdAt;

    public CommentAttributes() {

    }

    public CommentAttributes(String courseId, String giverEmail, CommentRecipientType recipientType,
            Set<String> recipients, Date createdAt, Text commentText) {
        this.courseId = courseId;
        this.giverEmail = giverEmail;
        this.recipientType = recipientType != null ? recipientType : CommentRecipientType.PERSON;
        this.recipients = recipients;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public CommentAttributes(Comment comment) {
        this.commentId = comment.getId();
        this.courseId = comment.getCourseId();
        this.giverEmail = comment.getGiverEmail();
        this.recipientType = comment.getRecipientType();
        this.status = comment.getStatus();
        this.sendingState = comment.getSendingState() != null? comment.getSendingState() : CommentSendingState.SENT;
        this.showCommentTo = comment.getShowCommentTo();
        this.showGiverNameTo = comment.getShowGiverNameTo();
        this.showRecipientNameTo = comment.getShowRecipientNameTo();
        this.recipients = comment.getRecipients();
        this.createdAt = comment.getCreatedAt();
        this.commentText = comment.getCommentText();
    }

    public Long getCommentId() {
        return this.commentId;
    }

    // Use only to match existing and known Comment
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        error = validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfo(FieldType.EMAIL, giverEmail);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        if (recipientType != null) {
            switch (recipientType) {
            case PERSON:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.EMAIL, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            case TEAM:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.TEAM_NAME, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            case SECTION:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.SECTION_NAME, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            case COURSE:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.COURSE_ID, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            default:// cases for NONE or null
                break;
            }
        }

        return errors;
    }

    public Comment toEntity() {
        return new Comment(courseId, giverEmail, recipientType, recipients, status,
                sendingState,
                showCommentTo, 
                showGiverNameTo, 
                showRecipientNameTo, 
                commentText, createdAt);
    }
    
    public Boolean isVisibleTo(CommentRecipientType targetViewer){
        return showCommentTo.contains(targetViewer);
    }

    @Override
    public String toString() {
        return "CommentAttributes [commentId = " + commentId +
                ", courseId = " + courseId +
                ", giverEmail = " + giverEmail +
                ", recipientType = " + recipientType +
                ", recipient = " + recipients +
                ", status = " + status +
                ", showCommentTo = " + showCommentTo +
                ", showGiverNameTo = " + showGiverNameTo +
                ", showRecipientNameTo = " + showRecipientNameTo +
                ", commentText = " + commentText.getValue() +
                ", createdAt = " + createdAt + "]";
    }

    @Override
    public String getIdentificationString() {
        return toString();
    }

    @Override
    public String getEntityTypeAsString() {
        return "Comment";
    }

    @Override
    public void sanitizeForSaving() {
        this.courseId = this.courseId.trim();
        this.commentText = Sanitizer.sanitizeTextField(this.commentText);
        this.courseId = Sanitizer.sanitizeForHtml(courseId);
        this.giverEmail = Sanitizer.sanitizeForHtml(giverEmail);

        HashSet<String> sanitizedRecipients = new HashSet<String>();
        for (String recipientId : recipients) {
            sanitizedRecipients.add(Sanitizer.sanitizeForHtml(recipientId));
        }
        recipients = sanitizedRecipients;

        if (commentText != null) {
            this.commentText = new Text(Sanitizer.sanitizeForHtml(commentText.getValue()));
        }
        
        sanitizeForVisibilityOptions();
    }

    private void sanitizeForVisibilityOptions() {
        switch(recipientType){
        case PERSON:
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.PERSON);
            break;
        case TEAM:
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.PERSON);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.TEAM);
            break;
        case SECTION:
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.PERSON);
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.TEAM);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.SECTION);
            break;
        case COURSE:
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.PERSON);
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.TEAM);
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.SECTION);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.COURSE);
            break;
        default:
            break;
        }
    }
    
    private void removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType typeToRemove){
        removeCommentRecipientTypeIn(showCommentTo, typeToRemove);
        removeCommentRecipientTypeIn(showGiverNameTo, typeToRemove);
        removeCommentRecipientTypeIn(showRecipientNameTo, typeToRemove);
    }
    
    private void removeCommentRecipientTypeIn(List<CommentRecipientType> visibilityOptions, 
            CommentRecipientType typeToRemove){
        if(visibilityOptions == null) return;
        
        Iterator<CommentRecipientType> iter = visibilityOptions.iterator();
        while(iter.hasNext()){
            CommentRecipientType otherType = iter.next();
            if(otherType == typeToRemove){
                iter.remove();
            }
        }
    }

    public static void sortCommentsByCreationTime(List<CommentAttributes> comments) {
        Collections.sort(comments, new Comparator<CommentAttributes>() {
            public int compare(CommentAttributes comment1, CommentAttributes comment2) {
                return comment1.createdAt.compareTo(comment2.createdAt);
            }
        });
    }

    public static void sortCommentsByCreationTimeDescending(List<CommentAttributes> comments) {
        Collections.sort(comments, new Comparator<CommentAttributes>() {
            public int compare(CommentAttributes comment1, CommentAttributes comment2) {
                return comment2.createdAt.compareTo(comment1.createdAt);
            }
        });
    }
    
    @Override
    public int compareTo(CommentAttributes o) {
        if(o == null){
            return 1;
        }
        return o.createdAt.compareTo(createdAt);
    }
    
    public Document toDocument(CourseAttributes course, 
            InstructorAttributes instructor, 
            List<StudentAttributes> relatedStudents){
        
        //populate recipients information
        StringBuilder recipientEmailsBuilder = new StringBuilder("");
        StringBuilder recipientNamesBuilder = new StringBuilder("");
        StringBuilder recipientTeamsBuilder = new StringBuilder("");
        StringBuilder recipientSectionsBuilder = new StringBuilder("");
        String delim = "";
        for(StudentAttributes student:relatedStudents){
            recipientEmailsBuilder.append(delim).append(student.email); 
            recipientNamesBuilder.append(delim).append(student.name);
            recipientTeamsBuilder.append(delim).append(student.team);
            recipientSectionsBuilder.append(delim).append(student.section);
            delim = ",";
        }
        
        //produce searchableText for this document:
        //it contains
        //courseId, courseName, giverEmail, giverName, 
        //recipientEmails/Teams/Sections, and commentText
        String searchableText = this.courseId + ","
                + (course != null? course.name: "") + ","
                + this.giverEmail + ","
                + (instructor != null? instructor.name: "") + ","
                + recipientEmailsBuilder.toString() + ","
                + recipientNamesBuilder.toString() + ","
                + recipientTeamsBuilder.toString() + ","
                + recipientSectionsBuilder.toString() + ","
                + this.commentText.getValue();
        
        Document doc = Document.newBuilder()
            //courseId and isVisibleToInstructor are used to filter documents visible to certain instructor
            .addField(Field.newBuilder().setName("courseId").setText(this.courseId))
            .addField(Field.newBuilder().setName("giverEmail").setText(this.giverEmail))
            .addField(Field.newBuilder().setName("isVisibleToInstructor").setText(this.isVisibleTo(CommentRecipientType.INSTRUCTOR).toString()))
            //searchableText and createdDate are used to filter the query string
            .addField(Field.newBuilder().setName("searchableText").setText(searchableText))
            .addField(Field.newBuilder().setName("createdDate").setDate(this.createdAt))
            //attribute field is used to convert a doc back to attribute
            .addField(Field.newBuilder().setName("attribute").setText(new Gson().toJson(this)))
            .setId(this.commentId.toString())
            .build();
        return doc;
    }
    
    public CommentAttributes fromDocument(ScoredDocument doc){
        CommentAttributes comment = new Gson().fromJson(doc.getOnlyField("attribute").getText(), CommentAttributes.class);
        this.commentId = comment.commentId;
        this.courseId = comment.courseId;
        this.giverEmail = comment.giverEmail;
        this.recipientType = comment.recipientType;
        this.recipients = comment.recipients;
        this.status = comment.status;
        this.sendingState = CommentSendingState.SENT;
        this.showCommentTo = comment.showCommentTo;
        this.showGiverNameTo = comment.showGiverNameTo;
        this.showRecipientNameTo = comment.showRecipientNameTo;
        this.commentText = comment.commentText;
        this.createdAt = comment.createdAt;
        return this;
    }
    
}
