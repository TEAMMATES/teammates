package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.ProfilesDb;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;

/**
 * Handles the logic related to accounts.
 */
public class AccountsLogic {
    //The API of this class doesn't have header comments because it sits behind
    //  the API of the logic class. Those who use this class is expected to be
    //  familiar with the its code and Logic's code. Hence, no need for header 
    //  comments.
        
    private static AccountsLogic instance = null;
    private static final AccountsDb accountsDb = new AccountsDb();
    private static final ProfilesDb profilesDb = new ProfilesDb();
    
    private static Logger log = Utils.getLogger();
    
    public static AccountsLogic inst() {
        if (instance == null)
            instance = new AccountsLogic();
        return instance;
    }
    
    
    public void createAccount(AccountAttributes accountData) 
                    throws InvalidParametersException {
    
        List<String> invalidityInfo = accountData.getInvalidityInfo();
        if (!invalidityInfo.isEmpty()) {
            throw new InvalidParametersException(invalidityInfo);
        }
        
        log.info("going to create account :\n"+accountData.toString());
        
        accountsDb.createAccount(accountData);
    }

    public AccountAttributes getAccount(String googleId) {
        return getAccount(googleId, false);
    }

    public AccountAttributes getAccount(String googleId, boolean retrieveStudentProfile) {
        return accountsDb.getAccount(googleId, retrieveStudentProfile);
    }
    
    public boolean isAccountPresent(String googleId) {
        return accountsDb.getAccount(googleId) != null;
    }
    
    public boolean isAccountAnInstructor(String googleId) {
        AccountAttributes a = accountsDb.getAccount(googleId);
        return a == null ? false : a.isInstructor;
    }

    public List<AccountAttributes> getInstructorAccounts() {
        return accountsDb.getInstructorAccounts();
    }
    
    public String getCourseInstitute(String courseId) {
        CourseAttributes cd = new CoursesLogic().getCourse(courseId);
        Assumption.assertNotNull("Trying to getCourseInstitute for inexistent course with id " + courseId, cd);
        List<InstructorAttributes> instructorList = InstructorsLogic.inst().getInstructorsForCourse(cd.id);
        
        Assumption.assertTrue("Course has no instructors: " + cd.id, !instructorList.isEmpty());
        // Retrieve institute field from one of the instructors of the course
        String institute = "";
        for (int i=0; i<instructorList.size(); i++) {
            String instructorGoogleId = instructorList.get(i).googleId;
            if(instructorGoogleId==null){
                continue;
            }
            AccountAttributes instructorAcc = accountsDb.getAccount(instructorGoogleId);
            if (instructorAcc != null) {
                institute = instructorAcc.institute;
                break;
            }
        }
        Assumption.assertNotEmpty("No institute found for the course", institute);
        return institute;
    }

    public void updateAccount(AccountAttributes account)
            throws InvalidParametersException, EntityDoesNotExistException {
        accountsDb.updateAccount(account, false);
    }
    
    public void updateAccount(AccountAttributes account, boolean updateStudentProfile) 
            throws InvalidParametersException, EntityDoesNotExistException {
        accountsDb.updateAccount(account, updateStudentProfile);
    }
    
    public void joinCourseForStudent(String registrationKey, String googleId) 
            throws JoinCourseException {
        
        verifyStudentJoinCourseRequest(registrationKey, googleId);
        
        StudentAttributes student = StudentsLogic.inst().getStudentForRegistrationKey(registrationKey);
        
        //register the student
        student.googleId = googleId;
        try {
            StudentsLogic.inst().updateStudentCascade(student.email, student);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Student disappered while trying to register " + TeammatesException.toStringWithStackTrace(e));
        } catch (InvalidParametersException e) {
            throw new JoinCourseException(e.getMessage());
        } 
        
        if (accountsDb.getAccount(googleId) == null) {
            try {
                createStudentAccount(student);
            } catch (InvalidParametersException e) {
                throw new JoinCourseException(e.getLocalizedMessage());
            }
        }
    }
    

    /**
     * Joins the user as an instructor, and sets the institute too.
     */
    public void joinCourseForInstructor(String encryptedKey, String googleId, String institute)
            throws JoinCourseException, InvalidParametersException{
        
        try {
            joinCourseForInstructorWithInstitute(encryptedKey, googleId, institute);
        } catch (EntityDoesNotExistException e) {
            throw new JoinCourseException(e.getMessage());
        }
        
    }
    
