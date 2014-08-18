package teammates.ui.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;


public class AdminSearchPageAction extends Action {
    
        
        
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
           
        String searchKey = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        String searchButtonHit = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT);       
        
        AdminSearchPageData data = new AdminSearchPageData(account);
        
        if(searchKey == null || searchKey.trim().isEmpty()){
            
            if(searchButtonHit != null){             
                statusToUser.add("Search key cannot be empty");
                isError = true;
            }
            return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }
        
        data.searchKey = searchKey;
       
        data.studentResultBundle  = logic.searchStudentsInWholeSystem(searchKey, "");
        
        data = putFeedbackSessionLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putHomePageLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentDetailsPageLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putInsitituteIntoMap(data.studentResultBundle.studentList, data);
           
        int numOfResults = data.studentResultBundle.getResultSize();
        if(numOfResults > 0){
            statusToUser.add("Total results found: " + numOfResults);
            isError = false;
        } else {
            statusToUser.add("No result found, please try again");
            isError = true;
        }
              
        
        return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
    }
    
    private AdminSearchPageData putInsitituteIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        Logic logic = new Logic();
        for(StudentAttributes student : students){
            
            InstructorAttributes instructor = logic.getInstructorsForCourse(student.course).get(0); 
            if(instructor.googleId == null){
                continue;
            }
            
            AccountAttributes account = logic.getAccount(instructor.googleId);           
            if(account == null){
                continue;
            }
            
            String institute = account.institute.trim().isEmpty() ? "None" : account.institute;
            
            data.studentInstituteMap.put(student.getIdentificationString(), institute);
        }
        
        return data;
    }
    
    
    private AdminSearchPageData putHomePageLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
        for(StudentAttributes student : students){
            
            if(student.googleId == null){
                continue;
            }
            
            String curLink = Url.addParamToUrl(Const.ActionURIs.STUDENT_HOME_PAGE,
                                                        Const.ParamsNames.USER_ID, 
                                                        student.googleId);
            
            data.studentIdToHomePageLinkMap.put(student.googleId, curLink);
        }
        
        return data;
    }
    
    private AdminSearchPageData putStudentDetailsPageLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
        for(StudentAttributes student : students){
            
            if(student.course == null ||student.email == null){
                continue;
            }
            
            String curLink = Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE,
                                                        Const.ParamsNames.COURSE_ID, 
                                                        student.course);
            curLink = Url.addParamToUrl(curLink, Const.ParamsNames.STUDENT_EMAIL, student.email);
            String availableGoogleId = findAvailableInstructorGoogleIdForCourse(student.course);
            
            if (!availableGoogleId.isEmpty()) {
                
                curLink = Url.addParamToUrl(curLink, Const.ParamsNames.USER_ID, availableGoogleId);
                data.studentDetailsPageLinkMap.put(student.getIdentificationString(), curLink);
            }
        }
        
        return data;
    }
    
    
    /**
     * This method loops through all instructors for the given course until a registered Instructor is found.
     * It returns the google id of the found instructor.
     * @param CourseId
     * @return empty string if no available instructor google id is found
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId){
        
        String googleId = "";
        
        if(logic.getInstructorsForCourse(courseId) == null){
            return googleId;
        }
        
        for(InstructorAttributes instructor : logic.getInstructorsForCourse(courseId)){
          
            if(instructor.googleId != null){
                googleId = instructor.googleId;
                break;
            }            
        }
        
        return googleId; 
    }

    private AdminSearchPageData putFeedbackSessionLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
        Logic logic = new Logic();
        
        for(StudentAttributes student : students){    
            List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(student.course); 
            
            for(FeedbackSessionAttributes fsa : feedbackSessions){               
                data = extractDataFromFeedbackSeesion(fsa, data, student);              
            }       
        }       
 
        return data;
           
    }
    
    private AdminSearchPageData extractDataFromFeedbackSeesion(FeedbackSessionAttributes fsa, 
                                                               AdminSearchPageData data, 
                                                               StudentAttributes student){
         
         String submitUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                .withCourseId(student.course)
                                .withSessionName(fsa.feedbackSessionName)
                                .withRegistrationKey(StringHelper.encrypt(student.key))
                                .withStudentEmail(student.email)
                                .toString();
         
         if(fsa.isOpened() == false){
             
             if (data.studentUnOpenedFeedbackSessionLinksMap.get(student.getIdentificationString()) == null){
                 List<String> submitUrlList = new ArrayList<String>();
                 submitUrlList.add(submitUrl);   
                 data.studentUnOpenedFeedbackSessionLinksMap.put(student.getIdentificationString(), submitUrlList);
             } else {
                 data.studentUnOpenedFeedbackSessionLinksMap.get(student.getIdentificationString()).add(submitUrl);
             }
             
             data.feedbackSeesionLinkToNameMap.put(submitUrl, fsa.feedbackSessionName + " (Currently Not Open)");   
             
         } else {                 
             if (data.studentOpenFeedbackSessionLinksMap.get(student.getIdentificationString()) == null){
                  List<String> submitUrlList = new ArrayList<String>();
                  submitUrlList.add(submitUrl);   
                  data.studentOpenFeedbackSessionLinksMap.put(student.getIdentificationString(), submitUrlList);
             } else {
                  data.studentOpenFeedbackSessionLinksMap.get(student.getIdentificationString()).add(submitUrl);
             }
             
             data.feedbackSeesionLinkToNameMap.put(submitUrl, fsa.feedbackSessionName);  
         }
         
           
         return data;
    }
    
}
