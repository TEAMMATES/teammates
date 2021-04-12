import { Directive, Input } from '@angular/core';
import {
  ContributionStatistics,
  ContributionStatisticsEntry,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../../types/api-output';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../../../../types/feedback-response-details';
import { QuestionStatistics } from '../question-statistics';

interface ContributionStatisticsEntryExt extends ContributionStatisticsEntry {
  claimedOthersValues: number[];
}

/**
 * Class to calculate stats for contribution question.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export class ContributionQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails> {

  @Input() statistics: string = '';

  emailToTeamName: Record<string, string> = {};
  emailToName: Record<string, string> = {};
  emailToDiff: Record<string, number> = {};
  questionOverallStatistics?: ContributionStatistics;
  questionStatisticsForStudent?: ContributionStatisticsEntryExt;

  constructor(question: FeedbackContributionQuestionDetails) {
    super(question);
  }

  parseStatistics(): void {
    this.emailToTeamName = {};
    this.emailToName = {};

    this.questionOverallStatistics = {
      results: {},
    };
    this.questionStatisticsForStudent = {
      claimed: 0,
      perceived: 0,
      claimedOthers: {},
      claimedOthersValues: [],
      perceivedOthers: [],
    };

    if (this.statistics) {
      const statisticsObject: ContributionStatistics = JSON.parse(this.statistics);
      if (this.isStudent) {
        const results: ContributionStatisticsEntry[] = Object.values(statisticsObject.results);
        if (results.length) {
          this.questionStatisticsForStudent = {
            ...results[0],
            claimedOthersValues: Object.values(results[0].claimedOthers).sort((a: number, b: number) => b - a),
          };
        }
      } else {
        for (const response of this.responses) {
          // the recipient email will always exist for contribution question when viewing by instructors
          if (!response.recipientEmail) {
            continue;
          }

          if (!this.emailToTeamName[response.recipientEmail]) {
            this.emailToTeamName[response.recipientEmail] = response.recipientTeam;
          }
          if (!this.emailToName[response.recipientEmail]) {
            this.emailToName[response.recipientEmail] = response.recipient;
          }
        }

        this.questionOverallStatistics = statisticsObject;

        for (const email of Object.keys(this.emailToName)) {
          const statisticsForEmail: ContributionStatisticsEntry = this.questionOverallStatistics.results[email];
          const { claimed }: { claimed: number } = statisticsForEmail;
          const { perceived }: { perceived: number } = statisticsForEmail;
          if (claimed < 0 || perceived < 0) {
            this.emailToDiff[email] = CONTRIBUTION_POINT_NOT_SUBMITTED;
          } else {
            this.emailToDiff[email] = perceived - claimed;
          }
        }
      }
    }
  }
}
