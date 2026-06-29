import { Component, Input } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FeedbackVisibilityType } from '../../../types/api-request';
import { FeedbackQuestionModel } from '../../pages-session/session-result-page/feedback-question.model';
import { SingleStatisticsComponent } from '../question-responses/single-statistics/single-statistics.component';
import { StudentViewResponsesComponent } from '../question-responses/student-view-responses/student-view-responses.component';
import { QuestionTextWithInfoComponent } from '../question-text-with-info/question-text-with-info.component';

/**
 * Displaying the question response panel.
 */
@Component({
  selector: 'tm-question-response-panel',
  templateUrl: './question-response-panel.component.html',
  styleUrls: ['./question-response-panel.component.scss'],
  imports: [QuestionTextWithInfoComponent, SingleStatisticsComponent, StudentViewResponsesComponent],
})
export class QuestionResponsePanelComponent {
  readonly RESPONSE_HIDDEN_QUESTIONS: FeedbackQuestionType[] = [FeedbackQuestionType.CONTRIB];

  @Input()
  questions: FeedbackQuestionModel[] = [];

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
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  @Input() entityType: 'student' | 'instructor' = 'student';

  @Input()
  previewAsPerson = '';

  canUserSeeResponses(question: FeedbackQuestionModel): boolean {
    const showResponsesTo: FeedbackVisibilityType[] = question.feedbackQuestion.showResponsesTo;
    if (this.entityType === 'student') {
      return (
        showResponsesTo.includes(FeedbackVisibilityType.RECIPIENT) ||
        showResponsesTo.includes(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS) ||
        showResponsesTo.includes(FeedbackVisibilityType.GIVER_TEAM_MEMBERS) ||
        showResponsesTo.includes(FeedbackVisibilityType.STUDENTS)
      );
    }
    if (this.entityType === 'instructor') {
      return showResponsesTo.includes(FeedbackVisibilityType.INSTRUCTORS);
    }
    return false;
  }
}
