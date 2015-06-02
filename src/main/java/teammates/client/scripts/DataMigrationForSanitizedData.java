package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.logic.core.StudentsLogic;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Comment;

public class DataMigrationForSanitizedData extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedData migrator = new DataMigrationForSanitizedData();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<StudentAttributes> allStudents = getAllStudents();
        for (StudentAttributes student : allStudents) {
            fixSanitizedDataForStudent(student);
        }
    }

    private boolean isSanitizedString(String s){
        
        if ((s.indexOf('<') >= 0) || (s.indexOf('>') >= 0) || (s.indexOf('\"') >= 0) || 
            (s.indexOf('/') >= 0) || (s.indexOf('\'') >= 0)) {
            return false;
        } else if ((s.indexOf("&lt;") >= 0) || (s.indexOf("&gt;") >= 0) || (s.indexOf("&quot;") >= 0) || 
                    (s.indexOf("&#x2f;") >= 0) || (s.indexOf("&#39;") >= 0) || (s.indexOf("&amp;") >= 0)) {
            return true;
        }
        return false;
    }
    
    private void fixSanitization(String s) {
        if (isSanitizedString(s)) {
            s = StringHelper.recoverFromSanitizedText(s);
        }
    }
    
    private void fixSanitizedDataForStudent(StudentAttributes student) {
        fixSanitization(student.comments);
        fixSanitization(student.course);
        fixSanitization(student.email);
        fixSanitization(student.googleId);
        fixSanitization(student.key);
        fixSanitization(student.lastName);
        fixSanitization(student.name);
        fixSanitization(student.section);
        fixSanitization(student.team);
        
        try {
            StudentsLogic.inst().updateStudentCascade(student.email, student);
            
        } catch (InvalidParametersException e) {
            Utils.getLogger().log(Level.INFO, "Student " + student.email + " invalid!");
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            Utils.getLogger().log(Level.INFO, "Student " + student.email + " does not exist!");
            e.printStackTrace();
        }
        
        getPM().close();
    }

    protected PersistenceManager getPM() {
        return Datastore.getPersistenceManager();
    }

    @SuppressWarnings("deprecation")
    protected List<StudentAttributes> getAllStudents() {
        StudentsLogic studentsLogic = StudentsLogic.inst();
        return studentsLogic.getAllStudents();
    }
}
