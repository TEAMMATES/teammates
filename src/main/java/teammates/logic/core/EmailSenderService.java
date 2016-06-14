package teammates.logic.core;

import teammates.common.util.EmailWrapper;

public abstract class EmailSenderService {
    
    protected abstract EmailWrapper parseToEmailWrapper(Object email);
    
    protected abstract Object parseToEmail(EmailWrapper wrapper);
    
    public abstract void sendEmail(EmailWrapper message);
    
}
