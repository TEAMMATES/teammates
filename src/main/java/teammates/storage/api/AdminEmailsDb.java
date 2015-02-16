package teammates.storage.api;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.AdminEmail;
import teammates.storage.entity.FeedbackQuestion;

public class AdminEmailsDb extends EntitiesDb {
    
    @SuppressWarnings("unused")
    private static final Logger log = Utils.getLogger();
    
    public void creatAdminEmail(AdminEmailAttributes adminEmailToAdd) throws InvalidParametersException{
        try {
            createEntity(adminEmailToAdd);
        } catch (EntityAlreadyExistsException e) {
            try {
                updateAdminEmail(adminEmailToAdd);
            } catch (EntityDoesNotExistException e1) {
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }
    
    public void updateAdminEmail(AdminEmailAttributes ae) throws InvalidParametersException, EntityDoesNotExistException{
        if(!ae.isValid()){
            throw new InvalidParametersException(ae.getInvalidityInfo());
        }
        
        AdminEmail adminEmailToUpdate = getAdminEmailEntity(ae.emailId);
        
        if(adminEmailToUpdate == null){
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + ae.getSubject() +
                                                  "/" + ae.getSendDate() +
                                                  ThreadHelper.getCurrentThreadStack());
        }
        
        ae.sanitizeForSaving();
        
        adminEmailToUpdate.setContent(ae.content);
        adminEmailToUpdate.setAddressReceiver(ae.addressReceiver);
        adminEmailToUpdate.setGroupReceiver(ae.groupReceiver);
        adminEmailToUpdate.setSubject(ae.subject);
        
        log.info(ae.getBackupIdentifier());
        closePM();
        
    }
    
    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all adminEmails in the database. 
     */
    @Deprecated
    public List<AdminEmailAttributes> getAllAdminEmails(){
        List<AdminEmailAttributes> list = new LinkedList<AdminEmailAttributes>();
        List<AdminEmail> entities = getAdminEmailEntities();
        Iterator<AdminEmail> it = entities.iterator();
        while(it.hasNext()){
            list.add(new AdminEmailAttributes(it.next()));
        }
        
        return list;
    }
    
    /**
     * get an admin email by email id
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailById(String emailId){
        
        AdminEmail matched = getAdminEmailEntity(emailId);
        
        if(matched == null){
            return null;
        }
        
        return new AdminEmailAttributes(matched);
    }
    
    private List<AdminEmail> getAdminEmailEntities(){
        Query q = getPM().newQuery(AdminEmail.class);
        
        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>)q.execute();
        
        return adminEmailList;
    }
    
    private AdminEmail getAdminEmailEntity(String adminEmailId){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, adminEmailId);
        
        Query q = getPM().newQuery(AdminEmail.class);
        q.declareParameters("String adminEmailIdParam");
        q.setFilter("emailId == adminEmailIdParam");
        
        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(adminEmailId);
       
        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return null;
        }
        return adminEmailList.get(0);
    }
    
    private AdminEmail getAdminEmailEntity(String subject, Date sendDate){
        
        
        Query q = getPM().newQuery(AdminEmail.class);
        q.declareParameters("String subjectParam, java.util.Date sendDateParam");
        q.setFilter("subject == subjectParam && " +
                    "sendDate == sendDateParam");
        
        @SuppressWarnings("unchecked")
        List<AdminEmail> adminEmailList = (List<AdminEmail>) q.execute(subject, sendDate);
       
        if (adminEmailList.isEmpty() || JDOHelper.isDeleted(adminEmailList.get(0))) {
            return null;
        }
        return adminEmailList.get(0);
    }
    
 
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        AdminEmailAttributes adminEmailToGet = (AdminEmailAttributes) attributes;
        
        if(adminEmailToGet.getEmailId() != null){
            return getAdminEmailEntity(adminEmailToGet.getEmailId());
        } else {
            return getAdminEmailEntity(adminEmailToGet.getSubject(),
                                       adminEmailToGet.getSendDate());
        }
        
    }

    
}
