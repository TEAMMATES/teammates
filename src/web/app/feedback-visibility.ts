/**
 * The feedback visibility type.
 */
export enum FeedbackVisibilityType {
  /**
   * General recipient.
   */
  RECIPIENT = 'RECIPIENT',

  /**
   * Giver's team member.
   */
  GIVER_TEAM_MEMBERS = 'GIVER_TEAM_MEMBERS',

  /**
   * Recipient's team members.
   */
  RECIPIENT_TEAM_MEMBERS = 'RECIPIENT_TEAM_MEMBERS',

  /**
   * Students in the course.
   */
  STUDENTS = 'STUDENTS',

  /**
   * Instructors in the course.
   */
  INSTRUCTORS = 'INSTRUCTORS',
}

/**
 * The visibility controls.
 */
export enum VisibilityControl {

  /**
   * Show response visibility control.
   */
  SHOW_RESPONSE = 'SHOW_RESPONSE',

  /**
   * Show giver name visibility control.
   */
  SHOW_GIVER_NAME = 'SHOW_GIVER_NAME',

  /**
   * Show recipient name visibility control.
   */
  SHOW_RECIPIENT_NAME = 'SHOW_RECIPIENT_NAME',
}
