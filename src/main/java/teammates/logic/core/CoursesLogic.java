package teammates.logic.core;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.storage.api.CoursesDb;

/**
 * Handles  operations related to courses.
 */
public class CoursesLogic {
    /* Explanation: Most methods in the API of this class doesn't have header 
     *  comments because it sits behind the API of the logic class. 
     *  Those who use this class is expected to be familiar with the its code 
     *  and Logic's code. Hence, no need for header comments.
     */ 
    
    //TODO: There's no need for this class to be a Singleton.
    private static CoursesLogic instance = null;
    
    private static final Logger log = Utils.getLogger();
    
    /* Explanation: This class depends on CoursesDb class but no other *Db classes.
     * That is because reading/writing entities from/to the datastore is the 
     * responsibility of the matching *Logic class.
     * However, this class can talk to other *Logic classes. That is because
     * the logic related to one entity type can involve the logic related to
     * other entity types.
     */

    private static final CoursesDb coursesDb = new CoursesDb();
    
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    private static final CommentsLogic commentsLogic = CommentsLogic.inst();

    
    public static CoursesLogic inst() {
        if (instance == null)
            instance = new CoursesLogic();
        return instance;
    }

    public void createCourse(String courseId, String courseName) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        CourseAttributes courseToAdd = new CourseAttributes(courseId, courseName);
        coursesDb.createEntity(courseToAdd);
    }
    
    /**
     * Creates a Course object and an Instructor object for the Course.
     */
    public void createCourseAndInstructor(String instructorGoogleId, String courseId, String courseName) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        AccountAttributes courseCreator = accountsLogic.getAccount(instructorGoogleId);
        Assumption.assertNotNull(
                "Trying to create a course for a non-existent instructor :"+ instructorGoogleId, 
                courseCreator);
        Assumption.assertTrue(
                "Trying to create a course for a person who doesn't have instructor privileges :"+ instructorGoogleId, 
                courseCreator.isInstructor);
        
        createCourse(courseId, courseName);
        
        /* Create the initial instructor for the course */
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor = new InstructorAttributes(instructorGoogleId, courseId, courseCreator.name, courseCreator.email, 
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER, true, InstructorAttributes.DEFAULT_DISPLAY_NAME,
                privileges);
        
        try {
            instructorsLogic.createInstructor(instructor);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            //roll back the transaction
            coursesDb.deleteCourse(courseId);
            String errorMessage = "Unexpected exception while trying to create instructor for a new course "+ EOL 
                    + instructor.toString() + EOL
                    + TeammatesException.toStringWithStackTrace(e);
            Assumption.fail(errorMessage);
        }
    }

    public CourseAttributes getCourse(String courseId) {
        return coursesDb.getCourse(courseId);
    }

    public boolean isCoursePresent(String courseId) {
        return coursesDb.getCourse(courseId) != null;
    }
    
    public boolean isSampleCourse(String courseId) {
        Assumption.assertNotNull("Course ID is null", courseId);
        return StringHelper.isMatching(courseId, FieldValidator.REGEX_SAMPLE_COURSE_ID);
    }

    public void verifyCourseIsPresent(String courseId) throws EntityDoesNotExistException{
        if (!isCoursePresent(courseId)){
            throw new EntityDoesNotExistException("Course does not exist: "+courseId);
        }
    }

    public CourseDetailsBundle getCourseDetails(String courseId) 
            throws EntityDoesNotExistException {
        CourseDetailsBundle courseSummary = getCourseSummary(courseId);

        ArrayList<EvaluationDetailsBundle> evaluationList = 
                evaluationsLogic.getEvaluationsDetailsForCourse(courseSummary.course.id);
        
        for (EvaluationDetailsBundle edd : evaluationList) {
            courseSummary.evaluations.add(edd);
        }

        return courseSummary;
    }

    public List<CourseDetailsBundle> getCourseDetailsListForStudent(
            String googleId) throws EntityDoesNotExistException {
        
        List<CourseAttributes> courseList = getCoursesForStudentAccount(googleId);
        List<CourseDetailsBundle> courseDetailsList = new ArrayList<CourseDetailsBundle>();
        
        for (CourseAttributes c : courseList) {

            StudentAttributes s = studentsLogic.getStudentForCourseIdAndGoogleId(c.id, googleId);
            
            if (s == null) {
                //TODO Remove excessive logging after the reason why s can be null is found
                String logMsg = "Student is null in CoursesLogic.getCourseDetailsListForStudent(String googleId)"
                        + "<br/> Student Google ID: " + googleId
                        + "<br/> Course: " + c.id
                        + "<br/> All Courses Retrieved using the Google ID:";
                for (CourseAttributes course : courseList) {
                    logMsg += "<br/>" + course.id;
                }
                log.severe(logMsg);
                
                //TODO Failing might not be the best course of action here. 
                //Maybe throw a custom exception and tell user to wait due to eventual consistency?
                Assumption.assertNotNull("Student should not be null at this point.", s);
            }
            
            List<EvaluationAttributes> evaluationDataList = evaluationsLogic
                    .getEvaluationsForCourse(c.id);            
            List<FeedbackSessionAttributes> feedbackSessionList = 
                    feedbackSessionsLogic.getFeedbackSessionsForUserInCourse(c.id, s.email);
            
            CourseDetailsBundle cdd = new CourseDetailsBundle(c);
            
            for (EvaluationAttributes ed : evaluationDataList) {
                EvaluationDetailsBundle edd = new EvaluationDetailsBundle(ed);
                log.fine("Adding evaluation " + ed.name + " to course " + c.id);
                if (ed.getStatus() != EvalStatus.AWAITING) {
                    cdd.evaluations.add(edd);
                }
            }
            for (FeedbackSessionAttributes fs : feedbackSessionList) {
                cdd.feedbackSessions.add(new FeedbackSessionDetailsBundle(fs));
            }
            
            courseDetailsList.add(cdd);
        }
        return courseDetailsList;
    }

    public List<String> getSectionsNameForCourse(String courseId) 
            throws EntityDoesNotExistException {

        verifyCourseIsPresent(courseId);
        
        List<StudentAttributes> studentDataList = 
                studentsLogic.getStudentsForCourse(courseId);

        List<String> sectionNameList = new ArrayList<String>();

        for(StudentAttributes sd: studentDataList) {
            if (!sd.section.equals(Const.DEFAULT_SECTION) && !sectionNameList.contains(sd.section)) {
                sectionNameList.add(sd.section);
            }
        }

        Collections.sort(sectionNameList);

        return sectionNameList;
    }

    public SectionDetailsBundle getSectionForCourse(String section, String courseId)
            throws EntityDoesNotExistException {

        verifyCourseIsPresent(courseId);
        
        List<StudentAttributes> students = studentsLogic.getStudentsForSection(section, courseId);
        StudentAttributes.sortByTeamName(students);

        SectionDetailsBundle sectionDetails = new SectionDetailsBundle();
        TeamDetailsBundle team = null;
        sectionDetails.name = section;
        for(int i = 0; i < students.size(); i++){
            StudentAttributes s = students.get(i);
    
            // first student of first team
            if (team == null) {
                team = new TeamDetailsBundle();
                team.name = s.team;
                team.students.add(s);
            } 
            // student in the same team as the previous student
            else if (s.team.equals(team.name)) {
                team.students.add(s);
            } 
            // first student of subsequent teams (not the first team)
            else {
                sectionDetails.teams.add(team);
                team = new TeamDetailsBundle();
                team.name = s.team;
                team.students.add(s);
            }
    
            // if last iteration
            if (i == (students.size() - 1)) {
                sectionDetails.teams.add(team);
            }
        }
        return sectionDetails;
    }

    public List<SectionDetailsBundle> getSectionsForCourse(String courseId, CourseDetailsBundle cdd) 
            throws EntityDoesNotExistException {
        
        verifyCourseIsPresent(courseId);
        
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);
        StudentAttributes.sortBySectionName(students);
        
        List<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();
        
        SectionDetailsBundle section = null;
        int teamIndexWithinSection = 0;
        
        for(int i = 0; i < students.size(); i++) {
            
            StudentAttributes s = students.get(i);
            cdd.stats.studentsTotal++;
            if(!s.isRegistered()){
                cdd.stats.unregisteredTotal++;
            }
            
            if(section == null) {   // First student of first section
                section = new SectionDetailsBundle();
                section.name = s.section;
                section.teams.add(new TeamDetailsBundle());
                cdd.stats.teamsTotal++;
                section.teams.get(teamIndexWithinSection).name = s.team;
                section.teams.get(teamIndexWithinSection).students.add(s);
            } else if(s.section.equals(section.name)){
                if(s.team.equals(section.teams.get(teamIndexWithinSection).name)){
                    section.teams.get(teamIndexWithinSection).students.add(s);
                } else {
                    teamIndexWithinSection++;
                    section.teams.add(new TeamDetailsBundle());
                    cdd.stats.teamsTotal++;
                    section.teams.get(teamIndexWithinSection).name = s.team;
                    section.teams.get(teamIndexWithinSection).students.add(s);
                }
            } else { // first student of subsequent section
                sections.add(section);
                if(!section.name.equals(Const.DEFAULT_SECTION)){
                    cdd.stats.sectionsTotal++;
                }
                teamIndexWithinSection = 0;
                section = new SectionDetailsBundle();
                section.name = s.section;
                section.teams.add(new TeamDetailsBundle());
                cdd.stats.teamsTotal++;
                section.teams.get(teamIndexWithinSection).name = s.team;
                section.teams.get(teamIndexWithinSection).students.add(s);
            }
            
            boolean isLastStudent = i == (students.size() -1);
            if(isLastStudent){
                sections.add(section);
                if(!section.name.equals(Const.DEFAULT_SECTION)){
                    cdd.stats.sectionsTotal++;
                }
            }
        }
        
        return sections;
    }
    
    public List<SectionDetailsBundle> getSectionsForCourseWithoutStats(String courseId) 
            throws EntityDoesNotExistException {
        
        verifyCourseIsPresent(courseId);
        
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);
        StudentAttributes.sortBySectionName(students);
        
        List<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();
        
        SectionDetailsBundle section = null;
        int teamIndexWithinSection = 0;
        
        for(int i = 0; i < students.size(); i++) {
            
            StudentAttributes s = students.get(i);
            
            if(section == null) {   // First student of first section
                section = new SectionDetailsBundle();
                section.name = s.section;
                section.teams.add(new TeamDetailsBundle());
                section.teams.get(teamIndexWithinSection).name = s.team;
                section.teams.get(teamIndexWithinSection).students.add(s);
            } else if(s.section.equals(section.name)){
                if(s.team.equals(section.teams.get(teamIndexWithinSection).name)){
                    section.teams.get(teamIndexWithinSection).students.add(s);
                } else {
                    teamIndexWithinSection++;
                    section.teams.add(new TeamDetailsBundle());
                    section.teams.get(teamIndexWithinSection).name = s.team;
                    section.teams.get(teamIndexWithinSection).students.add(s);
                }
            } else { // first student of subsequent section
                sections.add(section);
                teamIndexWithinSection = 0;
                section = new SectionDetailsBundle();
                section.name = s.section;
                section.teams.add(new TeamDetailsBundle());
                section.teams.get(teamIndexWithinSection).name = s.team;
                section.teams.get(teamIndexWithinSection).students.add(s);
            }
            
            boolean isLastStudent = i == (students.size() -1);
            if(isLastStudent){
                sections.add(section);
            }
        }
        
        return sections;
    }

    /**
     * Returns Teams for a particular courseId.<br> 
     * <b>Note:</b><br>
     * This method does not returns any Loner information presently,<br>
     * Loner information must be returned as we decide to support loners<br>in future.
     *  
     */
    public List<TeamDetailsBundle> getTeamsForCourse(String courseId) 
            throws EntityDoesNotExistException {

        if (getCourse(courseId) == null) {
            throw new EntityDoesNotExistException("The course " + courseId + " does not exist");
        }
    
        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);
        StudentAttributes.sortByTeamName(students);
        
        List<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>(); 
        
        TeamDetailsBundle team = null;
        
        for (int i = 0; i < students.size(); i++) {
    
            StudentAttributes s = students.get(i);
    
            // first student of first team
            if (team == null) {
                team = new TeamDetailsBundle();
                team.name = s.team;
                team.students.add(s);
            } 
            // student in the same team as the previous student
            else if (s.team.equals(team.name)) {
                team.students.add(s);
            } 
            // first student of subsequent teams (not the first team)
            else {
                teams.add(team);
                team = new TeamDetailsBundle();
                team.name = s.team;
                team.students.add(s);
            }
    
            // if last iteration
            if (i == (students.size() - 1)) {
                teams.add(team);
            }
        }
    
        return teams;
    }

    public int getNumberOfSections(String courseID) throws EntityDoesNotExistException {

        List<String> sectionNameList = getSectionsNameForCourse(courseID);

        return sectionNameList.size();
    }

    public int getNumberOfTeams(String courseID) throws EntityDoesNotExistException {

        verifyCourseIsPresent(courseID);
        List<StudentAttributes> studentDataList = 
                studentsLogic.getStudentsForCourse(courseID);

        List<String> teamNameList = new ArrayList<String>();

        for (StudentAttributes sd : studentDataList) {
            if (!teamNameList.contains(sd.team)) {
                teamNameList.add(sd.team);
            }
        }

        return teamNameList.size();
    }

    public int getTotalEnrolledInCourse(String courseId) throws EntityDoesNotExistException {
        verifyCourseIsPresent(courseId);
        return studentsLogic.getStudentsForCourse(courseId).size();
    }

    public int getTotalUnregisteredInCourse(String courseId) throws EntityDoesNotExistException {
        verifyCourseIsPresent(courseId);
        return studentsLogic.getUnregisteredStudentsForCourse(courseId).size();
    }

    public CourseDetailsBundle getCourseSummary(String courseId)
            throws EntityDoesNotExistException {
        CourseAttributes cd = coursesDb.getCourse(courseId);

        if (cd == null) {
            throw new EntityDoesNotExistException("The course does not exist: "
                    + courseId);
        }

        CourseDetailsBundle cdd = new CourseDetailsBundle(cd);
        cdd.sections= (ArrayList<SectionDetailsBundle>) getSectionsForCourse(courseId, cdd);
        
        return cdd;
    }

    public CourseSummaryBundle getCourseSummaryWithoutStats(String courseId)
            throws EntityDoesNotExistException {
        CourseAttributes cd = coursesDb.getCourse(courseId);

        if (cd == null) {
            throw new EntityDoesNotExistException("The course does not exist: "
                    + courseId);
        }

        CourseSummaryBundle cdd = new CourseSummaryBundle(cd);
        return cdd;
    }
    
    public List<CourseAttributes> getCoursesForStudentAccount(String googleId) throws EntityDoesNotExistException {
        
        List<StudentAttributes> studentDataList = studentsLogic.getStudentsForGoogleId(googleId);
        
        if (studentDataList.size() == 0) {
            throw new EntityDoesNotExistException("Student with Google ID "
                    + googleId + " does not exist");
        }
        
        ArrayList<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

        for (StudentAttributes s : studentDataList) {
            CourseAttributes course = coursesDb.getCourse(s.course);
            if(course==null){
                log.warning(
                        "Course was deleted but the Student still exists :"+Const.EOL 
                        + s.toString());
            }else{
                courseList.add(course);
            }
        }
        return courseList;
    }

    public List<CourseAttributes> getCoursesForInstructor(String googleId) throws EntityDoesNotExistException {

        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(googleId);

        ArrayList<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

        for (InstructorAttributes instructor : instructorList) {
            CourseAttributes course = coursesDb.getCourse(instructor.courseId);
            
            if (course == null) {
                log.warning("Course was deleted but the Instructor still exists: " + Const.EOL 
                        + instructor.toString());
            } else {
                courseList.add(course);
            }
        }
        
        return courseList;
    }
    
    public HashMap<String, CourseDetailsBundle> getCourseSummariesForInstructor(String googleId) throws EntityDoesNotExistException {
        
        instructorsLogic.verifyInstructorExists(googleId);

        List<InstructorAttributes> instructorAttributesList = instructorsLogic.getInstructorsForGoogleId(googleId);
    
        HashMap<String, CourseDetailsBundle> courseSummaryList = new HashMap<String, CourseDetailsBundle>();
        
        for (InstructorAttributes ia : instructorAttributesList) {
            CourseAttributes course = coursesDb.getCourse(ia.courseId);
            
            try {
                if (course != null) {
                    courseSummaryList.put(course.id, getCourseSummary(course.id));
                } else {
                    log.warning("Course was deleted but the Instructor still exists: "+Const.EOL 
                            + ia.toString());
                }
            } catch (EntityDoesNotExistException e) {
                log.warning("Course was deleted but the Instructor still exists: "+Const.EOL 
                        + ia.toString());
            }
        }
        
        return courseSummaryList;
    }
 
    public HashMap<String, CourseDetailsBundle> getCoursesDetailsListForInstructor(
            String instructorId) throws EntityDoesNotExistException {
        
        HashMap<String, CourseDetailsBundle> courseList = 
                getCourseSummariesForInstructor(instructorId);
        
        ArrayList<EvaluationDetailsBundle> evaluationList = 
                evaluationsLogic.getEvaluationsDetailsForInstructor(instructorId);
        List<FeedbackSessionDetailsBundle> feedbackSessionList = 
                feedbackSessionsLogic.getFeedbackSessionDetailsForInstructor(instructorId);
        
        for (EvaluationDetailsBundle edd : evaluationList) {
            CourseDetailsBundle courseSummary = courseList.get(edd.evaluation.courseId);
            courseSummary.evaluations.add(edd);
        }
        for (FeedbackSessionDetailsBundle fsb : feedbackSessionList) {
            CourseDetailsBundle courseSummary = courseList.get(fsb.feedbackSession.courseId);
            courseSummary.feedbackSessions.add(fsb);
        }
        return courseList;
    }
    
    public HashMap<String, CourseSummaryBundle> getCoursesSummaryWithoutStatsForInstructor(
            String instructorId) throws EntityDoesNotExistException {
        
        HashMap<String, CourseSummaryBundle> courseList = 
                getCourseSummaryWithoutStatsForInstructor(instructorId);
        
        ArrayList<EvaluationAttributes> evaluationList = 
                evaluationsLogic.getEvaluationsListForInstructor(instructorId);
        List<FeedbackSessionAttributes> feedbackSessionList = 
                feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorId);
        
        for (EvaluationAttributes edd : evaluationList) {
            CourseSummaryBundle courseSummary = courseList.get(edd.courseId);
            courseSummary.evaluations.add(edd);
        }
        for (FeedbackSessionAttributes fsb : feedbackSessionList) {
            CourseSummaryBundle courseSummary = courseList.get(fsb.courseId);
            courseSummary.feedbackSessions.add(fsb);
        }
        return courseList;
    }
    
    public List<CourseAttributes> getArchivedCoursesForInstructor(String googleId) throws EntityDoesNotExistException {
        
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(googleId);
        
        ArrayList<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

        for (InstructorAttributes instructor : instructorList) {
            CourseAttributes course = coursesDb.getCourse(instructor.courseId);
            
            if (course == null) {
                log.warning("Course was deleted but the Instructor still exists: " + Const.EOL 
                        + instructor.toString());
            } else if (course.isArchived) {
                courseList.add(course);
            }
        }
        
        return courseList;
    }
    
    public void setArchiveStatusOfCourse(String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        CourseAttributes courseToUpdate = getCourse(courseId);
        if (courseToUpdate != null) {
            courseToUpdate.isArchived = archiveStatus;
            coursesDb.updateCourse(courseToUpdate);
        } else {
            throw new EntityDoesNotExistException("Course does not exist: "+courseId);
        }
    }

    /**
     * Delete a course from its given corresponding ID
     * This will also cascade the data in other databases which are related to this course
     */ 
    public void deleteCourseCascade(String courseId) {
        evaluationsLogic.deleteEvaluationsForCourse(courseId);
        studentsLogic.deleteStudentsForCourse(courseId);
        instructorsLogic.deleteInstructorsForCourse(courseId);
        commentsLogic.deleteCommentsForCourse(courseId);
        feedbackSessionsLogic.deleteFeedbackSessionsForCourse(courseId);
        coursesDb.deleteCourse(courseId);
    }
    
    private HashMap<String, CourseSummaryBundle> getCourseSummaryWithoutStatsForInstructor(String googleId) throws EntityDoesNotExistException {
        
        instructorsLogic.verifyInstructorExists(googleId);
        
        List<InstructorAttributes> instructorAttributesList = instructorsLogic.getInstructorsForGoogleId(googleId);
        
        HashMap<String, CourseSummaryBundle> courseSummaryList = new HashMap<String, CourseSummaryBundle>();
        
        for (InstructorAttributes ia : instructorAttributesList) {
            CourseAttributes course = coursesDb.getCourse(ia.courseId);
            
            try {
                courseSummaryList.put(course.id, getCourseSummaryWithoutStats(course.id));
            } catch (EntityDoesNotExistException e) {
                log.warning("Course was deleted but the Instructor still exists: "+Const.EOL 
                        + ia.toString());
            }
        }
        
        return courseSummaryList;
    }
    
    public String getCourseStudentListAsCsv(String courseId, String googleId) throws EntityDoesNotExistException {

        HashMap<String, CourseDetailsBundle> courses = getCourseSummariesForInstructor(googleId);
        CourseDetailsBundle course = courses.get(courseId);
        boolean hasSection = hasIndicatedSections(courseId);
        
        String export = "";
        export += "Course ID" + "," + Sanitizer.sanitizeForCsv(courseId) + Const.EOL + 
                  "Course Name" + "," + Sanitizer.sanitizeForCsv(course.course.name) + Const.EOL + 
                  Const.EOL + Const.EOL;
        if(hasSection){
            export += "Section" + ",";
        }
        export  += "Team,First Name,Last Name,Status,Email" + Const.EOL;
        
        for (SectionDetailsBundle section : course.sections) {
            for (TeamDetailsBundle team  :   section.teams) {
                for(StudentAttributes student : team.students){
                    String studentStatus = null;
                    if(student.googleId == null || student.googleId.equals("")){
                        studentStatus = Const.STUDENT_COURSE_STATUS_YET_TO_JOIN;
                    } else {
                        studentStatus = Const.STUDENT_COURSE_STATUS_JOINED;
                    }
                    
                    if(hasSection){
                        export += Sanitizer.sanitizeForCsv(section.name) + ",";
                    }
                                        
                    String[] splitName = StringHelper.splitName(StringHelper.removeExtraSpace(student.name));

                    export += Sanitizer.sanitizeForCsv(StringHelper.recoverFromSanitizedText(team.name)) + "," + 
                        Sanitizer.sanitizeForCsv(StringHelper.recoverFromSanitizedText(splitName[0])) + "," +
                        Sanitizer.sanitizeForCsv(StringHelper.recoverFromSanitizedText(splitName[1])) + "," +
                        Sanitizer.sanitizeForCsv(studentStatus) + "," +
                        Sanitizer.sanitizeForCsv(student.email) + Const.EOL;
                }
            }
        }
        return export;
    }

    public boolean hasIndicatedSections(String courseId) throws EntityDoesNotExistException{

        verifyCourseIsPresent(courseId);
        
        List<StudentAttributes> studentList = studentsLogic.getStudentsForCourse(courseId);
        for(StudentAttributes student : studentList) {
            if(!student.section.equals(Const.DEFAULT_SECTION)){
                return true;
            }
        }
        return false;
    }
}
