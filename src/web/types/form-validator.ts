import { ApiConst, ApiStringConst } from './api-const';

/**
 * Represents the root FormValidator object of all form fields.
 */
export enum FormValidator {
  /**
   * Max length for the 'Course ID' field.
   */
  COURSE_ID_MAX_LENGTH = ApiConst.COURSE_ID_MAX_LENGTH,

  /**
   * Max length for the 'Course Name' field.
   */
  COURSE_NAME_MAX_LENGTH = ApiConst.COURSE_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Student Name` field.
   */
  STUDENT_NAME_MAX_LENGTH = ApiConst.STUDENT_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Section Name` field.
   */
  SECTION_NAME_MAX_LENGTH = ApiConst.SECTION_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Team Name` field.
   */
  TEAM_NAME_MAX_LENGTH = ApiConst.TEAM_NAME_MAX_LENGTH,

  /**
   * Max length for the 'E-mail Address` field.
   */
  EMAIL_MAX_LENGTH = ApiConst.EMAIL_MAX_LENGTH,

  /**
   * Regex used to verify URLs as Angular does not have a built-in URL validator.
   */
  URL_REGEX = ApiStringConst.URL_REGEX,

  /**
   * Regex used to verify emails in the back-end.
   */
  EMAIL_REGEX = ApiStringConst.EMAIL_REGEX,
}
