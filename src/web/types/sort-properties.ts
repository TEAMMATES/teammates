/**
 * Elements that tables are sorted by.
 */
export enum SortBy {
    /**
     * Nothing.
     */
    NONE,

    /**
     * Section Name.
     */
    SECTION_NAME,

    /**
     * Team name.
     */
    TEAM_NAME,

    /**
     * Respondent Name (student/instructor).
     */
    RESPONDENT_NAME,

    /**
     * The email of a respondent (student/instructor).
     */
    RESPONDENT_EMAIL,

    /**
     * The role of an instructor.
     */
    INSTRUCTOR_PERMISSION_ROLE,

    /**
     * The instructor text displayed to students.
     */
    INSTRUCTOR_DISPLAYED_TEXT,

    /**
     * Join status
     */
    JOIN_STATUS,

    /**
     * Course ID.
     */
    COURSE_ID,

    /**
     * Course Name.
     */
    COURSE_NAME,

    /**
     * The creation time of the course.
     */
    COURSE_CREATION_DATE,

    /**
     * The deletion time of the course.
     */
    COURSE_DELETION_DATE,

    /**
     * Completion status of feedback session.
     */
    SESSION_COMPLETION_STATUS,

    /**
     * Feedback session name.
     */
    SESSION_NAME,

    /**
     * Start time of the feedback session.
     */
    SESSION_START_DATE,

    /**
     * End time of the feedback session.
     */
    SESSION_END_DATE,

    /**
     * The creation time of the feedback session.
     */
    SESSION_CREATION_DATE,

    /**
     * The time when the feedback session is moved to recycle bin.
     */
    SESSION_DELETION_DATE,

    /**
     * Feedback question type.
     */
    QUESTION_TYPE,

    /**
     * Feedback question text (brief).
     */
    QUESTION_TEXT,

    /**
     * Team of the giver of the feedback response.
     */
    GIVER_TEAM,

    /**
     * Name of the giver of the feedback response.
     */
    GIVER_NAME,

    /**
     * Team of the recipient of the feedback response.
     */
    RECIPIENT_TEAM,

    /**
     * Name of the recipient of the feedback response.
     */
    RECIPIENT_NAME,

    /**
     * Email of the recipient of the feedback response.
     */
    RECIPIENT_EMAIL,

    /**
     * Average score of the numerical scale question.
     */
    NUMERICAL_SCALE_AVERAGE,

    /**
     * Maximum score of the numerical scale question.
     */
    NUMERICAL_SCALE_MAX,

    /**
     * Minimum score of the numerical scale question.
     */
    NUMERICAL_SCALE_MIN,

    /**
     * Average score (exclude self-review) of the numerical scale question.
     */
    NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF,

    /**
     * Option text
     */
    MCQ_CHOICE,

    /**
     * Weight assigned to the option
     */
    MCQ_WEIGHT,

    /**
     * Number of selection of that option
     */
    MCQ_RESPONSE_COUNT,

    /**
     * Percentage of selection of that option
     */
    MCQ_PERCENTAGE,

    /**
     * Weighted percentage of selection of that option
     */
    MCQ_WEIGHTED_PERCENTAGE,

    /**
     * Recipient's Team
     */
    MCQ_TEAM,

    /**
     * Recipient's Name
     */
    MCQ_RECIPIENT_NAME,

    /**
     * Number of times option chosen
     */
    MCQ_OPTION_SELECTED_TIMES,

    /**
     * Sum of weight of options
     */
    MCQ_WEIGHT_TOTAL,

    /**
     * Average of weights
     */
    MCQ_WEIGHT_AVERAGE,

    /**
     * Option text
     */
    MSQ_CHOICE,

    /**
     * Weight assigned to the option
     */
    MSQ_WEIGHT,

    /**
     * Number of selection of that option
     */
    MSQ_RESPONSE_COUNT,

