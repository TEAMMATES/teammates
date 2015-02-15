package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.AdminEmailsDb;

/**
 * Handles the logic related to admin emails
 *
 */

public class AdminEmailsLogic {
    private static AdminEmailsLogic instance = null;
    private static final AdminEmailsDb adminEmailsDb = new AdminEmailsDb();
    
    private static Logger log = Utils.getLogger();
    
    public static AdminEmailsLogic inst() {
        if (instance == null)
            instance = new AdminEmailsLogic();
        return instance;
    }
    
    
    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all adminEmails in the database. 
     */
    @Deprecated
    public List<AdminEmailAttributes> getAllAdminEmails(){
        return adminEmailsDb.getAllAdminEmails();
    }
    
    public void createAdminEmail(AdminEmailAttributes newAdminEmail) throws InvalidParametersException{
        adminEmailsDb.creatAdminEmail(newAdminEmail);
    }
}
