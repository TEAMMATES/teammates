import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FeedbackQuestionType,
  QuestionOutput,
  FeedbackSession,
  SessionVisibleSetting,
  ResponseVisibleSetting,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
} from '../../../types/api-output';

@Component({
  selector: 'tm-question-response-panel',
  templateUrl: './question-response-panel.component.html',
  styleUrls: ['./question-response-panel.component.scss']
})
export class QuestionResponsePanelComponent implements OnInit {

  RESPONSE_HIDDEN_QUESTIONS: FeedbackQuestionType[] = [
    FeedbackQuestionType.CONTRIB,
  ];

  @Input()
  questions: QuestionOutput[] = [];

  @Input()
  session: FeedbackSession = {
           courseId: '',
           timeZone: '',
           feedbackSessionName: '',
           instructions: '',
           submissionStartTimestamp: 0,
           submissionEndTimestamp: 0,
           gracePeriod: 0,
           sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
           responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
           submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
           publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
           isClosingEmailEnabled: true,
           isPublishedEmailEnabled: true,
           createdAtTimestamp: 0,
         };

  @Output()
  canUserSeeResponsesEvent: EventEmitter<QuestionOutput> = new EventEmitter<QuestionOutput>()

  constructor() { }

  ngOnInit(): void {
  }

  canUserSeeResponsesHandler(question: QuestionOutput): boolean {
    this.canUserSeeResponsesEvent.emit(question)
    return false;
  }

}
