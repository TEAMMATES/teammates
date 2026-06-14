import { KeyValuePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, Params } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, finalize, map, switchMap, tap } from 'rxjs/operators';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';
import { SessionView } from './session-view.enum';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { CourseService } from '../../../services/course.service';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { SubmissionReceiptService } from '../../../services/submission-receipt.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  CourseView,
  FeedbackQuestion,
  FeedbackQuestionRecipient,
  FeedbackQuestionType,
  FeedbackResponse,
  FeedbackQuestionResponses,
  FeedbackSession,
  FeedbackSessionLogType,
  FeedbackSessionSubmissionStatus,
  Instructor,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionRecipientType,
  RegkeyValidity,
  Student,
  SessionSubmission,
  SessionSubmissionQuestion,
} from '../../../types/api-output';
import { FeedbackResponseRequest, Intent } from '../../../types/api-request';
import { Milliseconds } from '../../../types/datetime-const';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../../types/default-retry-attempts';
import { AjaxLoadingComponent } from '../../components/ajax-loading/ajax-loading.component';
import { giverCommentToCommentRowModel } from '../../components/comment-box/comment-row-model-mapper';
import { ErrorReportComponent } from '../../components/error-report/error-report.component';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import {
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
  ResponseSubmissionStatus,
} from '../../components/question-submission-form/question-submission-form-model';
import { QuestionSubmissionFormComponent } from '../../components/question-submission-form/question-submission-form.component';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { SafeHtmlPipe } from '../../components/teammates-common/safe-html.pipe';
import { PageScrollService } from '../../../services/page-scroll.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { SingleQuestionSaveErrorModalComponent } from './single-question-save-error-modal/single-question-save-error-modal.component';

/**
 * Feedback session submission page.
 */
