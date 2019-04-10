import { Component, OnInit } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS, DEFAULT_MSQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

const NONE_OF_THE_ABOVE: string = 'None of the above';

/**
 * The Msq question submission form for a recipient.
 */
@Component({
  selector: 'tm-msq-question-edit-answer-form',
  templateUrl: './msq-question-edit-answer-form.component.html',
  styleUrls: ['./msq-question-edit-answer-form.component.scss'],
})
export class MsqQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails> implements OnInit {

  readonly NO_VALUE: number = NO_VALUE;
  isMsqOptionSelected: boolean[] = Array(this.questionDetails.msqChoices.length).fill(false);

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS(), DEFAULT_MSQ_RESPONSE_DETAILS());
  }

  ngOnInit(): void {
    if (this.responseDetails.answers[0] !== NONE_OF_THE_ABOVE) {
      for (let i: number = 0; i < this.questionDetails.msqChoices.length; i += 1) {
        const indexOfElementInAnswerArray: number
            = this.responseDetails.answers.indexOf(this.questionDetails.msqChoices[i]);
        if (indexOfElementInAnswerArray > -1) {
          this.isMsqOptionSelected[i] = true;
        }
      }
    }
  }

  /**
   * Checks if None of the above option is enabled and disables it.
   */
  disableNoneOfTheAboveOption(): void {
    if (this.isNoneOfTheAboveEnabled) {
      const answersCopy: string[] = this.responseDetails.answers.slice();
      answersCopy.splice(0 , 1);
      this.triggerResponseDetailsChange('answers', answersCopy);
    }
  }

  /**
   * Updates the answers to include/exclude the Msq option specified by the index.
   */
  updateSelectedAnswers(index: number): void {
    this.isMsqOptionSelected[index] = !this.isMsqOptionSelected[index];
    this.disableNoneOfTheAboveOption();
    const answersCopy: string[] = this.responseDetails.answers.slice();
    if (this.isMsqOptionSelected[index]) {
      answersCopy.push(this.questionDetails.msqChoices[index]);
    } else {
      const indexInResponseArray: number = this.responseDetails.answers.indexOf(this.questionDetails.msqChoices[index]);
      answersCopy.splice(indexInResponseArray, 1);
    }
    this.triggerResponseDetailsChange('answers', answersCopy);
  }

  /**
   * Updates the other option checkbox when clicked.
   */
  updateIsOtherOption(): void {
    this.disableNoneOfTheAboveOption();
    this.triggerResponseDetailsChange('isOther', !this.responseDetails.isOther);
    if (!this.responseDetails.isOther) {
      this.triggerResponseDetailsChange('otherFieldContent', '');
    }
  }

  /**
   * Updates the other field content.
   */
  updateOtherOptionText(otherOptionText: string): void {
    this.triggerResponseDetailsChange('otherFieldContent', otherOptionText);
  }

  /**
   * Checks if None of the above checkbox is enabled.
   */
  get isNoneOfTheAboveEnabled(): boolean {
    return this.responseDetails.answers[0] === NONE_OF_THE_ABOVE;
  }

  /**
   * Updates answers if None of the Above option is selected.
   */
  updateNoneOfTheAbove(): void {
    const answersCopy: string[] = this.responseDetails.answers.slice();
    if (this.isNoneOfTheAboveEnabled) {
      answersCopy.splice(0, 1);
    } else {
      this.isMsqOptionSelected = Array(this.questionDetails.msqChoices.length).fill(false);
      this.triggerResponseDetailsChange('answers', []);
      this.triggerResponseDetailsChange('isOther', false);
      this.triggerResponseDetailsChange('otherFieldContent', '');
      answersCopy[0] = NONE_OF_THE_ABOVE;
    }
    this.triggerResponseDetailsChange('answers', answersCopy);
  }
}
