import { Component, Input } from '@angular/core';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * Instruction of constsum recipients question.
 */
@Component({
  selector: 'tm-constsum-recipients-question-instruction',
  templateUrl: './constsum-recipients-question-instruction.component.html',
  styleUrls: ['./constsum-recipients-question-instruction.component.scss'],
})
export class ConstsumRecipientsQuestionInstructionComponent {

  @Input()
  questionDetails: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS();

  @Input()
  numOfRecipients: number = 0;

  // enum
  FeedbackConstantSumDistributePointsType: typeof FeedbackConstantSumDistributePointsType =
      FeedbackConstantSumDistributePointsType;

  /**
   * Gets the total points of the constant sum question.
   */
  get totalPoints(): number {
    if (this.questionDetails.pointsPerOption) {
      return this.questionDetails.points * this.numOfRecipients;
    }

    return this.questionDetails.points;
  }

}
