import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { finalize, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  Course,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackSession, FeedbackSessionLogType,
  FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  Instructor,
  RegkeyValidity,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionVisibleSetting, Student,
} from '../../../types/api-output';
import { FeedbackVisibilityType, Intent } from '../../../types/api-request';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../../types/default-retry-attempts';
import { ErrorReportComponent } from '../../components/error-report/error-report.component';
import { ErrorMessageOutput } from '../../error-message-output';

export interface FeedbackQuestionModel {
  feedbackQuestion: FeedbackQuestion;
  questionStatistics: string;
  allResponses: ResponseOutput[];
  responsesToSelf: ResponseOutput[];
  responsesFromSelf: ResponseOutput[];
  otherResponses: ResponseOutput[][];
  isLoading: boolean;
  isLoaded: boolean;
  hasResponse: boolean;
  errorMessage?: string;
  hasResponseButNotVisibleForPreview: boolean;
  hasCommentNotVisibleForPreview: boolean;
}

/**
 * Feedback session result page.
 */
@Component({
  selector: 'tm-session-result-page',
  templateUrl: './session-result-page.component.html',
  styleUrls: ['./session-result-page.component.scss'],
})
export class SessionResultPageComponent implements OnInit {

  // enum
  Intent: typeof Intent = Intent;

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
  questions: FeedbackQuestionModel[] = [];
  courseName: string = '';
  courseInstitute: string = '';
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  personName: string = '';
  personEmail: string = '';
  courseId: string = '';
  feedbackSessionName: string = '';
  entityType: string = 'student';
  regKey: string = '';
  loggedInUser: string = '';
  visibilityRecipient: FeedbackVisibilityType = FeedbackVisibilityType.RECIPIENT;

  intent: Intent = Intent.STUDENT_RESULT;

  previewAsPerson: string = '';
  isPreviewHintExpanded: boolean = false;

  isCourseLoading: boolean = true;
  isFeedbackSessionDetailsLoading: boolean = true;
  isFeedbackSessionResultsLoading: boolean = true;
  hasFeedbackSessionResultsLoadingFailed: boolean = false;
  retryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

  private backendUrl: string = environment.backendUrl;

  constructor(private feedbackQuestionsService: FeedbackQuestionsService,
              private feedbackSessionsService: FeedbackSessionsService,
              private route: ActivatedRoute,
              private timezoneService: TimezoneService,
              private navigationService: NavigationService,
              private authService: AuthService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private courseService: CourseService,
              private statusMessageService: StatusMessageService,
              private logService: LogService,
              private ngbModal: NgbModal) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.data.pipe(
        tap((data: any) => {
          this.intent = data.intent;
        }),
        switchMap(() => this.route.queryParams),
    ).subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.regKey = queryParams.key || '';
      this.previewAsPerson = queryParams.previewas ? queryParams.previewas : '';
      if (queryParams.entitytype === 'instructor') {
        this.entityType = 'instructor';
        this.intent = Intent.INSTRUCTOR_RESULT;
      }

