package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD Operations for accounts.
 * The API uses data transfer classes (i.e. *Attributes) instead of persistable classes.
 * 
 */
public class AccountsDb extends EntitiesDb {

    private static final Logger log = Utils.getLogger();
    
    /**
     * Preconditions: 
     * <br> * {@code accountToAdd} is not null and has valid data.
     */
    public void createAccount(AccountAttributes accountToAdd) 
            throws InvalidParametersException {
        // TODO: use createEntity once there is a proper way to add instructor accounts.
        try {
            // this is for legacy code to be handled
            if (accountToAdd != null && accountToAdd.studentProfile == null) {
                accountToAdd.studentProfile = new StudentProfileAttributes();
                accountToAdd.studentProfile.googleId = accountToAdd.googleId;
            }
            createEntity(accountToAdd);
        } catch (EntityAlreadyExistsException e) {
            // We update the account instead if it already exists. This is due to how
            // adding of instructor accounts work.
            try {
                updateAccount(accountToAdd, true);
            } catch (EntityDoesNotExistException edne) {
                // This situation is not tested as replicating such a situation is 
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }
    
    /**
     * Preconditions: 
     * <br> * All parameters are non-null. 
     * @return Null if not found.
     */
    public AccountAttributes getAccount(String googleId, boolean retrieveStudentProfile) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        
        Account a = getAccountEntity(googleId, retrieveStudentProfile);
    
        if (a == null) {
            return null;
        }
        closePM();
        
        AccountAttributes accAttr = new AccountAttributes(a);
        return accAttr;
    }
    
    public AccountAttributes getAccount(String googleId) {
        return getAccount(googleId, false);
    }

    /**
     * @return {@link AccountAttribute} objects for all accounts with instructor privileges.
     *   Returns an empty list if no such accounts are found.
     */
    public List<AccountAttributes> getInstructorAccounts() {
        Query q = getPM().newQuery(Account.class);
        q.setFilter("isInstructor == true");
        
        @SuppressWarnings("unchecked")
        List<Account> accountsList = (List<Account>) q.execute();
        
        List<AccountAttributes> instructorsAccountData = new ArrayList<AccountAttributes>();
                
        for (Account a : accountsList) {
            instructorsAccountData.add(new AccountAttributes(a));
        }
        
        return instructorsAccountData;
    }

    /**
     * Preconditions: 
     * <br> * {@code accountToAdd} is not null and has valid data.
     */
    public void updateAccount(AccountAttributes a, boolean updateStudentProfile) 
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, a);
        
        if (!a.isValid()) {
            throw new InvalidParametersException(a.getInvalidityInfo());
        }
        
        Account accountToUpdate = getAccountEntity(a.googleId, updateStudentProfile);

