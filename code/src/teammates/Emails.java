package teammates;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Email handles all operations with regards to sending e-mails.
 * 
 * @author Gerald GOH
 * 
 */
public class Emails {
        private String                  from;
        private Properties              props;
        
        private final String    HEADER_REGISTRATION_INVITATION  = "TEAMMATES: Registration Invitation: Register in the course %s";
        private final String    HEADER_REGISTRATION_REMINDER    = "TEAMMATES: Registration Reminder: Register in the course %s";
        private final String    HEADER_EVALUATION_OPEN                  = "TEAMMATES: Evaluation Opening: %s %s";
        private final String    HEADER_EVALUATION_CHANGE                = "TEAMMATES: Evaluation Changed: %s %s";
        private final String    HEADER_EVALUATION_REMINDER              = "TEAMMATES: Evaluation Reminder: %s %s";
        private final String    HEADER_EVALUATION_PUBLISH               = "TEAMMATES: Evaluation Published: %s %s";
        private final String    TEAMMATES_APP_SIGNATURE                 =  "\n\nIf you encounter any problems using the system, email TEAMMATES support team at teammates@comp.nus.edu.sg"      
                                                                                                                                +"\n\nRegards, \nTEAMMATES System";
        
        /**
         * Constructs an Email object. Sets the sender's e-mail address and
         * instantiate a new Properties object.
         * 
         */
        public Emails() {
                from = Config.TEAMMATES_APP_ACCOUNT;
                props = new Properties();
        }
        
        /**
         * Sends an email to a Student informing him of new Evaluation details.
         * 
         * @param email the email of the student (Precondition: Must not be null)
         * 
         * @param studentName the name of the student (Precondition: Must not be
         *        null)
         * 
         * @param courseID the course ID (Precondition: Must not be null)
         * 
         * @param evaluationName the evaluation name (Precondition: Must not be
         *        null)
         * 
         * @param instructions the evaluation instructions (Precondition: Must not
         *        be null)
         * 
         * @param deadline the evaluation deadline (Precondition: Must not be null)
         */
        public void informStudentsOfEvaluationChanges(String email,
                        String studentName, String courseID, String evaluationName,
                        String instructions, String start, String deadline) {
                try {
                        Session session = Session.getDefaultInstance(props, null);
                        MimeMessage message = new MimeMessage(session);
                        
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                                        email));
                        
                        message.setFrom(new InternetAddress(from));
                        message.setSubject(String.format(HEADER_EVALUATION_CHANGE,
                                        courseID, evaluationName));
                        message.setText("Dear "
                                        + studentName
                                        + ",\n\n"
                                        + "There are changes to the evaluation: \n\n"
                                        + courseID
                                        + " "
                                        + evaluationName
                                        + "\n\n"
                                        + "made by your coordinator. The start, deadline and instructions of the evaluation are as follow, \n\n"
                                        + "Start: " + start + "H. \n\n" + "Deadline: " + deadline
                                        + "H. \n\n" + "Instructions : " + instructions
                                        + "\n You can access the evaluation here: "
                                        + Config.TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);
                        
