import { Component, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for rank recipients questions.
 */
@Component({
  selector: 'tm-rank-recipients-question-statistics',
  templateUrl: './rank-recipients-question-statistics.component.html',
  styleUrls: ['./rank-recipients-question-statistics.component.scss'],
})
export class RankRecipientsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackRankRecipientsQuestionDetails, FeedbackRankRecipientsResponseDetails>
    implements OnInit, OnChanges {

  emailToTeamName: Record<string, string> = {};
  emailToName: Record<string, string> = {};
  ranksReceivedPerOption: Record<string, number[]> = {};
  selfRankPerOption: Record<string, number> = {};
  rankPerOption: Record<string, number> = {};
  rankPerOptionExcludeSelf: Record<string, number> = {};

  constructor() {
    super(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.calculateStatistics();
  }

  ngOnChanges(): void {
    this.calculateStatistics();
  }

  private calculateStatistics(): void {
    this.emailToTeamName = {};
    this.emailToName = {};
    this.ranksReceivedPerOption = {};
    this.selfRankPerOption = {};
    this.rankPerOption = {};
    this.rankPerOptionExcludeSelf = {};

    const ranksReceivedPerOptionExcludeSelf: Record<string, number[]> = {};

    const isRecipientTeam: boolean = this.recipientType === FeedbackParticipantType.TEAMS
        || this.recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF;

    for (const response of this.responses) {
      const identifier: string = isRecipientTeam ? response.recipient : (response.recipientEmail || response.recipient);

      this.ranksReceivedPerOption[identifier] = this.ranksReceivedPerOption[identifier] || [];
      this.ranksReceivedPerOption[identifier].push(response.responseDetails.answer);

      if (isRecipientTeam && response.recipient === response.giver
          || !isRecipientTeam && response.recipientEmail === response.giverEmail) {
        this.selfRankPerOption[identifier] = response.responseDetails.answer;
      } else {
        ranksReceivedPerOptionExcludeSelf[identifier] = ranksReceivedPerOptionExcludeSelf[identifier] || [];
        ranksReceivedPerOptionExcludeSelf[identifier].push(response.responseDetails.answer);
      }

      if (!this.emailToTeamName[identifier]) {
        this.emailToTeamName[identifier] = isRecipientTeam ? '' : response.recipientTeam;
      }
      if (!this.emailToName[identifier]) {
        this.emailToName[identifier] = response.recipient;
      }
    }

    this.rankPerOption = this.calculateRankPerOption(this.ranksReceivedPerOption);
    this.rankPerOptionExcludeSelf = this.calculateRankPerOption(ranksReceivedPerOptionExcludeSelf);
  }

  private calculateRankPerOption(ranksReceivedPerOption: Record<string, number[]>): Record<string, number> {
    const averageRanksReceivedPerOptions: Record<string, number> = {};
    for (const option of Object.keys(ranksReceivedPerOption)) {
      const answers: number[] = ranksReceivedPerOption[option];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      averageRanksReceivedPerOptions[option] = answers.length === 0 ? 0 : sum / answers.length;
    }

    const optionsOrderedByRank: string[] = Object.keys(averageRanksReceivedPerOptions).sort((a: string, b: string) => {
      return averageRanksReceivedPerOptions[a] - averageRanksReceivedPerOptions[b];
    });

    const rankPerOption: Record<string, number> = {};

    for (let i: number = 0; i < optionsOrderedByRank.length; i += 1) {
      const option: string = optionsOrderedByRank[i];
      if (i === 0) {
        rankPerOption[option] = 1;
        continue;
      }
      const rank: number = averageRanksReceivedPerOptions[option];
      const optionBefore: string = optionsOrderedByRank[i - 1];
      const rankBefore: number = averageRanksReceivedPerOptions[optionBefore];
      if (rank === rankBefore) {
        // If the average rank is the same, the overall rank will be the same
        rankPerOption[option] = rankPerOption[optionBefore];
      } else {
        // Otherwise, the rank is as determined by the order
        rankPerOption[option] = i + 1;
      }
    }

    return rankPerOption;
  }

}
