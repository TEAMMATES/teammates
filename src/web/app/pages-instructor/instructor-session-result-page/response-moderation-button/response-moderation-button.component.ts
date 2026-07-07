import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
} from '../../../../types/api-output';
import { RouterLink } from '@angular/router';

/**
 * Button for instructor moderating responses.
 */
@Component({
  selector: 'tm-response-moderation-button',
  templateUrl: './response-moderation-button.component.html',
  imports: [RouterLink, NgClass],
})
export class ResponseModerationButtonComponent {
  @Input()
  session: FeedbackSession = {
    feedbackSessionId: '',
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  @Input()
  userIdForModeration = '';

  @Input()
  moderatedQuestionId = '';

  @Input()
  isGiverInstructor = false;

  @Input()
  btnStyle: 'PRIMARY' | 'LIGHT' = 'LIGHT';
}
