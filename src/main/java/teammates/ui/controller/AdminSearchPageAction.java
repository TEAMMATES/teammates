package teammates.ui.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;


public class AdminSearchPageAction extends Action {
    
        
    
    private HashMap<String, String> tempCourseIdToInstituteMap = new HashMap<String, String>();
    private HashMap<String, String> tempCourseIdToInstructorGoogleIdMap = new HashMap<String, String>();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        Logic logic = new Logic();
        
        new GateKeeper().verifyAdminPrivileges(account);
           
        String searchKey = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        String searchButtonHit = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT);    
        boolean isResetGoogleId = getRequestParamAsBoolean(Const.ParamsNames.ADMIN_SEARCH_AND_RESET_GOOGLE_ID);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String studentCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        
        AdminSearchPageData data = new AdminSearchPageData(account);
        
        if(isResetGoogleId && studentEmail != null && studentCourseId != null){
            try {
                logic.resetStudentGoogleId(studentEmail, studentCourseId);
                
            } catch (InvalidParametersException e) {
                statusToUser.add("Error when reseting google id");
                statusToAdmin = "Trying to reset google id of student<br>" + 
                                "Email: " + studentEmail + "<br>" +
                                "CourseId: " + studentCourseId + "<br>" + 
                                "Failed with error<br>" + 
                                e.getMessage();
            }
           
            
            StudentAttributes updatedStudent = logic.getStudentForEmail(studentCourseId, studentEmail);
     
            if(updatedStudent.googleId == null || updatedStudent.googleId.isEmpty()){
                
                statusToUser.add(Const.StatusMessages.STUDENT_GOOGLEID_RESET);
                statusToUser.add("Email : " + studentEmail);
                statusToUser.add("CourseId : " + studentCourseId);
                
                statusToAdmin = "Successfully reset google id of student<br>" + 
                                "Email: " + studentEmail + "<br>" +
                                "CourseId: " + studentCourseId;
                
                data.statusForAjax = Const.StatusMessages.STUDENT_GOOGLEID_RESET + "<br>" + 
                                     "Email : " + studentEmail + "<br>" + 
                                     "CourseId : " + studentCourseId;
                
                data.isGoogleIdReset = true;
            } else {
                data.isGoogleIdReset = false;
                statusToUser.add("Error when reseting google id");
                statusToAdmin = "Failed to reset google id of student<br>" + 
                                "Email: " + studentEmail + "<br>" +
                                "CourseId: " + studentCourseId + "<br>";
                data.statusForAjax = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>" + 
                                     "Email : " + studentEmail + "<br>" + 
                                     "CourseId : " + studentCourseId;
            } 
           
            return createAjaxResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }
        
        if(searchKey == null || searchKey.trim().isEmpty()){
            
            if(searchButtonHit != null){             
                statusToUser.add("Search key cannot be empty");
                statusToAdmin = "Invalid Search: Search key cannot be empty";
                isError = true;
            } else {
                statusToAdmin = "AdminSearchPaga Page Load";
            }
            return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }
        
        data.searchKey = searchKey;
       
        data.studentResultBundle  = logic.searchStudentsInWholeSystem(searchKey, "");
        
        data = putFeedbackSessionLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentHomePageLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentRecordsPageLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentInsitituteIntoMap(data.studentResultBundle.studentList, data);
                   
        data.instructorResultBundle = logic.searchInstructorsInWholeSystem(searchKey, "");
        data = putInstructorInsitituteIntoMap(data.instructorResultBundle.instructorList, data);
        data = putInstructorHomePageLinkIntoMap(data.instructorResultBundle.instructorList, data);
        data = putInstructorCourseJoinLinkIntoMap(data.instructorResultBundle.instructorList, data);
        
        
        int numOfResults = data.studentResultBundle.getResultSize() 
                           + data.instructorResultBundle.getResultSize();
        
        if(numOfResults > 0){
            statusToUser.add("Total results found: " + numOfResults);
            statusToAdmin = "Search Key: " + searchKey + "<br>" + "Total results found: " + numOfResults;
            isError = false;
        } else {
            statusToUser.add("No result found, please try again");
            statusToAdmin = "Search Key: " + searchKey + "<br>" + "No result found";
            isError = true;
        }
              
        
        return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
    }
    
    private AdminSearchPageData putInstructorCourseJoinLinkIntoMap(List<InstructorAttributes> instructors, AdminSearchPageData data){

        for(InstructorAttributes instructor : instructors){
            
            String googleIdOfAlreadyRegisteredInstructor = findAvailableInstructorGoogleIdForCourse(instructor.courseId);
            
            if(!googleIdOfAlreadyRegisteredInstructor.isEmpty()){
                String joinLinkWithoutInsititute = Url.addParamToUrl(Config.APP_URL + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN, 
                                                                     Const.ParamsNames.REGKEY, 
                                                                     StringHelper.encrypt(instructor.key));
                data.instructorCourseJoinLinkMap.put(instructor.getIdentificationString(),
                                                     joinLinkWithoutInsititute);
            }
            
        }
        
        return data;
    }
    
    private AdminSearchPageData putInstructorInsitituteIntoMap(List<InstructorAttributes> instructors, AdminSearchPageData data){
        Logic logic = new Logic();
        for(InstructorAttributes instructor : instructors){
            
            if(tempCourseIdToInstituteMap.get(instructor.courseId) != null){
                data.instructorInstituteMap.put(instructor.getIdentificationString(), tempCourseIdToInstituteMap.get(instructor.courseId));
                continue;
            }
            
            String googleId = findAvailableInstructorGoogleIdForCourse(instructor.courseId);
            
            AccountAttributes account = logic.getAccount(googleId);           
            if(account == null){
                continue;
            }
            
            String institute = account.institute.trim().isEmpty() ? "None" : account.institute;
            
            tempCourseIdToInstituteMap.put(instructor.courseId, institute);
            data.instructorInstituteMap.put(instructor.getIdentificationString(), institute);
        }
        
        return data;
    }
    
    private AdminSearchPageData putInstructorHomePageLinkIntoMap(List<InstructorAttributes> instructors, AdminSearchPageData data){
        
        for(InstructorAttributes instructor : instructors){
            
            if(instructor.googleId == null){
                continue;
            }
            
            String curLink = Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                                                        Const.ParamsNames.USER_ID, 
                                                        instructor.googleId);
            
            data.instructorHomaPageLinkMap.put(instructor.googleId, curLink);
        }
        
        return data;
    }
    
    
    
    private AdminSearchPageData putStudentInsitituteIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
        Logic logic = new Logic();
        
        for(StudentAttributes student : students){
            
            if(tempCourseIdToInstituteMap.get(student.course) != null){
                data.studentInstituteMap.put(student.getIdentificationString(), tempCourseIdToInstituteMap.get(student.course));
                continue;
            }
            
            String instructorForCoursegoogleId = findAvailableInstructorGoogleIdForCourse(student.course);
            
            AccountAttributes account = logic.getAccount(instructorForCoursegoogleId);           
            if(account == null){
                continue;
            }
            
            String institute = account.institute.trim().isEmpty() ? "None" : account.institute;
            
            tempCourseIdToInstituteMap.put(student.course, institute);
            
            data.studentInstituteMap.put(student.getIdentificationString(), institute);
        }
        
        return data;
    }
    
    
    private AdminSearchPageData putStudentHomePageLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
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
    
    private AdminSearchPageData putStudentRecordsPageLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data){
        
        for(StudentAttributes student : students){
            
            if(student.course == null ||student.email == null){
                continue;
            }
            
            String curLink = Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE,
                                                        Const.ParamsNames.COURSE_ID, 
                                                        student.course);
            curLink = Url.addParamToUrl(curLink, Const.ParamsNames.STUDENT_EMAIL, student.email);
            String availableGoogleId = findAvailableInstructorGoogleIdForCourse(student.course);
            
            if (!availableGoogleId.isEmpty()) {        
                curLink = Url.addParamToUrl(curLink, Const.ParamsNames.USER_ID, availableGoogleId);
                data.studentRecordsPageLinkMap.put(student.getIdentificationString(), curLink);
            }
        }
        
        return data;
    }
    
    
    /**
     * This method loops through all instructors for the given course until a verified (Corresponding Account Exists) and registered Instructor is found.
     * It returns the google id of the found instructor.
     * @param CourseId
     * @return empty string if no available instructor google id is found
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId){
        
        if(tempCourseIdToInstructorGoogleIdMap.get(courseId) != null){
            return tempCourseIdToInstructorGoogleIdMap.get(courseId);
        }
        
        String googleId = "";
        
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);
        
        if(instructorList == null || instructorList.isEmpty()){
            return googleId;
        }
        
        for(InstructorAttributes instructor : instructorList){
          
            if(instructor.googleId != null){
               googleId = instructor.googleId;
               if(instructor.isAllowedForPrivilege(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER)){            
                   break;
               } 
            }            
        }
        
        tempCourseIdToInstructorGoogleIdMap.put(courseId, googleId);
        
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
         
         
         String viewResultUrl = new Url(Config.APP_URL + Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                    .withCourseId(student.course)
                                    .withSessionName(fsa.feedbackSessionName)
                                    .withRegistrationKey(StringHelper.encrypt(student.key))
                                    .withStudentEmail(student.email)
                                    .toString();
             
         if(fsa.isPublished()){
             if(data.studentPublishedFeedbackSessionLinksMap.get(student.getIdentificationString()) == null){
                 List<String> viewResultUrlList = new ArrayList<String>();
                 viewResultUrlList.add(viewResultUrl);
                 data.studentPublishedFeedbackSessionLinksMap.put(student.getIdentificationString(), viewResultUrlList);
             } else {
                 data.studentPublishedFeedbackSessionLinksMap.get(student.getIdentificationString()).add(viewResultUrl);
             }
             
             data.feedbackSeesionLinkToNameMap.put(viewResultUrl, fsa.feedbackSessionName + " (Published)"); 
         }
        
           
         return data;
    }
    
}
