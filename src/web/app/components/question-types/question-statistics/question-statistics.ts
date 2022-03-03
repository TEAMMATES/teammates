import { Directive, Input } from '@angular/core';
import {
  ContributionStatistics, ContributionStatisticsEntry,
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackResponseDetails,
} from '../../../../types/api-output';

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

/**
 * The abstract question statistics.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class QuestionStatistics<Q extends FeedbackQuestionDetails, R extends FeedbackResponseDetails> {

  @Input() responses: Response<R>[] = [];
  @Input() question: Q;
  @Input() recipientType: FeedbackParticipantType = FeedbackParticipantType.NONE;
  @Input() isStudent: boolean = false;

  constructor(question: Q) {
    this.question = question;
  }

  static appendStats = (prevStats: string, newStats: string): string => {

    if (prevStats === '') {
      return newStats;
    }
    if (newStats === '') {
      return prevStats;
    }

    // Stats not being empty means it belongs to contribution question
    const prevStatsJSON: ContributionStatistics = JSON.parse(prevStats);
    const newStatsJSON: ContributionStatistics = JSON.parse(newStats);
    for (const email of Object.keys(newStatsJSON.results)) {
      const newStatsEntryForEmail: ContributionStatisticsEntry = newStatsJSON.results[email];
      const { claimed }: { claimed: number } = newStatsEntryForEmail;
      const { perceived }: { perceived: number } = newStatsEntryForEmail;
      if (claimed < 0 && perceived < 0) {
        continue;
      }
      // If new entry has submitted stats, overwrite the old data
      prevStatsJSON.results[email] = newStatsEntryForEmail;
    }

    return JSON.stringify(prevStatsJSON);
  };

}
