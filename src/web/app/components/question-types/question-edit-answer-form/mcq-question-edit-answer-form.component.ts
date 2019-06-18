import {Component, ElementRef, Input, OnChanges, OnInit, ViewChild,} from '@angular/core';

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
    extends QuestionEditAnswerFormComponent<FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails>
    implements OnInit, OnChanges {

  /**
   * The unique ID in the page where the component is used.
   *
   * <p>This is to ensure that only one MCQ option can be selected.
   */
  @Input()
  id: string = '';

  @ViewChild('inputEle') inputEle?: ElementRef;

  isMcqOptionSelected: boolean[] = [];

  constructor() {
    super(DEFAULT_MCQ_QUESTION_DETAILS(), DEFAULT_MCQ_RESPONSE_DETAILS());
  }

  ngOnInit(): void {
  }

  // sync the internal status with the input data
  ngOnChanges(): void {
    this.isMcqOptionSelected = Array(this.questionDetails.numOfMcqChoices).fill(false);
    if (this.responseDetails.answer !== '' && !this.responseDetails.isOther) {
      const indexOfAnswerInPreviousSubmission: number =
          this.questionDetails.mcqChoices.indexOf(this.responseDetails.answer);
      this.isMcqOptionSelected[indexOfAnswerInPreviousSubmission] = true;
    }
  }

  /**
   * Updates the other option radio box when clicked.
   */
  updateIsOtherOption(): void {
    const fieldsToUpdate: any = {};
    // const div = this.elementRef.nativeElement.querySelector('.form-control');
    fieldsToUpdate.isOther = !this.responseDetails.isOther;
    if (fieldsToUpdate.isOther) {
      fieldsToUpdate.answer = '';
      setTimeout(()=>{ // this will make the execution after the above boolean has changed
        (this.inputEle as ElementRef).nativeElement.focus();
      },0);
    } else {
      fieldsToUpdate.otherFieldContent = '';
    }

    this.triggerResponseDetailsChangeBatch(fieldsToUpdate);
  }

  /**
   * Updates the answer to the Mcq option specified by the index.
   */
  updateSelectedMcqOption(index: number): void {
    this.triggerResponseDetailsChangeBatch({
      isOther: false,
      otherFieldContent: '',
      answer: this.questionDetails.mcqChoices[index],
    });
  }

}
