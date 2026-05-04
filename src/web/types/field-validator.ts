import { ApiConst, ApiStringConst } from './api-const';

/**
 * The maximum length of the feedback session name.
 */
export const FEEDBACK_SESSION_NAME_MAX_LENGTH: number = ApiConst.FEEDBACK_SESSION_NAME_MAX_LENGTH;

/**
 * Max length for the 'Course ID' field.
 */
export const COURSE_ID_MAX_LENGTH: number = ApiConst.COURSE_ID_MAX_LENGTH;

/**
 * Max length for the 'Course Name' field.
 */
export const COURSE_NAME_MAX_LENGTH: number = ApiConst.COURSE_NAME_MAX_LENGTH;

/**
 * Max length for the 'Student Name' field.
 */
export const STUDENT_NAME_MAX_LENGTH: number = ApiConst.STUDENT_NAME_MAX_LENGTH;

/**
 * Max length for the 'Section Name' field.
 */
export const SECTION_NAME_MAX_LENGTH: number = ApiConst.SECTION_NAME_MAX_LENGTH;

/**
 * Max length for the 'Team Name' field.
 */
export const TEAM_NAME_MAX_LENGTH: number = ApiConst.TEAM_NAME_MAX_LENGTH;

/**
 * Max length for the 'E-mail Address' field.
 */
export const EMAIL_MAX_LENGTH: number = ApiConst.EMAIL_MAX_LENGTH;

/**
 * Regex used to verify emails in the back-end.
 */
export const EMAIL_REGEX: string = ApiStringConst.EMAIL_REGEX;

/**
 * Regex used to verify names.
 *
 * Based on back-end's `FieldValidator.REGEX_NAME`.
 * The back-end regex is not converted to use here as the pattern syntax is not accepted in JS.
 */
export const NAME_REGEX = '^[a-zA-Z0-9][^|%]*$';

/**
 * Regex used to verify country names.
 *
 * Based on back-end's `FieldValidator.REGEX_NAME`, but without needing to start with alphanumeric
 * as the country is added to the end of the combined institute string.
 */
export const COUNTRY_REGEX = '^[^|%]*$';

/**
 * Max length for institution name in account request. (to be combined with country)
 */
export const INSTITUTION_NAME_MAX_LENGTH = 86;

/**
 * Max length for country in account request. (to be combined with institution name)
 */
export const COUNTRY_NAME_MAX_LENGTH = 40;
