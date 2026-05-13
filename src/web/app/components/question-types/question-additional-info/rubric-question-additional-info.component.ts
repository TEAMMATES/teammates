import { Component, Input } from '@angular/core';
import { FeedbackRubricQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Additional info for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-additional-info',
  templateUrl: './rubric-question-additional-info.component.html',
  imports: [],
})
export class RubricQuestionAdditionalInfoComponent {
  @Input() questionDetails: FeedbackRubricQuestionDetails = DEFAULT_RUBRIC_QUESTION_DETAILS();
}
