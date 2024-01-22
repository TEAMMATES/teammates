import { Component } from '@angular/core';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';
import { FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import {
  DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS,
  DEFAULT_CONSTSUM_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * The constsum question recipients submission form for a recipient.
 */
@Component({
  selector: 'tm-constsum-recipients-question-edit-answer-form',
  templateUrl: './constsum-recipients-question-edit-answer-form.component.html',
  styleUrls: ['./constsum-recipients-question-edit-answer-form.component.scss'],
})
export class ConstsumRecipientsQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails> {

  Math: typeof Math = Math;

  constructor() {
    super(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS(), DEFAULT_CONSTSUM_RESPONSE_DETAILS());
  }

  /**
   * Assigns a point to the recipient.
   */
  triggerResponse(event: number): void {

    let newAnswers: number[] = this.responseDetails.answers.slice();
    // index 0 will the answer
    if (newAnswers.length !== 1) {
      // initialize answers array on the fly
      newAnswers = [0];
    }
    if (event == null) {
      newAnswers = [];
    } else {
      newAnswers[0] = Math.ceil(event);
    }

    this.triggerResponseDetailsChange('answers', newAnswers);
  }

}
