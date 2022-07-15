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
   * Max length for the 'Person Name' field.
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

  /**
   * Max length for the 'Institute' field.
   */
  INSTITUTE_NAME_MAX_LENGTH = ApiConst.INSTITUTE_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Institute' field in the account request form.
   */
  ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH = ApiConst.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Country' field in the account request form.
   */
  ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH = ApiConst.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH,

  /**
   * Max length for the 'Home Page URL' field in the account request form.
   */
  ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH = ApiConst.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH,

  /**
   * Max length for the 'Other Comments' field in the account request form.
   */
  ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH = ApiConst.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH,

}