@Component({
  selector: 'tm-session-submission-page',
  templateUrl: './session-submission-page.component.html',
  styleUrls: ['./session-submission-page.component.scss'],
  imports: [
    LoadingSpinnerDirective,
    FormsModule,
    LoadingRetryComponent,
    QuestionSubmissionFormComponent,
    AjaxLoadingComponent,
    SafeHtmlPipe,
    KeyValuePipe,
  ],
})
export class SessionSubmissionPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly feedbackResponsesService = inject(FeedbackResponsesService);
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly submissionReceiptService = inject(SubmissionReceiptService);
  private readonly studentService = inject(StudentService);
  private readonly instructorService = inject(InstructorService);
  private readonly courseService = inject(CourseService);
  private readonly ngbModal = inject(NgbModal);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly pageScrollService = inject(PageScrollService);
  private readonly authService = inject(AuthService);
  private readonly navigationService = inject(NavigationService);
  private readonly logService = inject(LogService);

  // enum
  FeedbackSessionSubmissionStatus!: typeof FeedbackSessionSubmissionStatus;
  FeedbackQuestionType!: typeof FeedbackQuestionType;
  Intent!: typeof Intent;

  courseId = '';
  feedbackSessionName = '';
  feedbackSessionId = '';
  regKey = '';
  entityType = 'student';
  loggedInUser = '';

  moderatedPerson = '';
  previewAsPerson = '';
  // the name of the person involved
  // (e.g. the student name for unregistered student, the name of instructor being moderated)
  personName = '';
  personEmail = '';

  courseName = '';
  courseInstitute = '';
  formattedSessionOpeningTime = '';
  formattedSessionClosingTime = '';
  feedbackSessionInstructions = '';
  feedbackSessionTimezone = '';
  feedbackSessionSubmissionStatus: FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus.OPEN;
  userDeadlineExtension: number | undefined = undefined;

  intent: Intent = Intent.STUDENT_SUBMISSION;

  questionSubmissionForms: QuestionSubmissionFormModel[] = [];

  isSavingResponses = false;
  isDownloadingSubmissionReceipt = false;
  isSubmissionFormsDisabled = false;

  moderatedQuestionId = '';

  isFeedbackSessionLoading = true;
  isFeedbackSessionQuestionsLoading = true;
  hasFeedbackSessionQuestionsLoadingFailed = false;
  retryAttempts: number;

  isQuestionCountOne = false;

  allSessionViews!: typeof SessionView;
  currentSelectedSessionView: SessionView = SessionView.DEFAULT;
  // Records the recipient to groupable questions mapping used in grouping questions by recipients view
  recipientQuestionMap: Map<string, Set<number>> = new Map<string, Set<number>>();
  ungroupableQuestionsSorted: number[] = [];

  private readonly backendUrl: string = environment.backendUrl;

  constructor() {
    this.retryAttempts = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;
    this.FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
    this.FeedbackQuestionType = FeedbackQuestionType;
    this.Intent = Intent;
    this.allSessionViews = SessionView;
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.data
      .pipe(
        tap((data: Data) => {
          this.intent = data['intent'] as Intent;
          this.entityType =
            (data['intent'] as Intent) === Intent.INSTRUCTOR_SUBMISSION ? 'instructor' : this.entityType;
        }),
        switchMap(() => this.route.queryParams),
      )
      .subscribe((queryParams: Params) => {
        this.feedbackSessionId = queryParams['fsid'];
        this.regKey = queryParams['key'] ?? '';
        this.moderatedPerson = queryParams['moderatedperson'] ?? '';
        this.previewAsPerson = queryParams['previewas'] ?? '';
        if (queryParams['entitytype'] === 'instructor') {
          this.entityType = 'instructor';
          this.intent = Intent.INSTRUCTOR_SUBMISSION;
        }
        this.moderatedQuestionId = queryParams['moderatedquestionId'] ?? '';

        if (this.previewAsPerson) {
          // disable submission in the preview mode
          this.isSubmissionFormsDisabled = true;
        }

        const nextUrl = `${globalThis.location.pathname}${globalThis.location.search.replaceAll('&', '%26')}`;
        this.authService.getAuthUser(nextUrl).subscribe({
          next: (auth: AuthInfo) => {
            const isPreviewOrModeration = !!(auth.user && (this.moderatedPerson || this.previewAsPerson));
            if (auth.user) {
              this.loggedInUser = auth.user.id;
            }
            if (this.regKey && !isPreviewOrModeration) {
              this.authService.getAuthRegkeyValidity(this.regKey, this.intent).subscribe({
                next: (resp: RegkeyValidity) => {
                  if (resp.isAllowedAccess) {
                    if (resp.isUsed) {
                      // The logged in user matches the registration key; redirect to the logged in URL
                      this.navigationService.navigateByURLWithParamEncoding(
                        `/web/${this.entityType}/sessions/submission`,
                        {
                          fsid: this.feedbackSessionId,
                        },
                      );
                    } else {
                      // Valid, unused registration key; load information based on the key
                      this.loadFeedbackSession(false, auth);
                    }
                  } else if (resp.isValid) {
                    // At this point, registration key must already be used, otherwise access would be granted
                    if (this.loggedInUser) {
                      // Registration key belongs to another user who is not the logged in user
                      this.navigationService.navigateWithErrorMessage(
                        '/web/front',
                        `You are trying to access TEAMMATES using the Google account ${this.loggedInUser}, which
                        is not linked to this TEAMMATES account. If you used a different Google account to
                        join/access TEAMMATES before, please use that Google account to access TEAMMATES. If you
                        cannot remember which Google account you used before, please email us at
                        ${environment.supportEmail} for help.`,
                      );
                    } else {
                      this.loadFeedbackSession(true, auth);
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
            } else if (this.loggedInUser) {
              // Load information based on logged in user
              // This will also cover moderation/preview cases
              this.loadFeedbackSession(false, auth);
            } else {
              this.navigationService.navigateWithErrorMessage(
                '/web/front',
                'You are not authorized to view this page.',
              );
            }
          },
          error: () => {
            this.navigationService.navigateWithErrorMessage('/web/front', 'You are not authorized to view this page.');
          },
        });
      });
  }

  private loadCourseInfoData$(): Observable<CourseView | null> {
    let request: Observable<CourseView>;
    switch (this.intent) {
      case Intent.STUDENT_SUBMISSION:
        if (this.moderatedPerson || this.previewAsPerson) {
          request = this.courseService.getCourseAsInstructor(this.courseId);
        } else {
          request = this.courseService.getCourseAsStudent(this.courseId, this.regKey);
        }
        break;
      case Intent.INSTRUCTOR_SUBMISSION:
        request = this.courseService.getCourseAsInstructor(this.courseId, this.regKey);
        break;
      default:
        return of(null);
    }

    return request.pipe(
      tap((resp: CourseView) => {
        this.courseName = resp.course.courseName;
        this.courseInstitute = resp.course.institute;
      }),
      catchError(() => of(null)),
    );
  }

  /**
   * Loads the name of the person involved in the submission.
   */
  private loadUserData$(): Observable<Student | Instructor | null> {
    switch (this.intent) {
      case Intent.STUDENT_SUBMISSION:
        if (this.moderatedPerson || this.previewAsPerson) {
          const userId = this.moderatedPerson || this.previewAsPerson;
          return this.studentService.getStudent({ userId }).pipe(
            tap((student: Student) => {
              this.personName = student.name;
              this.personEmail = student.email;
              this.logStudentAccess();
            }),
            catchError(() => of(null)),
          );
        }
        return this.studentService.getOwnStudent({ courseId: this.courseId, regKey: this.regKey }).pipe(
          tap((student: Student) => {
            this.personName = student.name;
            this.personEmail = student.email;
            this.logStudentAccess();
          }),
          catchError(() => of(null)),
        );
      case Intent.INSTRUCTOR_SUBMISSION:
        if (this.moderatedPerson || this.previewAsPerson) {
          const userId = this.moderatedPerson || this.previewAsPerson;
          return this.instructorService.getInstructor({ userId }).pipe(
            tap((instructor: Instructor) => {
              this.personName = instructor.name;
              this.personEmail = instructor.email;
            }),
            catchError(() => of(null)),
          );
        }
        return this.instructorService
          .getOwnInstructor({
            courseId: this.courseId,
            key: this.regKey,
          })
          .pipe(
            tap((instructor: Instructor) => {
              this.personName = instructor.name;
              this.personEmail = instructor.email;
            }),
            catchError(() => of(null)),
          );
      default:
        return of(null);
    }
  }

  /**
   * Redirects to join course link for unregistered student/instructor.
   */
  joinCourseForUnregisteredEntity(): void {
    this.navigationService.navigateByURL('/web/join', { entitytype: this.entityType, key: this.regKey });
  }

  /**
   * Loads the feedback session information.
   */
  loadFeedbackSession(loginRequired: boolean, auth: AuthInfo): void {
    this.isFeedbackSessionLoading = true;
    const TIME_FORMAT = 'ddd, DD MMM, YYYY, hh:mm A zz';
    let cachedFeedbackSession: FeedbackSession;

    this.feedbackSessionsService
      .getFeedbackSession({
        feedbackSessionId: this.feedbackSessionId,
        intent: this.intent,
        key: this.regKey,
        moderatedPerson: this.moderatedPerson,
        previewAs: this.previewAsPerson,
      })
      .pipe(
        tap((feedbackSessionView) => {
          const feedbackSession = feedbackSessionView.feedbackSession;
          cachedFeedbackSession = feedbackSession;
          this.feedbackSessionId = feedbackSession.feedbackSessionId;
          this.courseId = feedbackSession.courseId;
          this.feedbackSessionName = feedbackSession.feedbackSessionName;
          this.feedbackSessionInstructions = feedbackSession.instructions;
          this.formattedSessionOpeningTime = this.timezoneService.formatToString(
            feedbackSession.submissionStartTimestamp,
            feedbackSession.timeZone,
            TIME_FORMAT,
          );
          this.feedbackSessionTimezone = feedbackSession.timeZone;
        }),
        switchMap(() =>
          forkJoin({
            courseInfo: this.loadCourseInfoData$(),
            userData: this.loadUserData$(),
            feedbackQuestions: this.loadFeedbackQuestionsData$(),
          }),
        ),
        switchMap(({ userData }) => {
          if (!userData?.userId) {
            return of(null);
          }
          return this.feedbackSessionsService
            .getDeadlineExtension({
              feedbackSessionId: this.feedbackSessionId,
              userId: userData.userId,
              key: this.regKey || undefined,
            })
            .pipe(catchError(() => of(null)));
        }),
        tap((deadlineExtension) => {
          this.userDeadlineExtension = deadlineExtension?.userDeadlineExtension;
          this.formattedSessionClosingTime = this.getFormattedSessionClosingTime(
            cachedFeedbackSession,
            TIME_FORMAT,
            this.userDeadlineExtension,
          );

          // Override CLOSED status if user has an active deadline extension
          let effectiveStatus = cachedFeedbackSession.submissionStatus;
          if (
            effectiveStatus === FeedbackSessionSubmissionStatus.CLOSED &&
            this.userDeadlineExtension !== undefined &&
            this.userDeadlineExtension > Date.now()
          ) {
            effectiveStatus = FeedbackSessionSubmissionStatus.OPEN;
          }
          this.feedbackSessionSubmissionStatus = effectiveStatus;
          this.handleSubmissionStatusBanner({ ...cachedFeedbackSession, submissionStatus: effectiveStatus });
        }),
        finalize(() => {
          this.isFeedbackSessionLoading = false;
        }),
      )
      .subscribe({
        next: () => {},
        error: (resp: ErrorMessageOutput) => {
          if (resp.status === 404) {
            const message =
              'The session does not exist ' +
              '(most likely deleted by an instructor after the submission link was sent).';
            this.simpleModalService.openInformationModal(
              'Feedback Session Does Not Exist!',
              SimpleModalType.DANGER,
              message,
              {
                onClosed: () =>
                  this.navigationService.navigateByURL(
                    this.loggedInUser ? `/web/${this.entityType}/home` : '/web/front/home',
                  ),
              },
              { backdrop: 'static' },
            );
          } else if (resp.status === 403) {
            if (loginRequired && !auth.user) {
              // There is no logged in user for a valid, used registration key, redirect to login page
              globalThis.location.href = `${this.backendUrl}${auth.loginUrl}`;
            } else {
              this.simpleModalService.openInformationModal(
                'Not Authorised To Access!',
                SimpleModalType.DANGER,
                resp.error.message,
                {
                  onClosed: () =>
                    this.navigationService.navigateByURL(
                      this.loggedInUser ? `/web/${this.entityType}/home` : '/web/front/home',
                    ),
                },
                { backdrop: 'static' },
              );
            }
          } else {
            this.navigationService.navigateWithErrorMessage(`/web/${this.entityType}/home`, resp.error.message);
          }
        },
      });
  }

  /**
   * Loads feedback questions to submit.
   */
  loadFeedbackQuestions(): void {
    this.loadFeedbackQuestionsData$().subscribe();
  }

  private loadFeedbackQuestionsData$(): Observable<QuestionSubmissionFormModel[]> {
    this.isFeedbackSessionQuestionsLoading = true;
    this.questionSubmissionForms = [];
    this.recipientQuestionMap = new Map<string, Set<number>>();
    this.ungroupableQuestionsSorted = [];

    return this.feedbackSessionsService
      .getSessionSubmissionData({
        feedbackSessionId: this.feedbackSessionId,
        intent: this.intent,
        key: this.regKey,
        moderatedPerson: this.moderatedPerson,
        previewAs: this.previewAsPerson,
      })
      .pipe(
        tap((response: SessionSubmission) => this.buildSubmissionForms(response)),
        tap(() => {
          this.ungroupableQuestionsSorted.sort((a: number, b: number) => a - b);
          this.isQuestionCountOne = this.questionSubmissionForms.length === 1;
          this.scrollToModeratedQuestion();
        }),
        map(() => this.questionSubmissionForms),
        catchError((resp: ErrorMessageOutput) => {
          this.handleError(resp);
          return of([]);
        }),
        finalize(() => {
          this.isFeedbackSessionQuestionsLoading = false;
        }),
      );
  }

  private buildSubmissionForms(response: SessionSubmission): void {
    response.questions.forEach((questionData: SessionSubmissionQuestion) => {
      const feedbackQuestion: FeedbackQuestion = questionData.question;
      const model: QuestionSubmissionFormModel = {
        feedbackQuestionId: feedbackQuestion.feedbackQuestionId,

        questionNumber: feedbackQuestion.questionNumber,
        questionBrief: feedbackQuestion.questionBrief,
        questionDescription: feedbackQuestion.questionDescription,

        giverType: feedbackQuestion.giverType,
        recipientType: feedbackQuestion.recipientType,
        recipientList: questionData.recipients.map((recipient: FeedbackQuestionRecipient) => ({
          recipientIdentifier: recipient.identifier,
          recipientName: recipient.name,
          recipientSection: recipient.section,
          recipientTeam: recipient.team,
        })),
        recipientSubmissionForms: [],

        questionType: feedbackQuestion.questionType,
        questionDetails: feedbackQuestion.questionDetails,

        numberOfEntitiesToGiveFeedbackToSetting: feedbackQuestion.numberOfEntitiesToGiveFeedbackToSetting,
        customNumberOfEntitiesToGiveFeedbackTo: feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo
          ? feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo
          : 0,

        showGiverNameTo: feedbackQuestion.showGiverNameTo,
        showRecipientNameTo: feedbackQuestion.showRecipientNameTo,
        showResponsesTo: feedbackQuestion.showResponsesTo,
      };

      this.questionSubmissionForms.push(model);
      this.addQuestionGrouping(model);

      if (this.previewAsPerson) {
        this.buildPreviewSubmissionForms(model);
      } else {
        this.populateSubmissionForms(model, questionData.responses);
      }
    });
  }

  private scrollToModeratedQuestion(): void {
    if (!this.moderatedQuestionId) {
      return;
    }

    setTimeout(() => this.pageScrollService.scrollToAnchor(this.moderatedQuestionId), 350);
  }

  private buildPreviewSubmissionForms(model: QuestionSubmissionFormModel): void {
    model.recipientSubmissionForms = [];
    const formMode: QuestionSubmissionFormMode = this.getQuestionSubmissionFormModeInDefaultView(model);
    model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
      if (
        formMode === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT &&
        model.recipientSubmissionForms.length >= model.customNumberOfEntitiesToGiveFeedbackTo
      ) {
        return;
      }

      model.recipientSubmissionForms.push({
        recipientIdentifier:
          formMode === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT ? '' : recipient.recipientIdentifier,
        responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
        responseId: '',
        status: ResponseSubmissionStatus.NEW,
        isValid: true,
      });
    });
  }

  private addQuestionGrouping(model: QuestionSubmissionFormModel): void {
    const isGroupableQuestion =
      this.getQuestionSubmissionFormModeInDefaultView(model) === QuestionSubmissionFormMode.FIXED_RECIPIENT &&
      model.recipientType !== QuestionRecipientType.NONE &&
      model.questionType !== FeedbackQuestionType.RANK_RECIPIENTS &&
      model.questionType !== FeedbackQuestionType.CONSTSUM_RECIPIENTS &&
      model.questionType !== FeedbackQuestionType.CONTRIB;

    if (!isGroupableQuestion) {
      this.ungroupableQuestionsSorted.push(model.questionNumber);
      return;
    }

    model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
      this.addQuestionForRecipient(recipient.recipientIdentifier, model.questionNumber);
    });
  }

  private handleSubmissionStatusBanner(feedbackSession: FeedbackSession): void {
    // don't show alert modal in moderation
    if (this.moderatedPerson) {
      return;
    }

    let modalContent: string;
    switch (feedbackSession.submissionStatus) {
      case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
        this.isSubmissionFormsDisabled = true;
        modalContent = `<p><strong>The feedback session is currently not open for submissions.</strong></p>
                <p>You can view the questions and any submitted responses
                for this feedback session but cannot submit new responses.</p>`;
        this.simpleModalService.openInformationModal(
          'Feedback Session Not Open',
          SimpleModalType.WARNING,
          modalContent,
        );
        break;
      case FeedbackSessionSubmissionStatus.OPEN:
        if (this.isFeedbackEndingLessThanFifteenMinutes(feedbackSession)) {
          modalContent = 'Warning: you have less than 15 minutes before the submission deadline expires!';
          this.simpleModalService.openInformationModal(
            'Feedback Session Will Be Closing Soon!',
            SimpleModalType.WARNING,
            modalContent,
          );
        }
        break;
      case FeedbackSessionSubmissionStatus.CLOSED:
        this.isSubmissionFormsDisabled = true;
        modalContent = `<p><strong>Feedback Session is Closed</strong></p>
                <p>You can view the questions and any submitted responses
                for this feedback session but cannot submit new responses.</p>`;
        this.simpleModalService.openInformationModal('Feedback Session Closed', SimpleModalType.WARNING, modalContent);
        break;
      case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
      default:
    }
  }

  /**
   * Gets the form mode of the question submission form.
   */
  getQuestionSubmissionFormMode(
    model: QuestionSubmissionFormModel,
    recipientListLength: number,
  ): QuestionSubmissionFormMode {
    const isNumberOfEntitiesToGiveFeedbackToSettingLimited: boolean =
      (model.recipientType === QuestionRecipientType.STUDENTS ||
        model.recipientType === QuestionRecipientType.STUDENTS_EXCLUDING_SELF ||
        model.recipientType === QuestionRecipientType.STUDENTS_IN_SAME_SECTION ||
        model.recipientType === QuestionRecipientType.TEAMS ||
        model.recipientType === QuestionRecipientType.TEAMS_EXCLUDING_SELF ||
        model.recipientType === QuestionRecipientType.TEAMS_IN_SAME_SECTION ||
        model.recipientType === QuestionRecipientType.INSTRUCTORS) &&
      model.numberOfEntitiesToGiveFeedbackToSetting === NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM &&
      recipientListLength > model.customNumberOfEntitiesToGiveFeedbackTo;

    return isNumberOfEntitiesToGiveFeedbackToSettingLimited
      ? QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT
      : QuestionSubmissionFormMode.FIXED_RECIPIENT;
  }

  /**
   * Gets the form mode of the question submission form in {@code DEFAULT} view.
   */
  getQuestionSubmissionFormModeInDefaultView(model: QuestionSubmissionFormModel): QuestionSubmissionFormMode {
    return this.getQuestionSubmissionFormMode(model, model.recipientList.length);
  }

  /**
   * Loads the responses of the feedback question to {@recipientSubmissionForms} in the model.
   */
  private populateSubmissionForms(model: QuestionSubmissionFormModel, existingResponses: FeedbackResponse[]): void {
    model.recipientSubmissionForms = [];
    if (this.getQuestionSubmissionFormModeInDefaultView(model) === QuestionSubmissionFormMode.FIXED_RECIPIENT) {
      // need to generate a full list of submission forms
      model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
        const matchedExistingResponse: FeedbackResponse | undefined = existingResponses.find(
          (response: FeedbackResponse) => response.recipientIdentifier === recipient.recipientIdentifier,
        );
        const submissionForm: FeedbackResponseRecipientSubmissionFormModel = {
          recipientIdentifier: recipient.recipientIdentifier,
          responseDetails: matchedExistingResponse
            ? matchedExistingResponse.responseDetails
            : this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
          responseId: matchedExistingResponse ? matchedExistingResponse.feedbackResponseId : '',
          status: matchedExistingResponse ? ResponseSubmissionStatus.SAVED : ResponseSubmissionStatus.NEW,
          isValid: true,
        };
        if (matchedExistingResponse?.giverComment) {
          submissionForm.commentByGiver = giverCommentToCommentRowModel(matchedExistingResponse.giverComment);
        }
        model.recipientSubmissionForms.push(submissionForm);
      });
    }

    if (this.getQuestionSubmissionFormModeInDefaultView(model) === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT) {
      // need to generate limited number of submission forms
      let numberOfRecipientSubmissionFormsNeeded: number =
        model.customNumberOfEntitiesToGiveFeedbackTo - existingResponses.length;

      existingResponses.forEach((response: FeedbackResponse) => {
        const submissionForm: FeedbackResponseRecipientSubmissionFormModel = {
          recipientIdentifier: response.recipientIdentifier,
          responseDetails: response.responseDetails,
          responseId: response.feedbackResponseId,
          status: ResponseSubmissionStatus.SAVED,
          isValid: true,
        };
        if (response.giverComment) {
          submissionForm.commentByGiver = giverCommentToCommentRowModel(response.giverComment);
        }
        model.recipientSubmissionForms.push(submissionForm);
      });

      // generate empty submission forms
      while (numberOfRecipientSubmissionFormsNeeded > 0) {
        model.recipientSubmissionForms.push({
          recipientIdentifier: '',
          responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
          responseId: '',
          status: ResponseSubmissionStatus.NEW,
          isValid: true,
        });
        numberOfRecipientSubmissionFormsNeeded -= 1;
      }
    }
  }

  /**
   * Checks whether there is any submission forms in the current page.
   */
  get questionsNeedingSubmission(): QuestionSubmissionFormModel[] {
    return this.questionSubmissionForms.filter(
      (model: QuestionSubmissionFormModel) => model.recipientSubmissionForms.length !== 0,
    );
  }

  /**
   * Saves the feedback responses for the specific questions.
   *
   * <p>All empty feedback response will be deleted; For non-empty responses, update/create them if necessary.
   *
   * @param questionSubmissionForms An array of question submission forms to be saved
   */
  saveFeedbackResponses(questionSubmissionForms: QuestionSubmissionFormModel[]): void {
    const notYetAnsweredQuestions: Set<number> = new Set();
    const failToSaveQuestions: Record<number, string> = {}; // Map of question number to error message
    const questionResponses: Record<string, FeedbackResponseRequest[]> = {};
    const submittedQuestions: Set<number> = new Set();

    questionSubmissionForms.forEach((questionSubmissionFormModel: QuestionSubmissionFormModel) => {
      const responses: FeedbackResponseRequest[] = [];
      let hasValidationErrorInQuestion = false;

      questionSubmissionFormModel.recipientSubmissionForms.forEach(
        (recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
          const isEmptyResponse = this.feedbackResponsesService.isFeedbackResponseDetailsEmpty(
            questionSubmissionFormModel.questionType,
            recipientSubmissionFormModel.responseDetails,
          );

          // Consider untouched questions to be valid
          // Untouched questions are those that are not filled in and are not saved before
          const isValid =
            recipientSubmissionFormModel.status === ResponseSubmissionStatus.NEW ||
            recipientSubmissionFormModel.isValid;

          if (!isValid || (!isEmptyResponse && recipientSubmissionFormModel.recipientIdentifier === '')) {
            recipientSubmissionFormModel.status = ResponseSubmissionStatus.ERROR;
            failToSaveQuestions[questionSubmissionFormModel.questionNumber] =
              'Invalid responses provided. Please check question constraints.';
            hasValidationErrorInQuestion = true;
            return;
          }

          if (!isEmptyResponse) {
            // Only include non-empty responses in the request, empty responses will be deleted in the backend
            responses.push({
              responseId: recipientSubmissionFormModel.responseId,
              recipient: recipientSubmissionFormModel.recipientIdentifier,
              responseDetails: recipientSubmissionFormModel.responseDetails,
              giverComment: recipientSubmissionFormModel.commentByGiver?.commentEditFormModel.commentText ?? '',
            });
          }
        },
      );

      const isQuestionFullyAnswered = responses.length > 0;
      if (!hasValidationErrorInQuestion) {
        questionResponses[questionSubmissionFormModel.feedbackQuestionId] = responses;
        if (isQuestionFullyAnswered) {
          submittedQuestions.add(questionSubmissionFormModel.questionNumber);
        }
      }

      if (!isQuestionFullyAnswered) {
        notYetAnsweredQuestions.add(questionSubmissionFormModel.questionNumber);
      }
    });

    if (Object.keys(questionResponses).length === 0) {
      if (questionSubmissionForms.length === 1) {
        const questionNumber = questionSubmissionForms[0].questionNumber;
        if (Object.keys(failToSaveQuestions).length === 0) {
          this.showSubmissionSuccessToast(questionSubmissionForms);
        } else {
          this.openSingleQuestionSaveErrorModal(
            questionNumber,
            failToSaveQuestions[questionNumber] ?? 'Invalid responses provided. Please check question constraints.',
          );
        }
      } else {
        this.openSavingCompleteModal(
          questionSubmissionForms,
          submittedQuestions,
          notYetAnsweredQuestions,
          failToSaveQuestions,
        );
      }
      return;
    }

    this.submitFeedbackResponses(
      questionResponses,
      questionSubmissionForms,
      submittedQuestions,
      notYetAnsweredQuestions,
      failToSaveQuestions,
    );
  }

  private submitFeedbackResponses(
    questionResponses: Record<string, FeedbackResponseRequest[]>,
    questionSubmissionForms: QuestionSubmissionFormModel[],
    submittedQuestions: Set<number>,
    notYetAnsweredQuestions: Set<number>,
    failToSaveQuestions: Record<number, string>,
  ) {
    this.isSavingResponses = true;
    this.feedbackResponsesService
      .submitFeedbackResponses(
        this.feedbackSessionId,
        { questionResponses },
        {
          intent: this.intent,
          key: this.regKey,
          moderatedperson: this.moderatedPerson,
        },
      )
      .pipe(
        finalize(() => {
          this.isSavingResponses = false;
        }),
      )
      .subscribe({
        next: (resp: FeedbackQuestionResponses) => {
          questionSubmissionForms.forEach((questionSubmissionFormModel: QuestionSubmissionFormModel) => {
            if (!(questionSubmissionFormModel.feedbackQuestionId in questionResponses)) {
              return;
            }

            const submittedQuestionResponses: FeedbackResponse[] =
              resp.questionResponses[questionSubmissionFormModel.feedbackQuestionId] ?? [];
            const responsesMap: Record<string, FeedbackResponse> = {};
            submittedQuestionResponses.forEach((response: FeedbackResponse) => {
              responsesMap[response.recipientIdentifier] = response;
            });

            questionSubmissionFormModel.recipientSubmissionForms.forEach(
              (recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
                if (responsesMap[recipientSubmissionFormModel.recipientIdentifier]) {
                  const correspondingResp: FeedbackResponse =
                    responsesMap[recipientSubmissionFormModel.recipientIdentifier];
                  recipientSubmissionFormModel.responseId = correspondingResp.feedbackResponseId;
                  recipientSubmissionFormModel.status = ResponseSubmissionStatus.SAVED;
                  recipientSubmissionFormModel.responseDetails = correspondingResp.responseDetails;
                  recipientSubmissionFormModel.recipientIdentifier = correspondingResp.recipientIdentifier;
                  recipientSubmissionFormModel.commentByGiver = correspondingResp.giverComment
                    ? giverCommentToCommentRowModel(correspondingResp.giverComment)
                    : undefined;
                } else {
                  // empty response is deleted in the backend, reset the form model to default state
                  recipientSubmissionFormModel.responseId = '';
                  recipientSubmissionFormModel.status = ResponseSubmissionStatus.NEW;
                  recipientSubmissionFormModel.commentByGiver = undefined;
                }
              },
            );
          });

          if (questionSubmissionForms.length === 1 && Object.keys(failToSaveQuestions).length === 0) {
            this.showSubmissionSuccessToast(questionSubmissionForms);
          } else {
            this.openSavingCompleteModal(
              questionSubmissionForms,
              submittedQuestions,
              notYetAnsweredQuestions,
              failToSaveQuestions,
            );
          }
          this.logStudentSubmission();
        },
        error: (resp: ErrorMessageOutput) => {
          const contextMessage = resp.error?.message ?? 'An unknown error occurred.';
          if (questionSubmissionForms.length === 1) {
            this.openSingleQuestionSaveErrorModal(
              questionSubmissionForms[0].questionNumber,
              `An error occurred and your response could not be saved. Error details: ${contextMessage}`,
            );
            return;
          }
          this.simpleModalService.openInformationModal(
            'Saving Failed',
            SimpleModalType.DANGER,
            `An error occurred and your responses could not be saved. Error details: ${contextMessage}`,
          );
        },
      });
  }

  private showSubmissionSuccessToast(questionSubmissionForms: QuestionSubmissionFormModel[]): void {
    const successMessage = `Response to question ${questionSubmissionForms[0].questionNumber} submitted successfully.`;
    this.statusMessageService.showSuccessToast(successMessage);
  }

  private openSavingCompleteModal(
    questionSubmissionForms: QuestionSubmissionFormModel[],
    submittedQuestions: Set<number>,
    notYetAnsweredQuestions: Set<number>,
    failToSaveQuestions: Record<number, string>,
  ): void {
    const modalRef: NgbModalRef = this.ngbModal.open(SavingCompleteModalComponent);
    modalRef.componentInstance.questions = questionSubmissionForms;
    modalRef.componentInstance.submittedQuestions = Array.from(submittedQuestions.values());
    modalRef.componentInstance.notYetAnsweredQuestions = Array.from(notYetAnsweredQuestions.values());
    modalRef.componentInstance.failToSaveQuestions = failToSaveQuestions;
  }

  private openSingleQuestionSaveErrorModal(questionNumber: number, errorMessage: string): void {
    const modalRef: NgbModalRef = this.ngbModal.open(SingleQuestionSaveErrorModalComponent);
    modalRef.componentInstance.questionNumber = questionNumber;
    modalRef.componentInstance.errorMessage = errorMessage;
  }

  downloadSubmissionReceipt(): void {
    this.isDownloadingSubmissionReceipt = true;
    this.submissionReceiptService
      .downloadSubmissionReceipt({
        questionSubmissionForms: this.questionSubmissionForms,
        intent: this.intent,
        key: this.regKey,
        moderatedPerson: this.moderatedPerson,
        feedbackSessionTimezone: this.feedbackSessionTimezone,
        personName: this.personName,
        personEmail: this.personEmail,
        courseName: this.courseName,
        courseId: this.courseId,
        feedbackSessionName: this.feedbackSessionName,
      })
      .pipe(
        finalize(() => {
          this.isDownloadingSubmissionReceipt = false;
        }),
      )
      .subscribe({
        next: (hasResponses: boolean) => {
          if (!hasResponses) {
            this.statusMessageService.showWarningToast(
              'No submitted responses found to include in submission receipt.',
            );
          }
        },
        error: () => {
          this.statusMessageService.showErrorToast('An error occurred while generating the submission receipt.');
        },
      });
  }

  /**
   * Deletes a comment by participants.
   */
  deleteParticipantComment(questionIndex: number, responseIdx: number): void {
    const recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel =
      this.questionSubmissionForms[questionIndex].recipientSubmissionForms[responseIdx];

    if (!recipientSubmissionFormModel.responseId) {
      recipientSubmissionFormModel.commentByGiver = undefined;
      return;
    }

    this.feedbackResponsesService
      .deleteGiverComment({
        responseId: recipientSubmissionFormModel.responseId,
        intent: this.intent,
        key: this.regKey,
        moderatedPerson: this.moderatedPerson,
      })
      .subscribe({
        next: () => {
          recipientSubmissionFormModel.commentByGiver = undefined;
          this.statusMessageService.showSuccessToast('Your comment has been deleted!');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  retryLoadingFeedbackSessionQuestions(): void {
    this.hasFeedbackSessionQuestionsLoadingFailed = false;
    if (this.retryAttempts >= 0) {
      this.retryAttempts -= 1;
    }
    this.loadFeedbackQuestions();
  }

  handleError(resp: ErrorMessageOutput): void {
    this.hasFeedbackSessionQuestionsLoadingFailed = true;
    if (this.retryAttempts < 0) {
      const report: NgbModalRef = this.ngbModal.open(ErrorReportComponent);
      report.componentInstance.requestId = resp.headers?.get('X-Request-Id');
      report.componentInstance.errorMessage = resp.error.message;
    } else {
      this.statusMessageService.showErrorToast(resp.error.message);
    }
  }

  private getFormattedSessionClosingTime(
    feedbackSession: FeedbackSession,
    TIME_FORMAT: string,
    userDeadlineExtension?: number,
  ): string {
    const userSessionEndingTime = DeadlineExtensionHelper.getUserFeedbackSessionEndingTimestamp(
      feedbackSession,
      userDeadlineExtension,
    );
    let formattedString = this.timezoneService.formatToString(
      userSessionEndingTime,
      feedbackSession.timeZone,
      TIME_FORMAT,
    );
    if (DeadlineExtensionHelper.hasUserExtension(feedbackSession, userDeadlineExtension)) {
      formattedString += ' (Extension given)';
    }
    return formattedString;
  }

  private isFeedbackEndingLessThanFifteenMinutes(feedbackSession: FeedbackSession): boolean {
    const userSessionEndingTime = DeadlineExtensionHelper.getOngoingUserFeedbackSessionEndingTimestamp(
      feedbackSession,
      this.userDeadlineExtension,
    );
    return userSessionEndingTime - Date.now() < Milliseconds.IN_FIFTEEN_MINUTES;
  }

  private addQuestionForRecipient(recipientId: string, questionId: number): void {
    if (this.recipientQuestionMap.has(recipientId)) {
      this.recipientQuestionMap.get(recipientId)!.add(questionId);
    } else {
      const feedbackQuestionIds: Set<number> = new Set<number>();
      feedbackQuestionIds.add(questionId);
      this.recipientQuestionMap.set(recipientId, feedbackQuestionIds);
    }
  }

  toggleViewChange(selectedView: string | SessionView): void {
    if (selectedView === this.currentSelectedSessionView) {
      return;
    }

    if (selectedView === SessionView.DEFAULT) {
      this.currentSelectedSessionView = SessionView.DEFAULT;
    } else if (selectedView === SessionView.GROUP_RECIPIENTS) {
      this.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
    }
  }

  getSubmissionStatusSummary(): {
    totalQuestions: number;
    saved: number;
    modified: number;
    new: number;
    error: number;
  } {
    const summary = {
      totalQuestions: this.questionSubmissionForms.length,
      saved: 0,
      modified: 0,
      new: 0,
      error: 0,
    };

    this.questionSubmissionForms.forEach((questionSubmissionForm: QuestionSubmissionFormModel) => {
      const status = this.getQuestionSubmissionStatus(questionSubmissionForm);
      if (status === ResponseSubmissionStatus.SAVED) {
        summary.saved += 1;
      } else if (status === ResponseSubmissionStatus.MODIFIED) {
        summary.modified += 1;
      } else if (status === ResponseSubmissionStatus.ERROR) {
        summary.error += 1;
      } else {
        summary.new += 1;
      }
    });

    return summary;
  }

  pluralizeQuestionCount(count: number): string {
    return `${count} question${count === 1 ? '' : 's'}`;
  }

  /**
   * Gets recipient name in {@code FIXED_RECIPIENT} mode and in {@code GROUP_RECIPIENTS} view.
   */
  getRecipientName(recipientIdentifier: string): string {
    const question: QuestionSubmissionFormModel | undefined = this.questionSubmissionForms.find(
      (model: QuestionSubmissionFormModel) =>
        model.questionNumber === this.recipientQuestionMap.get(recipientIdentifier)!.values().next().value,
    );

    if (!question) {
      this.statusMessageService.showErrorToast('Failed to build groupable questions');
      return 'Unknown';
    }

    const recipient: FeedbackResponseRecipient | undefined = question.recipientList.find(
      (r: FeedbackResponseRecipient) => r.recipientIdentifier === recipientIdentifier,
    );

    return recipient ? recipient.recipientName : 'Unknown';
  }

  private getQuestionSubmissionStatus(questionSubmissionForm: QuestionSubmissionFormModel): ResponseSubmissionStatus {
    const recipientStatuses = questionSubmissionForm.recipientSubmissionForms.map(
      (recipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel) => recipientSubmissionForm.status,
    );

    if (recipientStatuses.some((status: ResponseSubmissionStatus) => status === ResponseSubmissionStatus.ERROR)) {
      return ResponseSubmissionStatus.ERROR;
    }

    if (recipientStatuses.some((status: ResponseSubmissionStatus) => status === ResponseSubmissionStatus.MODIFIED)) {
      return ResponseSubmissionStatus.MODIFIED;
    }

    if (recipientStatuses.some((status: ResponseSubmissionStatus) => status === ResponseSubmissionStatus.SAVED)) {
      return ResponseSubmissionStatus.SAVED;
    }

    return ResponseSubmissionStatus.NEW;
  }

  /**
   * Logs student activity after student/session details have been fetched.
   */
  logStudentAccess(): void {
    if (this.intent !== Intent.STUDENT_SUBMISSION) {
      return;
    }

    if (!this.feedbackSessionId) {
      return;
    }

    this.logService
      .createFeedbackSessionLog({
        logType: FeedbackSessionLogType.ACCESS,
        key: this.regKey,
        feedbackSessionId: this.feedbackSessionId,
      })
      .subscribe();
  }

  /**
   * Logs student submission after successful response submission.
   */
  logStudentSubmission(): void {
    if (this.intent !== Intent.STUDENT_SUBMISSION) {
      return;
    }

    if (!this.feedbackSessionId) {
      return;
    }

    this.logService
      .createFeedbackSessionLog({
        logType: FeedbackSessionLogType.SUBMISSION,
        key: this.regKey,
        feedbackSessionId: this.feedbackSessionId,
      })
      .subscribe();
  }
}
