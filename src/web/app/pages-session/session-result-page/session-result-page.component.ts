import { Component, Input, OnInit, inject } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { FeedbackQuestionModel } from './feedback-question.model';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  CourseView,
  FeedbackSession,
  FeedbackSessionView,
  FeedbackSessionLogType,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  Instructor,
  RegkeyValidity,
  UserQuestionOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { FeedbackVisibilityType, Intent } from '../../../types/api-request';
import { Timestamps } from '../../../types/datetime-const';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../../types/default-retry-attempts';
import { ErrorReportComponent } from '../../components/error-report/error-report.component';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { QuestionResponsePanelComponent } from '../../components/question-response-panel/question-response-panel.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Feedback session result page.
 */
@Component({
  selector: 'tm-session-result-page',
  templateUrl: './session-result-page.component.html',
  styleUrls: ['./session-result-page.component.scss'],
  imports: [LoadingSpinnerDirective, LoadingRetryComponent, QuestionResponsePanelComponent],
})
export class SessionResultPageComponent implements OnInit {
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly navigationService = inject(NavigationService);
  private readonly authService = inject(AuthService);
  private readonly studentService = inject(StudentService);
  private readonly instructorService = inject(InstructorService);
  private readonly courseService = inject(CourseService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly logService = inject(LogService);
  private readonly ngbModal = inject(NgbModal);

  // enum
  Intent!: typeof Intent;

  retryAttempts!: number;

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
  questions: FeedbackQuestionModel[] = [];
  courseName = '';
  courseInstitute = '';
  formattedSessionOpeningTime = '';
  formattedSessionClosingTime = '';
  personName = '';
  personEmail = '';
  userId = '';
  courseId = '';
  feedbackSessionName = '';
  @Input({ required: true }) feedbackSessionId!: string;
  @Input() intent: Intent = Intent.STUDENT_RESULT;
  @Input() key = '';
  @Input() previewAs = '';
  @Input() entityType = 'student';
  accountEmail = '';
  visibilityRecipient: FeedbackVisibilityType = FeedbackVisibilityType.RECIPIENT;

  isPreviewHintExpanded = false;

  isCourseLoading = true;
  isFeedbackSessionDetailsLoading = true;
  isFeedbackSessionResultsLoading = true;
  hasFeedbackSessionResultsLoadingFailed = false;

  private readonly backendUrl: string = environment.backendUrl;

  constructor() {
    this.Intent = Intent;
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
    this.retryAttempts = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;
  }

  ngOnInit(): void {
    if (this.intent === Intent.INSTRUCTOR_RESULT) {
      this.entityType = 'instructor';
    }
    if (this.entityType === 'instructor') {
      this.intent = Intent.INSTRUCTOR_RESULT;
    }
    // withComponentInputBinding() can reset @Input() defaults to undefined; restore the 'student' default
    this.entityType ||= 'student';

    const nextUrl = `${globalThis.location.pathname}${globalThis.location.search.replaceAll('&', '%26')}`;
    this.authService.getAuthUser(nextUrl).subscribe({
      next: (auth: AuthInfo) => {
        const isPreview = !!(auth.user && this.previewAs);
        if (auth.user) {
          this.accountEmail = auth.user.accountEmail;
        }
        // prevent having both key and previewas parameters in URL
        if (this.key && isPreview) {
          this.navigationService.navigateWithErrorMessage('/web/front', 'You are not authorized to view this page.');
          return;
        }
        if (this.key) {
          this.authService.getAuthRegkeyValidity(this.key, this.intent).subscribe({
            next: (resp: RegkeyValidity) => {
              if (resp.isAllowedAccess) {
                if (resp.isUsed) {
                  // The signed in user matches the registration key; redirect to the signed in URL

                  this.navigationService.navigateByURL(
                    `/web/${this.entityType}/sessions/${this.feedbackSessionId}/result`,
                  );
                } else {
                  // Valid, unused registration key; load information based on the key
                  this.loadFeedbackSession();
                }
              } else if (resp.isValid) {
                // At this point, registration key must already be used, otherwise access would be granted
                if (this.accountEmail) {
                  // Registration key belongs to another user who is not the signed in user
                  this.navigationService.navigateWithErrorMessage(
                    '/web/front',
                    `You are signed in as ${this.accountEmail}, but this course is linked to a different TEAMMATES account. If you used a different account to join/access TEAMMATES before, please use that account to access TEAMMATES. If you cannot remember which account you used before, please email us at ${environment.supportEmail} for help.`,
                  );
                } else {
                  // There is no logged in user for a valid, used registration key, redirect to login page
                  globalThis.location.href = `${this.backendUrl}${auth.loginUrl}`;
                }
              } else {
                // The registration key is invalid
                this.navigationService.navigateWithErrorMessage(
                  '/web/front',
                  'You are not authorized to view this page.',
                );
              }
            },
            error: () => {
              this.navigationService.navigateWithErrorMessage(
                '/web/front',
                'You are not authorized to view this page.',
              );
            },
          });
        } else if (this.accountEmail) {
          // Load information based on signed in user
          // This will also cover preview cases
          this.loadFeedbackSession();
        } else {
          this.navigationService.navigateWithErrorMessage('/web/front', 'You are not authorized to view this page.');
        }
      },
      error: () => {
        this.navigationService.navigateWithErrorMessage('/web/front', 'You are not authorized to view this page.');
      },
    });
  }

  private loadCourseInfo(): void {
    this.isCourseLoading = true;
    let request: Observable<CourseView>;
    switch (this.intent) {
      case Intent.STUDENT_RESULT:
        if (this.previewAs) {
          request = this.courseService.getCourseAsInstructor(this.courseId);
        } else {
          request = this.courseService.getCourseAsStudent(this.courseId, this.key);
        }
        break;
      case Intent.INSTRUCTOR_RESULT:
        request = this.courseService.getCourseAsInstructor(this.courseId, this.key);
        break;
      default:
        this.isCourseLoading = false;
        return;
    }
    request.subscribe({
      next: (resp: CourseView) => {
        this.courseName = resp.course.courseName;
        this.courseInstitute = resp.course.institute;
        this.isCourseLoading = false;
      },
      error: () => {
        this.isCourseLoading = false;
      },
    });
  }

  private userDetails$(): Observable<Instructor | Student> {
    if (this.entityType === 'student' && this.previewAs) {
      return this.studentService.getStudent({ userId: this.previewAs });
    } else if (this.entityType === 'student') {
      return this.studentService.getOwnStudent({ courseId: this.courseId, regKey: this.key });
    } else if (this.previewAs) {
      return this.instructorService.getInstructor({ userId: this.previewAs });
    } else {
      return this.instructorService.getOwnInstructor({ courseId: this.courseId, key: this.key });
    }
  }

  private loadFeedbackSession(): void {
    this.isFeedbackSessionDetailsLoading = true;
    this.isFeedbackSessionResultsLoading = true;
    this.feedbackSessionsService
      .getFeedbackSession({
        feedbackSessionId: this.feedbackSessionId,
        key: this.key,
      })
      .pipe(
        finalize(() => {
          this.isFeedbackSessionDetailsLoading = false;
        }),
      )
      .subscribe({
        next: (feedbackSessionView: FeedbackSessionView) => {
          const TIME_FORMAT = 'ddd, DD MMM, YYYY, hh:mm A zz';
          const feedbackSession = feedbackSessionView.feedbackSession;
          this.session = feedbackSession;
          this.feedbackSessionId = feedbackSession.feedbackSessionId;
          this.courseId = feedbackSession.courseId;
          this.feedbackSessionName = feedbackSession.feedbackSessionName;
          this.formattedSessionOpeningTime = this.timezoneService.formatToString(
            this.session.submissionStartTimestamp,
            this.session.timeZone,
            TIME_FORMAT,
          );
          this.formattedSessionClosingTime = this.timezoneService.formatToString(
            this.session.submissionEndTimestamp,
            this.session.timeZone,
            TIME_FORMAT,
          );

          this.logStudentView();
          this.loadCourseInfo();
          this.userDetails$().subscribe({
            next: (user) => {
              this.userId = user.userId;
              this.personName = user.name;
              this.personEmail = user.email;
              this.loadFeedbackSessionResults(user.userId);
            },
          });
        },
        error: (resp: ErrorMessageOutput) => {
          this.isFeedbackSessionResultsLoading = false;
          this.handleError(resp);
        },
      });
  }

  private loadFeedbackSessionResults(userId: string): void {
    this.isFeedbackSessionResultsLoading = true;
    this.feedbackSessionsService
      .getUserSessionResults({
        feedbackSessionId: this.feedbackSessionId,
        userId,
        isPreview: !!this.previewAs,
        key: this.key || undefined,
      })
      .pipe(
        finalize(() => {
          this.isFeedbackSessionResultsLoading = false;
        }),
      )
      .subscribe({
        next: (sessionResults) => {
          sessionResults.questions.sort(
            (a: UserQuestionOutput, b: UserQuestionOutput) =>
              a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber,
          );

          this.questions = sessionResults.questions.map((question: UserQuestionOutput) => ({
            feedbackQuestion: question.feedbackQuestion,
            questionStatistics: question.questionStatistics,
            allResponses: question.allResponses,
            responsesToSelf: question.responsesToSelf,
            responsesFromSelf: question.responsesFromSelf,
            otherResponses: question.otherResponses,
            isLoading: false,
            isLoaded: true,
            hasResponseButNotVisibleForPreview: question.hasResponseButNotVisibleForPreview,
            hasCommentNotVisibleForPreview: question.hasCommentNotVisibleForPreview,
          }));
        },
        error: (resp: ErrorMessageOutput) => {
          this.handleError(resp);
        },
      });
  }

  /**
   * Redirects to join course link for unregistered student/instructor.
   */
  joinCourseForUnregisteredEntity(): void {
    this.navigationService.navigateByURL('/web/join', { entityType: this.entityType, key: this.key });
  }

  navigateToSessionReportPage(): void {
    this.navigationService.navigateByURL(`/web/instructor/sessions/${this.feedbackSessionId}/report`);
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
      report.componentInstance.requestId = resp.headers?.get('X-Request-Id');
      report.componentInstance.errorMessage = resp.error.message;
    } else {
      this.statusMessageService.showErrorToast(resp.error.message);
    }
  }

  /**
   * Logs student activity after student/session details have been fetched.
   */
  logStudentView(): void {
    if (this.intent !== Intent.STUDENT_RESULT) {
      return;
    }

    if (!this.feedbackSessionId) {
      return;
    }

    this.logService
      .createFeedbackSessionLog({
        key: this.key,
        logType: FeedbackSessionLogType.VIEW_RESULT,
        feedbackSessionId: this.feedbackSessionId,
      })
      .subscribe();
  }

  protected isSessionPublished(): boolean {
    return this.session.resultVisibleFromTimestamp !== Timestamps.TIME_REPRESENTS_LATER;
  }
}
