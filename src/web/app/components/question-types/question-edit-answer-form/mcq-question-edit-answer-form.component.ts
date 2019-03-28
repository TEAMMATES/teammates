import { Component, Input, OnInit } from '@angular/core';

import {
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
} from '../../../../types/api-output';
import { DEFAULT_MCQ_QUESTION_DETAILS, DEFAULT_MCQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The Mcq question submission form for a recipient.
 */
@Component({
  selector: 'tm-mcq-question-edit-answer-form',
  templateUrl: './mcq-question-edit-answer-form.component.html',
  styleUrls: ['./mcq-question-edit-answer-form.component.scss'],
})
export class McqQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent<FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails> implements OnInit {

  @Input()
  id: string = '';

  isMcqOptionSelected: boolean[] = Array(this.questionDetails.numOfMcqChoices).fill(false);
  indexOfPreviousOptionSelected: number = 0;

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS(), DEFAULT_MCQ_RESPONSE_DETAILS());
  }

  ngOnInit(): void {
    if (this.responseDetails.answer !== '' && !this.responseDetails.isOther) {
      const indexOfAnswerInPreviousSubmission: number =
          this.questionDetails.mcqChoices.indexOf(this.responseDetails.answer);
      this.isMcqOptionSelected[indexOfAnswerInPreviousSubmission] = true;
      this.indexOfPreviousOptionSelected = indexOfAnswerInPreviousSubmission;
    }
  }

  /**
   * Updates the other option radio box when clicked.
   */
  updateIsOtherOption(): void {
    this.responseDetails.isOther = !this.responseDetails.isOther;
    this.isMcqOptionSelected[this.indexOfPreviousOptionSelected] = false;
    if (!this.responseDetails.isOther) {
      this.responseDetails.otherFieldContent = '';
    } else {
      this.responseDetails.answer = '';
    }
  }

  /**
   * Updates the other field content.
   */
  updateOtherOptionText(otherOptionText: string): void {
    this.responseDetails.otherFieldContent = otherOptionText;
  }

  /**
   * Updates the answer to the Mcq option specified by the index.
   */
  updateSelectedMcqOption(index: number): void {
    this.responseDetails.isOther = false;
    this.responseDetails.otherFieldContent = '';
    this.isMcqOptionSelected[this.indexOfPreviousOptionSelected] = false;
    this.isMcqOptionSelected[index] = true;
    this.indexOfPreviousOptionSelected = index;
    this.responseDetails.answer = this.questionDetails.mcqChoices[index];
  }

}
