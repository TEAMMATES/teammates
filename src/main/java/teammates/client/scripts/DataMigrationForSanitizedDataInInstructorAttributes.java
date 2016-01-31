package teammates.client.scripts;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.storage.datastore.Datastore;

public class DataMigrationForSanitizedDataInInstructorAttributes extends RemoteApiClient {
    private final boolean isPreview = true;
    private InstructorsDb instructorsDb = new InstructorsDb();
    private InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    
    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedDataInInstructorAttributes migrator = new DataMigrationForSanitizedDataInInstructorAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<InstructorAttributes> allInstructors = getAllInstructors();
        if (isPreview) {
            System.out.println("Checking Sanitization in Instructors...");
        }
        int numberOfAffectedInstructors = 0;
        for (InstructorAttributes instructor : allInstructors) {
            if (!isPreview) {
                fixSanitizedDataForInstructor(instructor);
            } else {
                if (previewSanitizedDataForInstructor(instructor)) {
                    numberOfAffectedInstructors++;
                }
            }
        }
        if (isPreview) {
            System.out.println("There are/is " + numberOfAffectedInstructors + " instructor(s) with sanitized data!");
        } else {
            System.out.println("Sanitization fixing done!");
        }
    }

    private boolean previewSanitizedDataForInstructor(InstructorAttributes instructor) {
        boolean hasSanitizedData = checkInstructorHasSanitizedData(instructor);
        if (hasSanitizedData) {
            System.out.println("Checking instructor having email: " + instructor.email);
            if (isSanitizedString(instructor.displayedName)) {
                System.out.println("displayedName: " + instructor.displayedName);
                System.out.println("new displayedName: " + fixSanitization(instructor.displayedName));
            }
            if (isSanitizedString(instructor.name)) {
                System.out.println("name: " + instructor.name);
                System.out.println("new name: " + fixSanitization(instructor.name));
            }
            if (isSanitizedString(instructor.role)) {
                System.out.println("role: " + instructor.role);
                System.out.println("new role: " + fixSanitization(instructor.role));
            }
            System.out.println();
        }
        return hasSanitizedData;
    }

    private boolean isSanitizedString(String s){
        if (s == null) return false;
        if ((s.indexOf('<') >= 0) || (s.indexOf('>') >= 0) || (s.indexOf('\"') >= 0) || 
            (s.indexOf('/') >= 0) || (s.indexOf('\'') >= 0)) {
            return false;
        } else if ((s.indexOf("&lt;") >= 0) || (s.indexOf("&gt;") >= 0) || (s.indexOf("&quot;") >= 0) || 
                    (s.indexOf("&#x2f;") >= 0) || (s.indexOf("&#39;") >= 0) || (s.indexOf("&amp;") >= 0)) {
            return true;
        }
        return false;
    }
    
    private String fixSanitization(String s) {
        if (isSanitizedString(s)) {
            return StringHelper.recoverFromSanitizedText(s);
        }
        return s;
    }
    
    private boolean checkInstructorHasSanitizedData(InstructorAttributes instructor) {
        
        return isSanitizedString(instructor.role) || isSanitizedString(instructor.displayedName) 
                || isSanitizedString(instructor.name);
    }
    
    private void fixSanitizationForInstructor(InstructorAttributes instructor) {
        instructor.displayedName = fixSanitization(instructor.displayedName);
        instructor.name = fixSanitization(instructor.name);
        instructor.role = fixSanitization(instructor.role);
    }
    
    private void fixSanitizedDataForInstructor(InstructorAttributes instructor) {
        try {
            boolean hasSanitizedData = checkInstructorHasSanitizedData(instructor);
            if (hasSanitizedData) {
                fixSanitizationForInstructor(instructor);
                updateInstructor(instructor);
            }
        } catch (InvalidParametersException e) {
            Utils.getLogger().log(Level.INFO, "Student " + instructor.email + " invalid!");
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            Utils.getLogger().log(Level.INFO, "Student " + instructor.email + " does not exist!");
            e.printStackTrace();
        }
    }

    protected PersistenceManager getPM() {
        return Datastore.getPersistenceManager();
    }

    @SuppressWarnings("deprecation")
    protected List<InstructorAttributes> getAllInstructors() {
        return instructorsLogic.getAllInstructors();
    }
    
    public void updateInstructor(InstructorAttributes instructor) throws InvalidParametersException,
                                                                         EntityDoesNotExistException {
        if (!instructor.isValid()) {
            throw new InvalidParametersException(instructor.getInvalidityInfo());
        }
        instructorsDb.updateInstructorByGoogleId(instructor);
    }
}