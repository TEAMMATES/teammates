package teammates.common.exception;

@SuppressWarnings("serial")
public class EmailSendingException extends TeammatesException {

    public EmailSendingException(Exception e) {
        super(e.getMessage());
    }

}
