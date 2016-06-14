package teammates.logic.core;

import teammates.common.util.EmailWrapper;

public interface EmailSenderService {
    
    Object parseToEmail(EmailWrapper wrapper) throws Exception;
    
    void sendEmail(EmailWrapper message) throws Exception;
    
}