    /**
     * Joins the user as an instructor.
     */
    public void joinCourseForInstructor(String encryptedKey, String googleId)
            throws JoinCourseException, InvalidParametersException{
        
        try {
            joinCourseForInstructorWithInstitute(encryptedKey, googleId, null);
        } catch (EntityDoesNotExistException e) {
            throw new JoinCourseException(e.getMessage());
        }
        
    }
    
    
    /**
     * Institute is set only if it is not null. If it is null, this instructor
     * is given the the institute of an existing instructor of the same course. 
     */
    private void joinCourseForInstructorWithInstitute(String encryptedKey,String googleId, String institute)
            throws JoinCourseException, InvalidParametersException, EntityDoesNotExistException {

        confirmValidJoinCourseRequest(encryptedKey, googleId, institute);

        InstructorAttributes instructor = InstructorsLogic.inst().getInstructorForRegistrationKey(encryptedKey);
        AccountAttributes account = accountsDb.getAccount(googleId);
        String instituteToSave = (institute == null? getCourseInstitute(instructor.courseId) : institute ) ;
        
        if (account == null){
            createAccount(new AccountAttributes(googleId,
                                                instructor.name,
                                                true,
                                                instructor.email,
                                                instituteToSave));
        } else {
            makeAccountInstructor(googleId);
        }
                  
        instructor.googleId = googleId;
        InstructorsLogic.inst().updateInstructorByEmail(instructor.email, instructor);
        
        //Update the goolgeId of the student entity for the instructor which was created from sampleData.
        StudentAttributes student = StudentsLogic.inst().getStudentForEmail(instructor.courseId, instructor.email);
        if(student != null){
            student.googleId = googleId;
            StudentsLogic.inst().updateStudentCascade(instructor.email, student);
        }
        
    }
    
    /**
     * @throws JoinCourseException if the request is invalid. Do nothing otherwise.
     */
    private void confirmValidJoinCourseRequest(String encryptedKey, String googleId, String institute)
            throws JoinCourseException {
        
        //The order in which these confirmations are done is important. Reorder with care.
        confirmValidKey(encryptedKey);
        
        InstructorAttributes instructorForKey = InstructorsLogic.inst().getInstructorForRegistrationKey(encryptedKey);
        
        confirmNotAlreadyJoinedAsInstructor(instructorForKey, googleId, institute);
        confirmUnusedKey(instructorForKey, googleId);
        confirmNotRejoiningUsingDifferentKey(instructorForKey, googleId);
        
    }
    
