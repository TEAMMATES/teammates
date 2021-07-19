import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackSession, FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../../types/api-output';

/**
 * Button for instructor moderating responses.
 */
@Component({
  selector: 'tm-response-moderation-button',
  templateUrl: './response-moderation-button.component.html',
  styleUrls: ['./response-moderation-button.component.scss'],
})
export class ResponseModerationButtonComponent implements OnInit {

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

  @Input()
  relatedGiverEmail: string = '';

  @Input()
  moderatedQuestionId: string = '';

  @Input()
  isGiverInstructor: boolean = false;

  @Input()
  btnStyle: 'PRIMARY' | 'LIGHT' = 'LIGHT';

  constructor() { }

  ngOnInit(): void { }

}
