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

export interface ConstsumOptionsQuestionStatistics {
  pointsPerOption: Record<string, number[]>;
  totalPointsPerOption: Record<string, number>;
  averagePointsPerOption: Record<string, number>;
}

export interface ConstsumRecipientsQuestionStatistics {
  emailToTeamName: Record<string, string>;
  emailToName: Record<string, string>;
  pointsPerOption: Record<string, number[]>;
  totalPointsPerOption: Record<string, number>;
  averagePointsPerOption: Record<string, number>;
  averagePointsExcludingSelf: Record<string, number>;
}

export interface NumScaleRecipientStatistics {
  responses: { answer: number; isSelf: boolean }[];
  max?: number;
  min?: number;
  average?: number;
  averageExcludingSelf?: number;
}

export interface NumScaleQuestionStatistics {
  teamToRecipientToScores: Record<string, Record<string, NumScaleRecipientStatistics>>;
  recipientEmails: Record<string, string>;
}

export interface RankOptionsQuestionStatistics {
  ranksReceivedPerOption: Record<string, number[]>;
  rankPerOption: Record<string, number>;
}

export interface RankRecipientsQuestionStatistics {
  emailToTeamName: Record<string, string>;
  emailToName: Record<string, string>;
  ranksReceivedPerOption: Record<string, number[]>;
  selfRankPerOption: Record<string, number>;
  rankPerOption: Record<string, number>;
  rankPerOptionExcludeSelf: Record<string, number>;
  rankPerOptionInTeam: Record<string, number>;
  rankPerOptionInTeamExcludeSelf: Record<string, number>;
}

