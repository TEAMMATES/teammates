import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';
import { FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import { WheelDisablerDirective } from '../../wheel-disabler/wheel-disabler.directive';

/**
 * The constsum question recipients submission form for a recipient.
 */
@Component({
  selector: 'tm-constsum-recipients-question-edit-answer-form',
  templateUrl: './constsum-recipients-question-edit-answer-form.component.html',
  imports: [FormsModule, WheelDisablerDirective],
})
export class ConstsumRecipientsQuestionEditAnswerFormComponent extends QuestionEditAnswerFormComponent<FeedbackConstantSumResponseDetails> {
  Math: typeof Math = Math;

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
