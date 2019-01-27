/**
 * Feedback response.
 */
export interface FeedbackResponse {
  feedbackResponseId: string;

  giverIdentifier: string;

  recipientIdentifier: string;

  responseDetails: FeedbackResponseDetails;
}

/**
 * The abstract feedback question details.
 */
// tslint:disable-next-line:no-empty-interface
export interface FeedbackResponseDetails {

}

/**
 * Feedback text question response details.
 */
export interface FeedbackTextResponseDetails {
  answer: string;
}

/**
 * Feedback contribution question response details.
 */
export interface FeedbackContributionResponseDetails {
  answer: number;
}

/**
 * Special answer of a contribution question response to indicate the response is not submitted.
 */
export const CONTRIBUTION_POINT_NOT_SUBMITTED: number = -999;

/**
 * Special answer of a contribution question response for 'Not Sure' answer.
 */
export const CONTRIBUTION_POINT_NOT_SURE: number = -101;
