import { Component } from '@angular/core';

import {
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS, DEFAULT_MCQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The MCQ question submission form for a recipient.
 */
@Component({
  selector: 'tm-mcq-scale-question-edit-answer-form',
  templateUrl: './mcq-scale-question-edit-answer-form.component.html',
  styleUrls: ['./mcq-scale-question-edit-answer-form.component.scss'],
})
export class McqScaleQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails> {

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS(), DEFAULT_MCQ_RESPONSE_DETAILS());
  }

}
