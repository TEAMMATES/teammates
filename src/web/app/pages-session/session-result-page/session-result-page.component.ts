import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  QuestionOutput, RegkeyValidity,
  ResponseVisibleSetting,
  SessionResults,
  SessionVisibleSetting, Student,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../../types/default-retry-attempts';
import { ErrorReportComponent } from '../../components/error-report/error-report.component';
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
  personName: string = '';
  courseId: string = '';
  feedbackSessionName: string = '';
  regKey: string = '';
  loggedInUser: string = '';

  isFeedbackSessionResultsLoading: boolean = false;
  hasFeedbackSessionResultsLoadingFailed: boolean = false;
  retryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

  private backendUrl: string = environment.backendUrl;

  constructor(private feedbackSessionsService: FeedbackSessionsService,
              private route: ActivatedRoute,
              private router: Router,
              private timezoneService: TimezoneService,
              private navigationService: NavigationService,
              private authService: AuthService,
              private studentService: StudentService,
              private statusMessageService: StatusMessageService,
              private ngbModal: NgbModal) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.regKey = queryParams.key || '';

      const nextUrl: string = `${window.location.pathname}${window.location.search}`;
      this.authService.getAuthUser(undefined, nextUrl).subscribe((auth: AuthInfo) => {
        if (auth.user) {
          this.loggedInUser = auth.user.id;
        }
        if (this.regKey) {
          const intent: Intent = Intent.STUDENT_RESULT;
          this.authService.getAuthRegkeyValidity(this.regKey, intent).subscribe((resp: RegkeyValidity) => {
            if (resp.isAllowedAccess) {
              if (resp.isUsed) {
                // The logged in user matches the registration key; redirect to the logged in URL

                this.navigationService.navigateByURLWithParamEncoding(this.router, '/web/student/sessions/result',
                    { courseid: this.courseId, fsname: this.feedbackSessionName });
              } else {
                // Valid, unused registration key; load information based on the key
                this.loadPersonName();
                this.loadFeedbackSession();
              }
            } else if (resp.isValid) {
              // At this point, registration key must already be used, otherwise access would be granted
              if (this.loggedInUser) {
                // Registration key belongs to another user who is not the logged in user
                this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                    'You are not authorized to view this page.');
              } else {
                // There is no logged in user for a valid, used registration key, redirect to login page
                window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
              }
            } else {
              // The registration key is invalid
              this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                  'You are not authorized to view this page.');
            }
          }, () => {
            this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                'You are not authorized to view this page.');
          });
        } else if (this.loggedInUser) {
          // Load information based on logged in user
          this.loadFeedbackSession();
        } else {
          this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
              'You are not authorized to view this page.');
        }
      }, () => {
        this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
            'You are not authorized to view this page.');
      });
    });
  }

  private loadPersonName(): void {
    this.studentService.getStudent(this.courseId, '', this.regKey).subscribe((student: Student) => {
      this.personName = student.name;
    });
  }

  private loadFeedbackSession(): void {
    this.isFeedbackSessionResultsLoading = true;
    this.feedbackSessionsService.getFeedbackSession({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: Intent.STUDENT_RESULT,
      key: this.regKey,
    }).subscribe((feedbackSession: FeedbackSession) => {
      const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
      this.session = feedbackSession;
      this.formattedSessionOpeningTime = this.timezoneService
          .formatToString(this.session.submissionStartTimestamp, this.session.timeZone, TIME_FORMAT);
      this.formattedSessionClosingTime = this.timezoneService
          .formatToString(this.session.submissionEndTimestamp, this.session.timeZone, TIME_FORMAT);
      this.feedbackSessionsService.getFeedbackSessionResults({
        courseId: this.courseId,
        feedbackSessionName: this.feedbackSessionName,
        intent: Intent.STUDENT_RESULT,
        key: this.regKey,
      })
          .pipe(finalize(() => this.isFeedbackSessionResultsLoading = false))
          .subscribe((sessionResults: SessionResults) => {
            this.questions = sessionResults.questions.sort(
                (a: QuestionOutput, b: QuestionOutput) =>
                    a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber);
          }, (resp: ErrorMessageOutput) => {
            this.handleError(resp);
          });
    }, (resp: ErrorMessageOutput) => {
      this.isFeedbackSessionResultsLoading = false;
      this.handleError(resp);
    });
  }

  /**
   * Redirects to join course link for unregistered student.
   */
  joinCourseForUnregisteredStudent(): void {
    this.navigationService.navigateByURL(this.router, '/web/join', { entitytype: 'student', key: this.regKey });
  }

  retryLoadingFeedbackSessionResults(): void {
    this.hasFeedbackSessionResultsLoadingFailed = false;
    if (this.retryAttempts >= 0) {
      this.retryAttempts -= 1;
    }
    this.loadFeedbackSession();
  }

  /**
   * Handles error according to number of attempts at retry
   */
  handleError(resp: ErrorMessageOutput): void {
    this.hasFeedbackSessionResultsLoadingFailed = true;
    if (this.retryAttempts < 0) {
      const report: NgbModalRef = this.ngbModal.open(ErrorReportComponent);
      report.componentInstance.requestId = resp.error.requestId;
      report.componentInstance.errorMessage = resp.error.message;
    } else {
      this.statusMessageService.showErrorToast(resp.error.message);
    }
  }
}