        if (accountToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + a.googleId
                + ThreadHelper.getCurrentThreadStack());
        }
        
        a.sanitizeForSaving();
        accountToUpdate.setName(a.name);
        accountToUpdate.setEmail(a.email);
        accountToUpdate.setIsInstructor(a.isInstructor);
        accountToUpdate.setInstitute(a.institute);
        
        // if the student profile has changed then update the store
        // this is to maintain integrity of the modified date.
        if (updateStudentProfile) {
            String existingProfile = new StudentProfileAttributes(accountToUpdate.getStudentProfile()).toString();
            if(!(existingProfile.equals(a.studentProfile.toString()))) {
                accountToUpdate.setStudentProfile((StudentProfile) a.studentProfile.toEntity());
            }
        }
        
        closePM();
    }
    
    public void updateAccount(AccountAttributes a) 
            throws InvalidParametersException, EntityDoesNotExistException {
        if (a != null && a.studentProfile == null) {
            a.studentProfile = new StudentProfileAttributes();
            a.studentProfile.googleId = a.googleId;
        }
        updateAccount(a, false);
    }

    /**
     * Note: This is a non-cascade delete. <br>
     *   <br> Fails silently if there is no such account.
     * <br> Preconditions: 
     * <br> * {@code googleId} is not null.
     */
    public void deleteAccount(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        
        AccountAttributes accountToDelete = getAccount(googleId, true);

        if (accountToDelete == null) {
            return;
        }
        
        if (!accountToDelete.studentProfile.pictureKey.equals("")) {
            deleteProfilePicFromGcs(new BlobKey(accountToDelete.studentProfile.pictureKey));
        }
        deleteEntity(accountToDelete);
        closePM();
    }

    private Account getAccountEntity(String googleId, boolean retrieveStudentProfile) {
        
        Key key = KeyFactory.createKey(Account.class.getSimpleName(), googleId);
        try {
            Account account = getPM().getObjectById(Account.class, key);
            
            if (JDOHelper.isDeleted(account)) {
                return null;
            } else if (retrieveStudentProfile) {
                if (account.getStudentProfile() == null) {
                    // This situation cannot be reproduced and hence not tested
                    // This only happens when existing data in the store do not have a profile 
                    account.setStudentProfile(new StudentProfile(account.getGoogleId()));
                }
            }
            
            return account;
        } catch(JDOObjectNotFoundException je) {
            return null;
        }
    }
    
    private Account getAccountEntity(String googleId) {
        
        return getAccountEntity(googleId, false);
    }
    
    private StudentProfile getStudentProfileEntity(String googleId) {
        Key childKey = KeyFactory.createKey(Account.class.getSimpleName(), googleId)
                .getChild(StudentProfile.class.getSimpleName(), googleId);
        
        try {
            return getPM().getObjectById(StudentProfile.class, childKey);
        } catch (JDOObjectNotFoundException je) {
            
            Account a = getAccountEntity(googleId, true);
            if (a == null) {
                return null;
            } else {
                // This situation cannot be reproduced and hence not tested
                // This only happens when existing data in the store do not have a profile
                return a.getStudentProfile();
            }
        }
                
    }
    
    public StudentProfileAttributes getStudentProfile(String accountGoogleId) {        
        StudentProfile sp = getStudentProfileEntity(accountGoogleId);
        
        if (sp == null || JDOHelper.isDeleted(sp)) {
            return null;
        }

        if (sp.getPictureKey() == null) {
            // This situation cannot be reproduced and hence not tested
            // This only happens when existing data in the store do not have a picture
            sp.setPictureKey(new BlobKey(""));
        }
        
        return new StudentProfileAttributes(sp);
    }
    
    public void updateStudentProfile(StudentProfileAttributes newSpa) 
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newSpa);
        
        // TODO: update the profile with the valid values regardless of the validity of 
        //       the entire profile attributes entity
        
        if (!newSpa.isValid()) {
            throw new InvalidParametersException(newSpa.getInvalidityInfo());
        }
        
        StudentProfile profileToUpdate = getStudentProfileEntity(newSpa.googleId);
        
        if (profileToUpdate == null || JDOHelper.isDeleted(profileToUpdate)) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE + newSpa.googleId
                    + ThreadHelper.getCurrentThreadStack());
        }

        newSpa.sanitizeForSaving();
        profileToUpdate.setShortName(newSpa.shortName);
        profileToUpdate.setEmail(newSpa.email);
        profileToUpdate.setInstitute(newSpa.institute);
        profileToUpdate.setCountry(newSpa.country);
        profileToUpdate.setGender(newSpa.gender);
        profileToUpdate.setMoreInfo(new Text(newSpa.moreInfo));
        if (newSpa.pictureKey != "") {
            if (!profileToUpdate.getPictureKey().equals(new BlobKey(""))) {
                try {
                    deleteProfilePicFromGcs(profileToUpdate.getPictureKey());
                } catch (BlobstoreFailureException bfe) {
                    // this branch is not tested as it is 
                    //      => difficult to reproduce during testing
                    //      => properly handled higher up
                    closePM();
                    throw bfe;
                }
            }
            profileToUpdate.setPictureKey(new BlobKey(newSpa.pictureKey));
        }
        closePM();
    }
    
    public void deleteProfilePicFromGcs(BlobKey key) throws BlobstoreFailureException {
        BlobstoreServiceFactory.getBlobstoreService().delete(key);
    }
    
    private void closePM() {
        if (!getPM().isClosed()) {
            getPM().close();
        }
    }

    @Override
    protected Object getEntity(EntityAttributes entity) {
        return getAccountEntity(((AccountAttributes)entity).googleId);
    }
}

