/**
 * Unique identifiers for each question in the sessions section of instructor help page
 */
export enum SessionsSectionQuestions {
  /**
   * Tips for conducting team peer evaluation sessions
   */
  TIPS_FOR_CONDUCTION_PEER_EVAL = 'tips-for-conducting-peer-eval',

  /**
   * How do I add questions to a session?
   */
  SESSION_QUESTIONS = 'session-questions',

  /**
   * How do I preview a session?
   */
  SESSION_PREVIEW = 'session-preview',

  /**
   * Can I submit responses on behalf of a student?
   */
  SUBMIT_FOR_STUDENT = 'submit-for-student',

  /**
   * How do I view the results of my session?
   */
  SESSION_VIEW_RESULTS = 'session-view-results',

  /**
   * How do I create a comment on a response?
   */
  SESSION_ADD_COMMENTS = 'session-add-comments',

  /**
   * How do I view sessions I have deleted?
   */
  VIEW_DELETED_SESSION = 'view-deleted-session',

  /**
   * How do I restore a deleted session?
   */
  RESTORE_SESSION = 'restore-session',

  /**
   * How do I create and schedule a new feedback session?
   */
  SESSION_NEW_FEEDBACK = 'session-new-feedback',

  /**
   * How do I view all the responses a student has given and received?
   */
  VIEW_ALL_RESPONSES = 'view-all-responses',

  /**
   * How do I edit or delete a comment on a response?
   */
  EDIT_DEL_COMMENT = 'edit-del-comment',

  /**
   * How do I permanently delete a session?
   */
  PERMANENT_DEL_SESSION = 'permenant-del-session',

  /**
   * How do I restore/delete all sessions from Recycle Bin?
   */
  RESTORE_DEL_ALL = 'restore-del-all',

  /**
   * How do I let students know about a session?
   */
  LET_STUDENT_KNOW_SESSION = 'let-student-know-session',

  /**
   * What should I do if students say they didn't receive the link to submit responses, as they were supposed to?
   */
  STUDENT_DID_NOT_RECEIVE_SUBMISSION_LINK = 'student-did-not-receive-submission-link',

  /**
   * How do I extend the deadline of a session?
   */
  EXTEND_SESSION_DEADLINE = 'extend-session-deadline',

  /**
   * Can I set a question as 'compulsory'?
   */
  SET_QUESTION_COMPULSORY = 'set-question-compulsory',

  /**
   * Can I change the visibility settings of questions after the session has started?
   */
  CHANGE_VISIBILITY_AFTER_SESSION_START = 'change-visibility-after-session-start',

  /**
   * Can I use a different team structure for different sessions in the same course?
   */
  DIFFERENT_TEAM_STRUCTURE = 'different-team-structure',

  /**
   * Can students edit responses they submitted earlier?
   */
  STUDENT_EDIT_RESPONSE = 'student-edit-response',

  /**
   * Can I see when a student tried to access the submission page?
   */
  STUDENT_ACCESS_SUBMISSION_PAGE = 'student-access-submission-page',

  /**
   * Am I able to moderate responses submitted by students?
   */
  MODERATE_RESPONSE = 'moderate-response',

  /**
   * When do students get to see the responses collected by a session?
   */
  STUDENT_SEE_RESPONSE = 'student-see-response',

  /**
   * What should I do if students say they didn't receive the link to view results,
   * even after I published the session responses?
   */
  STUDENT_DID_NOT_RECEIVE_RESULT_LINK = 'student-did-not-receive-session-link',

}
