import { Component, Input, OnInit } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  QuestionOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FeedbackVisibilityType, Intent } from '../../../types/api-request';

/**
 * Displaying the question response panel.
 */
@Component({
  selector: 'tm-question-response-panel',
  templateUrl: './question-response-panel.component.html',
  styleUrls: ['./question-response-panel.component.scss'],
})
export class QuestionResponsePanelComponent implements OnInit {

  readonly RESPONSE_HIDDEN_QUESTIONS: FeedbackQuestionType[] = [
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

  @Input()
  intent: Intent = Intent.STUDENT_RESULT;

  constructor() { }

  ngOnInit(): void {
  }

  canUserSeeResponses(question: QuestionOutput): boolean {
    const showResponsesTo: FeedbackVisibilityType[] = question.feedbackQuestion.showResponsesTo;

    if (this.intent === Intent.STUDENT_RESULT) {
      return showResponsesTo.filter((visibilityType: FeedbackVisibilityType) =>
          visibilityType !== FeedbackVisibilityType.INSTRUCTORS).length > 0;
    }
    if (this.intent === Intent.INSTRUCTOR_RESULT) {
      return showResponsesTo.filter((visibilityType: FeedbackVisibilityType) =>
          visibilityType === FeedbackVisibilityType.INSTRUCTORS).length > 0;
    }
    return false;
  }

}
