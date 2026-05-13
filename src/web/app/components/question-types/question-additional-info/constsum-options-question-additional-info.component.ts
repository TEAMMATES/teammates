import { Component, Input } from '@angular/core';
import { FeedbackConstantSumQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for constsum options questions.
 */
@Component({
  selector: 'tm-constsum-options-question-additional-info',
  templateUrl: './constsum-options-question-additional-info.component.html',
  styleUrls: ['./constsum-options-question-additional-info.component.scss'],
  imports: [],
})
export class ConstsumOptionsQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackConstantSumQuestionDetails = DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS();

  /**
   * Returns the label for the number of points.
   */
  getPointsLabel(): string {
    return this.questionDetails.pointsPerOption ? 'Points per option' : 'Total points';
  }
}
