/**
 * Represents the root FormValidator object of all form fields.
 */
export enum FormValidator {
  /**
   * Max length for the 'Course ID' field.
   */
  COURSE_ID_MAX_LENGTH = 40,

  /**
   * Max length for the 'Course Name' field.
   */
  COURSE_NAME_MAX_LENGTH = 64,

  /**
   * Max length for the 'Student Name` field.
   */
  STUDENT_NAME_MAX_LENGTH = 100,

  /**
   * Max length for the 'Section Name` field.
   */
  SECTION_NAME_MAX_LENGTH = 60,

  /**
   * Max length for the 'Team Name` field.
   */
  TEAM_NAME_MAX_LENGTH = 60,

  /**
   * Max length for the 'E-mail Address` field.
   */
  EMAIL_MAX_LENGTH = 254,
}
