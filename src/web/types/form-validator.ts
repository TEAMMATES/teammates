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
   * Regex used to verify emails in the back-end.
   */
  EMAIL_REGEX = ApiStringConst.EMAIL_REGEX,

  /**
   * Regex used to verify names.
   *
   * Based on back-end's `FieldValidator.REGEX_NAME`.
   * The back-end regex is not converted to use here as the pattern syntax is not accepted in JS.
   */
  NAME_REGEX = '^[a-zA-Z0-9][^|%]*$',

  /**
   * Regex used to verify country names.
   *
   * Based on back-end's `FieldValidator.REGEX_NAME`, but without needing to start with alphanumeric
   * as the country is added to the end of the combined institute string.
   */
  COUNTRY_REGEX = '^[^|%]*$',

  /**
   * Max length for institution name in account request. (to be combined with country)
   */
  INSTITUTION_NAME_MAX_LENGTH = 86,

  /**
   * Max length for country in account request. (to be combined with institution name)
   */
  COUNTRY_NAME_MAX_LENGTH = 40,
}
