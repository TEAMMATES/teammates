import { Component, Input, OnChanges, OnInit } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackResponseDetails,
} from '../../../../types/api-output';

/**
 * The component that will map a generic response statistics to its specialized view component.
 */
@Component({
  selector: 'tm-single-statistics',
  templateUrl: './single-statistics.component.html',
  styleUrls: ['./single-statistics.component.scss'],
})
export class SingleStatisticsComponent implements OnInit, OnChanges {

  @Input() responses: FeedbackResponseDetails[] = [];
  @Input() question: FeedbackQuestionDetails = {
    questionType: FeedbackQuestionType.TEXT,
    questionText: '',
  };
  @Input() recipientType: FeedbackParticipantType = FeedbackParticipantType.NONE;
  @Input() isStudent: boolean = false;
  @Input() statistics: string = '';

  // enum
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
  }

}
