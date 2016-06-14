package teammates.logic.core;

import teammates.common.util.EmailWrapper;

public abstract class EmailSenderService {
    
    protected abstract Object parseToEmail(EmailWrapper wrapper) throws Exception;
    
    protected abstract void sendEmail(EmailWrapper message) throws Exception;
    
}
