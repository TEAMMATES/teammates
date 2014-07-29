package teammates.ui.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
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
    
    

    private AdminSearchPageData putFeedbackSessionLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
        Logic logic = new Logic();
        
        for(StudentAttributes student : students){
        
            List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(student.course); 
            
            for(FeedbackSessionAttributes fsa : feedbackSessions){
                
                if(!fsa.isOpened()){
                   continue; 
                }
                
                String submitUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                                       .withCourseId(student.course)
                                       .withSessionName(fsa.feedbackSessionName)
                                       .withRegistrationKey(StringHelper.encrypt(student.key))
                                       .withStudentEmail(student.email)
                                       .toString();
                
                if (data.studentfeedbackSessionLinksMap.get(student.getIdentificationString()) == null){
                     List<String> submitUtlList = new ArrayList<String>();
                     submitUtlList.add(submitUrl);   
                     data.studentfeedbackSessionLinksMap.put(student.getIdentificationString(), submitUtlList);
                } else {
                    data.studentfeedbackSessionLinksMap.get(student.getIdentificationString()).add(submitUrl);
                }       
                
                data.feedbackSeesionLinkToNameMap.put(submitUrl, fsa.feedbackSessionName);    
                
            }
        
        }       
 
        return data;
           
    }
    
    
    
    
}
