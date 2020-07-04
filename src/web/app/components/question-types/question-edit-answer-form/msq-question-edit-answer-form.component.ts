import { Component, ElementRef, OnChanges, OnInit, ViewChild } from '@angular/core';
import {
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS, DEFAULT_MSQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { MSQ_ANSWER_NONE_OF_THE_ABOVE, NO_VALUE } from '../../../../types/feedback-response-details';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The Msq question submission form for a recipient.
 */
@Component({
  selector: 'tm-msq-question-edit-answer-form',
  templateUrl: './msq-question-edit-answer-form.component.html',
  styleUrls: ['./msq-question-edit-answer-form.component.scss'],
})
export class MsqQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackMsqQuestionDetails, FeedbackMsqResponseDetails>
    implements OnInit, OnChanges {

  readonly NO_VALUE: number = NO_VALUE;
  isMsqOptionSelected: boolean[] = [];

  @ViewChild('inputTextBoxOther') inputTextBoxOther?: ElementRef;

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS(), DEFAULT_MSQ_RESPONSE_DETAILS());
  }

  ngOnInit(): void {
  }

  // sync the internal status with the input data
  ngOnChanges(): void {
    this.isMsqOptionSelected = Array(this.questionDetails.msqChoices.length).fill(false);
    if (!this.isNoneOfTheAboveEnabled) {
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
   * Updates the answers to include/exclude the Msq option specified by the index.
   */
  updateSelectedAnswers(index: number): void {
    let newAnswers: string[] = [];
    if (!this.isNoneOfTheAboveEnabled) {
      newAnswers = this.responseDetails.answers.slice();
    }
    const indexInResponseArray: number = this.responseDetails.answers.indexOf(this.questionDetails.msqChoices[index]);
    if (indexInResponseArray > -1) {
      newAnswers.splice(indexInResponseArray, 1);
    } else {
      newAnswers.unshift(this.questionDetails.msqChoices[index]);
    }

    this.triggerResponseDetailsChange('answers', newAnswers);
  }

  /**
   * Updates the other option checkbox when clicked.
   */
  updateIsOtherOption(): void {
    const fieldsToUpdate: any = {};
    fieldsToUpdate.isOther = !this.responseDetails.isOther;
    fieldsToUpdate.answers = this.responseDetails.answers;
    if (this.isNoneOfTheAboveEnabled) {
      fieldsToUpdate.answers = [];
    }
    if (fieldsToUpdate.isOther) {
      // create a placeholder for other answer
      fieldsToUpdate.answers.push('');
      setTimeout(() => { // focus on the text box after the isOther field is updated to enable the text box
        (this.inputTextBoxOther as ElementRef).nativeElement.focus();
      }, 0);
    } else {
      // remove other answer (last element) from the answer list
      fieldsToUpdate.answers.splice(-1, 1);
      fieldsToUpdate.otherFieldContent = '';
    }
    this.triggerResponseDetailsChangeBatch(fieldsToUpdate);
  }

  /**
   * Updates other answer field.
   */
  updateOtherAnswerField($event: string): void {
    const fieldsToUpdate: any = {};
    // we shall update both the other field content and the answer list
    fieldsToUpdate.otherFieldContent = $event;
    fieldsToUpdate.answers = this.responseDetails.answers.slice();
    fieldsToUpdate.answers[fieldsToUpdate.answers.length - 1] = $event;
    this.triggerResponseDetailsChangeBatch(fieldsToUpdate);
  }

  /**
   * Checks if None of the above checkbox is enabled.
   */
  get isNoneOfTheAboveEnabled(): boolean {
    return !this.responseDetails.isOther && this.responseDetails.answers.length === 1
        && this.responseDetails.answers[0] === MSQ_ANSWER_NONE_OF_THE_ABOVE;
  }

  /**
   * Updates answers if None of the Above option is selected.
   */
  updateNoneOfTheAbove(): void {
    this.triggerResponseDetailsChangeBatch({
      answers: this.isNoneOfTheAboveEnabled ? [] : [MSQ_ANSWER_NONE_OF_THE_ABOVE],
      isOther: false,
      otherFieldContent: '',
    });
  }
}
