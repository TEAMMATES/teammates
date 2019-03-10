import { Component } from '@angular/core';
import { FeedbackRubricQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RUBRIC_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-additional-info',
  templateUrl: './rubric-question-additional-info.component.html',
  styleUrls: ['./rubric-question-additional-info.component.scss'],
})
export class RubricQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackRubricQuestionDetails> {

  constructor() {
    super(DEFAULT_RUBRIC_QUESTION_DETAILS());
  }

}
