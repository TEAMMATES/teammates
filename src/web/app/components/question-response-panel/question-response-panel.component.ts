import { Component, Input } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { FeedbackSessionsService } from 'src/web/services/feedback-sessions.service';
import {
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  QuestionOutput,
  ResponseVisibleSetting,
  SessionResults,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FeedbackVisibilityType, Intent } from '../../../types/api-request';
import { FeedbackQuestionModel } from '../../pages-session/session-result-page/session-result-page.component';

/**
 * Displaying the question response panel.
 */
@Component({
  selector: 'tm-question-response-panel',
  templateUrl: './question-response-panel.component.html',
  styleUrls: ['./question-response-panel.component.scss'],
})
export class QuestionResponsePanelComponent {

  readonly RESPONSE_HIDDEN_QUESTIONS: FeedbackQuestionType[] = [
    FeedbackQuestionType.CONTRIB,
  ];

  constructor(private feedbackSessionsService: FeedbackSessionsService) {};

  @Input()
  questions: FeedbackQuestionModel[] = [];

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
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  @Input()
  intent: Intent = Intent.STUDENT_RESULT;

  canUserSeeResponses(question: FeedbackQuestionModel): boolean {
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

  loadQuestionResults(question: FeedbackQuestionModel): void {
    this.feedbackSessionsService.getFeedbackSessionResults({
      questionId: question.feedbackQuestion.feedbackQuestionId,
      courseId: this.session.courseId,
      feedbackSessionName: this.session.feedbackSessionName,
      intent: this.intent,
    }).pipe(finalize(() => {
      question.isLoaded = true;
      }))
      .subscribe((sessionResults: SessionResults) => {
        const responses: QuestionOutput = sessionResults.questions[0];
        if (responses) {
          question.allResponses = responses.allResponses;
          question.otherResponses = responses.otherResponses;
          question.questionStatistics = responses.questionStatistics;
          question.responsesFromSelf = responses.responsesFromSelf;
          question.responsesToSelf = responses.responsesToSelf;
        }
        question.isLoading = false;
      });
  }

  loadQuestion(event: any, question: FeedbackQuestionModel): void {
    if (event && event.visible && !question.isLoaded && !question.isLoading) {
      question.isLoading = true;
      this.loadQuestionResults(question);
    } 
  }
}
