import { Component, Input, OnInit } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackQuestionType } from '../../../types/api-output';

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

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  additionalInfoIsExpanded: boolean = false;

  constructor() { }

  hasAdditionalInfo(questionDetails: FeedbackQuestionDetails): boolean {
    return questionDetails.questionType !== FeedbackQuestionType.TEXT;
  }

  ngOnInit(): void {
  }

}
