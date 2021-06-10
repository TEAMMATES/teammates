package teammates.logic.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Email sender service which will not send any email.
 */
public class EmptyEmailService implements EmailSenderService {

    private static final Logger log = Logger.getLogger();

    @Override
    public EmailWrapper parseToEmail(EmailWrapper wrapper) {
        return wrapper;
    }

    @Override
    public EmailSendingStatus sendEmail(EmailWrapper wrapper) {
        List<String> messages = Arrays.asList(
                "Sending email:",
                String.format("From: %s <%s>", wrapper.getSenderName(), wrapper.getSenderEmail()),
                String.format("To: %s", wrapper.getRecipient()),
                String.format("Reply-to: %s", wrapper.getReplyTo()),
                String.format("Subject: %s", wrapper.getSubject()),
                String.format("Data length: %s", wrapper.getContent().length())
        );
        log.info(messages.stream().collect(Collectors.joining(System.lineSeparator())));
        return new EmailSendingStatus(HttpStatus.SC_OK, null);
    }

}
