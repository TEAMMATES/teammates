import { Component, ElementRef, OnChanges, OnInit, ViewChild } from '@angular/core';
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
      const newAnswers: string[] = this.responseDetails.answers.slice();
      newAnswers.splice(0 , 1);
      this.triggerResponseDetailsChange('answers', newAnswers);
    }
  }

  /**
   * Updates the answers to include/exclude the Msq option specified by the index.
   */
  updateSelectedAnswers(index: number): void {
    this.disableNoneOfTheAboveOption();
    const newAnswers: string[] = this.responseDetails.answers.slice();
    const indexInResponseArray: number = this.responseDetails.answers.indexOf(this.questionDetails.msqChoices[index]);
    if (indexInResponseArray > -1) {
      newAnswers.splice(indexInResponseArray, 1);
    } else {
      newAnswers.push(this.questionDetails.msqChoices[index]);
    }
    this.triggerResponseDetailsChange('answers', newAnswers);
  }

  /**
   * Updates the other option checkbox when clicked.
   */
  updateIsOtherOption(): void {
    const fieldsToUpdate: any = {};
    fieldsToUpdate.isOther = !this.responseDetails.isOther;
    this.disableNoneOfTheAboveOption();
    if (!fieldsToUpdate.isOther) {
      fieldsToUpdate.otherFieldContent = '';
    } else {
      setTimeout(() => { // focus on the text box after the isOther field is updated to enable the text box
        (this.inputTextBoxOther as ElementRef).nativeElement.focus();
      }, 0);
    }
    this.triggerResponseDetailsChangeBatch(fieldsToUpdate);
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
    let newAnswers: string[] = this.responseDetails.answers.slice();
    const fieldsToUpdate: any = {};
    if (this.isNoneOfTheAboveEnabled) {
      newAnswers.splice(0, 1);
    } else {
      this.isMsqOptionSelected = Array(this.questionDetails.msqChoices.length).fill(false);
      newAnswers = [];
      fieldsToUpdate.isOther = false;
      fieldsToUpdate.otherFieldContent = '';
      newAnswers[0] = NONE_OF_THE_ABOVE;
    }
    fieldsToUpdate.answers = newAnswers;
    this.triggerResponseDetailsChangeBatch(fieldsToUpdate);
  }
}