    /**
     * Percentage of selection of that option
     */
    MSQ_PERCENTAGE,

    /**
     * Weighted percentage of selection of that option
     */
    MSQ_WEIGHTED_PERCENTAGE,

    /**
     * Recipient's Team
     */
    MSQ_TEAM,

    /**
     * Recipient's Name
     */
    MSQ_RECIPIENT_NAME,

    /**
     * Number of times option chosen
     */
    MSQ_OPTION_SELECTED_TIMES,

    /**
     * Sum of weight of options
     */
    MSQ_WEIGHT_TOTAL,

    /**
     * Average of weights
     */
    MSQ_WEIGHT_AVERAGE,

    /**
     * Option to rank
     */
    RANK_OPTIONS_OPTION,

    /**
     * Overall ranking of the option
     */
    RANK_OPTIONS_OVERALL_RANK,

    /**
     * Giver's / Recipient's team
     */
    RANK_RECIPIENTS_TEAM,

    /**
     * Recipient's name
     */
    RANK_RECIPIENTS_RECIPIENT,

    /**
     * Recipient's self rank
     */
    RANK_RECIPIENTS_SELF_RANK,

    /**
     * Recipient's overall rank
     */
    RANK_RECIPIENTS_OVERALL_RANK,

    /**
     * Recipient's overall rank excluding self
     */
    RANK_RECIPIENTS_OVERALL_RANK_EXCLUDING_SELF,

    /**
     * Recipient's team rank
     */
    RANK_RECIPIENTS_TEAM_RANK,

    /**
     * Recipient's team rank excluding self
     */
    RANK_RECIPIENTS_TEAM_RANK_EXCLUDING_SELF,

    /**
     * Rubric sub question
     */
    RUBRIC_SUBQUESTION,

    /**
     * Frequency of choice
     */
    RUBRIC_CHOICE,

    /**
     * Weight average.
     */
    RUBRIC_WEIGHT_AVERAGE,

    /**
     * Total chosen weight.
     */
    RUBRIC_TOTAL_CHOSEN_WEIGHT,

    /**
     * Overall weight average.
     */
    RUBRIC_OVERALL_WEIGHT_AVERAGE,

    /**
     * Overall total weight.
     */
    RUBRIC_OVERALL_TOTAL_WEIGHT,

    /**
     * Option to constsum options
     */
    CONSTSUM_OPTIONS_OPTION,

    /**
     * Option's received/total/average points
     */
    CONSTSUM_OPTIONS_POINTS,

    /**
     * Recipient's received/total/average/average excluding self points
     */
    CONSTSUM_RECIPIENTS_POINTS,

    /**
     * Team for contribution measurement
     */
    CONTRIBUTION_TEAM,

    /**
     * Contribution recipient's name
     */
    CONTRIBUTION_RECIPIENT,

    /**
     * Amount of contribution measured in numbers
     */
    CONTRIBUTION_VALUE,

    /**
     * The creation date of a comment
     */
    COMMENTS_CREATION_DATE,

    /**
     * The type of log
     */
    LOG_TYPE,

    /**
     * The date of log
     */
    LOG_DATE,

    /**
     * The status of result view
     */
    RESULT_VIEW_STATUS,

    /**
     * The title of notification
     */
    NOTIFICATION_TITLE,

    /**
     * The start date/time of notification
     */
    NOTIFICATION_START_TIME,

    /**
     * The end date/time of notification
     */
    NOTIFICATION_END_TIME,

    /**
     * The date/time for the creation of notification
     */
    NOTIFICATION_CREATE_TIME,

    /**
     * The target user (visibility) of notification
     */
    NOTIFICATION_TARGET_USER,

    /**
     * The style of notification
     */
    NOTIFICATION_STYLE,
}

/**
 * Sort order.
 */
export enum SortOrder {
    /**
     * Descending sort order.
     */
    DESC,

    /**
     * Ascending sort order
     */
    ASC,
}
