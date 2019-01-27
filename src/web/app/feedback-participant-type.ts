/**
 * Feedback participant type.
 */
export enum FeedbackParticipantType {
  // Giver: SELF, STUDENTS, INSTRUCTORS, TEAMS
  // Recipient: SELF, STUDENTS, INSTRUCTORS, TEAMS, OWN_TEAM, OWN_TEAM_MEMBERS, OWN_TEAM_MEMBERS_INCLUDING_SELF, NONE

  /**
   * The creator (i.e. instructor) of a session.
   */
  SELF = 'SELF',

  /**
   * Students in the course.
   */
  STUDENTS = 'STUDENTS',

  /**
   * Instructors in the course.
   */
  INSTRUCTORS = 'INSTRUCTORS',

  /**
   * Teams in the course.
   */
  TEAMS = 'TEAMS',

  /**
   * The team of the giver.
   */
  OWN_TEAM = 'OWN_TEAM',

  /**
   * The team members of the giver (NOT INCLUDING the giver).
   */
  OWN_TEAM_MEMBERS = 'OWN_TEAM_MEMBERS',

  /**
   * The team members of the giver (INCLUDING the giver).
   */
  OWN_TEAM_MEMBERS_INCLUDING_SELF = 'OWN_TEAM_MEMBERS_INCLUDING_SELF',

  /**
   * Nobody specific (general feedback).
   */
  NONE = 'NONE',
}