    /**
     * @throws JoinCourseException if this is a case of an instructor who has
     *     already joined the course using the key of another unregistered user.
     */
    private void confirmNotRejoiningUsingDifferentKey(
            InstructorAttributes instructorForKey, String googleId) throws JoinCourseException {
        
        if (instructorForKey.googleId != null) { //using a used key. this means no danger of rejoining using different key
            return;
        }
        
        //check if this Google ID has already joined this course
        InstructorAttributes existingInstructor = InstructorsLogic.inst().getInstructorForGoogleId(instructorForKey.courseId, googleId);
        
        if (existingInstructor != null) {
            throw new JoinCourseException(
                    String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                                  googleId));
        }
        
    }


    /**
     * @throws JoinCourseException if the instructor has already joined this 
     *     course using the same key.
     */
    private void confirmNotAlreadyJoinedAsInstructor(InstructorAttributes instructorForKey, String googleId, String institute) 
            throws JoinCourseException {
        if(instructorForKey.googleId ==null || !instructorForKey.googleId.equals(googleId)){
            return;
        }
        AccountAttributes existingAccount = accountsDb.getAccount(googleId);
        if (existingAccount != null && existingAccount.isInstructor){
            throw new JoinCourseException(Const.StatusCodes.ALREADY_JOINED, 
                                          googleId + " has already joined this course");
        }
        
    }


    /**
     * @throws JoinCourseException if the key does not correspond to an
     *    Instructor entity.
     */
    private void confirmValidKey(String encryptedKey) throws JoinCourseException{
        InstructorAttributes instructorForKey = InstructorsLogic.inst().getInstructorForRegistrationKey(encryptedKey);
        
        if (instructorForKey == null) {
            String joinUrl = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN + "?key=" + encryptedKey;
            throw new JoinCourseException(Const.StatusCodes.INVALID_KEY,
                                          "You have used an invalid join link: " + joinUrl);
            
        }
    }
    
    /**
     * @throws JoinCourseException if the key has been used before.
     */
    private void confirmUnusedKey(InstructorAttributes instructorForKey, String googleId) throws JoinCourseException{
        if(instructorForKey.googleId==null){
            return;
        }
        
        //We assume we have already confirmed that the key was not used by this
        //  person already.
        if (!instructorForKey.googleId.equals(googleId)) {
            throw new JoinCourseException(Const.StatusCodes.KEY_BELONGS_TO_DIFFERENT_USER,
                                          String.format(Const.StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER,
                                                  StringHelper.obscure(instructorForKey.googleId)));
        }
    }
    


    private void verifyStudentJoinCourseRequest(String encryptedKey, String googleId)
            throws JoinCourseException {
        
        StudentAttributes studentRole = StudentsLogic.inst().getStudentForRegistrationKey(encryptedKey);
        
        if(studentRole == null){
            throw new JoinCourseException(Const.StatusCodes.INVALID_KEY,
                    "You have used an invalid join link: %s");
        } else if (studentRole.isRegistered()) {
            if (studentRole.googleId.equals(googleId)) {
                throw new JoinCourseException(Const.StatusCodes.ALREADY_JOINED,
                        "You (" + googleId + ") have already joined this course");
            } else {
                throw new JoinCourseException(
                        Const.StatusCodes.KEY_BELONGS_TO_DIFFERENT_USER,
                        String.format(Const.StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER,
                                    StringHelper.obscure(studentRole.googleId)));
            }
        } 
    
        StudentAttributes existingStudent =
                StudentsLogic.inst().getStudentForCourseIdAndGoogleId(studentRole.course, googleId);
        
        if (existingStudent != null) {
            throw new JoinCourseException(
                    String.format(Const.StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER,
                            googleId));
        }
    }

    public void downgradeInstructorToStudentCascade(String googleId) {
        InstructorsLogic.inst().deleteInstructorsForGoogleIdAndCascade(googleId);
        makeAccountNonInstructor(googleId);
    }

    public void makeAccountNonInstructor(String googleId) {
        AccountAttributes account = accountsDb.getAccount(googleId, true);
        if (account != null) {
            account.isInstructor = false;
            try {
                accountsDb.updateAccount(account);
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail("Invalid account data detected unexpectedly " +
                        "while removing instruction privileges from account :"+account.toString());
            }
        }else {
            log.warning("Accounts logic trying to modify non-existent account a non-instructor :" + googleId );
        }
    }

    public void makeAccountInstructor(String googleId) {
        
        AccountAttributes account = accountsDb.getAccount(googleId, true);
        
        if (account != null) {
            account.isInstructor = true;
            try {
                accountsDb.updateAccount(account);
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                Assumption.fail("Invalid account data detected unexpectedly " +
                        "while adding instruction privileges to account :"+account.toString());
            }
        } else {
            log.warning("Accounts logic trying to modify non-existent account an instructor:" + googleId );
        }
    }

    public void deleteAccountCascade(String googleId) {
        InstructorsLogic.inst().deleteInstructorsForGoogleIdAndCascade(googleId);
        StudentsLogic.inst().deleteStudentsForGoogleIdAndCascade(googleId);
        accountsDb.deleteAccount(googleId);
        //TODO: deal with orphan courses, submissions etc.
    }
    
    private void createStudentAccount(StudentAttributes student) 
            throws InvalidParametersException {
        AccountAttributes account = new AccountAttributes();
        account.googleId = student.googleId;
        account.email = student.email;
        account.name = student.name;
        account.isInstructor = false;
        account.institute = getCourseInstitute(student.course);
        
        StudentProfileAttributes spa = new StudentProfileAttributes();
        spa.googleId = student.googleId;
        spa.institute = account.institute;
        account.studentProfile = spa;
        accountsDb.createAccount(account);
    }

    public StudentProfileAttributes getStudentProfile(String googleId) {
        return profilesDb.getStudentProfile(googleId);
    }

    public void updateStudentProfile(StudentProfileAttributes newStudentProfileAttributes) 
            throws InvalidParametersException, EntityDoesNotExistException {
        profilesDb.updateStudentProfile(newStudentProfileAttributes);
    }

    public void deleteStudentProfilePicture(String googleId) 
            throws BlobstoreFailureException, EntityDoesNotExistException {
        profilesDb.deleteStudentProfilePicture(googleId);
    }
    
    public void deletePicture(BlobKey key) throws BlobstoreFailureException {
        profilesDb.deletePicture(key);
    }

    public void updateStudentProfilePicture (String googleId, String newPictureKey)
        throws EntityDoesNotExistException, BlobstoreFailureException {
        profilesDb.updateStudentProfilePicture(googleId, newPictureKey);
        
    }
}