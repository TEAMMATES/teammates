package teammates.ui.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;


public class AdminSearchPageAction extends Action {
    
        
    
    private HashMap<String, String> tempCourseIdToInstituteMap = new HashMap<String, String>();
    private HashMap<String, String> tempCourseIdToInstructorGoogleIdMap = new HashMap<String, String>();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
           
        String searchKey = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        String searchButtonHit = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT);    
        
        AdminSearchPageData data = new AdminSearchPageData(account);
        
        if(searchKey == null || searchKey.trim().isEmpty()){
            
            if(searchButtonHit != null){             
                statusToUser.add(new StatusMessage("Search key cannot be empty", StatusMessageColor.WARNING));
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
        
        data = putCourseNameIntoMap(data.studentResultBundle.studentList, 
                                    data.instructorResultBundle.instructorList,
                                    data);
        
        
        int numOfResults = data.studentResultBundle.getResultSize() 
                           + data.instructorResultBundle.getResultSize();
        
        if(numOfResults > 0){
            statusToUser.add(new StatusMessage("Total results found: " + numOfResults, StatusMessageColor.INFO));
            statusToAdmin = "Search Key: " + searchKey + "<br>" + "Total results found: " + numOfResults;
            isError = false;
        } else {
            statusToUser.add(new StatusMessage("No result found, please try again", StatusMessageColor.WARNING));
            statusToAdmin = "Search Key: " + searchKey + "<br>" + "No result found";
            isError = true;
        }
              
        data.init();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
    }
    
    private AdminSearchPageData putCourseNameIntoMap(List<StudentAttributes> students, List<InstructorAttributes> instructors, AdminSearchPageData data){
        
        Logic logic = new Logic();
        
        for(StudentAttributes student : students){
            if(student.course != null && !data.courseIdToCourseNameMap.containsKey(student.course)){
                CourseAttributes course = logic.getCourse(student.course);
                if(course != null){
                    data.courseIdToCourseNameMap.put(student.course, course.name);
                }
            }
        }
        
        for(InstructorAttributes instructor : instructors){
            if(instructor.courseId != null && !data.courseIdToCourseNameMap.containsKey(instructor.courseId)){
                CourseAttributes course = logic.getCourse(instructor.courseId);
                if(course != null){
                    data.courseIdToCourseNameMap.put(instructor.courseId, course.name);
                }
            }
        }
        
        return data;
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
