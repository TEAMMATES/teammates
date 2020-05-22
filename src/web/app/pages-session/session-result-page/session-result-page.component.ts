import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  QuestionOutput,
  ResponseVisibleSetting,
  SessionResults,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Feedback session result page.
 */
@Component({
  selector: 'tm-session-result-page',
  templateUrl: './session-result-page.component.html',
  styleUrls: ['./session-result-page.component.scss'],
})
export class SessionResultPageComponent implements OnInit {

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
  questions: QuestionOutput[] = [];
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';

  constructor(private feedbackSessionsService: FeedbackSessionsService, private route: ActivatedRoute,
      private timezoneService: TimezoneService, private statusMessageService: StatusMessageService) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const { courseid: courseId, fsname: feedbackSessionName }: Record<string, string> = queryParams;
      this.feedbackSessionsService.getFeedbackSession({
        courseId,
        feedbackSessionName,
        intent: Intent.STUDENT_RESULT,
      }).subscribe((feedbackSession: FeedbackSession) => {
        const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
        this.session = feedbackSession;
        this.formattedSessionOpeningTime =
            moment(this.session.submissionStartTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.formattedSessionClosingTime =
              moment(this.session.submissionEndTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.feedbackSessionsService.getFeedbackSessionsResult({
          courseId,
          feedbackSessionName,
          intent: Intent.STUDENT_RESULT,
        }).subscribe((sessionResults: SessionResults) => {
          this.questions = sessionResults.questions.sort(
              (a: QuestionOutput, b: QuestionOutput) =>
                  a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    });
  }

}
