import { Component, OnInit } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS, DEFAULT_MSQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
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
      this.responseDetails.answers.splice(0, 1);
    }
  }

  /**
   * Updates the answers to include/exclude the Msq option specified by the index.
   */
  updateSelectedAnswers(index: number): void {
    this.isMsqOptionSelected[index] = !this.isMsqOptionSelected[index];
    this.disableNoneOfTheAboveOption();
    if (this.isMsqOptionSelected[index]) {
      this.responseDetails.answers.push(this.questionDetails.msqChoices[index]);
    } else {
      const indexInResponseArray: number = this.responseDetails.answers.indexOf(this.questionDetails.msqChoices[index]);
      this.responseDetails.answers.splice(indexInResponseArray, 1);
    }
  }

  /**
   * Updates the other option checkbox when clicked.
   */
  updateIsOtherOption(): void {
    this.disableNoneOfTheAboveOption();
    this.responseDetails.isOther = !this.responseDetails.isOther;
    if (!this.responseDetails.isOther) {
      this.responseDetails.otherFieldContent = '';
    }
  }

  /**
   * Updates the other field content.
   */
  updateOtherOptionText(otherOptionText: string): void {
    this.responseDetails.otherFieldContent = otherOptionText;
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
    if (this.isNoneOfTheAboveEnabled) {
      this.responseDetails.answers.splice(0, 1);
    } else {
      this.isMsqOptionSelected = Array(this.questionDetails.msqChoices.length).fill(false);
      this.responseDetails.answers = [];
      this.responseDetails.isOther = false;
      this.responseDetails.otherFieldContent = '';
      this.responseDetails.answers[0] = NONE_OF_THE_ABOVE;
    }
  }
}
