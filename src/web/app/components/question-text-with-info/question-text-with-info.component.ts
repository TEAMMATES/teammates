import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackQuestionType } from '../../../types/api-output';

/**
 * Question text with toggle-able additional info.
 */
@Component({
  selector: 'tm-question-text-with-info',
  templateUrl: './question-text-with-info.component.html',
  styleUrls: ['./question-text-with-info.component.scss'],
})
export class QuestionTextWithInfoComponent implements OnInit {

  @Input() questionNumber: number = 0;
  @Input() questionDetails: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };
  @Input() shouldShowDownloadQuestionResult: boolean = false;

  @Output() downloadQuestionResultEvent: EventEmitter<any> = new EventEmitter();

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  additionalInfoIsExpanded: boolean = false;

  constructor() { }

  /**
   * Returns true if the question has additional info.
   */
  hasAdditionalInfo(questionDetails: FeedbackQuestionDetails): boolean {
    return questionDetails.questionType !== FeedbackQuestionType.TEXT;
  }

  ngOnInit(): void {
  }

}
