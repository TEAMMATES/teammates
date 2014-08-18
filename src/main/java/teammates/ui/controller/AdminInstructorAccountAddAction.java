package teammates.ui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator;
import teammates.logic.api.GateKeeper;
import teammates.logic.backdoor.BackDoorLogic;

import com.google.gson.Gson;

public class AdminInstructorAccountAddAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        new GateKeeper().verifyAdminPrivileges(account);

        AdminHomePageData data = new AdminHomePageData(account);

        data.instructorShortName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_SHORT_NAME);
        Assumption.assertNotNull(data.instructorShortName);
        data.instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertNotNull(data.instructorName);
        data.instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(data.instructorEmail);
        data.instructorInstitution = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
        Assumption.assertNotNull(data.instructorInstitution);
        
        data.instructorShortName = data.instructorShortName.trim();
        data.instructorName = data.instructorName.trim();
        data.instructorEmail = data.instructorEmail.trim();
        data.instructorInstitution = data.instructorInstitution.trim();        
        
        String joinLink = "";
        
        try {
                       
            logic.verifyInputForAdminHomePage(data.instructorShortName, data.instructorName, data.instructorInstitution, data.instructorEmail);
            
            BackDoorLogic backDoor = new BackDoorLogic();
            String CourseId = importDemoData(data);              
            InstructorAttributes instructor = backDoor.getInstructorsForCourse(CourseId).get(0);   
            
            joinLink = logic.sendJoinLinkToNewInstructor(instructor, data.instructorShortName, data.instructorInstitution);
            
        } catch (Exception e) {
            setStatusForException(e);
            return createShowPageResult(Const.ViewURIs.ADMIN_HOME, data);
        }

        statusToUser.add("Instructor " + data.instructorName
                + " has been successfully created with join link:<br>" + joinLink);
        statusToAdmin = "A New Instructor <span class=\"bold\">"
                + data.instructorName + "</span> has been created.<br>"
                + "<span class=\"bold\">Id: </span>" + "ID will be assigned when the verification link was clicked and confirmed"
                + "<br>"
                + "<span class=\"bold\">Email: </span>" + data.instructorEmail
                + "<span class=\"bold\">Institution: </span>"
                + data.instructorInstitution;
 
        
        return createRedirectResult(Const.ActionURIs.ADMIN_HOME_PAGE);
    }

    private String importDemoData(AdminHomePageData helper)
            throws EntityAlreadyExistsException,
            InvalidParametersException, EntityDoesNotExistException {

        String jsonString;
        String courseId = generateDemoCourseId(helper.instructorEmail); 

        jsonString = FileHelper.readStream(Config.class.getClassLoader()
                    .getResourceAsStream("InstructorSampleData.json"));

        // replace email
        jsonString = jsonString.replaceAll(
                "teammates.demo.instructor@demo.course",
                helper.instructorEmail);
        // replace name
        jsonString = jsonString.replaceAll("Demo_Instructor",
                helper.instructorName);
        // replace course
        jsonString = jsonString.replaceAll("demo.course", courseId);
        // update evaluation time
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.AM_PM, Calendar.PM);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a Z");

        jsonString = jsonString.replace("2013-04-01 11:59 PM UTC",
                formatter.format(c.getTime()));

        Gson gson = Utils.getTeammatesGson();
        DataBundle data = gson.fromJson(jsonString, DataBundle.class);
        
        BackDoorLogic backdoor = new BackDoorLogic();
        backdoor.persistDataBundle(data);        
        
        //produce searchable documents
        List<CommentAttributes> comments = backdoor.getCommentsForGiver(courseId, helper.instructorEmail);
        List<FeedbackResponseCommentAttributes> frComments = backdoor.getFeedbackResponseCommentForGiver(courseId, helper.instructorEmail);
        List<StudentAttributes> students = backdoor.getStudentsForCourse(courseId);
        
        for(CommentAttributes comment:comments){
            backdoor.putDocument(comment);
        }
        for(FeedbackResponseCommentAttributes comment:frComments){
            backdoor.putDocument(comment);
        }
        for(StudentAttributes student:students){
            backdoor.putDocument(student);
        }
        
        return courseId;

    }

    /**
    * Strategy to Generate New Demo Course Id:
    *     a.  keep the part of email before "@"
    *         replace "@" with "."
    *         replace email host with their first 3 chars. eg, gmail.com -> gma
    *         append "-demo"
    *       to sum up: lebron@gmail.com -> lebron.gma-demo
    *       
    *   b.  if the generated courseId already exists, create another one by appending a integer to the previous courseId.
    *       if the newly generate id still exists, increment the id, until we find a feasible one
    *       eg.
    *       lebron@gmail.com -> lebron.gma-demo  // already exists!
    *       lebron@gmail.com -> lebron.gma-demo0 // already exists!
    *       lebron@gmail.com -> lebron.gma-demo1 // already exists!
    *       ...
    *       lebron@gmail.com -> lebron.gma-demo99 // already exists!
    *       lebron@gmail.com -> lebron.gma-demo100 // found! a feasible id
    *   
    *   c.  in any cases(a or b), if generated Id is longer than FieldValidator.COURSE_ID_MAX_LENGTH, shorten the part 
    *       before "@" of the intial input email, by continuously remove its last character
    *    
    *    @see #generateDemoCourseId(String)  
    *    @see #generateNextDemoCourseId(String, int)
    */
    
    /**    
    * Generate a course ID for demo course, and if the generated id already exists, try another one
    *       
    * @param instructorEmail is the instructor email.
    * @return generated course id 
    */
    private String generateDemoCourseId(String instructorEmail) {
        String proposedCourseId = generateNextDemoCourseId(instructorEmail, FieldValidator.COURSE_ID_MAX_LENGTH);
        while(logic.getCourse(proposedCourseId) != null){
            proposedCourseId = generateNextDemoCourseId(proposedCourseId, FieldValidator.COURSE_ID_MAX_LENGTH);
        }
        return proposedCourseId;
    }
    
    /**    
    * Generate a course ID for demo course from a given email
    *       
    * @param instructorEmail is the instructor email.
    * @return the first proposed course id. eg.lebron@gmail.com -> lebron.gma-demo
    */
    private String getDemoCourseIdRoot(String instructorEmail){
        final String[] splitedEmail = instructorEmail.split("@");
        final String head = splitedEmail[0];
        final String emailAbbreviation = splitedEmail[1].substring(0, 3);
        return head + "." + emailAbbreviation
                + "-demo";
    }
    
    /**    
    * Generate a course ID for demo course from a given email or a generated course Id
    * here we check the input string is a email or course Id and handle them accordingly
    * check the resulting course id, and if bigger than maximumIdLength, cut it so that it equals maximumIdLength
    * 
    * @param instructorEmailOrProposedCourseId is the instructor email or a proposed course id that already exists.
    * @param maximumIdLength is the maximum resulting id length allowed, above which we will cut the part before "@" 
    * @return the proposed course id. 
    *     eg.
    *         lebron@gmail.com -> lebron.gma-demo
    *         lebron.gma-demo -> lebron.gma-demo0
    *         lebron.gma-demo0 -> lebron.gma-demo1
    *         012345678901234567890123456789.gma-demo9 -> 01234567890123456789012345678.gma-demo10 (being cut)
    */
    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength){
        final boolean isFirstCourseId = instructorEmailOrProposedCourseId.contains("@");
        if(isFirstCourseId){
            return trimCourseIdToMaximumLengthIfNecessary(getDemoCourseIdRoot(instructorEmailOrProposedCourseId)
                    , maximumIdLength);
        } else {
            final boolean isFirstTimeDuplicate = instructorEmailOrProposedCourseId.endsWith("-demo"); 
            if(isFirstTimeDuplicate){
                return trimCourseIdToMaximumLengthIfNecessary(instructorEmailOrProposedCourseId + "0"
                        , maximumIdLength);
            } else {
                final int lastIndexOfDemo = instructorEmailOrProposedCourseId.lastIndexOf("-demo");
                final String root = instructorEmailOrProposedCourseId.substring(0, lastIndexOfDemo);
                final int previousDedupSuffix = Integer.parseInt(instructorEmailOrProposedCourseId.substring(lastIndexOfDemo + 5));
                
                return trimCourseIdToMaximumLengthIfNecessary(root + "-demo" + (previousDedupSuffix+1)
                        , maximumIdLength);
            }
        }
    }

    private String trimCourseIdToMaximumLengthIfNecessary(String demoCourseId, final int maximumIdLength) {
        final int courseIdLength = demoCourseId.length();
        if (courseIdLength <= maximumIdLength) {
            return demoCourseId;
        } else {
            return demoCourseId.substring(courseIdLength
                    - maximumIdLength);
        }
    }

}
