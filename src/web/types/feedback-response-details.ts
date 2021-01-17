import { ApiConst } from './api-const';

/**
 * Special answer of a contribution question response to indicate the response is not submitted.
 */
export const CONTRIBUTION_POINT_NOT_SUBMITTED: number = ApiConst.CONTRIBUTION_POINT_NOT_SUBMITTED;

/**
 * Special answer of a contribution question response to indicate the response is not initialized.
 *
 * <p>Used in session result.
 */
export const CONTRIBUTION_POINT_NOT_INITIALIZED: number = ApiConst.CONTRIBUTION_POINT_NOT_INITIALIZED;

/**
 * Special answer of a contribution question response for 'Not Sure' answer.
 */
export const CONTRIBUTION_POINT_NOT_SURE: number = ApiConst.CONTRIBUTION_POINT_NOT_SURE;

/**
 * Special answer of a contribution question response for 'Equal Share' answer.
 */
export const CONTRIBUTION_POINT_EQUAL_SHARE: number = ApiConst.CONTRIBUTION_POINT_EQUAL_SHARE;

/**
 * Special answer of a MSQ question indicating 'None of the above'.
 */
export const MSQ_ANSWER_NONE_OF_THE_ABOVE: string = '';

/**
 * Special answer of a numerical scale question response to indicate the response is not submitted.
 */
export const NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED: number = ApiConst.NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED;

/**
 * Special answer of a rank options question response to indicate the response is not submitted.
 */
export const RANK_OPTIONS_ANSWER_NOT_SUBMITTED: number = ApiConst.RANK_OPTIONS_ANSWER_NOT_SUBMITTED;

/**
 * Special answer of a rank recipients question response to indicate the response is not submitted.
 */
export const RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED: number = ApiConst.RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED;

/**
 * Special answer of a rubric question response to indicate no choice is chosen.
 */
export const RUBRIC_ANSWER_NOT_CHOSEN: number = -1;

/**
 * Special value to indicate whether a value is present or not.
 */
export const NO_VALUE: number = ApiConst.NO_VALUE;
