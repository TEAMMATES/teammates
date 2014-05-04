package teammates.common.datatransfer;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;

/**
 *  Represents detailed results of one student for one evaluation. 
 *  <br> Contains: 
 *  <br> *  The student details (as a {@link StudentAttributes} object)
 *  <br> *  The submissions given and received by the student (as {@link SubmissionAttributes} objects),
 *  <br> *  Contribution ratings for the student (as a {@link StudentResultSummary} object). 
 */
public class StudentResultBundle implements SessionResultsBundle{

    public StudentAttributes student;
    
    public ArrayList<SubmissionAttributes> incoming = new ArrayList<SubmissionAttributes>();
    public ArrayList<SubmissionAttributes> outgoing = new ArrayList<SubmissionAttributes>();
    public ArrayList<SubmissionAttributes> selfEvaluations = new ArrayList<SubmissionAttributes>();

    public StudentResultSummary summary;

    @SuppressWarnings("unused")
    private static final Logger log = Utils.getLogger();
    
    public StudentResultBundle(StudentAttributes student){
        this.student = student;
        this.summary = new StudentResultSummary();
    }
    
    //TODO: unit test this
    /** returns the self-evaluation selected from outgoing submissions */
    public SubmissionAttributes getSelfEvaluation() {
        for (SubmissionAttributes s : outgoing) {
            if (s.reviewee.equals(s.reviewer)) {
                return s;
            }
        }
        return null;
    }

    //TODO: unit test these sort methods
    public void sortOutgoingByStudentNameAscending() {
        Collections.sort(outgoing, new Comparator<SubmissionAttributes>() {
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
                // email is appended to avoid mix ups due to two students with
                // same name.
                return (s1.details.revieweeName + s1.reviewee)
                        .compareTo(s2.details.revieweeName + s2.reviewee);
            }
        });
    }

    public void sortIncomingByStudentNameAscending() {
        Collections.sort(incoming, new Comparator<SubmissionAttributes>() {
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
                // email is appended to avoid mix ups due to two students with
                // same name.
                return (s1.details.reviewerName + s1.reviewer)
                        .compareTo(s2.details.reviewerName + s2.reviewer);
            }
        });
    }

    public void sortIncomingByFeedbackAscending() {
        Collections.sort(incoming, new Comparator<SubmissionAttributes>() {
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
                return s1.p2pFeedback.getValue().compareTo(
                        s2.p2pFeedback.getValue());
            }
        });
    }

    public String getOwnerEmail() {
        for (SubmissionAttributes sb : outgoing) {
            if (sb.reviewee.equals(sb.reviewer)) {
                return sb.reviewer;
            }
        }
        return null;
    }
    
    public String toString(){
        return toString(0);
    }

    //TODO: unit test this
    public String toString(int indent) {
        String indentString = StringHelper.getIndent(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString + "claimedFromStudent:" + summary.claimedFromStudent
                + EOL);
        sb.append(indentString + "claimedToInstructor:" + summary.claimedToInstructor + EOL);
        sb.append(indentString + "perceivedToStudent:" + summary.perceivedToStudent
                + EOL);
        sb.append(indentString + "perceivedToInstructor:" + summary.perceivedToInstructor + EOL);

        sb.append(indentString + "outgoing:" + EOL);
        for (SubmissionAttributes submission : outgoing) {
            sb.append(submission.toString(indent + 2) + EOL);
        }

        sb.append(indentString + "incoming:" + EOL);
        for (SubmissionAttributes submission : incoming) {
            sb.append(submission.toString(indent + 2) + EOL);
        }
        
        sb.append(indentString + "self evaluations:" + EOL);
        for (SubmissionAttributes submission : selfEvaluations) {
            sb.append(submission.toString(indent + 2) + EOL);
        }
        
        return replaceMagicNumbers(sb.toString());
    }
    
    private String replaceMagicNumbers(String input){
        return input.replace(Const.INT_UNINITIALIZED + ".0", " NA")
                .replace(Const.INT_UNINITIALIZED + "", " NA")
                .replace(Const.POINTS_NOT_SUBMITTED + "", "NSB")
                .replace(Const.POINTS_NOT_SURE + "", "NSU");
    }

}
