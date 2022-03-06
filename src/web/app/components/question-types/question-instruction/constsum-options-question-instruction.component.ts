import { Component, Input } from '@angular/core';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * Instruction of constsum options question.
 */
@Component({
  selector: 'tm-constsum-options-question-instruction',
  templateUrl: './constsum-options-question-instruction.component.html',
  styleUrls: ['./constsum-options-question-instruction.component.scss'],
})
export class ConstsumOptionsQuestionInstructionComponent {

  @Input()
  questionDetails: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS();

  // enum
  FeedbackConstantSumDistributePointsType: typeof FeedbackConstantSumDistributePointsType =
      FeedbackConstantSumDistributePointsType;

  /**
   * Gets the total points of the constant sum question.
   */
  get totalPoints(): number {
    if (this.questionDetails.pointsPerOption) {
      return this.questionDetails.points * this.questionDetails.constSumOptions.length;
    }

    return this.questionDetails.points;
  }

}
