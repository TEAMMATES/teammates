import { Component, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionStatistics } from './question-statistics';

/**
 * Statistics for constsum recipients questions.
 */
@Component({
  selector: 'tm-constsum-recipients-question-statistics',
  templateUrl: './constsum-recipients-question-statistics.component.html',
  styleUrls: ['./constsum-recipients-question-statistics.component.scss'],
})
export class ConstsumRecipientsQuestionStatisticsComponent
    extends QuestionStatistics<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails>
    implements OnInit, OnChanges {

  emailToTeamName: Record<string, string> = {};
  emailToName: Record<string, string> = {};
  pointsPerOption: Record<string, number[]> = {};
  totalPointsPerOption: Record<string, number> = {};
  averagePointsPerOption: Record<string, number> = {};

  constructor() {
    super(DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS());
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
    this.pointsPerOption = {};
    this.totalPointsPerOption = {};
    this.averagePointsPerOption = {};

    const isRecipientTeam: boolean = this.recipientType === FeedbackParticipantType.TEAMS
        || this.recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF;

    for (const response of this.responses) {
      const identifier: string = isRecipientTeam ? response.recipient : (response.recipientEmail || response.recipient);

      this.pointsPerOption[identifier] = this.pointsPerOption[identifier] || [];
      this.pointsPerOption[identifier].push(response.responseDetails.answers[0]);

      if (!this.emailToTeamName[identifier]) {
        this.emailToTeamName[identifier] = isRecipientTeam ? '' : response.recipientTeam;
      }
      if (!this.emailToName[identifier]) {
        this.emailToName[identifier] = response.recipient;
      }
    }

    for (const recipient of Object.keys(this.pointsPerOption)) {
      const answers: number[] = this.pointsPerOption[recipient];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      this.totalPointsPerOption[recipient] = sum;
      this.averagePointsPerOption[recipient] = +(answers.length === 0 ? 0 : sum / answers.length).toFixed(2);
    }
  }

}
