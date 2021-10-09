import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  QuestionOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';

/**
 * Displaying the question response panel.
 */
@Component({
  selector: 'tm-question-response-panel',
  templateUrl: './question-response-panel.component.html',
  styleUrls: ['./question-response-panel.component.scss'],
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
  canUserSeeResponsesEvent: EventEmitter<QuestionOutput> = new EventEmitter<QuestionOutput>();

  constructor() { }

  ngOnInit(): void {
  }

  canUserSeeResponsesHandler(question: QuestionOutput): boolean {
    this.canUserSeeResponsesEvent.emit(question);
    return false;
  }

}