                        Transport.send(message);
                        
                }
                
                catch(MessagingException e) {
                        
                }
                
        }
        
        /**
         * Sends an email to a Student informing him of the opening of an
         * evaluation.
         * 
         * @param email the email of the student (Precondition: Must not be null)
         * 
         * @param studentName the name of the student (Precondition: Must not be
         *        null)
         * 
         * @param courseID the course ID (Precondition: Must not be null)
         * 
         * @param evaluationName the evaluation name (Precondition: Must not be
         *        null)
         */
        public void informStudentsOfEvaluationOpening(String email,
                        String studentName, String courseID, String evaluationName) {
                try {
                        Session session = Session.getDefaultInstance(props, null);
                        MimeMessage message = new MimeMessage(session);
                        
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                                        email));
                        
                        message.setFrom(new InternetAddress(from));
                        message.setSubject(String.format(HEADER_EVALUATION_OPEN, courseID,
                                        evaluationName));
                        message.setText("Dear " + studentName + ",\n\n"
                                        + "The following evaluation: \n\n" + courseID + " "
                                        + evaluationName + "\n\n" + "is now open.\n"
                                        + "You can access the evaluation here: "
                                        + Config.TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);
                        
                        Transport.send(message);
                        
                }
                
                catch(MessagingException e) {
                        
                }
                
        }
        
        /**
         * Sends an email to a Student informing him of the publishing of results
         * for a particular evaluation.
         * 
         * @param email the email of the student (Precondition: Must not be null)
         * 
         * @param studentName the name of the student (Precondition: Must not be
         *        null)
         * 
         * @param courseID the course ID (Precondition: Must not be null)
         * 
         * @param evaluationName the evaluation name (Precondition: Must not be
         *        null)
         */
        public void informStudentsOfPublishedEvaluation(String email,
                        String studentName, String courseID, String evaluationName) {
                try {
                        Session session = Session.getDefaultInstance(props, null);
                        MimeMessage message = new MimeMessage(session);
                        
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                                        email));
                        
                        message.setFrom(new InternetAddress(from));
                        message.setSubject(String.format(HEADER_EVALUATION_PUBLISH,
                                        courseID, evaluationName));
                        message.setText("Dear " + studentName + ",\n\n"
                                        + "The results of the evaluation: \n\n" + courseID + " "
                                        + evaluationName + "\n\n" + "have been published.\n"
                                        + "You can view the result here: "
                                        + Config.TEAMMATES_APP_URL + TEAMMATES_APP_SIGNATURE);
                        
                        Transport.send(message);
                        
                }
                
                catch(MessagingException e) {
                        
                }
                
        }
        
        /**
         * Sends an email reminding the Student of the Evaluation deadline.
         * 
         * @param email the email of the student (Precondition: Must not be null)
         * 
         * @param studentName the name of the student (Precondition: Must not be
         *        null)
         * 
         * @param courseID the course ID (Precondition: Must not be null)
         * 
         * @param evaluationName the evaluation name (Precondition: Must not be
         *        null)
         * 
         * @param deadline the evaluation deadline (Precondition: Must not be null)
         */
        public void remindStudent(String email, String studentName,
                        String courseID, String evaluationName, String deadline) {
                try {
                        Session session = Session.getDefaultInstance(props, null);
                        MimeMessage message = new MimeMessage(session);
                        
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                                        email));
                        
                        message.setFrom(new InternetAddress(from));
                        message.setSubject(String.format(HEADER_EVALUATION_REMINDER, courseID, evaluationName));
                        message.setText("Dear " + studentName + ",\n\n"
                                        + "You are reminded to submit the evaluation: \n\n"
                                        + courseID + " " + evaluationName + "\n\n" + "by "
                                        + deadline + "H.\n" + "You can access the evaluation here: " + Config.TEAMMATES_APP_URL
                                        + TEAMMATES_APP_SIGNATURE);
                        
                        Transport.send(message);
                        
                }
                
                catch(MessagingException e) {
                        System.out.println("remindStudent: fail to send message");
                }
        }
        
        /**
         * Sends a registration key to an e-mail address.
         * 
         * Pre-conditions: email, registrationKey, studentName, courseID, courseName
         * and coordinatorName must not be null. Post-condition: The specified
         * registrationKey is sent to the specified email.
         * 
         * Subject line: [Coordinator name] sent you an invitation to register in
         * Teammates System.
         * 
         * Dear [Name], The course [course name] will be using Teammates
         * Peer-Evaluation System for peer-evaluations. [Coordinator name] has
         * invited you to use the system to evaluate your team members. These are
         * the steps to follow. Login to the system: Go to URL {provide the correct
         * url here} Login as a ‘Student’ using your Google ID. If you do not have a
         * Google ID, please create one. Join the course: Enter this key : Key
         * 
         * 
         * Now, [course] should appear in the course list and the names of your
         * teammates will appear when you click the ‘view’ link corresponding to the
         * course. Submit pending evaluations: Click ‘Evaluations’ button at the top
         * to check if there are any pending peer-evaluations you have to submit.
         * 
         * Please inform [coordinator email] if your encounter any problems or if
         * your team details are not correct.
         * 
         * @param email
         * @param registrationKey
         */
        public void sendRegistrationKey(String email, String registrationKey,
                        String studentName, String courseID, String courseName,
                        String coordinatorName, String coordinatorEmail) {
                try {
                        Session session = Session.getDefaultInstance(props, null);
                        MimeMessage message = new MimeMessage(session);
                        
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                                        email));
                        
                        message.setFrom(new InternetAddress(from));
                        message.setSubject(String.format(HEADER_REGISTRATION_INVITATION, courseID));
                        message.setText("Dear "
                                        + studentName
                                        + ",\n\n"
                                        + "The course "
                                        + courseName
                                        + " will be using Teammates Peer-Evaluation System for peer-evaluations. "
                                        + coordinatorName
                                        + " has invited you to use the system to evaluate your team members. "
                                        + "These are the steps to follow.\n\n"
                                        + "Login to the system:\n"
                                        + "* Go to URL "
                                        + Config.TEAMMATES_APP_URL
                                        + "\n"
                                        + "* Login as \"Student\" using your Google ID. If you do not have a Google ID, please create one.\n\n"
                                        + "Join the course: \n"
                                        + "* Enter this key to join "+ courseID + ": "
                                        + registrationKey
                                        + "\n"
                                        + "* Now, "
                                        + courseID
                                        + " should appear in the course list and you can see names of your teammates by clicking "
                                        + "the \"View\" link corresponding to the course. \n\n"
                                        + "Submit pending evaluations:\n"
                                        + "* Click \"Evaluations\" button at the top to check if there are any pending peer-evaluations.\n\n"
                                        + "In case of problems:\n"
                                        + "If team details are not correct, please contact the coordinator of "+ courseID +".\n"                                        
                                        + TEAMMATES_APP_SIGNATURE);
                        
                        Transport.send(message);
                }
                
                catch(MessagingException e) {
                        System.out.println("sendRegistrationKey: fail to send email.");
                }
        }
        
        /**
         * Stress testing of mail account
         * 
         * @param email
         * @param size
         * @author wangsha
         */
        public void mailStressTesting(String email, int size) {
                try {
                        Session session = Session.getDefaultInstance(props, null);
                        
                        for(int i = 0; i < size; i ++ ) {
                                MimeMessage message = new MimeMessage(session);
                                
                                message.addRecipient(Message.RecipientType.TO,
                                                new InternetAddress(email));
                                
                                message.setFrom(new InternetAddress(from));
                                message.setSubject("Teammates Mail Stree Testing [" + i + "|"
                                                + size + "]");
                                message.setText("This is a testing email");
                                
                                Transport.send(message);
                                System.out.println("send email " + i + "|" + size);
                        }
                        
                }
                
                catch(MessagingException e) {
                        
                }
        }
}