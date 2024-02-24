import { Directive } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../../types/api-output';
import { QuestionStatistics } from '../question-statistics';

/**
 * Class to calculate stats for rank recipients question.
 */
@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class RankRecipientsQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackRankRecipientsQuestionDetails, FeedbackRankRecipientsResponseDetails> {

  emailToTeamName: Record<string, string> = {};
  emailToName: Record<string, string> = {};
  ranksReceivedPerOption: Record<string, number[]> = {};
  selfRankPerOption: Record<string, number> = {};
  rankPerOption: Record<string, number> = {};
  rankPerOptionExcludeSelf: Record<string, number> = {};
  rankPerOptionInTeam: Record<string, number> = {};
  rankPerOptionInTeamExcludeSelf: Record<string, number> = {};

  // eslint-disable-next-line @typescript-eslint/no-useless-constructor
  constructor(question: FeedbackRankRecipientsQuestionDetails) {
    super(question);
  }

  calculateStatistics(): void {
    this.emailToTeamName = {};
    this.emailToName = {};
    this.ranksReceivedPerOption = {};
    this.selfRankPerOption = {};
    this.rankPerOption = {};
    this.rankPerOptionExcludeSelf = {};
    this.rankPerOptionInTeam = {};
    this.rankPerOptionInTeamExcludeSelf = {};

    const ranksReceivedPerOptionExcludeSelf: Record<string, number[]> = {};

    const isRecipientTeam: boolean = this.recipientType === FeedbackParticipantType.TEAMS
        || this.recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF;

    const isRecipientOwnTeamMember: boolean = this.recipientType === FeedbackParticipantType.OWN_TEAM_MEMBERS
        || this.recipientType === FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

    const teamMembersPerTeam: Record<string, string[]> = {};

    for (const response of this.responses) {
      const identifier: string = isRecipientTeam ? response.recipient : (response.recipientEmail || response.recipient);

      this.ranksReceivedPerOption[identifier] = this.ranksReceivedPerOption[identifier] || [];
      this.ranksReceivedPerOption[identifier].push(response.responseDetails.answer);

      if (response.recipient === response.giver) {
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

      if (isRecipientOwnTeamMember) {
        teamMembersPerTeam[response.recipientTeam] = teamMembersPerTeam[response.recipientTeam] || [];
        if (!teamMembersPerTeam[response.recipientTeam].includes(identifier)) {
          teamMembersPerTeam[response.recipientTeam].push(identifier);
        }
      }

    }

    for (const option of Object.keys(this.ranksReceivedPerOption)) {
      this.ranksReceivedPerOption[option].sort((a: number, b: number) => a - b);
    }
    this.rankPerOption = this.calculateRankPerOption(this.ranksReceivedPerOption);
    this.rankPerOptionExcludeSelf = this.calculateRankPerOption(ranksReceivedPerOptionExcludeSelf);

    if (isRecipientOwnTeamMember) {
      this.rankPerOptionInTeam = this.calculateRankPerOptionInTeam(this.ranksReceivedPerOption, teamMembersPerTeam);
      this.rankPerOptionInTeamExcludeSelf = this.calculateRankPerOptionInTeam(ranksReceivedPerOptionExcludeSelf,
        teamMembersPerTeam);
    }

  }

  private calculateRankPerOption(ranksReceivedPerOption: Record<string, number[]>): Record<string, number> {
    const averageRanksReceivedPerOptions: Record<string, number> = {};
    for (const option of Object.keys(ranksReceivedPerOption)) {
      const answers: number[] = ranksReceivedPerOption[option];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      if (answers.length === 0) {
        // skip recipient which has no answer collected
        continue;
      }
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

  private calculateRankPerOptionInTeam(ranksReceivedPerOption: Record<string, number[]>,
    teamMembersPerTeam: Record<string, string[]>): Record<string, number> {

    const teams: string[] = Object.keys(teamMembersPerTeam);

    return teams
        .map((team: string) => teamMembersPerTeam[team])
        .map((teamMembers: string[]) => teamMembers
            .reduce((ranksReceivedPerOptionInTeam: Record<string, number[]>, teamMember: string) => {
              ranksReceivedPerOptionInTeam[teamMember] = ranksReceivedPerOption[teamMember] || [];
              return ranksReceivedPerOptionInTeam;
            }, {}))
        .map(this.calculateRankPerOption)
        .reduce((rankPerOptionInTeam: Record<string, number>, rankPerOptionInEachTeam: Record<string, number>) => {
          return { ...rankPerOptionInTeam, ...rankPerOptionInEachTeam };
        }, { });
  }

}
