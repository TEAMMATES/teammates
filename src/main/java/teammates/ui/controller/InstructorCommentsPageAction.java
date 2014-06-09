package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openqa.jetty.html.Comment;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;
import teammates.storage.entity.FeedbackResponseComment;

public class InstructorCommentsPageAction extends Action {

    private InstructorCommentsPageData data;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String isDisplayArchiveString = getRequestParamValue(Const.ParamsNames.DISPLAY_ARCHIVE);
        InstructorAttributes instructor = null;
        Boolean isViewingDraft = false;
        Boolean isDisplayArchive = Boolean.parseBoolean(isDisplayArchiveString);
        
        //verify permissions
        if(courseId != null){//view by Course
            instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
            new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));
        } else {//view by Draft
            courseId = "";
            isViewingDraft = true;
            new GateKeeper().verifyInstructorPrivileges(account);
        }
        
        //handle whether to display archive
        if(isDisplayArchiveString != null){
            //TODO: make this session attr into const.java
            session.setAttribute("comments_page_displayarchive", isDisplayArchive);
        } else {
            Boolean isDisplayBooleanInSession = (Boolean) session.getAttribute("comments_page_displayarchive");
            isDisplayArchive = isDisplayBooleanInSession != null? isDisplayBooleanInSession: false;
        }
        
        //handle pagination result
        List<CourseAttributes> courses = logic.getCoursesForInstructor(account.googleId);
        String courseNameToView = null;
        List<String> courseIdList = new ArrayList<String>(); 
        for(CourseAttributes course : courses){
            if(course.id.equals(courseId)){
                courseNameToView = course.id + " : " + course.name;
            }
            if(isDisplayArchive || !course.isArchived){
                courseIdList.add(course.id);
            }
        }
        
        //get comments data
        List<CommentAttributes> comments;
        if(isViewingDraft){//for comment drafts
            comments = logic.getCommentDrafts(account.email);
        } else {//for normal comments
            comments = logic.getCommentsForGiverAndStatus(courseId, account.email, CommentStatus.FINAL);
        }
        
        //put student details (name/team/section) into map
        //TODO: for this kind of map, need to describe what's its key... especially those string key
        Map<String, StudentAttributes> studentsMap = new HashMap<String, StudentAttributes>();
        if(!isViewingDraft){
            List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
            for(StudentAttributes student : studentList){
                studentsMap.put(student.email, student);
            }
        }
        
        //order data by recipients
        Map<String, List<CommentAttributes>> commentsMap = new TreeMap<String, List<CommentAttributes>>();
        for(CommentAttributes comment : comments){
            for(String recipient : comment.recipients){
                List<CommentAttributes> commentList = commentsMap.get(recipient);
                if(commentList == null){
                    commentList = new ArrayList<CommentAttributes>();
                    commentList.add(comment);
                    commentsMap.put(recipient, commentList);
                } else {
                    commentList.add(comment);
                }
            }
        }
        //sort comment by created date
        for(String recipient : commentsMap.keySet()){
            List<CommentAttributes> commentList = commentsMap.get(recipient);
            //TODO: use override-compareto
            java.util.Collections.sort(commentList);
        }
        
        //get frComment 
        //TODO: consider using getFeedbackSessionResultsForInstructor
        Map<String, List<FeedbackQuestionAttributes>> fsNameTofeedbackQuestionsMap = new HashMap<String, List<FeedbackQuestionAttributes>>();
        Map<String, List<FeedbackResponseAttributes>> questionIdToFeedbackResponsesMap = new HashMap<String, List<FeedbackResponseAttributes>>();
        Map<String, List<FeedbackResponseCommentAttributes>> responseIdToFrCommentsMap = new HashMap<String, List<FeedbackResponseCommentAttributes>>();
        if(!isViewingDraft){
            List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsForCourse(courseId);
            for(FeedbackSessionAttributes fsAttributes : fsList){
                String fsName = fsAttributes.feedbackSessionName;
                //TODO: only get your own frComment
                List<FeedbackResponseCommentAttributes> allFrCommentsInCourse = logic.getFeedbackResponseComment(courseId, fsName);
                List<FeedbackQuestionAttributes> allFeedbackQuestionsOfSession = logic.getFeedbackQuestionsForSession(fsName, courseId);
                @SuppressWarnings("deprecation")
                List<FeedbackResponseAttributes> allFeedbackResponsesOfSession = logic.getFeedbackResponseForSession(fsName, courseId);
                
                Map<String, FeedbackQuestionAttributes> questionIdToFeedbackQuestionMap = new HashMap<String, FeedbackQuestionAttributes>();
                for(FeedbackQuestionAttributes fqAttributes : allFeedbackQuestionsOfSession){
                    questionIdToFeedbackQuestionMap.put(fqAttributes.getId(), fqAttributes);
                }
                Map<String, FeedbackResponseAttributes> responseIdToFeedbackResponseMap = new HashMap<String, FeedbackResponseAttributes>();
                for(FeedbackResponseAttributes frAttributes : allFeedbackResponsesOfSession){
                    responseIdToFeedbackResponseMap.put(frAttributes.getId(), frAttributes);
                }
                
                HashSet<String> isQuestionAddedHashSet = new HashSet<String>();
                HashSet<String> isResponseAddedHashSet = new HashSet<String>();
                for(FeedbackResponseCommentAttributes frCommentAttributes : allFrCommentsInCourse){
                    if(!frCommentAttributes.giverEmail.equals(account.email)) continue;//TODO: use a better way instead of 'continue'
                    FeedbackQuestionAttributes fq = questionIdToFeedbackQuestionMap.get(frCommentAttributes.feedbackQuestionId);
                    if(fq != null && fq.showResponsesTo != null 
                            && fq.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS)){
                        FeedbackResponseAttributes fr = responseIdToFeedbackResponseMap.get(frCommentAttributes.feedbackResponseId);
                        
                        List<FeedbackResponseCommentAttributes> frcList = responseIdToFrCommentsMap.get(frCommentAttributes.feedbackResponseId);
                        if(frcList == null){
                            frcList = new ArrayList<FeedbackResponseCommentAttributes>();
                            frcList.add(frCommentAttributes);
                            responseIdToFrCommentsMap.put(frCommentAttributes.feedbackResponseId, frcList);
                        } else {
                            frcList.add(frCommentAttributes);
                        }
                        
                        if(!fq.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS)){
                            fr.giverEmail = "Anonymous giver";
                        }
                        if(!fq.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS)){
                            fr.recipientEmail = "Anonymous recipient";
                        }
                        List<FeedbackResponseAttributes> frList = questionIdToFeedbackResponsesMap.get(fr.feedbackQuestionId);
                        if(frList == null){
                            frList = new ArrayList<FeedbackResponseAttributes>();
                            frList.add(fr);
                            isResponseAddedHashSet.add(fr.getId());
                            questionIdToFeedbackResponsesMap.put(fr.feedbackQuestionId, frList);
                        } else {
                            if(!isResponseAddedHashSet.contains(fr.getId())){
                                frList.add(fr);
                                isResponseAddedHashSet.add(fr.getId());
                            }
                        }
                        
                        List<FeedbackQuestionAttributes> fqList = fsNameTofeedbackQuestionsMap.get(fsName);
                        if(fqList == null){
                            fqList = new ArrayList<FeedbackQuestionAttributes>();
                            fqList.add(fq);
                            isQuestionAddedHashSet.add(fq.getId());
                            fsNameTofeedbackQuestionsMap.put(fsName, fqList);
                        } else {
                            if(!isQuestionAddedHashSet.contains(fq.getId())){
                                fqList.add(fq);
                                isQuestionAddedHashSet.add(fq.getId());
                            }
                        }
                    }
                }
                List<FeedbackQuestionAttributes> fqList = fsNameTofeedbackQuestionsMap.get(fsName);
                if(fqList != null){
                    java.util.Collections.sort(fqList);
                    fsNameTofeedbackQuestionsMap.put(fsName, fqList);
                }
            }
        }
        
        data = new InstructorCommentsPageData(account);
        //TODO: use data bundles
        data.isViewingDraft = isViewingDraft;
        data.courseIdToView = courseId;
        data.courseNameToView = courseNameToView;
        data.courseIdList = courseIdList;
        data.comments = commentsMap;
        data.students = studentsMap;
        data.isDisplayArchive = isDisplayArchive;
        data.fsNameTofeedbackQuestionsMap = fsNameTofeedbackQuestionsMap;
        data.questionIdToFeedbackResponsesMap = questionIdToFeedbackResponsesMap;
        data.responseIdToFrCommentsMap = responseIdToFrCommentsMap;
        
        statusToAdmin = "instructorComments Page Load<br>" + 
                "Viewing <span class=\"bold\">" + account.googleId + "'s</span> comment records " +
                "for Course <span class=\"bold\">[" + courseId + "]</span>";
            
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COMMENTS, data);
    }
}
