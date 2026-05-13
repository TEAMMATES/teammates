import { ContributionStatistics, ContributionStatisticsEntry, FeedbackResponseDetails } from './api-output';

/**
 * Holds some information of the feedback response.
 *
 * This is an adaptation of one of the API output format. A new interface
 * is required as the said API output format does not support precise typing
 * for the subclass of FeedbackResponseDetails.
 */
export interface Response<R extends FeedbackResponseDetails> {
  giver: string;
  giverEmail?: string;
  giverTeam: string;
  giverSection: string;
  recipient: string;
  recipientEmail?: string;
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

export interface ContributionQuestionStatistics {
  emailToTeamName: Record<string, string>;
  emailToName: Record<string, string>;
  emailToDiff: Record<string, number>;
  questionOverallStatistics?: ContributionStatistics;
  questionStatisticsForStudent?: ContributionStatisticsEntry & { claimedOthersValues: number[] };
}

export interface McqMsqQuestionStatistics {
  answerFrequency: Record<string, number>;
  percentagePerOption: Record<string, number>;
  weightPerOption: Record<string, number>;
  weightedPercentagePerOption: Record<string, number>;
  perRecipientResponses: Record<string, any>;
}

export type McqQuestionStatistics = McqMsqQuestionStatistics;

export interface MsqQuestionStatistics extends McqMsqQuestionStatistics {
  hasAnswers: boolean;
}

export interface NumScaleQuestionStatistics {
  teamToRecipientToScores: Record<string, Record<string, any>>;
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

export interface RubricQuestionStatistics {
  subQuestions: string[];
  choices: string[];
  hasWeights: boolean;
  weights: number[][];
  answers: number[][];
  isWeightStatsVisible: boolean;

  percentages: number[][];
  subQuestionWeightAverage: number[];
  answersExcludeSelf: number[][];
  percentagesExcludeSelf: number[][];
  subQuestionWeightAverageExcludeSelf: number[];

  perRecipientStatsMap: Record<string, RubricPerRecipientStats>;
}

export interface RubricPerRecipientStats {
  recipientName: string;
  recipientEmail?: string;
  recipientTeam: string;
  answers: number[][];
  answersSum: number[];
  percentages: number[][];
  percentagesAverage: number[];
  weightsAverage: number[];
  areSubQuestionChosenWeightsAllNull: boolean[];
  subQuestionTotalChosenWeight: number[];
  subQuestionWeightAverage: number[];
  overallWeightedSum: number;
  overallWeightAverage: number;
}
