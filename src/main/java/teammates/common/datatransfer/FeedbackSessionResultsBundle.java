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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.logic.core.TeamEvalResult;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes} 
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle implements SessionResultsBundle {
    public FeedbackSessionAttributes feedbackSession = null;
    public List<FeedbackResponseAttributes> responses = null;
    public Map<String, FeedbackQuestionAttributes> questions = null;
    public Map<String, String> emailNameTable = null;
    public Map<String, String> emailLastNameTable = null;
    public Map<String, String> emailTeamNameTable = null;
    public Map<String, Set<String>> rosterTeamNameMembersTable = null;
    public Map<String, Set<String>> rosterSectionTeamNameTable = null;
    public Map<String, boolean[]> visibilityTable = null;
    public FeedbackSessionResponseStatus responseStatus = null;
    public CourseRoster roster = null;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseComments = null;
    public boolean isComplete;

    protected static Logger log = Utils.getLogger();
	 
    /**
     * Responses with identities of giver/recipients NOT hidden.
     * To be used for anonymous result calculation only, and identities hidden before showing to users.
     */
    public List<FeedbackResponseAttributes> actualResponses = null;

    // For contribution questions.
    // Key is questionId, value is a map of student email to StudentResultSumary
    public Map<String, Map<String, StudentResultSummary>> contributionQuestionStudentResultSummary =
            new HashMap<String, Map<String, StudentResultSummary>>();
    // Key is questionId, value is a map of team name to TeamEvalResult
    public Map<String, Map<String, TeamEvalResult>> contributionQuestionTeamEvalResults =
            new HashMap<String, Map<String, TeamEvalResult>>();
    
    /* 
     * sectionTeamNameTable takes into account the section viewing privileges of the logged-in instructor 
     * whereas rosterSectionTeamNameTable doesn't. 
     * As a result, sectionTeamNameTable only contains sections viewable to the logged-in instructor 
     * whereas rosterSectionTeamNameTable contains all sections in the course.
     * As sectionTeamNameTable is dependent on instructor privileges, 
     * it can only be used for instructor pages and not for student pages 
    */
    public Map<String, Set<String>> sectionTeamNameTable = null;

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
        this.sectionTeamNameTable = sectionTeamNameTable;
        this.visibilityTable = visibilityTable;
        this.responseStatus = responseStatus;
        this.roster = roster;
        this.responseComments = responseComments;
        this.actualResponses = new ArrayList<FeedbackResponseAttributes>();

        // We change user email to team name here for display purposes.
        for (FeedbackResponseAttributes response : responses) {
            if (questions.get(response.feedbackQuestionId).giverType == FeedbackParticipantType.TEAMS) {
                response.giverEmail += Const.TEAM_OF_EMAIL_OWNER;
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
            String name = emailNameTable.get(response.recipientEmail);
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            FeedbackParticipantType participantType = question.recipientType;

            if (!isRecipientVisible(response)) {
                String anonEmail = getAnonEmail(participantType, name);
                name = getAnonName(participantType, name);

                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name+ Const.TEAM_OF_EMAIL_OWNER);

                response.recipientEmail = anonEmail;
            }

            // Hide giver details if its not visible to the current user
            name = emailNameTable.get(response.giverEmail);
            participantType = question.giverType;

            if (!isGiverVisible(response)) {
                String anonEmail = getAnonEmail(participantType, name);
                name = getAnonName(participantType, name);

                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                if (participantType == FeedbackParticipantType.TEAMS) {
                    emailTeamNameTable.put(anonEmail, name);
                }
                response.giverEmail = anonEmail;
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
        boolean isTypeSelf = (participantType == FeedbackParticipantType.SELF);
        boolean isTypeNone = (participantType == FeedbackParticipantType.NONE);

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

    private String getAnonEmail(FeedbackParticipantType type, String name) {
        String anonName = getAnonName(type, name);
        return anonName + "@@" + anonName + ".com";
    }

    public String getAnonEmailFromStudentEmail(String studentEmail) {
        String name = roster.getStudentForEmail(studentEmail).name;
        return getAnonEmail(FeedbackParticipantType.STUDENTS, name);
    }

    private String getAnonName(FeedbackParticipantType type, String name) {
        String hash = getHashOfName(name);
        String anonName = type.toSingularFormString();
        anonName = "Anonymous " + anonName + " " + hash;
        return anonName;
    }

    private String getHashOfName(String name) {
        return Integer.toString(Math.abs(name.hashCode()));
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
            } else {
                return student.lastName;
            }
        }

        // return person name if participant is an instructor
        if (isParticipantIdentifierInstructor(participantIdentifier)) {
            InstructorAttributes instructor = roster.getInstructorForEmail(participantIdentifier);
            return instructor.name;
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
     * This function is different from getNameForEmail as it obtains the name
     * using the class roster, instead of from the responses. 
     * @param participantIdentifier
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
     * This function is different from getLastNameForEmail as it obtains the name
     * using the class roster, instead of from the responses. 
     * @param participantIdentifier
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
     * @param participantIdentifier
     * @return true if the participantIdentifier is an email of either a student
     * or instructor in the course roster, false otherwise.
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
     * Otherwise, Const.USER_NOBODY_TEXT is returned.
     * @see getDisplayableEmail
     * @param participantIdentifier
     * @return
     */
    public String getDisplayableEmailFromRoster(String participantIdentifier) {
        if (isEmailOfPersonFromRoster(participantIdentifier)) {
            return participantIdentifier;
        } else {
            return Const.USER_NOBODY_TEXT;
        }
    }

    /**
     * Get the displayable team name from an email.
     * If the email is not an email of someone in the class roster, an empty string is returned.
     * 
     * This function is different from getTeamNameForEmail as it obtains the name
     * using the class roster, instead of from the responses. 
     * @param participantIdentifier
     */
    public String getTeamNameFromRoster(String participantIdentifier) {
        if (participantIdentifier.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (isParticipantIdentifierStudent(participantIdentifier)) {
            StudentAttributes student = roster
                    .getStudentForEmail(participantIdentifier);
            return student.team;
        } else if (isParticipantIdentifierInstructor(participantIdentifier)) {
            return Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            return "";
        }
    }

    /**
     * Get the displayable section name from an email.
     * If the email is not an email of someone in the class roster, an empty string is returned.
     * 
     * If the email of an instructor or "%GENERAL%" is passed in, "Not in a section" is returned.
     * @param participantIdentifier
     */
    public String getSectionFromRoster(String participantIdentifier) {
        boolean isStudent = isParticipantIdentifierStudent(participantIdentifier);
        boolean isInstructor = isParticipantIdentifierInstructor(participantIdentifier);
        boolean participantIsGeneral = participantIdentifier.equals(Const.GENERAL_QUESTION);

        if (isStudent) {
            StudentAttributes student = roster.getStudentForEmail(participantIdentifier);
            return student.section;
        } else if (isInstructor || participantIsGeneral) {
            return Const.USER_NOT_IN_A_SECTION;
        } else {
            return "";
        }
    }

    /**
     * Get the emails of the students given a teamName,
     * if teamName is "Instructors", returns the list of instructors.
     * @param teamName
     * @return a set of emails of the students in the team
     */
    public Set<String> getTeamMembersFromRoster(String teamName) {
        if (rosterTeamNameMembersTable.get(teamName) != null) {
            Set<String> teamMembers = new HashSet<String>(rosterTeamNameMembersTable.get(teamName));
            return teamMembers;
        } else {
            return new HashSet<String>();
        }
    }

    /**
     * Get the team names in a section. <br> 
     * 
     * Instructors are not contained in any section.
     * @param sectionName
     * @return a set of team names of the teams in the section
     */
    public Set<String> getTeamsInSectionFromRoster(String sectionName) {
        if (rosterSectionTeamNameTable.containsKey(sectionName)) {
            Set<String> teams = new HashSet<String>(rosterSectionTeamNameTable.get(sectionName));
            return teams;
        } else {
            return new HashSet<String>();
        }
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
     * a question
     * 
     * @param fqa
     * @param recipientParticipantIdentifier
     * @return a list of participant identifiers that can give a response to the recipient specified
     */
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa,
                                          String recipientParticipantIdentifier) {
        boolean recipientIsAnonymous = recipientParticipantIdentifier.contains("@@");

        if (recipientParticipantIdentifier == null || recipientIsAnonymous) {
            return new ArrayList<String>();
        }

        if (isParticipantIdentifierStudent(recipientParticipantIdentifier)) {
            StudentAttributes student = roster.getStudentForEmail(recipientParticipantIdentifier);
            return getPossibleGivers(fqa, student);
        } else if (isParticipantIdentifierInstructor(recipientParticipantIdentifier)) {
            InstructorAttributes instructor = roster.getInstructorForEmail(recipientParticipantIdentifier);
            return getPossibleGivers(fqa, instructor);
        } else if (recipientParticipantIdentifier.equals(Const.GENERAL_QUESTION)) {
            switch (fqa.giverType) {
                case STUDENTS:
                    return getSortedListOfStudentEmails();
                case TEAMS:
                    return getSortedListOfTeams();
                case INSTRUCTORS:
                    return getSortedListOfInstructorEmails();
                case SELF:
                    List<String> creatorEmail = new ArrayList<String>();
                    creatorEmail.add(fqa.creatorEmail);
                    return creatorEmail;
                 default:
                    log.severe("Invalid giver type specified");
                    return new ArrayList<String>();
            }
        } else {
            return getPossibleGiversForTeam(fqa, recipientParticipantIdentifier);
        }
    }

    /**
     * Get the possible givers for a TEAM recipient for the question specified
     * @param fqa
     * @param recipientTeam 
     * @return a list of possible givers that can give a response to the team 
     *         specified as the recipient
     */
    private List<String> getPossibleGiversForTeam(FeedbackQuestionAttributes fqa,
                                                  String recipientTeam) {
        FeedbackParticipantType giverType = fqa.giverType;
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<String>();

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
                possibleGivers = new ArrayList<String>(getTeamMembersFromRoster(recipientTeam));
            }
        }

        return possibleGivers;
    }

    /**
     * Get the possible givers for a STUDENT recipient for the question specified
     * @param fqa
     * @param studentRecipient
     * @return a list of possible givers that can give a response to the student 
     *         specified as the recipient
     */
    private List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, 
                                           StudentAttributes studentRecipient) {
        FeedbackParticipantType giverType = fqa.giverType;
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<String>();

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
                possibleGivers = new ArrayList<String>();
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
     * Get the possible givers for a INSTRUCTOR recipient for the question specified
     * @param fqa
     * @param instructorRecipient
     * @return a list of possible givers that can give a response to the instructor 
     *         specified as the recipient
     */
    private List<String> getPossibleGivers(FeedbackQuestionAttributes fqa,
                                           InstructorAttributes instructorRecipient) {
        FeedbackParticipantType giverType = fqa.giverType;
        List<String> possibleGivers = new ArrayList<String>();

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
        List<String> possibleGivers = new ArrayList<String>();

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
                possibleGivers = new ArrayList<String>();
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
                possibleRecipients = new ArrayList<String>(); 
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
     * Get the possible recipients for a giver for the question specified
     * @param fqa
     * @param giverParticipantIdentifier
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the participantIdentifier
     */
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                              String giverParticipantIdentifier) {
        boolean giverIsAnonymous = giverParticipantIdentifier.contains("@@");

        if (giverParticipantIdentifier == null || giverIsAnonymous) {
            return new ArrayList<String>();
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
     * Get the possible recipients for a INSTRUCTOR giver for the question specified
     * @param fqa
     * @param instructorGiver
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the instructorGiver
     */
    private List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa, 
                                               InstructorAttributes instructorGiver) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();

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
     * Get the possible recipients for a STUDENT giver for the question specified
     * @param fqa
     * @param studentGiver
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the studentGiver
     */
    private List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa, 
                                               StudentAttributes studentGiver) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();

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
     * Get the possible recipients for a TEAM giver for the question specified
     * @param fqa
     * @param givingTeam
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the givingTeam
     */
    private List<String> getPossibleRecipientsForTeam(FeedbackQuestionAttributes fqa,
                                                      String givingTeam) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();

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
                    possibleRecipients = new ArrayList<String>(studentEmailsToNames);
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
        List<String> teams = new ArrayList<String>(rosterTeamNameMembersTable.keySet());
        teams.remove(Const.USER_TEAM_FOR_INSTRUCTOR);
        Collections.sort(teams);
        return teams;
    }

    /**
     * Get a sorted list of team members, who are in the same team as the student.<br>
     * This list includes the student.
     * 
     * @see getSortedListOfTeamMembersEmailsExcludingSelf
     * @param student
     * @return a list of team members, including the original student
     */
    public List<String> getSortedListOfTeamMembersEmails(StudentAttributes student) {
        String teamName = student.team;
        Set<String> teamMembersEmailsToNames = rosterTeamNameMembersTable.get(teamName);
        List<String> teamMembers = new ArrayList<String>(teamMembersEmailsToNames);
        Collections.sort(teamMembers);
        return teamMembers;
    }

    /**
     * Get a sorted list of team members, who are in the same team as the student, 
     * EXCLUDING the student.
     * 
     * @see getSortedListOfTeamMembersEmails
     * @param student
     * @return a list of team members, excluding the original student
     */
    private List<String> getSortedListOfTeamMembersEmailsExcludingSelf(StudentAttributes student) {
        List<String> teamMembers = getSortedListOfTeamMembersEmails(student);
        String currentStudentEmail = student.email;
        teamMembers.remove(currentStudentEmail);
        return teamMembers;
    }

    /**
     * Get a list of student emails, sorted by section name 
     * @return a list of student emails, sorted by section name
     */
    private List<String> getSortedListOfStudentEmails() {
        List<String> emailList = new ArrayList<String>();
        List<StudentAttributes> students = roster.getStudents();
        StudentAttributes.sortBySectionName(students);
        for (StudentAttributes student : students) {
            emailList.add(student.email);
        }
        return emailList;
    }

    /**
     * Get a list of instructor emails, sorted alphabetically
     * @return a list of instructor emails, sorted alphabetically
     */
    private List<String> getSortedListOfInstructorEmails() {
        List<String> emailList = new ArrayList<String>();
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
        if (name == null) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(email);
        } else {
            return Sanitizer.sanitizeForHtml(name);
        }
    }

    public String getLastNameForEmail(String email) {
        String name = emailLastNameTable.get(email);
        if (name == null) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(email);
        } else {
            return Sanitizer.sanitizeForHtml(name);
        }
    }

    public String getTeamNameForEmail(String email) {
        String teamName = emailTeamNameTable.get(email);
        if (teamName == null || email.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return Sanitizer.sanitizeForHtml(teamName);
        }
    }

    /**
     * Returns displayable email if the email of a giver/recipient in the course
     * and it is allowed to be displayed. 
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmail(boolean isGiver, FeedbackResponseAttributes response) {
        String participantIdentifier;
        if (isGiver) {
            participantIdentifier = response.giverEmail;
        } else {
            participantIdentifier = response.recipientEmail;
        }

        if (isEmailOfPerson(participantIdentifier) && isFeedbackParticipantVisible(isGiver, response)) {
            return participantIdentifier;
        } else {
            return Const.USER_NOBODY_TEXT;
        }
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
        boolean isIdentifierName = (name == null) ? false
                                                  : name.equals(participantIdentifier);
        boolean isIdentifierTeam = (name == null) ? false
                                                  : name.equals(Const.USER_IS_TEAM);

        String teamName = emailTeamNameTable.get(participantIdentifier);
        boolean isIdentifierTeamName = (teamName == null) ? false
                                                          : teamName.equals(participantIdentifier);
        return isIdentifierEmail && !(isIdentifierName || isIdentifierTeamName || isIdentifierTeam);
    }
    
    public String getRecipientNameForResponse(FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.recipientEmail);
        if (name == null || name.equals(Const.USER_IS_TEAM)) {
            return Const.USER_UNKNOWN_TEXT; // TODO: this doesn't look right
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return Sanitizer.sanitizeForHtml(name);
        }
    }

    public String getGiverNameForResponse(FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.giverEmail);
        if (name == null || name.equals(Const.USER_IS_TEAM)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return Sanitizer.sanitizeForHtml(name);
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
        return Sanitizer.sanitizeForHtml(questions.get(feedbackQuestionId)
                                                  .getQuestionDetails()
                                                  .questionText);
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

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap =
                new TreeMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();

        for (FeedbackQuestionAttributes question : questions.values()) {
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

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap =
                new TreeMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();

        for (FeedbackQuestionAttributes question : questions.values()) {
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

    public LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getQuestionResponseMapByRecipientTeam() {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient = null;
        List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion = null;

        Collections.sort(responses, compareByTeamQuestionRecipientTeamGiver);

        String recipientTeam = null;
        String questionId = null;

        for (FeedbackResponseAttributes response : responses) {
            if (recipientTeam == null ||
                    !(getTeamNameForEmail(response.recipientEmail).equals("")
                            ? getNameForEmail(response.recipientEmail).equals(recipientTeam)
                            : getTeamNameForEmail(response.recipientEmail).equals(recipientTeam))) {
                if (questionId != null && responsesForOneRecipientOneQuestion != null
                 && responsesForOneRecipient != null) {
                    responsesForOneRecipient.put(questions.get(questionId),
                                                 responsesForOneRecipientOneQuestion);
                }
                if (recipientTeam != null && responsesForOneRecipient != null) {
                    sortedMap.put(recipientTeam, responsesForOneRecipient);
                }
                responsesForOneRecipient = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                recipientTeam = getTeamNameForEmail(response.recipientEmail);
                if (recipientTeam == "") {
                    recipientTeam = getNameForEmail(response.recipientEmail);
                }
                questionId = null;
            }
            if (questionId == null || !response.feedbackQuestionId.equals(questionId)) {
                if (questionId != null && responsesForOneRecipientOneQuestion != null) {
                    responsesForOneRecipient.put(questions.get(questionId),
                                                 responsesForOneRecipientOneQuestion);
                }
                responsesForOneRecipientOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesForOneRecipientOneQuestion.add(response);
        }
        if (questionId != null && responsesForOneRecipientOneQuestion != null
         && responsesForOneRecipient != null) {
            responsesForOneRecipient.put(questions.get(questionId),
                                         responsesForOneRecipientOneQuestion);
        }
        if (recipientTeam != null && responsesForOneRecipient != null) {
            sortedMap.put(recipientTeam, responsesForOneRecipient);
        }

        return sortedMap;
    }

    public LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getQuestionResponseMapByGiverTeam() {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver = null;
        List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion = null;

        Collections.sort(responses, compareByTeamQuestionGiverTeamRecipient);

        String giverTeam = null;
        String questionId = null;

        for (FeedbackResponseAttributes response : responses) {
            if (giverTeam == null
                    || !(getTeamNameForEmail(response.giverEmail).equals("")
                            ? getNameForEmail(response.giverEmail).equals(giverTeam)
                            : getTeamNameForEmail(response.giverEmail).equals(giverTeam))) {
                if (questionId != null && responsesFromOneGiverOneQuestion != null
                 && responsesFromOneGiver != null) {
                    responsesFromOneGiver.put(questions.get(questionId),
                                              responsesFromOneGiverOneQuestion);
                }
                if (giverTeam != null && responsesFromOneGiver != null) {
                    sortedMap.put(giverTeam, responsesFromOneGiver);
                }
                responsesFromOneGiver = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                giverTeam = getTeamNameForEmail(response.giverEmail);
                if (giverTeam == "") {
                    giverTeam = getNameForEmail(response.giverEmail);
                }
                questionId = null;
            }
            if (questionId == null || !response.feedbackQuestionId.equals(questionId)) {
                if (questionId != null && responsesFromOneGiverOneQuestion != null) {
                    responsesFromOneGiver.put(questions.get(questionId),
                                              responsesFromOneGiverOneQuestion);
                }
                responsesFromOneGiverOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesFromOneGiverOneQuestion.add(response);
        }
        if (questionId != null && responsesFromOneGiverOneQuestion != null
         && responsesFromOneGiver != null) {
            responsesFromOneGiver.put(questions.get(questionId),
                                      responsesFromOneGiverOneQuestion);
        }
        if (giverTeam != null && responsesFromOneGiver != null) {
            sortedMap.put(giverTeam, responsesFromOneGiver);
        }

        return sortedMap;
    }
    
    /**
     * Returns responses as a Map<recipientName, Map<question, List<response>>>
     * Where the responses are sorted in the order of recipient, question, giver.
     * @param sortByTeam
     * @return responses sorted by Recipient > Question > Giver
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipientQuestionGiver(boolean sortByTeam) {
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient = null;
        List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion = null;

        if (sortByTeam) {
            Collections
                    .sort(responses, compareByTeamRecipientQuestionTeamGiver);
        } else {
            Collections.sort(responses, compareByRecipientQuestionTeamGiver);
        }

        String recipient = null;
        String questionId = null;

        for (FeedbackResponseAttributes response : responses) {
            if (recipient == null || !response.recipientEmail.equals(recipient)) {
                if (questionId != null && responsesForOneRecipientOneQuestion != null
                 && responsesForOneRecipient != null) {
                    responsesForOneRecipient.put(questions.get(questionId),
                                                 responsesForOneRecipientOneQuestion);
                }
                if (recipient != null && responsesForOneRecipient != null) {
                    sortedMap.put(recipient, responsesForOneRecipient);
                }
                responsesForOneRecipient = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                recipient = response.recipientEmail;
                
                questionId = null;
            }
            if (questionId == null || !response.feedbackQuestionId.equals(questionId)) {
                if (questionId != null && responsesForOneRecipientOneQuestion != null) {
                    responsesForOneRecipient.put(questions.get(questionId),
                                                 responsesForOneRecipientOneQuestion);
                }
                responsesForOneRecipientOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesForOneRecipientOneQuestion.add(response);
        }
        if (questionId != null && responsesForOneRecipientOneQuestion != null
         && responsesForOneRecipient != null) {
            responsesForOneRecipient.put(questions.get(questionId),
                                         responsesForOneRecipientOneQuestion);
        }
        if (recipient != null && responsesForOneRecipient != null) {
            sortedMap.put(recipient, responsesForOneRecipient);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
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
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();
        
        if (sortByTeam) {
            Collections.sort(responses, compareByTeamRecipientGiverQuestion);
        } else {
            Collections.sort(responses, compareByRecipientGiverQuestion);
        }
        
        String prevGiver = null;
        String prevRecipient = null;
        String recipientName = null;
        String giverName = null;
        String recipientTeamName = null;
        String giverTeamName = null;
        
        List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
        
        for (FeedbackResponseAttributes response : responses) {
            // New recipient, add response package to map.
            if (!(response.recipientEmail.equals(prevRecipient)) && prevRecipient != null) {
                // Put previous giver responses into inner map.
                responsesToOneRecipient.put(giverName, responsesFromOneGiverToOneRecipient);
                // Put all responses for previous recipient into outer map.
                sortedMap.put(recipientName, responsesToOneRecipient);
                // Clear responses
                responsesToOneRecipient = new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            } else if (!(response.giverEmail.equals(prevGiver)) && prevGiver != null) {
                // New giver, add giver responses to response package for one recipient
                responsesToOneRecipient.put(giverName, responsesFromOneGiverToOneRecipient);
                // Clear response list
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            }
        
            responsesFromOneGiverToOneRecipient.add(response);
        
            prevGiver = response.giverEmail;
            prevRecipient = response.recipientEmail;
            recipientName = this.getRecipientNameForResponse(response);
            recipientTeamName = this.getTeamNameForEmail(response.recipientEmail);
            recipientName = this.appendTeamNameToName(recipientName,
                                                      recipientTeamName);
            giverName = this.getGiverNameForResponse(response);
            giverTeamName = this.getTeamNameForEmail(response.giverEmail);
            giverName = this.appendTeamNameToName(giverName, giverTeamName);
        }
        
        if (!(responses.isEmpty())) {
            // Put responses for final giver
            responsesToOneRecipient.put(giverName, responsesFromOneGiverToOneRecipient);
            sortedMap.put(recipientName, responsesToOneRecipient);
        }
        
        return sortedMap;
    }
    
    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by recipientName > giverName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by recipient identifier > giver identifier > question number.
     * @see {@link getResponsesSortedByRecipient}. 
     */
    public LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipientGiverQuestion() {
        
        LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        Collections.sort(responses, compareByTeamRecipientGiverQuestion);
   
        
        String prevGiver = null;
        String prevRecipient = null;
        String recipient = null;
        String giver = null;
        
        List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                new ArrayList<FeedbackResponseAttributes>();
        LinkedHashMap<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
        
        for (FeedbackResponseAttributes response : responses) {
            // New recipient, add response package to map.
            if (!(response.recipientEmail.equals(prevRecipient)) && prevRecipient != null) {
                // Put previous giver responses into inner map.
                responsesToOneRecipient.put(giver, responsesFromOneGiverToOneRecipient);
                // Put all responses for previous recipient into outer map.
                sortedMap.put(recipient, responsesToOneRecipient);
                // Clear responses
                responsesToOneRecipient = new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            } else if (!(response.giverEmail.equals(prevGiver)) && prevGiver != null) {
                // New giver, add giver responses to response package for one recipient
                responsesToOneRecipient.put(giver, responsesFromOneGiverToOneRecipient);
                // Clear response list
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            }
        
            responsesFromOneGiverToOneRecipient.add(response);
        
            prevGiver = response.giverEmail;
            prevRecipient = response.recipientEmail;
            
            recipient = response.recipientEmail;
            giver = response.giverEmail;
        }
        
        if (!(responses.isEmpty())) {
            // Put responses for final giver
            responsesToOneRecipient.put(giver, responsesFromOneGiverToOneRecipient);
            sortedMap.put(recipient, responsesToOneRecipient);
        }
        
        return sortedMap;
    }

    /**
     * Returns responses as a Map<giverName, Map<question, List<response>>>
     * Where the responses are sorted in the order of giver, question, recipient.
     * @param sortByTeam
     * @return responses sorted by Giver > Question > Recipient
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
                getResponsesSortedByGiverQuestionRecipient(boolean sortByTeam) {
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver = null;
        List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion = null;

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverQuestionTeamRecipient);
        } else {
            Collections.sort(responses, compareByGiverQuestionTeamRecipient);
        }

        String giver = null;
        String questionId = null;

        for (FeedbackResponseAttributes response : responses) {
            if (giver == null || !response.giverEmail.equals(giver)) {
                if (questionId != null && responsesFromOneGiverOneQuestion != null
                 && responsesFromOneGiver != null) {
                    responsesFromOneGiver.put(questions.get(questionId),
                                              responsesFromOneGiverOneQuestion);
                }
                if (giver != null && responsesFromOneGiver != null) {
                    sortedMap.put(giver, responsesFromOneGiver);
                }
                responsesFromOneGiver = new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
                giver = response.giverEmail;
            
                questionId = null;
            }
            if (questionId == null || !response.feedbackQuestionId.equals(questionId)) {
                if (questionId != null && responsesFromOneGiverOneQuestion != null) {
                    responsesFromOneGiver.put(questions.get(questionId),
                                              responsesFromOneGiverOneQuestion);
                }
                responsesFromOneGiverOneQuestion = new ArrayList<FeedbackResponseAttributes>();
                questionId = response.feedbackQuestionId;
            }
            responsesFromOneGiverOneQuestion.add(response);
        }
        if (questionId != null && responsesFromOneGiverOneQuestion != null
         && responsesFromOneGiver != null) {
            responsesFromOneGiver.put(questions.get(questionId),
                                      responsesFromOneGiverOneQuestion);
        }
        if (giver != null && responsesFromOneGiver != null) {
            sortedMap.put(giver, responsesFromOneGiver);
        }

        return sortedMap;
    }
    
    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
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
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();
        
        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        } else {
            Collections.sort(responses, compareByGiverRecipientQuestion);
        }
        
        String prevRecipient = null;
        String prevGiver = null;
        String recipientName = null;
        String giverName = null;
        String recipientTeamName = null;
        String giverTeamName = null;
        
        List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
        
        for (FeedbackResponseAttributes response : responses) {
            // New recipient, add response package to map.
            if (!(response.giverEmail.equals(prevGiver)) && prevGiver != null) {
                // Put previous recipient responses into inner map.
                responsesFromOneGiver.put(recipientName, responsesFromOneGiverToOneRecipient);
                // Put all responses for previous giver into outer map.
                sortedMap.put(giverName, responsesFromOneGiver);
                // Clear responses
                responsesFromOneGiver = new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            } else if (!(response.recipientEmail.equals(prevRecipient)) && prevRecipient != null) {
                // New recipient, add recipient responses to response package for one giver
                responsesFromOneGiver.put(recipientName, responsesFromOneGiverToOneRecipient);
                // Clear response list
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            }
        
            responsesFromOneGiverToOneRecipient.add(response);
        
            prevRecipient = response.recipientEmail;
            prevGiver = response.giverEmail;
            recipientName = this.getRecipientNameForResponse(response);
            recipientTeamName = this.getTeamNameForEmail(response.recipientEmail);
            recipientName = this.appendTeamNameToName(recipientName, recipientTeamName);
            giverName = this.getGiverNameForResponse(response);
            giverTeamName = this.getTeamNameForEmail(response.giverEmail);
            giverName = this.appendTeamNameToName(giverName, giverTeamName);
        }
        
        if (!(responses.isEmpty())) {
            // Put responses for final recipient
            responsesFromOneGiver.put(recipientName, responsesFromOneGiverToOneRecipient);
            sortedMap.put(giverName, responsesFromOneGiver);
        }
        
        return sortedMap;
    }
    
    /**
     *  Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by giverName > recipientName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by giver's identifier > recipient's identifier > question number.
     * @see {@link getResponsesSortedByGiver}. 
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
                getResponsesSortedByGiverRecipientQuestion() {
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();
        Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        
        
        String prevRecipient = null;
        String prevGiver = null;
        
        
        List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                new ArrayList<FeedbackResponseAttributes>();
        Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
        
        for (FeedbackResponseAttributes response : responses) {
            // New recipient, add response package to map.
            if (!(response.giverEmail.equals(prevGiver)) && prevGiver != null) {
                // Put previous recipient responses into inner map.
                responsesFromOneGiver.put(prevRecipient, responsesFromOneGiverToOneRecipient);
                // Put all responses for previous giver into outer map.
                sortedMap.put(prevGiver, responsesFromOneGiver);
                // Clear responses
                responsesFromOneGiver = new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            } else if (!(response.recipientEmail.equals(prevRecipient)) && prevRecipient != null) {
                // New recipient, add recipient responses to response package for one giver
                responsesFromOneGiver.put(prevRecipient, responsesFromOneGiverToOneRecipient);
                // Clear response list
                responsesFromOneGiverToOneRecipient = new ArrayList<FeedbackResponseAttributes>();
            }
        
            responsesFromOneGiverToOneRecipient.add(response);
        
            prevRecipient = response.recipientEmail;
            prevGiver = response.giverEmail;
        }
        
        if (!(responses.isEmpty())) {
            // Put responses for final recipient
            responsesFromOneGiver.put(prevRecipient, responsesFromOneGiverToOneRecipient);
            sortedMap.put(prevGiver, responsesFromOneGiver);
        }
        
        return sortedMap;
    }

    public boolean isStudentHasSomethingNewToSee(StudentAttributes student) {
        for (FeedbackResponseAttributes response : responses) {
            // There is a response not written by the student 
            // which is visible to the student 
            if (!response.giverEmail.equals(student.email)) {
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
        Map<String, Set<String>> teamNameToEmails = new HashMap<String, Set<String>>();

        for (StudentAttributes student : students) {
            String studentTeam = student.team;
            Set<String> studentEmails;

            if (teamNameToEmails.containsKey(studentTeam)) {
                studentEmails = teamNameToEmails.get(studentTeam);
            } else {
                studentEmails = new TreeSet<String>();
            }

            studentEmails.add(student.email);
            teamNameToEmails.put(studentTeam, studentEmails);
        }

        List<InstructorAttributes> instructors = courseroster.getInstructors();
        String instructorsTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        Set<String> instructorEmails = new HashSet<String>();

        for (InstructorAttributes instructor : instructors) {
            instructorEmails.add(instructor.email);
            teamNameToEmails.put(instructorsTeam, instructorEmails);
        }

        return teamNameToEmails;
    }

    private Map<String, Set<String>> getSectionToTeamNamesFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Set<String>> sectionToTeam = new HashMap<String, Set<String>>();

        for (StudentAttributes student : students) {
            String studentSection = student.section;
            String studentTeam = student.team;
            Set<String> teamNames;

            if (sectionToTeam.containsKey(studentSection)) {
                teamNames = sectionToTeam.get(studentSection);
            } else {
                teamNames = new HashSet<String>();
            }

            teamNames.add(studentTeam);
            sectionToTeam.put(studentSection, teamNames);
        }

        return sectionToTeam;
    }

    @SuppressWarnings("unused")
    // TODO unused. Can remove?
    private void ________________COMPARATORS_____________() {
    }

    // Sorts by giverName > recipientName
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            int order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            String resp1 = o1.getResponseDetails().getAnswerString();
            String resp2 = o2.getResponseDetails().getAnswerString();
            order = compareByNames(resp1, resp2);
            return order;
        }
    };

    // Sorts by giverName > recipientName > qnNumber
    // General questions and team questions at the bottom.
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipientQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order;
        }
    };

    // Sorts by teamName > giverName > recipientName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByTeamGiverRecipientQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            String t1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                      : getTeamNameForEmail(o1.giverEmail);
            String t2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                      : getTeamNameForEmail(o2.giverEmail);
            order = t1.compareTo(t2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order;
        }
    };

    // Sorts by recipientName > giverName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByRecipientGiverQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order;
        }
    };

    // Sorts by teamName > recipientName > giverName > qnNumber
    public final Comparator<FeedbackResponseAttributes> compareByTeamRecipientGiverQuestion =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            String t1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                          : getTeamNameForEmail(o1.recipientEmail);
            String t2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                          : getTeamNameForEmail(o2.recipientEmail);
            order = t1.compareTo(t2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            return order;
        }
    };

    // Sorts by giverName > question > recipientTeam > recipientName
    public final Comparator<FeedbackResponseAttributes> compareByGiverQuestionTeamRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String t1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                          : getTeamNameForEmail(o1.recipientEmail);
            String t2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                          : getTeamNameForEmail(o2.recipientEmail);
            order = t1.compareTo(t2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            return order;
        }
    };

    // Sorts by giverTeam > giverName > question > recipientTeam > recipientName
    public final Comparator<FeedbackResponseAttributes> compareByTeamGiverQuestionTeamRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String giverSection1 = o1.giverSection;
            String giverSection2 = o2.giverSection;
            int order = giverSection1.compareTo(giverSection2);
            if (order != 0) {
                return order;
            }

            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                              : getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                              : getTeamNameForEmail(o2.giverEmail);
            order = giverTeam1.compareTo(giverTeam2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String receiverTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                                     : getTeamNameForEmail(o1.recipientEmail);
            String receiverTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                                     : getTeamNameForEmail(o2.recipientEmail);
            order = receiverTeam1.compareTo(receiverTeam2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            return order;
        }
    };

    // Sorts by recipientName > question > giverTeam > giverName
    public final Comparator<FeedbackResponseAttributes> compareByRecipientQuestionTeamGiver =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String t1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                      : getTeamNameForEmail(o1.giverEmail);
            String t2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                      : getTeamNameForEmail(o2.giverEmail);
            order = t1.compareTo(t2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            return order;
        }
    };

    // Sorts by recipientTeam > recipientName > question > giverTeam > giverName
    public final Comparator<FeedbackResponseAttributes> compareByTeamRecipientQuestionTeamGiver =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String recipientSection1 = o1.recipientSection;
            String recipientSection2 = o2.recipientSection;
            int order = recipientSection1.compareTo(recipientSection2);
            if (order != 0) {
                return order;
            }

            String recipientTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                                      : getTeamNameForEmail(o1.recipientEmail);
            String recipientTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                                      : getTeamNameForEmail(o2.recipientEmail);
            order = recipientTeam1.compareTo(recipientTeam2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                              : getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                              : getTeamNameForEmail(o2.giverEmail);
            order = giverTeam1.compareTo(giverTeam2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            return order;
        }
    };

    // Sorts by recipientTeam > question > recipientName > giverTeam > giverName
    public final Comparator<FeedbackResponseAttributes> compareByTeamQuestionRecipientTeamGiver =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String recipientTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                                      : getTeamNameForEmail(o1.recipientEmail);
            String recipientTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                                      : getTeamNameForEmail(o2.recipientEmail);
            int order = recipientTeam1.compareTo(recipientTeam2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            if (order != 0) {
                return order;
            }

            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                              : getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                              : getTeamNameForEmail(o2.giverEmail);
            order = giverTeam1.compareTo(giverTeam2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            return order;
        }
    };

    // Sorts by giverTeam > question > giverName > recipientTeam > recipientName
    public final Comparator<FeedbackResponseAttributes> compareByTeamQuestionGiverTeamRecipient =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String giverTeam1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                              : getTeamNameForEmail(o1.giverEmail);
            String giverTeam2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                              : getTeamNameForEmail(o2.giverEmail);
            int order = giverTeam1.compareTo(giverTeam2);
            if (order != 0) {
                return order;
            }

            order = compareByQuestionNumber(o1, o2);
            if (order != 0) {
                return order;
            }

            String giverName1 = emailNameTable.get(o1.giverEmail);
            String giverName2 = emailNameTable.get(o2.giverEmail);
            order = compareByNames(giverName1, giverName2);
            if (order != 0) {
                return order;
            }

            String receiverTeam1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                                     : getTeamNameForEmail(o1.recipientEmail);
            String receiverTeam2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                                     : getTeamNameForEmail(o2.recipientEmail);
            order = receiverTeam1.compareTo(receiverTeam2);
            if (order != 0) {
                return order;
            }

            String recipientName1 = emailNameTable.get(o1.recipientEmail);
            String recipientName2 = emailNameTable.get(o2.recipientEmail);
            order = compareByNames(recipientName1, recipientName2);
            return order;
        }
    };

    // Sorts by questionNumber
    public final Comparator<FeedbackResponseAttributes> compareByQuestionNumber =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            return compareByQuestionNumber(o1, o2);
        }
    };

    // Sorts by recipientName > recipientEmail > giverName > giverEmail
    public final Comparator<FeedbackResponseAttributes> compareByRecipientNameEmailGiverNameEmail =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            // Compare by Recipient Name
            int recipientNameCompareResult = compareByNames(getNameForEmail(o1.recipientEmail),
                                                            getNameForEmail(o2.recipientEmail));
            if (recipientNameCompareResult != 0) {
                return recipientNameCompareResult;
            }
            
            // Compare by Recipient Email
            int recipientEmailCompareResult = compareByNames(o1.recipientEmail, o2.recipientEmail);
            if (recipientEmailCompareResult != 0) {
                return recipientEmailCompareResult;
            }
            
            // Compare by Giver Name            
            int giverNameCompareResult = compareByNames(getNameForEmail(o1.giverEmail),
                                                        getNameForEmail(o2.giverEmail));
            if (giverNameCompareResult != 0) {
                return giverNameCompareResult;
            }
            
            // Compare by Giver Email
            int giverEmailCompareResult = compareByNames(o1.giverEmail, o2.giverEmail);
            if (giverEmailCompareResult != 0) {
                return giverEmailCompareResult;
            }
            
            return 0;
        }
    };

    // Sorts by giverName
    public final Comparator<FeedbackResponseAttributes> compareByGiverName =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            return compareByNames(getNameForEmail(o1.giverEmail),
                                  getNameForEmail(o2.giverEmail));
        }
    };

    // Sorts by recipientTeamName
    public final Comparator<FeedbackResponseAttributes> compareByRecipientTeamName =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String t1 = getTeamNameForEmail(o1.recipientEmail).equals("") ? getNameForEmail(o1.recipientEmail)
                                                                          : getTeamNameForEmail(o1.recipientEmail);
            String t2 = getTeamNameForEmail(o2.recipientEmail).equals("") ? getNameForEmail(o2.recipientEmail)
                                                                          : getTeamNameForEmail(o2.recipientEmail);
            return t1.compareTo(t2);
        }
    };

    // Sorts by giverTeamName
    public final Comparator<FeedbackResponseAttributes> compareByGiverTeamName =
            new Comparator<FeedbackResponseAttributes>() {
        @Override
        public int compare(FeedbackResponseAttributes o1,
                           FeedbackResponseAttributes o2) {
            String t1 = getTeamNameForEmail(o1.giverEmail).equals("") ? getNameForEmail(o1.giverEmail)
                                                                      : getTeamNameForEmail(o1.giverEmail);
            String t2 = getTeamNameForEmail(o2.giverEmail).equals("") ? getNameForEmail(o2.giverEmail)
                                                                      : getTeamNameForEmail(o2.giverEmail);
            return t1.compareTo(t2);
        }
    };

    private int compareByQuestionNumber(FeedbackResponseAttributes r1,
                                        FeedbackResponseAttributes r2) {
        FeedbackQuestionAttributes q1 = questions.get(r1.feedbackQuestionId);
        FeedbackQuestionAttributes q2 = questions.get(r2.feedbackQuestionId);
        if (q1 == null || q2 == null) {
            return 0;
        } else {
            return q1.compareTo(q2);
        }
    }

    private int compareByNames(String n1, String n2) {
        // Make class feedback always appear on top, and team responses at bottom.
        int n1Priority = 0;
        int n2Priority = 0;

        if (n1.equals(Const.USER_IS_NOBODY)) {
            n1Priority = -1;
        } else if (n1.equals(Const.USER_IS_TEAM)) {
            n1Priority = 1;
        }
        if (n2.equals(Const.USER_IS_NOBODY)) {
            n2Priority = -1;
        } else if (n2.equals(Const.USER_IS_TEAM)) {
            n2Priority = 1;
        }

        int order = Integer.compare(n1Priority, n2Priority);
        return order == 0 ? n1.compareTo(n2) : order;
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


}
