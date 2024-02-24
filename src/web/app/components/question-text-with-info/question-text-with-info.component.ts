import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackQuestionType } from '../../../types/api-output';

/**
 * Question text with toggle-able additional info.
 */
@Component({
  selector: 'tm-question-text-with-info',
  templateUrl: './question-text-with-info.component.html',
  styleUrls: ['./question-text-with-info.component.scss'],
})
export class QuestionTextWithInfoComponent {

  @Input() questionNumber: number = 0;
  @Input() questionDetails: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };

  @Output() downloadQuestionResultEvent: EventEmitter<any> = new EventEmitter();

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  additionalInfoIsExpanded: boolean = false;

  /**
   * Returns true if the question has additional info.
   */
  hasAdditionalInfo(questionDetails: FeedbackQuestionDetails): boolean {
    return questionDetails.questionType !== FeedbackQuestionType.TEXT;
  }

}
