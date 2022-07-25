import { ApiConst } from './api-const';

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
   * Max length for the 'Name' field.
   */
  PERSON_NAME_MAX_LENGTH = ApiConst.PERSON_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Section Name' field.
   */
  SECTION_NAME_MAX_LENGTH = ApiConst.SECTION_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Team Name' field.
   */
  TEAM_NAME_MAX_LENGTH = ApiConst.TEAM_NAME_MAX_LENGTH,

  /**
   * Max length for the 'E-mail Address' field.
   */
  EMAIL_MAX_LENGTH = ApiConst.EMAIL_MAX_LENGTH,
}