      const nextUrl: string = `${window.location.pathname}${window.location.search.replace(/&/g, '%26')}`;
      this.authService.getAuthUser(undefined, nextUrl).subscribe({
        next: (auth: AuthInfo) => {
          const isPreview: boolean = !!(auth.user && this.previewAsPerson);
          if (auth.user) {
            this.loggedInUser = auth.user.id;
          }
          // prevent having both key and previewas parameters in URL
          if (this.regKey && isPreview) {
            this.navigationService.navigateWithErrorMessage('/web/front',
              'You are not authorized to view this page.');
            return;
          }
          if (this.regKey) {
            this.authService.getAuthRegkeyValidity(this.regKey, this.intent).subscribe({
              next: (resp: RegkeyValidity) => {
                if (resp.isAllowedAccess) {
                  if (resp.isUsed) {
                    // The logged in user matches the registration key; redirect to the logged in URL

                    this.navigationService.navigateByURLWithParamEncoding(
                        `/web/${this.entityType}/sessions/result`,
                        { courseid: this.courseId, fsname: this.feedbackSessionName });
                  } else {
                    // Valid, unused registration key; load information based on the key
                    this.loadCourseInfo();
                    this.loadPersonName();
                    this.loadFeedbackSession();
                  }
                } else if (resp.isValid) {
                  // At this point, registration key must already be used, otherwise access would be granted
                  if (this.loggedInUser) {
                    // Registration key belongs to another user who is not the logged in user
                    this.navigationService.navigateWithErrorMessage('/web/front',
                        `You are trying to access TEAMMATES using the Google account ${this.loggedInUser}, which
                        is not linked to this TEAMMATES account. If you used a different Google account to
                        join/access TEAMMATES before, please use that Google account to access TEAMMATES. If you
                        cannot remember which Google account you used before, please email us at
                        ${environment.supportEmail} for help.`);
                  } else {
                    // There is no logged in user for a valid, used registration key, redirect to login page
                    // eslint-disable-next-line no-lonely-if
                    if (this.entityType === 'student') {
                      window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
                    } else if (this.entityType === 'instructor') {
                      window.location.href = `${this.backendUrl}${auth.instructorLoginUrl}`;
                    }
                  }
                } else {
                  // The registration key is invalid
                  this.navigationService.navigateWithErrorMessage('/web/front',
                      'You are not authorized to view this page.');
                }
              },
              error: () => {
                this.navigationService.navigateWithErrorMessage('/web/front',
                    'You are not authorized to view this page.');
              },
            });
          } else if (this.loggedInUser) {
            // Load information based on logged in user
            // This will also cover preview cases
            this.loadCourseInfo();
            this.loadPersonName();
            this.loadFeedbackSession();
          } else {
            this.navigationService.navigateWithErrorMessage('/web/front',
                'You are not authorized to view this page.');
          }
        },
        error: () => {
          this.navigationService.navigateWithErrorMessage('/web/front',
              'You are not authorized to view this page.');
        },
      });
    });
  }

  private loadCourseInfo(): void {
    this.isCourseLoading = true;
    let request: Observable<Course>;
    switch (this.intent) {
      case Intent.STUDENT_RESULT:
        if (this.previewAsPerson) {
          request = this.courseService.getCourseAsInstructor(this.courseId);
        } else {
          request = this.courseService.getCourseAsStudent(this.courseId, this.regKey);
        }
        break;
      case Intent.INSTRUCTOR_RESULT:
        request = this.courseService.getCourseAsInstructor(this.courseId, this.regKey);
        break;
      default:
        this.isCourseLoading = false;
        return;
    }
    request.subscribe({
      next: (resp: Course) => {
        this.courseName = resp.courseName;
        this.courseInstitute = resp.institute;
        this.isCourseLoading = false;
      },
      error: () => {
        this.isCourseLoading = false;
      },
    });
  }

  private loadPersonName(): void {
    switch (this.intent) {
      case Intent.STUDENT_RESULT:
        this.studentService.getStudent(
          this.courseId,
          this.previewAsPerson,
          this.regKey,
        ).subscribe((student: Student) => {
          this.personName = student.name;
          this.personEmail = student.email;

          this.logService.createFeedbackSessionLog({
            courseId: this.courseId,
            feedbackSessionName: this.feedbackSessionName,
            studentEmail: this.personEmail,
            logType: FeedbackSessionLogType.VIEW_RESULT,
          }).subscribe({
            next: () => {
              // No action needed if log is successfully created.
            },
            error: () => this.statusMessageService.showWarningToast('Failed to log feedback session view'),
          });
        });
        break;
      case Intent.INSTRUCTOR_RESULT:
        this.instructorService.getInstructor({
          courseId: this.courseId,
          feedbackSessionName: this.feedbackSessionName,
          intent: this.intent,
          key: this.regKey,
          previewAs: this.previewAsPerson,
        }).subscribe((instructor: Instructor) => {
          this.personName = instructor.name;
          this.personEmail = instructor.email;
        });
        break;
      default:
    }
  }

  private loadFeedbackSession(): void {
    this.isFeedbackSessionDetailsLoading = true;
    this.isFeedbackSessionResultsLoading = true;
    this.feedbackSessionsService.getFeedbackSession({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      previewAs: this.previewAsPerson,
    })
    .pipe(finalize(() => { this.isFeedbackSessionDetailsLoading = false; }))
    .subscribe({
      next: (feedbackSession: FeedbackSession) => {
        const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
        this.session = feedbackSession;
        this.formattedSessionOpeningTime = this.timezoneService
            .formatToString(this.session.submissionStartTimestamp, this.session.timeZone, TIME_FORMAT);
        this.formattedSessionClosingTime = this.timezoneService
            .formatToString(this.session.submissionEndTimestamp, this.session.timeZone, TIME_FORMAT);
        this.feedbackQuestionsService.getFeedbackQuestions({
          courseId: this.courseId,
          feedbackSessionName: this.feedbackSessionName,
          intent: this.intent,
          key: this.regKey,
          previewAs: this.previewAsPerson,
        }).pipe(finalize(() => {
          this.isFeedbackSessionResultsLoading = false;
        }))
            .subscribe({
              next: (feedbackQuestions: FeedbackQuestions) => {
                feedbackQuestions.questions.sort(
                    (a: FeedbackQuestion, b: FeedbackQuestion) =>
                        a.questionNumber - b.questionNumber);
                for (const question of feedbackQuestions.questions) {
                  this.questions.push({
                    feedbackQuestion: question,
                    questionStatistics: '',
                    allResponses: [],
                    responsesToSelf: [],
                    responsesFromSelf: [],
                    otherResponses: [],
                    isLoading: false,
                    isLoaded: false,
                    hasResponse: true,
                    hasResponseButNotVisibleForPreview: false,
                    hasCommentNotVisibleForPreview: false,
                  });
                }
              },
              error: (resp: ErrorMessageOutput) => {
                this.handleError(resp);
              },
            });
      },
      error: (resp: ErrorMessageOutput) => {
        this.isFeedbackSessionResultsLoading = false;
        this.handleError(resp);
      },
    });
  }

  /**
   * Redirects to join course link for unregistered student/instructor.
   */
  joinCourseForUnregisteredEntity(): void {
    this.navigationService.navigateByURL('/web/join', { entitytype: this.entityType, key: this.regKey });
  }

  navigateToSessionReportPage(): void {
    this.navigationService.navigateByURL('/web/instructor/sessions/report',
        { courseid: this.courseId, fsname: this.feedbackSessionName });
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
