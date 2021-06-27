import { Directive } from '@angular/core';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackParticipantType,
} from '../../../../../types/api-output';
import { QuestionStatistics } from '../question-statistics';

/**
 * Class to calculate stats for constsum recipients question.
 */
@Directive()
// tslint:disable-next-line:directive-class-suffix
export class ConstsumRecipientsQuestionStatisticsCalculation
    extends QuestionStatistics<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails> {

  emailToTeamName: Record<string, string> = {};
  emailToName: Record<string, string> = {};
  pointsPerOption: Record<string, number[]> = {};
  totalPointsPerOption: Record<string, number> = {};
  averagePointsPerOption: Record<string, number> = {};
  averagePointsExcludingSelf: Record<string, number> = {};
  totalSum: number = 0;
  totalStudents: number = 0;

  constructor(question: FeedbackConstantSumQuestionDetails) {
    super(question);
  }

  calculateStatistics(): void {
    this.emailToTeamName = {};
    this.emailToName = {};
    this.pointsPerOption = {};
    this.totalPointsPerOption = {};
    this.averagePointsPerOption = {};
    this.averagePointsExcludingSelf = {};
    this.totalSum = 0;
    this.totalStudents = 0;

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
      this.pointsPerOption[recipient].sort((a: number, b: number) => a - b);
      const answers: number[] = this.pointsPerOption[recipient];
      const sum: number = answers.reduce((a: number, b: number) => a + b, 0);
      this.totalPointsPerOption[recipient] = sum;
      this.averagePointsPerOption[recipient] = +(answers.length === 0 ? 0 : sum / answers.length).toFixed(2);
      this.totalSum += sum;
      this.totalStudents += 1;
      console.log("sum is " + sum);
      console.log("length is " + answers.length);
    }

    for (const recipient of Object.keys(this.pointsPerOption)) {
      this.averagePointsExcludingSelf[recipient] =
          +(this.totalStudents === 0 ? 0 : (this.totalSum - this.totalPointsPerOption[recipient]) / this.totalStudents).toFixed(2);
      console.log("totalSum is " + this.totalSum);
    }

  }

}
