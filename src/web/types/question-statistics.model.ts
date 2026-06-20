import { FeedbackResponseDetails } from './api-output';

/**
 * Holds some information of the feedback response.
 *
 * This is an adaptation of one of the API output format. A new interface
 * is required as the said API output format does not support precise typing
 * for the subclass of FeedbackResponseDetails.
 */
export interface Response<R extends FeedbackResponseDetails> {
  giver: string;
  giverUserId?: string;
  giverEmail?: string;
  giverTeamId?: string;
  giverTeam: string;
  giverSection: string;
  recipient: string;
  recipientUserId?: string;
  recipientEmail?: string;
  recipientTeamId?: string;
  recipientTeam: string;
  recipientSection: string;
  responseDetails: R;
}
