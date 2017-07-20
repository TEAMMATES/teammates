package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes}
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle {

    private static final Logger log = Logger.getLogger();

    public FeedbackSessionAttributes feedbackSession;
    public List<FeedbackResponseAttributes> responses;
    public Map<String, FeedbackQuestionAttributes> questions;
    public Map<String, String> emailNameTable;
    public Map<String, String> emailLastNameTable;
    public Map<String, String> emailTeamNameTable;
    public Map<String, String> instructorEmailNameTable;
    public Map<String, Set<String>> rosterTeamNameMembersTable;
    public Map<String, Set<String>> rosterSectionTeamNameTable;
    public Map<String, boolean[]> visibilityTable;
    public FeedbackSessionResponseStatus responseStatus;
    public CourseRoster roster;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseComments;
    public boolean isComplete;

    /**
     * Responses with identities of giver/recipients NOT hidden.
     * To be used for anonymous result calculation only, and identities hidden before showing to users.
     */
    public List<FeedbackResponseAttributes> actualResponses;

    // For contribution questions.
    // Key is questionId, value is a map of student email to StudentResultSumary
    public Map<String, Map<String, StudentResultSummary>> contributionQuestionStudentResultSummary = new HashMap<>();
    // Key is questionId, value is a map of team name to TeamEvalResult
    public Map<String, Map<String, TeamEvalResult>> contributionQuestionTeamEvalResults = new HashMap<>();

    /*
     * sectionTeamNameTable takes into account the section viewing privileges of the logged-in instructor
     * and the selected section for viewing
     * whereas rosterSectionTeamNameTable doesn't.
     * As a result, sectionTeamNameTable only contains sections viewable to the logged-in instructor
     * whereas rosterSectionTeamNameTable contains all sections in the course.
     * As sectionTeamNameTable is dependent on instructor privileges,
     * it can only be used for instructor pages and not for student pages
    */
    public Map<String, Set<String>> sectionTeamNameTable;

    // Sorts by giverName > recipientName > qnNumber
    // General questions and team questions at the bottom.
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipientQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by giverName > recipientName
    private Comparator<FeedbackResponseAttributes> compareByGiverRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            int order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by teamName > giverName > recipientName > qnNumber
    private Comparator<FeedbackResponseAttributes> compareByTeamGiverRecipientQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String t1 = getTeamNameForEmail(o1.giver).isEmpty() ? getNameForEmail(o1.giver)
                                                                : getTeamNameForEmail(o1.giver);
            String t2 = getTeamNameForEmail(o2.giver).isEmpty() ? getNameForEmail(o2.giver)
                                                                : getTeamNameForEmail(o2.giver);
            order = compareByNames(t1, t2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by recipientName > giverName > qnNumber
    private Comparator<FeedbackResponseAttributes> compareByRecipientGiverQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by teamName > recipientName > giverName > qnNumber
    private Comparator<FeedbackResponseAttributes> compareByTeamRecipientGiverQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String t1 = getTeamNameForEmail(o1.recipient).isEmpty() ? getNameForEmail(o1.recipient)
                                                                    : getTeamNameForEmail(o1.recipient);
            String t2 = getTeamNameForEmail(o2.recipient).isEmpty() ? getNameForEmail(o2.recipient)
                                                                    : getTeamNameForEmail(o2.recipient);
            order = compareByNames(t1, t2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by giverName > question > recipientTeam > recipientName
    private Comparator<FeedbackResponseAttributes> compareByGiverQuestionTeamRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String t1 = getTeamNameForEmail(o1.recipient).isEmpty() ? getNameForEmail(o1.recipient)
                                                                    : getTeamNameForEmail(o1.recipient);
            String t2 = getTeamNameForEmail(o2.recipient).isEmpty() ? getNameForEmail(o2.recipient)
                                                                    : getTeamNameForEmail(o2.recipient);
            order = compareByNames(t1, t2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);

            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by giverTeam > giverName > question > recipientTeam > recipientName
    private Comparator<FeedbackResponseAttributes> compareByTeamGiverQuestionTeamRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverTeam1 = getTeamNameForEmail(o1.giver).isEmpty() ? getNameForEmail(o1.giver)
                                                                        : getTeamNameForEmail(o1.giver);
            String giverTeam2 = getTeamNameForEmail(o2.giver).isEmpty() ? getNameForEmail(o2.giver)
                                                                        : getTeamNameForEmail(o2.giver);
            order = compareByNames(giverTeam1, giverTeam2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String receiverTeam1 = getTeamNameForEmail(o1.recipient).isEmpty() ? getNameForEmail(o1.recipient)
                                                                               : getTeamNameForEmail(o1.recipient);
            String receiverTeam2 = getTeamNameForEmail(o2.recipient).isEmpty() ? getNameForEmail(o2.recipient)
                                                                               : getTeamNameForEmail(o2.recipient);
            order = compareByNames(receiverTeam1, receiverTeam2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);

            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by recipientName > question > giverTeam > giverName
    private final Comparator<FeedbackResponseAttributes> compareByRecipientQuestionTeamGiver =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String t1 = getTeamNameForEmail(o1.giver).isEmpty() ? getNameForEmail(o1.giver)
                                                                : getTeamNameForEmail(o1.giver);
            String t2 = getTeamNameForEmail(o2.giver).isEmpty() ? getNameForEmail(o2.giver)
                                                                : getTeamNameForEmail(o2.giver);
            order = compareByNames(t1, t2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());

        }
    };

    // Sorts by recipientTeam > recipientName > question > giverTeam > giverName
    private Comparator<FeedbackResponseAttributes> compareByTeamRecipientQuestionTeamGiver =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {

            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);
            String recipientTeam1 = getTeamNameForEmail(o1.recipient).isEmpty() ? getNameForEmail(o1.recipient)
                                                                                : getTeamNameForEmail(o1.recipient);
            String recipientTeam2 = getTeamNameForEmail(o2.recipient).isEmpty() ? getNameForEmail(o2.recipient)
                                                                                : getTeamNameForEmail(o2.recipient);
            order = compareByNames(recipientTeam1, recipientTeam2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverTeam1 = getTeamNameForEmail(o1.giver).isEmpty() ? getNameForEmail(o1.giver)
                                                                        : getTeamNameForEmail(o1.giver);
            String giverTeam2 = getTeamNameForEmail(o2.giver).isEmpty() ? getNameForEmail(o2.giver)
                                                                        : getTeamNameForEmail(o2.giver);
            order = compareByNames(giverTeam1, giverTeam2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }
            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by recipientTeam > question > recipientName > giverTeam > giverName
    private Comparator<FeedbackResponseAttributes> compareByTeamQuestionRecipientTeamGiver =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);
            String recipientTeam1 = getTeamNameForEmail(o1.recipient).isEmpty() ? getNameForEmail(o1.recipient)
                                                                                : getTeamNameForEmail(o1.recipient);
            String recipientTeam2 = getTeamNameForEmail(o2.recipient).isEmpty() ? getNameForEmail(o2.recipient)
                                                                                : getTeamNameForEmail(o2.recipient);
            int order = compareByNames(recipientTeam1, recipientTeam2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            String giverTeam1 = getTeamNameForEmail(o1.giver).isEmpty() ? getNameForEmail(o1.giver)
                                                                        : getTeamNameForEmail(o1.giver);
            String giverTeam2 = getTeamNameForEmail(o2.giver).isEmpty() ? getNameForEmail(o2.giver)
                                                                        : getTeamNameForEmail(o2.giver);
            order = compareByNames(giverTeam1, giverTeam2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);
            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by giverTeam > question > giverName > recipientTeam > recipientName
    private Comparator<FeedbackResponseAttributes> compareByTeamQuestionGiverTeamRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);

            String giverTeam1 = getTeamNameForEmail(o1.giver).isEmpty() ? getNameForEmail(o1.giver)
                                                                        : getTeamNameForEmail(o1.giver);
            String giverTeam2 = getTeamNameForEmail(o2.giver).isEmpty() ? getNameForEmail(o2.giver)
                                                                        : getTeamNameForEmail(o2.giver);
            int order = compareByNames(giverTeam1, giverTeam2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giver);
            String giverName2 = emailNameTable.get(o2.giver);
            order = compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
            if (order != 0) {
                return order;
            }

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);

            String receiverTeam1 = getTeamNameForEmail(o1.recipient).isEmpty() ? getNameForEmail(o1.recipient)
                                                                               : getTeamNameForEmail(o1.recipient);
            String receiverTeam2 = getTeamNameForEmail(o2.recipient).isEmpty() ? getNameForEmail(o2.recipient)
                                                                               : getTeamNameForEmail(o2.recipient);
            order = compareByNames(receiverTeam1, receiverTeam2, isRecipientVisible1, isRecipientVisible2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipient);
            String recipientName2 = emailNameTable.get(o2.recipient);
            order = compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);

            if (order != 0) {
                return order;
            }

            order = compareByResponseString(o1, o2);
            if (order != 0) {
                return order;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    // Sorts by recipientName > recipientEmail > giverName > giverEmail
    private Comparator<FeedbackResponseAttributes> compareByRecipientNameEmailGiverNameEmail =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {

            boolean isRecipientVisible1 = isRecipientVisible(o1);
            boolean isRecipientVisible2 = isRecipientVisible(o2);
            // Compare by Recipient Name
            int recipientNameCompareResult = compareByNames(getNameForEmail(o1.recipient),
                                                            getNameForEmail(o2.recipient),
                                                            isRecipientVisible1, isRecipientVisible2);
            if (recipientNameCompareResult != 0) {
                return recipientNameCompareResult;
            }

            // Compare by Recipient Email
            int recipientEmailCompareResult = compareByNames(o1.recipient, o2.recipient,
                                                             isRecipientVisible1, isRecipientVisible2);
            if (recipientEmailCompareResult != 0) {
                return recipientEmailCompareResult;
            }

            boolean isGiverVisible1 = isGiverVisible(o1);
            boolean isGiverVisible2 = isGiverVisible(o2);
            // Compare by Giver Name
            int giverNameCompareResult = compareByNames(getNameForEmail(o1.giver),
                                                        getNameForEmail(o2.giver),
                                                        isGiverVisible1, isGiverVisible2);
            if (giverNameCompareResult != 0) {
                return giverNameCompareResult;
            }

            // Compare by Giver Email
            int giverEmailCompareResult = compareByNames(o1.giver, o2.giver,
                                                         isGiverVisible1, isGiverVisible2);
            if (giverEmailCompareResult != 0) {
                return giverEmailCompareResult;
            }

            int responseStringResult = compareByResponseString(o1, o2);
            if (responseStringResult != 0) {
                return responseStringResult;
            }

            return o1.getId().compareTo(o2.getId());
        }
    };

    public FeedbackSessionResultsBundle(FeedbackSessionAttributes feedbackSession,
            Map<String, FeedbackQuestionAttributes> questions, CourseRoster roster) {
        this(feedbackSession, new ArrayList<FeedbackResponseAttributes>(), questions, new HashMap<String, String>(),
             new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, Set<String>>(),
             new HashMap<String, boolean[]>(), new FeedbackSessionResponseStatus(), roster,
             new HashMap<String, List<FeedbackResponseCommentAttributes>>());
    }

    public FeedbackSessionResultsBundle(FeedbackSessionAttributes feedbackSession,
                                        List<FeedbackResponseAttributes> responses,
                                        Map<String, FeedbackQuestionAttributes> questions,
                                        Map<String, String> emailNameTable,
                                        Map<String, String> emailLastNameTable,
                                        Map<String, String> emailTeamNameTable,
                                        Map<String, Set<String>> sectionTeamNameTable,
                                        Map<String, boolean[]> visibilityTable,
                                        FeedbackSessionResponseStatus responseStatus,
                                        CourseRoster roster,
                                        Map<String, List<FeedbackResponseCommentAttributes>> responseComments) {
        this(feedbackSession, responses, questions, emailNameTable, emailLastNameTable,
             emailTeamNameTable, sectionTeamNameTable, visibilityTable, responseStatus, roster, responseComments, true);
    }

    public FeedbackSessionResultsBundle(FeedbackSessionAttributes feedbackSession,
                                        List<FeedbackResponseAttributes> responses,
                                        Map<String, FeedbackQuestionAttributes> questions,
                                        Map<String, String> emailNameTable,
                                        Map<String, String> emailLastNameTable,
                                        Map<String, String> emailTeamNameTable,
                                        Map<String, Set<String>> sectionTeamNameTable,
                                        Map<String, boolean[]> visibilityTable,
                                        FeedbackSessionResponseStatus responseStatus,
                                        CourseRoster roster,
                                        Map<String, List<FeedbackResponseCommentAttributes>> responseComments,
                                        boolean isComplete) {
        this.feedbackSession = feedbackSession;
        this.questions = questions;
        this.responses = responses;
        this.emailNameTable = emailNameTable;
        this.emailLastNameTable = emailLastNameTable;
        this.emailTeamNameTable = emailTeamNameTable;
        this.instructorEmailNameTable = getInstructorEmailNameTableFromRoster(roster);
        this.sectionTeamNameTable = sectionTeamNameTable;
        this.visibilityTable = visibilityTable;
        this.responseStatus = responseStatus;
        this.roster = roster;
        this.responseComments = responseComments;
        this.actualResponses = new ArrayList<>();

        // We change user email to team name here for display purposes.
        for (FeedbackResponseAttributes response : responses) {
            if (questions.get(response.feedbackQuestionId).giverType == FeedbackParticipantType.TEAMS
                    && roster.isStudentInCourse(response.giver)) {
                // for TEAMS giver type, for older responses,
                // the giverEmail is stored as the student giver's email in the database
                // so we convert it to the team name for use in FeedbackSessionResultsBundle
                response.giver = emailNameTable.get(response.giver + Const.TEAM_OF_EMAIL_OWNER);
            }
            // Copy the data before hiding response recipient and giver.
            FeedbackResponseAttributes fraCopy = new FeedbackResponseAttributes(response);
            actualResponses.add(fraCopy);
        }
        this.isComplete = isComplete;

        hideResponsesGiverRecipient();
        // unlike emailTeamNameTable, emailLastNameTable and emailTeamNameTable,
        // roster.*Table is populated using the CourseRoster data directly
        this.rosterTeamNameMembersTable = getTeamNameToEmailsTableFromRoster(roster);
        this.rosterSectionTeamNameTable = getSectionToTeamNamesFromRoster(roster);
    }

    /**
     * Hides response names/emails and teams that are not visible to the current user.
     * Replaces the giver/recipient email in responses to an email with two "@@"s
     * to indicate it is invalid and should not be displayed.
     */
    private void hideResponsesGiverRecipient() {
        for (FeedbackResponseAttributes response : responses) {
            // Hide recipient details if its not visible to the current user
            String name = emailNameTable.get(response.recipient);
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            FeedbackParticipantType participantType = question.recipientType;

            if (!isRecipientVisible(response)) {
                String anonEmail = getAnonEmail(participantType, name);
                name = getAnonName(participantType, name);

                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);

                response.recipient = anonEmail;
            }

            // Hide giver details if its not visible to the current user
            name = emailNameTable.get(response.giver);
            participantType = question.giverType;

            if (!isGiverVisible(response)) {
                String anonEmail = getAnonEmail(participantType, name);
                name = getAnonName(participantType, name);

                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                if (participantType == FeedbackParticipantType.TEAMS) {
                    emailTeamNameTable.put(anonEmail, name);
                }
                response.giver = anonEmail;
            }
        }
    }

    /**
     * Checks if the giver/recipient for a response is visible/hidden from the current user.
     */
    public boolean isFeedbackParticipantVisible(boolean isGiver, FeedbackResponseAttributes response) {
        FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
        FeedbackParticipantType participantType;
        String responseId = response.getId();

        boolean isVisible;
        if (isGiver) {
            isVisible = visibilityTable.get(responseId)[Const.VISIBILITY_TABLE_GIVER];
            participantType = question.giverType;
        } else {
            isVisible = visibilityTable.get(responseId)[Const.VISIBILITY_TABLE_RECIPIENT];
            participantType = question.recipientType;
        }
        boolean isTypeSelf = participantType == FeedbackParticipantType.SELF;
        boolean isTypeNone = participantType == FeedbackParticipantType.NONE;

        return isVisible || isTypeSelf || isTypeNone;
    }

    /**
     * Returns true if the recipient from a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isRecipientVisible(FeedbackResponseAttributes response) {
        return isFeedbackParticipantVisible(false, response);
    }

    /**
     * Returns true if the giver from a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isGiverVisible(FeedbackResponseAttributes response) {
        return isFeedbackParticipantVisible(true, response);
    }

    public static String getAnonEmail(FeedbackParticipantType type, String name) {
        String anonName = getAnonName(type, name);
        return anonName + "@@" + anonName + ".com";
    }

    public String getAnonEmailFromStudentEmail(String studentEmail) {
        String name = roster.getStudentForEmail(studentEmail).name;
        return getAnonEmail(FeedbackParticipantType.STUDENTS, name);
    }

    public String getAnonNameWithoutNumericalId(FeedbackParticipantType type) {
        return "Anonymous " + type.toSingularFormString();
    }

    public static String getAnonName(FeedbackParticipantType type, String name) {
        String hashedEncryptedName = getHashOfName(getEncryptedName(name));
        String participantType = type.toSingularFormString();
        return String.format("Anonymous %s %s", participantType, hashedEncryptedName);
    }

    private static String getEncryptedName(String name) {
        return StringHelper.encrypt(name);
    }

    private static String getHashOfName(String name) {
        return Long.toString(Math.abs((long) name.hashCode()));
    }

    private String getNameFromRoster(String participantIdentifier, boolean isFullName) {
        if (participantIdentifier.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }

        // return person name if participant is a student
        if (isParticipantIdentifierStudent(participantIdentifier)) {
            StudentAttributes student = roster.getStudentForEmail(participantIdentifier);
            if (isFullName) {
                return student.name;
            }
            return student.lastName;
        }

        // return person name if participant is an instructor
        if (isParticipantIdentifierInstructor(participantIdentifier)) {
            return roster.getInstructorForEmail(participantIdentifier)
                         .name;
        }

        // return team name if participantIdentifier is a team name
        boolean isTeamName = rosterTeamNameMembersTable.containsKey(participantIdentifier);
        if (isTeamName) {
            return participantIdentifier;
        }

        // return team name if participant is team identified by a member
        boolean isNameRepresentingStudentsTeam = participantIdentifier.contains(Const.TEAM_OF_EMAIL_OWNER);
        if (isNameRepresentingStudentsTeam) {
            int index = participantIdentifier.indexOf(Const.TEAM_OF_EMAIL_OWNER);
            return getTeamNameFromRoster(participantIdentifier.substring(0, index));
        }

        return "";
    }

    /**
     * Get the displayable full name from an email.
     *
     * <p>This function is different from {@link #getNameForEmail} as it obtains the name
     * using the class roster, instead of from the responses.
     * @return the full name of a student, if participantIdentifier is the email of a student, <br>
     *         the name of an instructor, if participantIdentifier is the email of an instructor, <br>
     *         or the team name, if participantIdentifier represents a team. <br>
     *         Otherwise, return an empty string
     */
    public String getFullNameFromRoster(String participantIdentifier) {
        return getNameFromRoster(participantIdentifier, true);
    }

    /**
     * Get the displayable last name from an email.
     *
     * <p>This function is different from {@link #getLastNameForEmail} as it obtains the name
     * using the class roster, instead of from the responses.
     * @return the last name of a student, if participantIdentifier is the email of a student, <br>
     *         the name of an instructor, if participantIdentifier is the email of an instructor, <br>
     *         or the team name, if participantIdentifier represents a team. <br>
     *         Otherwise, return an empty string
     */
    public String getLastNameFromRoster(String participantIdentifier) {
        return getNameFromRoster(participantIdentifier, false);
    }

    /**
     * Return true if the participantIdentifier is an email of either a student
     * or instructor in the course roster. Otherwise, return false.
     *
     * @return true if the participantIdentifier is an email of either a student
     *         or instructor in the course roster, false otherwise.
     */
    public boolean isEmailOfPersonFromRoster(String participantIdentifier) {
        boolean isStudent = isParticipantIdentifierStudent(participantIdentifier);
        boolean isInstructor = isParticipantIdentifierInstructor(participantIdentifier);
        return isStudent || isInstructor;
    }

    /**
     * If the participantIdentifier identifies a student or instructor,
     * the participantIdentifier is returned.
     *
     * <p>Otherwise, Const.USER_NOBODY_TEXT is returned.
     * @see #getDisplayableEmail
     */
    public String getDisplayableEmailFromRoster(String participantIdentifier) {
        if (isEmailOfPersonFromRoster(participantIdentifier)) {
            return participantIdentifier;
        }
        return Const.USER_NOBODY_TEXT;
    }

    /**
     * Get the displayable team name from an email.
     * If the email is not an email of someone in the class roster, an empty string is returned.
     *
     * <p>This function is different from {@link #getTeamNameForEmail} as it obtains the name
     * using the class roster, instead of from the responses.
     */
    public String getTeamNameFromRoster(String participantIdentifier) {
        if (participantIdentifier.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (isParticipantIdentifierStudent(participantIdentifier)) {
            return roster.getStudentForEmail(participantIdentifier).team;
        } else if (isParticipantIdentifierInstructor(participantIdentifier)) {
            return Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            return "";
        }
    }

    /**
     * Get the displayable section name from an email.
     *
     * <p>If the email is not an email of someone in the class roster, an empty string is returned.
     *
     * <p>If the email of an instructor or "%GENERAL%" is passed in, "No specific recipient" is returned.
     */
    public String getSectionFromRoster(String participantIdentifier) {
        boolean isStudent = isParticipantIdentifierStudent(participantIdentifier);
        boolean isInstructor = isParticipantIdentifierInstructor(participantIdentifier);
        boolean participantIsGeneral = participantIdentifier.equals(Const.GENERAL_QUESTION);

        if (isStudent) {
            return roster.getStudentForEmail(participantIdentifier)
                         .section;
        } else if (isInstructor || participantIsGeneral) {
            return Const.NO_SPECIFIC_RECIPIENT;
        } else {
            return "";
        }
    }

    /**
     * Get the emails of the students given a teamName,
     * if teamName is "Instructors", returns the list of instructors.
     * @return a set of emails of the students in the team
     */
    public Set<String> getTeamMembersFromRoster(String teamName) {
        if (!rosterTeamNameMembersTable.containsKey(teamName)) {
            return new HashSet<>();
        }

        return new HashSet<>(rosterTeamNameMembersTable.get(teamName));
    }

    /**
     * Get the team names in a section. <br>
     *
     * <p>Instructors are not contained in any section.
     * @return a set of team names of the teams in the section
     */
    public Set<String> getTeamsInSectionFromRoster(String sectionName) {
        if (rosterSectionTeamNameTable.containsKey(sectionName)) {
            return new HashSet<>(rosterSectionTeamNameTable.get(sectionName));
        }
        return new HashSet<>();
    }

    public boolean isParticipantIdentifierStudent(String participantIdentifier) {
        StudentAttributes student = roster.getStudentForEmail(participantIdentifier);
        return student != null;
    }

    public boolean isParticipantIdentifierInstructor(String participantIdentifier) {
        InstructorAttributes instructor = roster.getInstructorForEmail(participantIdentifier);
        return instructor != null;
    }

    /**
     * Get the possible givers for a recipient specified by its participant identifier for
     * a question.
     *
     * @return a list of participant identifiers that can give a response to the recipient specified
     */
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa,
                                          String recipientParticipantIdentifier) {
        if (recipientParticipantIdentifier.contains("@@")) {
            return new ArrayList<>();
        }

        if (isParticipantIdentifierStudent(recipientParticipantIdentifier)) {
            StudentAttributes student = roster.getStudentForEmail(recipientParticipantIdentifier);
            return getPossibleGivers(fqa, student);
        } else if (isParticipantIdentifierInstructor(recipientParticipantIdentifier)) {
            return getPossibleGiversForInstructor(fqa);
        } else if (recipientParticipantIdentifier.equals(Const.GENERAL_QUESTION)) {
            switch (fqa.giverType) {
            case STUDENTS:
                return getSortedListOfStudentEmails();
            case TEAMS:
                return getSortedListOfTeams();
            case INSTRUCTORS:
                return getSortedListOfInstructorEmails();
            case SELF:
                List<String> creatorEmail = new ArrayList<>();
                creatorEmail.add(fqa.creatorEmail);
                return creatorEmail;
            default:
                log.severe("Invalid giver type specified");
                return new ArrayList<>();
            }
        } else {
            return getPossibleGiversForTeam(fqa, recipientParticipantIdentifier);
        }
    }

    /**
     * Get the possible givers for a TEAM recipient for the question specified.
     * @return a list of possible givers that can give a response to the team
     *         specified as the recipient
     */
    private List<String> getPossibleGiversForTeam(FeedbackQuestionAttributes fqa,
                                                  String recipientTeam) {
        FeedbackParticipantType giverType = fqa.giverType;
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<>();

        if (recipientType == FeedbackParticipantType.TEAMS) {
            switch (giverType) {
            case TEAMS:
                possibleGivers = getSortedListOfTeams();
                break;
            case STUDENTS:
                possibleGivers = getSortedListOfStudentEmails();
                break;
            case INSTRUCTORS:
                possibleGivers = getSortedListOfInstructorEmails();
                break;
            case SELF:
                possibleGivers.add(fqa.creatorEmail);
                break;
            default:
                log.severe("Invalid giver type specified");
                break;
            }
        } else if (recipientType == FeedbackParticipantType.OWN_TEAM) {
            if (giverType == FeedbackParticipantType.TEAMS) {
                possibleGivers.add(recipientTeam);
            } else {
                possibleGivers = new ArrayList<>(getTeamMembersFromRoster(recipientTeam));
            }
        }

        return possibleGivers;
    }

    /**
     * Get the possible givers for a STUDENT recipient for the question specified.
     * @return a list of possible givers that can give a response to the student
     *         specified as the recipient
     */
    private List<String> getPossibleGivers(FeedbackQuestionAttributes fqa,
                                           StudentAttributes studentRecipient) {
        FeedbackParticipantType giverType = fqa.giverType;
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleGivers = getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = getSortedListOfTeams();
            break;
        case SELF:
            possibleGivers.add(fqa.creatorEmail);
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        switch (recipientType) {
        case STUDENTS:
        case TEAMS:
            break;
        case SELF:
            possibleGivers = new ArrayList<>();
            possibleGivers.add(studentRecipient.email);
            break;
        case OWN_TEAM_MEMBERS:
            possibleGivers.retainAll(getSortedListOfTeamMembersEmailsExcludingSelf(studentRecipient));
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleGivers.retainAll(getSortedListOfTeamMembersEmails(studentRecipient));
            break;
        default:
            break;
        }

        return possibleGivers;
    }

    /**
     * Get the possible givers for a INSTRUCTOR recipient for the question specified.
     * @return a list of possible givers that can give a response to the instructor
     *         specified as the recipient
     */
    private List<String> getPossibleGiversForInstructor(FeedbackQuestionAttributes fqa) {
        FeedbackParticipantType giverType = fqa.giverType;
        List<String> possibleGivers = new ArrayList<>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleGivers = getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = getSortedListOfTeams();
            break;
        case SELF:
            possibleGivers.add(fqa.creatorEmail);
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        return possibleGivers;
    }

    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa) {
        FeedbackParticipantType giverType = fqa.giverType;
        List<String> possibleGivers = new ArrayList<>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleGivers = getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = getSortedListOfTeams();
            break;
        case SELF:
            possibleGivers = new ArrayList<>();
            possibleGivers.add(fqa.creatorEmail);
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        return possibleGivers;
    }

    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = null;

        // use giver type to determine recipients if recipient is "self"
        if (fqa.recipientType == FeedbackParticipantType.SELF) {
            recipientType = fqa.giverType;
        }

        switch (recipientType) {
        case STUDENTS:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleRecipients = getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleRecipients = getSortedListOfInstructorEmails();
            break;
        case TEAMS:
        case OWN_TEAM:
            possibleRecipients = getSortedListOfTeams();
            break;
        case NONE:
            possibleRecipients = new ArrayList<>();
            possibleRecipients.add(Const.USER_NOBODY_TEXT);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    // TODO code duplication between this function and in FeedbackQuestionsLogic getRecipientsForQuestion
    /**
     * Get the possible recipients for a giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the participantIdentifier
     */
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                              String giverParticipantIdentifier) {
        if (giverParticipantIdentifier.contains("@@")) {
            return new ArrayList<>();
        }

        if (isParticipantIdentifierStudent(giverParticipantIdentifier)) {
            StudentAttributes student = roster.getStudentForEmail(giverParticipantIdentifier);
            return getPossibleRecipients(fqa, student);
        } else if (isParticipantIdentifierInstructor(giverParticipantIdentifier)) {
            InstructorAttributes instructor = roster.getInstructorForEmail(giverParticipantIdentifier);
            return getPossibleRecipients(fqa, instructor);
        } else {
            return getPossibleRecipientsForTeam(fqa, giverParticipantIdentifier);
        }
    }

    /**
     * Get the possible recipients for a INSTRUCTOR giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the instructorGiver
     */
    private List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                               InstructorAttributes instructorGiver) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<>();

        switch (recipientType) {
        case STUDENTS:
            possibleRecipients = getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleRecipients = getSortedListOfInstructorEmails();
            possibleRecipients.remove(instructorGiver.email);
            break;
        case TEAMS:
            possibleRecipients = getSortedListOfTeams();
            break;
        case SELF:
            possibleRecipients.add(instructorGiver.email);
            break;
        case OWN_TEAM:
            possibleRecipients.add(Const.USER_TEAM_FOR_INSTRUCTOR);
            break;
        case NONE:
            possibleRecipients.add(Const.GENERAL_QUESTION);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    /**
     * Get the possible recipients for a STUDENT giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the studentGiver
     */
    private List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                               StudentAttributes studentGiver) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<>();

        switch (recipientType) {
        case STUDENTS:
            possibleRecipients = getSortedListOfStudentEmails();
            possibleRecipients.remove(studentGiver.email);
            break;
        case OWN_TEAM_MEMBERS:
            possibleRecipients = getSortedListOfTeamMembersEmailsExcludingSelf(studentGiver);
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleRecipients = getSortedListOfTeamMembersEmails(studentGiver);
            break;
        case INSTRUCTORS:
            possibleRecipients = getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleRecipients = getSortedListOfTeamsExcludingOwnTeam(studentGiver);
            break;
        case OWN_TEAM:
            possibleRecipients.add(studentGiver.team);
            break;
        case SELF:
            possibleRecipients.add(studentGiver.email);
            break;
        case NONE:
            possibleRecipients.add(Const.GENERAL_QUESTION);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    /**
     * Get the possible recipients for a TEAM giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the givingTeam
     */
    private List<String> getPossibleRecipientsForTeam(FeedbackQuestionAttributes fqa,
                                                      String givingTeam) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<>();

        switch (recipientType) {
        case TEAMS:
            possibleRecipients = getSortedListOfTeams();
            possibleRecipients.remove(givingTeam);
            break;
        case SELF:
        case OWN_TEAM:
            possibleRecipients.add(givingTeam);
            break;
        case INSTRUCTORS:
            possibleRecipients = getSortedListOfInstructorEmails();
            break;
        case STUDENTS:
            possibleRecipients = getSortedListOfStudentEmails();
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            if (rosterTeamNameMembersTable.containsKey(givingTeam)) {
                Set<String> studentEmailsToNames = rosterTeamNameMembersTable.get(givingTeam);
                possibleRecipients = new ArrayList<>(studentEmailsToNames);
                Collections.sort(possibleRecipients);
            }
            break;
        case NONE:
            possibleRecipients.add(Const.GENERAL_QUESTION);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    private List<String> getSortedListOfTeamsExcludingOwnTeam(StudentAttributes student) {
        String studentTeam = student.team;
        List<String> listOfTeams = getSortedListOfTeams();
        listOfTeams.remove(studentTeam);
        return listOfTeams;
    }

    /**
     * Get a sorted list of teams for the feedback session.<br>
     * Instructors are not present as a team.
     */
    private List<String> getSortedListOfTeams() {
        List<String> teams = new ArrayList<>(rosterTeamNameMembersTable.keySet());
        teams.remove(Const.USER_TEAM_FOR_INSTRUCTOR);
        Collections.sort(teams);
        return teams;
    }

    /**
     * Get a sorted list of team members, who are in the same team as the student.<br>
     * This list includes the student.
     *
     * @return a list of team members, including the original student
     * @see #getSortedListOfTeamMembersEmailsExcludingSelf
     */
    public List<String> getSortedListOfTeamMembersEmails(StudentAttributes student) {
        String teamName = student.team;
        Set<String> teamMembersEmailsToNames = rosterTeamNameMembersTable.get(teamName);
        List<String> teamMembers = new ArrayList<>(teamMembersEmailsToNames);
        Collections.sort(teamMembers);
        return teamMembers;
    }

    /**
     * Get a sorted list of team members, who are in the same team as the student,
     * EXCLUDING the student.
     *
     * @return a list of team members, excluding the original student
     * @see #getSortedListOfTeamMembersEmails
     */
    private List<String> getSortedListOfTeamMembersEmailsExcludingSelf(StudentAttributes student) {
        List<String> teamMembers = getSortedListOfTeamMembersEmails(student);
        String currentStudentEmail = student.email;
        teamMembers.remove(currentStudentEmail);
        return teamMembers;
    }

    /**
     * Returns a list of student emails, sorted by section name.
     */
    private List<String> getSortedListOfStudentEmails() {
        List<String> emailList = new ArrayList<>();
        List<StudentAttributes> students = roster.getStudents();
        StudentAttributes.sortBySectionName(students);
        for (StudentAttributes student : students) {
            emailList.add(student.email);
        }
        return emailList;
    }

    /**
     * Returns a list of instructor emails, sorted alphabetically.
     */
    private List<String> getSortedListOfInstructorEmails() {
        List<String> emailList = new ArrayList<>();
        List<InstructorAttributes> instructors = roster.getInstructors();
        for (InstructorAttributes instructor : instructors) {
            emailList.add(instructor.email);
        }
        Collections.sort(emailList);
        return emailList;
    }

    /**
     * Used for instructor feedback results views.
     */
    public String getResponseAnswerHtml(FeedbackResponseAttributes response,
                                        FeedbackQuestionAttributes question) {
        return response.getResponseDetails().getAnswerHtml(response, question, this);
    }

    public String getResponseAnswerCsv(FeedbackResponseAttributes response,
                                       FeedbackQuestionAttributes question) {
        return response.getResponseDetails().getAnswerCsv(response, question, this);
    }

    public FeedbackResponseAttributes getActualResponse(FeedbackResponseAttributes response) {
        FeedbackResponseAttributes actualResponse = null;
        for (FeedbackResponseAttributes resp : actualResponses) {
            if (resp.getId().equals(response.getId())) {
                actualResponse = resp;
                break;
            }
        }
        return actualResponse;
    }

    public String getNameForEmail(String email) {
        String name = emailNameTable.get(email);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(email);
        } else {
            return name;
        }
    }

    public String getLastNameForEmail(String email) {
        String name = emailLastNameTable.get(email);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(email);
        } else {
            return name;
        }
    }

    public String getTeamNameForEmail(String email) {
        String teamName = emailTeamNameTable.get(email);
        if (teamName == null || email.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        return teamName;
    }

    /**
     * Returns displayable email if the email of a giver/recipient in the course
     * and it is allowed to be displayed.
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmail(boolean isGiver, FeedbackResponseAttributes response) {
        String participantIdentifier;
        if (isGiver) {
            participantIdentifier = response.giver;
        } else {
            participantIdentifier = response.recipient;
        }

        if (isEmailOfPerson(participantIdentifier) && isFeedbackParticipantVisible(isGiver, response)) {
            return participantIdentifier;
        }
        return Const.USER_NOBODY_TEXT;
    }

    /**
     * Returns displayable email if the email of a recipient in the course
     * and it is allowed to be displayed.
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmailRecipient(FeedbackResponseAttributes response) {
        return getDisplayableEmail(false, response);
    }

    /**
     * Returns displayable email if the email of a giver in the course
     * and it is allowed to be displayed.
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmailGiver(FeedbackResponseAttributes response) {
        return getDisplayableEmail(true, response);
    }

    /**
     * Returns true if the given identifier is an email of a person in the course.
     * Returns false otherwise.
     */
    public boolean isEmailOfPerson(String participantIdentifier) {
        // An email must at least contains '@' character
        boolean isIdentifierEmail = participantIdentifier.contains("@");

        /*
         * However, a team name may also contains '@'
         * To differentiate a team name and an email of a person,
         * we check against the name & team name associated by the participant identifier
         */
        String name = emailNameTable.get(participantIdentifier);
        boolean isIdentifierName = name != null && name.equals(participantIdentifier);
        boolean isIdentifierTeam = name != null && name.equals(Const.USER_IS_TEAM);

        String teamName = emailTeamNameTable.get(participantIdentifier);
        boolean isIdentifierTeamName = teamName != null && teamName.equals(participantIdentifier);
        return isIdentifierEmail && !(isIdentifierName || isIdentifierTeamName || isIdentifierTeam);
    }

    public String getRecipientNameForResponse(FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.recipient);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return name;
        }
    }

    public String getGiverNameForResponse(FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.giver);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return name;
        }
    }

    public String appendTeamNameToName(String name, String teamName) {
        String outputName;
        if (name.contains("Anonymous") || name.equals(Const.USER_UNKNOWN_TEXT)
                || name.equals(Const.USER_NOBODY_TEXT) || teamName.isEmpty()) {
            outputName = name;
        } else {
            outputName = name + " (" + teamName + ")";
        }
        return outputName;
    }

    // TODO consider removing this to increase cohesion
    public String getQuestionText(String feedbackQuestionId) {
        return SanitizationHelper.sanitizeForHtml(questions.get(feedbackQuestionId)
                                                  .getQuestionDetails()
                                                  .getQuestionText());
    }

    // TODO: make responses to the student calling this method always on top.
    /**
     * Gets the questions and responses in this bundle as a map.
     *
     * @return An ordered {@code Map} with keys as {@link FeedbackQuestionAttributes}
     *         sorted by questionNumber.
     *         The mapped values for each key are the corresponding
     *         {@link FeedbackResponseAttributes} as a {@code List}.
     */
    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMap() {
        if (questions == null || responses == null) {
            return null;
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap = new LinkedHashMap<>();
        List<FeedbackQuestionAttributes> sortedQuestions = new ArrayList<>(questions.values());
        // sorts the questions by its natural ordering, which is by question number
        Collections.sort(sortedQuestions);
        for (FeedbackQuestionAttributes question : sortedQuestions) {
            sortedMap.put(question, new ArrayList<FeedbackResponseAttributes>());
        }

        for (FeedbackResponseAttributes response : responses) {
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            List<FeedbackResponseAttributes> responsesForQuestion = sortedMap.get(question);
            responsesForQuestion.add(response);
        }

        for (List<FeedbackResponseAttributes> responsesForQuestion : sortedMap.values()) {
            Collections.sort(responsesForQuestion, compareByGiverRecipient);
        }

        return sortedMap;
    }

    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMapSortedByRecipient() {
        if (questions == null || responses == null) {
            return null;
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap = new LinkedHashMap<>();

        List<FeedbackQuestionAttributes> sortedQuestions = new ArrayList<>(questions.values());
        // sorts the questions by its natural ordering, which is by question number
        Collections.sort(sortedQuestions);
        for (FeedbackQuestionAttributes question : sortedQuestions) {
            sortedMap.put(question, new ArrayList<FeedbackResponseAttributes>());
        }

        for (FeedbackResponseAttributes response : responses) {
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            List<FeedbackResponseAttributes> responsesForQuestion = sortedMap.get(question);
            responsesForQuestion.add(response);
        }

        for (List<FeedbackResponseAttributes> responsesForQuestion : sortedMap.values()) {
            Collections.sort(responsesForQuestion, compareByRecipientNameEmailGiverNameEmail);
        }

        return sortedMap;
    }

    /**
     * Returns an ordered Map with {@code recipientTeam} name as key
     * sorted by recipientTeam > question > recipientName > giverTeam > giverName.
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getQuestionResponseMapByRecipientTeam() {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<>();

        Collections.sort(responses, compareByTeamQuestionRecipientTeamGiver);

        for (FeedbackResponseAttributes response : responses) {
            String recipientTeam = getTeamNameForEmail(response.recipient);
            if (recipientTeam.isEmpty()) {
                recipientTeam = getNameForEmail(response.recipient);
            }

            if (!sortedMap.containsKey(recipientTeam)) {
                sortedMap.put(recipientTeam,
                        new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient =
                                            sortedMap.get(recipientTeam);

            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            if (!responsesForOneRecipient.containsKey(question)) {
                responsesForOneRecipient.put(question, new ArrayList<FeedbackResponseAttributes>());
            }

            List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion =
                                            responsesForOneRecipient.get(question);
            responsesForOneRecipientOneQuestion.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns an ordered Map with {@code giverTeam} name as key
     * sorted by giverTeam > question > giverName > recipientTeam > recipientName.
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getQuestionResponseMapByGiverTeam() {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<>();

        Collections.sort(responses, compareByTeamQuestionGiverTeamRecipient);

        for (FeedbackResponseAttributes response : responses) {
            String giverTeam = getTeamNameForEmail(response.giver);
            if (giverTeam.isEmpty()) {
                giverTeam = getNameForEmail(response.giver);
            }

            if (!sortedMap.containsKey(giverTeam)) {
                sortedMap.put(giverTeam,
                        new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                                            sortedMap.get(giverTeam);

            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            if (!responsesFromOneGiver.containsKey(question)) {
                responsesFromOneGiver.put(question, new ArrayList<FeedbackResponseAttributes>());
            }

            List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion = responsesFromOneGiver.get(question);
            responsesFromOneGiverOneQuestion.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns responses as a {@code Map<recipientName, Map<question, List<response>>>}
     * Where the responses are sorted in the order of recipient, question, giver.
     * @return responses sorted by Recipient > Question > Giver
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipientQuestionGiver(boolean sortByTeam) {
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap = new LinkedHashMap<>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamRecipientQuestionTeamGiver);
        } else {
            Collections.sort(responses, compareByRecipientQuestionTeamGiver);
        }

        for (FeedbackResponseAttributes response : responses) {
            String recipientEmail = response.recipient;
            if (!sortedMap.containsKey(recipientEmail)) {
                sortedMap.put(recipientEmail,
                              new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient =
                                            sortedMap.get(recipientEmail);

            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            if (!responsesForOneRecipient.containsKey(question)) {
                responsesForOneRecipient.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion =
                                            responsesForOneRecipient.get(question);
            responsesForOneRecipientOneQuestion.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by recipientName > giverName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by recipient's name > giver's name > question number.
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByRecipient() {
        return getResponsesSortedByRecipient(false);
    }

    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipient(boolean sortByTeam) {
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap = new LinkedHashMap<>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamRecipientGiverQuestion);
        } else {
            Collections.sort(responses, compareByRecipientGiverQuestion);
        }

        for (FeedbackResponseAttributes response : responses) {
            String recipientName = this.getRecipientNameForResponse(response);
            String recipientTeamName = this.getTeamNameForEmail(response.recipient);
            String recipientNameWithTeam = this.appendTeamNameToName(recipientName, recipientTeamName);
            if (!sortedMap.containsKey(recipientNameWithTeam)) {
                sortedMap.put(recipientNameWithTeam,
                        new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                                            sortedMap.get(recipientNameWithTeam);

            String giverName = this.getGiverNameForResponse(response);
            String giverTeamName = this.getTeamNameForEmail(response.giver);
            String giverNameWithTeam = this.appendTeamNameToName(giverName, giverTeamName);
            if (!responsesToOneRecipient.containsKey(giverNameWithTeam)) {
                responsesToOneRecipient.put(giverNameWithTeam, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                                            responsesToOneRecipient.get(giverNameWithTeam);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by recipientName > giverName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by recipient identifier > giver identifier > question number.
     * @see #getResponsesSortedByRecipient
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipientGiverQuestion(boolean sortByTeam) {

        LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap = new LinkedHashMap<>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamRecipientGiverQuestion);
        } else {
            Collections.sort(responses, compareByRecipientGiverQuestion);
        }

        for (FeedbackResponseAttributes response : responses) {
            String recipientEmail = response.recipient;
            if (!sortedMap.containsKey(recipientEmail)) {
                sortedMap.put(recipientEmail,
                              new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                                            sortedMap.get(recipientEmail);

            String giverEmail = response.giver;
            if (!responsesToOneRecipient.containsKey(giverEmail)) {
                responsesToOneRecipient.put(giverEmail, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                                            responsesToOneRecipient.get(giverEmail);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns responses as a {@code Map<giverName, Map<question, List<response>>>}
     * Where the responses are sorted in the order of giver, question, recipient.
     * @return responses sorted by Giver > Question > Recipient
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
                getResponsesSortedByGiverQuestionRecipient(boolean sortByTeam) {
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap = new LinkedHashMap<>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverQuestionTeamRecipient);
        } else {
            Collections.sort(responses, compareByGiverQuestionTeamRecipient);
        }

        for (FeedbackResponseAttributes response : responses) {
            String giverEmail = response.giver;
            if (!sortedMap.containsKey(giverEmail)) {
                sortedMap.put(giverEmail,
                        new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                                            sortedMap.get(giverEmail);

            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            if (!responsesFromOneGiver.containsKey(question)) {
                responsesFromOneGiver.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion =
                                            responsesFromOneGiver.get(question);
            responsesFromOneGiverOneQuestion.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by giverName > recipientName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by giver's name > recipient's name > question number.
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByGiver() {
        return getResponsesSortedByGiver(false);
    }

    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByGiver(boolean sortByTeam) {
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap = new LinkedHashMap<>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        } else {
            Collections.sort(responses, compareByGiverRecipientQuestion);
        }

        for (FeedbackResponseAttributes response : responses) {
            String giverName = this.getGiverNameForResponse(response);
            String giverTeamName = this.getTeamNameForEmail(response.giver);
            String giverNameWithTeam = this.appendTeamNameToName(giverName, giverTeamName);
            if (!sortedMap.containsKey(giverNameWithTeam)) {
                sortedMap.put(giverNameWithTeam,
                              new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver = sortedMap.get(giverNameWithTeam);

            String recipientName = this.getRecipientNameForResponse(response);
            String recipientTeamName = this.getTeamNameForEmail(response.recipient);
            String recipientNameWithTeam = this.appendTeamNameToName(recipientName, recipientTeamName);
            if (!responsesFromOneGiver.containsKey(recipientNameWithTeam)) {
                responsesFromOneGiver.put(recipientNameWithTeam,
                                          new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                    responsesFromOneGiver.get(recipientNameWithTeam);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by giverName > recipientName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by giver's identifier > recipient's identifier > question number.
     * @see #getResponsesSortedByGiver
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
                getResponsesSortedByGiverRecipientQuestion(boolean sortByTeam) {
        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        } else {
            Collections.sort(responses, compareByGiverRecipientQuestion);
        }

        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap = new LinkedHashMap<>();

        for (FeedbackResponseAttributes response : responses) {
            String giverEmail = response.giver;
            if (!sortedMap.containsKey(giverEmail)) {
                sortedMap.put(giverEmail,
                              new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver = sortedMap.get(giverEmail);

            String recipientEmail = response.recipient;
            if (!responsesFromOneGiver.containsKey(recipientEmail)) {
                responsesFromOneGiver.put(recipientEmail,
                                          new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                                            responsesFromOneGiver.get(recipientEmail);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    public boolean isStudentHasSomethingNewToSee(StudentAttributes student) {
        for (FeedbackResponseAttributes response : responses) {
            // There is a response not written by the student
            // which is visible to the student
            if (!response.giver.equals(student.email)) {
                return true;
            }
            // There is a response comment visible to the student
            if (responseComments.containsKey(response.getId())) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Set<String>> getTeamNameToEmailsTableFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Set<String>> teamNameToEmails = new HashMap<>();

        for (StudentAttributes student : students) {
            String studentTeam = student.team;
            Set<String> studentEmails;

            if (teamNameToEmails.containsKey(studentTeam)) {
                studentEmails = teamNameToEmails.get(studentTeam);
            } else {
                studentEmails = new TreeSet<>();
            }

            studentEmails.add(student.email);
            teamNameToEmails.put(studentTeam, studentEmails);
        }

        List<InstructorAttributes> instructors = courseroster.getInstructors();
        String instructorsTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        Set<String> instructorEmails = new HashSet<>();

        for (InstructorAttributes instructor : instructors) {
            instructorEmails.add(instructor.email);
            teamNameToEmails.put(instructorsTeam, instructorEmails);
        }

        return teamNameToEmails;
    }

    private Map<String, Set<String>> getSectionToTeamNamesFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Set<String>> sectionToTeam = new HashMap<>();

        for (StudentAttributes student : students) {
            String studentSection = student.section;
            String studentTeam = student.team;
            Set<String> teamNames;

            if (sectionToTeam.containsKey(studentSection)) {
                teamNames = sectionToTeam.get(studentSection);
            } else {
                teamNames = new HashSet<>();
            }

            teamNames.add(studentTeam);
            sectionToTeam.put(studentSection, teamNames);
        }

        return sectionToTeam;
    }

    private int compareByQuestionNumber(FeedbackResponseAttributes r1,
                                        FeedbackResponseAttributes r2) {
        FeedbackQuestionAttributes q1 = questions.get(r1.feedbackQuestionId);
        FeedbackQuestionAttributes q2 = questions.get(r2.feedbackQuestionId);
        if (q1 == null || q2 == null) {
            return 0;
        }
        return q1.compareTo(q2);
    }

    /**
     * Compares the values of {@code name1} and {@code name2}.
     * Anonymous names are ordered later than non-anonymous names.
     * @param isFirstNameVisible  true if the first name should be visible to the user
     * @param isSecondNameVisible true if the second name should be visible to the user
     */
    private int compareByNames(String name1, String name2,
                               boolean isFirstNameVisible, boolean isSecondNameVisible) {
        if (!isFirstNameVisible && !isSecondNameVisible) {
            return 0;
        }
        if (!isFirstNameVisible && isSecondNameVisible) {
            return 1;
        } else if (isFirstNameVisible && !isSecondNameVisible) {
            return -1;
        }

        // Make class feedback always appear on top, and team responses at bottom.
        int n1Priority = 0;
        int n2Priority = 0;

        if (name1.equals(Const.USER_IS_NOBODY)) {
            n1Priority = -1;
        } else if (name1.equals(Const.USER_IS_TEAM)) {
            n1Priority = 1;
        }
        if (name2.equals(Const.USER_IS_NOBODY)) {
            n2Priority = -1;
        } else if (name2.equals(Const.USER_IS_TEAM)) {
            n2Priority = 1;
        }

        int order = Integer.compare(n1Priority, n2Priority);
        return order == 0 ? name1.compareTo(name2) : order;
    }

    private int compareByResponseString(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
        String responseAnswer1 = o1.getResponseDetails().getAnswerString();

        String responseAnswer2 = o2.getResponseDetails().getAnswerString();

        return responseAnswer1.compareTo(responseAnswer2);
    }

    public FeedbackSessionAttributes getFeedbackSession() {
        return feedbackSession;
    }

    public List<FeedbackResponseAttributes> getResponses() {
        return responses;
    }

    public Map<String, FeedbackQuestionAttributes> getQuestions() {
        return questions;
    }

    public Map<String, String> getEmailNameTable() {
        return emailNameTable;
    }

    public Map<String, String> getEmailLastNameTable() {
        return emailLastNameTable;
    }

    public Map<String, String> getEmailTeamNameTable() {
        return emailTeamNameTable;
    }

    public Map<String, Set<String>> getRosterTeamNameMembersTable() {
        return rosterTeamNameMembersTable;
    }

    public Set<String> sectionsInCourse() {
        return new HashSet<>(rosterSectionTeamNameTable.keySet());
    }

    public Map<String, Set<String>> getRosterSectionTeamNameTable() {
        return rosterSectionTeamNameTable;
    }

    public Map<String, boolean[]> getVisibilityTable() {
        return visibilityTable;
    }

    public FeedbackSessionResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public CourseRoster getRoster() {
        return roster;
    }

    public Map<String, List<FeedbackResponseCommentAttributes>> getResponseComments() {
        return responseComments;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public double getTimeZone() {
        return feedbackSession.getTimeZone();
    }

    private Map<String, String> getInstructorEmailNameTableFromRoster(CourseRoster roster) {
        Map<String, String> instructorEmailNameTable = new HashMap<>();
        List<InstructorAttributes> instructorList = roster.getInstructors();
        for (InstructorAttributes instructor : instructorList) {
            instructorEmailNameTable.put(instructor.email, instructor.name);
        }
        return instructorEmailNameTable;
    }

    public StringBuilder getCsvDetailedFeedbackResponseCommentsRow(FeedbackResponseAttributes response) {
        List<FeedbackResponseCommentAttributes> frcList = this.responseComments.get(response.getId());
        StringBuilder commentRow = new StringBuilder(200);
        for (FeedbackResponseCommentAttributes frc : frcList) {
            commentRow.append("," + instructorEmailNameTable.get(frc.giverEmail) + ","
                    + getTextFromComment(frc.commentText));
        }
        return commentRow;
    }

    public String getTextFromComment(Text commentText) {
        String htmlText = commentText.getValue();
        StringBuilder comment = new StringBuilder(200);
        comment.append(Jsoup.parse(htmlText).text());
        if (!(Jsoup.parse(htmlText).getElementsByTag("img").isEmpty())) {
            comment.append("Images Link: ");
            Elements ele = Jsoup.parse(htmlText).getElementsByTag("img");
            for (Element element : ele) {
                comment.append(element.absUrl("src") + ' ');
            }
        }
        return SanitizationHelper.sanitizeForCsv(comment.toString());
    }
}
