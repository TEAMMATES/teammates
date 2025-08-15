import { DOCUMENT } from '@angular/common';
import { AfterViewInit, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, finalize, switchMap, tap } from 'rxjs/operators';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { CourseService } from '../../../services/course.service';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponseCommentService } from '../../../services/feedback-response-comment.service';
import { FeedbackResponsesResponse, FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  Course,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionRecipient,
  FeedbackQuestionRecipients,
  FeedbackQuestionType,
  FeedbackResponse,
  FeedbackResponseComment,
  FeedbackResponses,
  FeedbackSession,
  FeedbackSessionLogType,
  FeedbackSessionSubmissionStatus,
  Instructor,
  NumberOfEntitiesToGiveFeedbackToSetting,
  RegkeyValidity,
  Student,
} from '../../../types/api-output';
import { FeedbackResponseRequest, Intent } from '../../../types/api-request';
import { Milliseconds } from '../../../types/datetime-const';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../../types/default-retry-attempts';
import { CommentRowModel } from '../../components/comment-box/comment-row/comment-row.component';
import { ErrorReportComponent } from '../../components/error-report/error-report.component';
import {
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from '../../components/question-submission-form/question-submission-form-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';

interface FeedbackQuestionsResponse {
  questions: FeedbackQuestion[];
}

// To export out
export enum SessionView {
  DEFAULT = 'Question',
  GROUP_RECIPIENTS = 'Recipient',
}

/**
 * Feedback session submission page.
 */
@Component({
  selector: 'tm-session-submission-page',
  templateUrl: './session-submission-page.component.html',
  styleUrls: ['./session-submission-page.component.scss'],
})
export class SessionSubmissionPageComponent implements OnInit, AfterViewInit {

  // enum
  FeedbackSessionSubmissionStatus: typeof FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  Intent: typeof Intent = Intent;

  courseId: string = '';
  feedbackSessionName: string = '';
  regKey: string = '';
  entityType: string = 'student';
  loggedInUser: string = '';

  moderatedPerson: string = '';
  previewAsPerson: string = '';
  // the name of the person involved
  // (e.g. the student name for unregistered student, the name of instructor being moderated)
  personName: string = '';
  personEmail: string = '';

  courseName: string = '';
  courseInstitute: string = '';
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  feedbackSessionInstructions: string = '';
  feedbackSessionTimezone: string = '';
  feedbackSessionSubmissionStatus: FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus.OPEN;

  intent: Intent = Intent.STUDENT_SUBMISSION;

  questionSubmissionForms: QuestionSubmissionFormModel[] = [];
  originalQuestionSubmissionForms: QuestionSubmissionFormModel[] = [];

  isSavingResponses: boolean = false;
  isSubmissionFormsDisabled: boolean = false;

  isModerationHintExpanded: boolean = false;
  moderatedQuestionId: string = '';

  isCourseLoading: boolean = true;
  isFeedbackSessionLoading: boolean = true;
  isFeedbackSessionQuestionsLoading: boolean = true;
  hasFeedbackSessionQuestionsLoadingFailed: boolean = false;
  retryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

  isQuestionCountOne: boolean = false;
  isSubmitAllClicked: boolean = false;

  allSessionViews = SessionView;
  currentSelectedSessionView: SessionView = SessionView.DEFAULT;
  hasLoadedAllRecipients: boolean = false;
  // Records the recipient to groupable questions mapping used in grouping questions by recipients view
  recipientQuestionMap: Map<string, Set<number>> = new Map<string, Set<number>>();
  ungroupableQuestions: Set<number> = new Set();
  ungroupableQuestionsSorted: number[] = [];

  feedbackSessionId: string | undefined = '';
  studentId: string | undefined = '';

  autoSaveTimeout: any;
  autoSaveDelay = 100; // 0.1 second delay

  private backendUrl: string = environment.backendUrl;

  private readonly AUTOSAVE_KEY = 'autosave';

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService,
              private feedbackQuestionsService: FeedbackQuestionsService,
              private feedbackResponsesService: FeedbackResponsesService,
              private feedbackSessionsService: FeedbackSessionsService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private courseService: CourseService,
              private ngbModal: NgbModal,
              private simpleModalService: SimpleModalService,
              private pageScrollService: PageScrollService,
              private authService: AuthService,
              private navigationService: NavigationService,
              private commentService: FeedbackResponseCommentService,
              private logService: LogService,
              @Inject(DOCUMENT) private document: any) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  handleAutoSave(event: { id: string, model: QuestionSubmissionFormModel }): void {
    // Disable autosave in preview mode
    if (this.previewAsPerson) {
      return;
    }

    clearTimeout(this.autoSaveTimeout);
    this.autoSaveTimeout = setTimeout(() => {
      const savedData = this.getLocalStorageItem(this.AUTOSAVE_KEY);
      const clonedModel = {
        ...event.model,
        hasResponseChangedForRecipients: Array.from(event.model.hasResponseChangedForRecipients.entries()),
        isTabExpandedForRecipients: Array.from(event.model.isTabExpandedForRecipients.entries()),
      };
      savedData[event.id] = clonedModel;
      this.setLocalStorageItem(this.AUTOSAVE_KEY, savedData);
    }, this.autoSaveDelay);
  }

  loadAutoSavedData(questionId: string): void {
    // Disable loading autosaved data in preview mode
    if (this.previewAsPerson) {
      return;
    }

    const savedData = this.getLocalStorageItem(this.AUTOSAVE_KEY);
    const savedModel = savedData[questionId];

    if (savedModel) {
        const index = this.questionSubmissionForms.findIndex((q) => q.feedbackQuestionId === questionId);
        if (index !== -1) {
            savedModel.hasResponseChangedForRecipients = new Map(savedModel.hasResponseChangedForRecipients);
            savedModel.isTabExpandedForRecipients = new Map(savedModel.isTabExpandedForRecipients);
            this.questionSubmissionForms[index] = savedModel;
        }
    }
  }

  ngOnInit(): void {
    this.route.data.pipe(
        tap((data: any) => {
          this.intent = data.intent;
          this.entityType = data.intent === Intent.INSTRUCTOR_SUBMISSION ? 'instructor' : this.entityType;
        }),
        switchMap(() => this.route.queryParams),
    ).subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.regKey = queryParams.key ? queryParams.key : '';
      this.moderatedPerson = queryParams.moderatedperson ? queryParams.moderatedperson : '';
      this.previewAsPerson = queryParams.previewas ? queryParams.previewas : '';
      if (queryParams.entitytype === 'instructor') {
        this.entityType = 'instructor';
        this.intent = Intent.INSTRUCTOR_SUBMISSION;
      }
      this.moderatedQuestionId = queryParams.moderatedquestionId ? queryParams.moderatedquestionId : '';

      if (this.previewAsPerson) {
        // disable submission in the preview mode
        this.isSubmissionFormsDisabled = true;
      }

      const nextUrl: string = `${window.location.pathname}${window.location.search.replace(/&/g, '%26')}`;
      this.authService.getAuthUser(undefined, nextUrl).subscribe({
        next: (auth: AuthInfo) => {
          const isPreviewOrModeration: boolean = !!(auth.user && (this.moderatedPerson || this.previewAsPerson));
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
                        { courseid: this.courseId, fsname: this.feedbackSessionName });
                  } else {
                    // Valid, unused registration key; load information based on the key
                    this.loadCourseInfo();
                    this.loadPersonName();
                    this.loadFeedbackSession(false, auth);
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
                    this.loadFeedbackSession(true, auth);
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
            // This will also cover moderation/preview cases
            this.loadCourseInfo();
            this.loadPersonName();
            this.loadFeedbackSession(false, auth);
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

  // Solution for checking partial element visibility adapted from
  // https://stackoverflow.com/questions/30943662/check-if-element-is-partially-in-viewport
  /**
   * Checks if a given element is in view.
   *
   * @param e element to perform check for
   */
  isInViewport(e: HTMLElement): boolean {
    const rect = e.getBoundingClientRect();
    const windowHeight: number = (window.innerHeight || document.documentElement.clientHeight);

    return !(
      Math.floor(100 - (((rect.top >= 0 ? 0 : rect.top) / +-rect.height) * 100)) < 1
      || Math.floor(100 - ((rect.bottom - windowHeight) / rect.height) * 100) < 1
    );
  }

  /**
   * Scrolls to the question based on its given question id.
   */
  scrollToQuestion(): void {
    const div: HTMLElement | null = document.getElementById(this.moderatedQuestionId);

    // continue scrolling as long as the element to scroll to is yet to be found or not in view
    if (div == null || !(this.isInViewport(div))) {
      setTimeout(() => {
        this.pageScrollService.scroll({
          document: this.document,
          scrollTarget: `#${this.moderatedQuestionId}`,
          scrollOffset: 70,
        });
        this.scrollToQuestion();
      }, 500);
    }
  }

  ngAfterViewInit(): void {
    if (!this.moderatedQuestionId) {
      return;
    }
    this.scrollToQuestion();
  }

  private loadCourseInfo(): void {
    this.isCourseLoading = true;
    let request: Observable<Course>;
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

  /**
   * Loads the name of the person involved in the submission.
   */
  loadPersonName(): void {
    switch (this.intent) {
      case Intent.STUDENT_SUBMISSION:
        this.studentService.getStudent(
            this.courseId,
            this.moderatedPerson || this.previewAsPerson,
            this.regKey,
        ).subscribe((student: Student) => {
          this.studentId = student.studentId;
          this.personName = student.name;
          this.personEmail = student.email;
          this.logStudentAccess();
        });
        break;
      case Intent.INSTRUCTOR_SUBMISSION:
        this.instructorService.getInstructor({
          courseId: this.courseId,
          feedbackSessionName: this.feedbackSessionName,
          intent: this.intent,
          key: this.regKey,
          moderatedPerson: this.moderatedPerson,
          previewAs: this.previewAsPerson,
        }).subscribe((instructor: Instructor) => {
          this.personName = instructor.name;
          this.personEmail = instructor.email;
        });
        break;
      default:
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
    const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
    this.feedbackSessionsService.getFeedbackSession({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).pipe(finalize(() => {
      this.isFeedbackSessionLoading = false;
    }))
      .subscribe({
        next: (feedbackSession: FeedbackSession) => {
          this.feedbackSessionId = feedbackSession.feedbackSessionId;
          this.feedbackSessionInstructions = feedbackSession.instructions;
          this.formattedSessionOpeningTime = this.timezoneService
              .formatToString(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone, TIME_FORMAT);

          this.formattedSessionClosingTime = this.getformattedSessionClosingTime(feedbackSession, TIME_FORMAT);

          this.feedbackSessionSubmissionStatus = feedbackSession.submissionStatus;
          this.feedbackSessionTimezone = feedbackSession.timeZone;

          this.logStudentAccess();

          // don't show alert modal in moderation
          if (!this.moderatedPerson) {
            let modalContent: string;
            switch (feedbackSession.submissionStatus) {
              case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
                this.isSubmissionFormsDisabled = true;
                modalContent = `<p><strong>The feedback session is currently not open for submissions.</strong></p>
                <p>You can view the questions and any submitted responses
                for this feedback session but cannot submit new responses.</p>`;
                this.simpleModalService.openInformationModal(
                    'Feedback Session Not Open', SimpleModalType.WARNING, modalContent);
                break;
              case FeedbackSessionSubmissionStatus.OPEN:
                if (this.isFeedbackEndingLessThanFifteenMinutes(feedbackSession)) {
                  modalContent = 'Warning: you have less than 15 minutes before the submission deadline expires!';
                  this.simpleModalService.openInformationModal(
                      'Feedback Session Will Be Closing Soon!', SimpleModalType.WARNING, modalContent);
                }
                break;
              case FeedbackSessionSubmissionStatus.CLOSED:
                this.isSubmissionFormsDisabled = true;
                modalContent = `<p><strong>Feedback Session is Closed</strong></p>
                <p>You can view the questions and any submitted responses
                for this feedback session but cannot submit new responses.</p>`;
                this.simpleModalService.openInformationModal(
                    'Feedback Session Closed', SimpleModalType.WARNING, modalContent);
                break;
              case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
              default:
            }
          }

          this.loadFeedbackQuestions();

          // Display note on submission on mobile device
          const mobileDeviceWidth: number = 768;
          if (this.feedbackSessionSubmissionStatus === FeedbackSessionSubmissionStatus.OPEN
              && window.innerWidth < mobileDeviceWidth) {
            const modalContent: string = `Note that you can use the Submit button to save responses already entered,
              and continue to answer remaining questions after that.
              You may also edit your submission any number of times before the closing time of this session.`;
            this.simpleModalService.openInformationModal(
                'Note On Submission', SimpleModalType.INFO, modalContent);
          }
        },
        error: (resp: ErrorMessageOutput) => {
          if (resp.status === 404) {
            const message = 'The session does not exist '
                + '(most likely deleted by an instructor after the submission link was sent).';
            this.simpleModalService.openInformationModal('Feedback Session Does Not Exist!', SimpleModalType.DANGER,
                message,
                {
                  onClosed: () => this.navigationService.navigateByURL(
                      this.loggedInUser ? `/web/${this.entityType}/home` : '/web/front/home'),
                },
                { backdrop: 'static' });
          } else if (resp.status === 403) {
            if (loginRequired && !auth.user) {
              // There is no logged in user for a valid, used registration key, redirect to login page
              if (this.entityType === 'student') {
                window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
              } else if (this.entityType === 'instructor') {
                window.location.href = `${this.backendUrl}${auth.instructorLoginUrl}`;
              }
            } else {
              this.simpleModalService.openInformationModal('Not Authorised To Access!', SimpleModalType.DANGER,
                  resp.error.message,
                  {
                    onClosed: () => this.navigationService.navigateByURL(
                        this.loggedInUser ? `/web/${this.entityType}/home` : '/web/front/home'),
                  },
                  { backdrop: 'static' });
            }
          } else {
            this.navigationService.navigateWithErrorMessage(
                `/web/${this.entityType}/home`, resp.error.message);
          }
        },
      });
  }

  /**
   * Loads feedback questions to submit.
   */
  loadFeedbackQuestions(): void {
    this.isFeedbackSessionQuestionsLoading = true;
    this.questionSubmissionForms = [];
    this.feedbackQuestionsService.getFeedbackQuestions({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).pipe(finalize(() => {
      this.isFeedbackSessionQuestionsLoading = false;
    }))
        .subscribe({
          next: (response: FeedbackQuestionsResponse) => {
            response.questions.forEach((feedbackQuestion: FeedbackQuestion) => {
              const model: QuestionSubmissionFormModel = {
                isLoading: false,
                isLoaded: false,
                isTabExpanded: true,
                feedbackQuestionId: feedbackQuestion.feedbackQuestionId,

                questionNumber: feedbackQuestion.questionNumber,
                questionBrief: feedbackQuestion.questionBrief,
                questionDescription: feedbackQuestion.questionDescription,

                giverType: feedbackQuestion.giverType,
                recipientType: feedbackQuestion.recipientType,
                recipientList: [],
                recipientSubmissionForms: [],

                questionType: feedbackQuestion.questionType,
                questionDetails: feedbackQuestion.questionDetails,

                numberOfEntitiesToGiveFeedbackToSetting: feedbackQuestion.numberOfEntitiesToGiveFeedbackToSetting,
                customNumberOfEntitiesToGiveFeedbackTo: feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo
                    ? feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo : 0,

                showGiverNameTo: feedbackQuestion.showGiverNameTo,
                showRecipientNameTo: feedbackQuestion.showRecipientNameTo,
                showResponsesTo: feedbackQuestion.showResponsesTo,

                hasResponseChangedForRecipients: new Map<string, boolean>(),
                isTabExpandedForRecipients: new Map<string, boolean>(),
              };
              this.questionSubmissionForms.push(model);
            });

            this.isQuestionCountOne = this.questionSubmissionForms.length === 1;
          },
          error: (resp: ErrorMessageOutput) => {
            this.handleError(resp);
          },
        });
  }

  /**
   * Tracks the question submission form by feedback question id.
   *
   * @see https://angular.io/api/common/NgForOf#properties
   */
  trackQuestionSubmissionFormByFn(_: any, item: QuestionSubmissionFormModel): any {
    return item.feedbackQuestionId;
  }

  /**
   * Loads the feedback question recipients for the question.
   */
  loadFeedbackQuestionRecipientsForQuestion(model: QuestionSubmissionFormModel): void {
    this.feedbackQuestionsService.loadFeedbackQuestionRecipients({
      questionId: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).subscribe({
      next: (response: FeedbackQuestionRecipients) => {
        response.recipients.forEach((recipient: FeedbackQuestionRecipient) => {
          model.recipientList.push({
            recipientIdentifier: recipient.identifier,
            recipientName: recipient.name,
            recipientSection: recipient.section,
            recipientTeam: recipient.team,
          });
        });

        if (!this.hasLoadedAllRecipients) {
          // Keep track of the recipient to questions mapping and the ungroupable questions even before
          // changing to grouping questions by recipients view
          if (this.getQuestionSubmissionFormModeInDefaultView(model) === QuestionSubmissionFormMode.FIXED_RECIPIENT
              && model.questionType !== FeedbackQuestionType.RANK_RECIPIENTS
              && model.questionType !== FeedbackQuestionType.CONSTSUM_RECIPIENTS
              && model.questionType !== FeedbackQuestionType.CONTRIB) {
            model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
              this.addQuestionForRecipient(recipient.recipientIdentifier, model.questionNumber);
            });
          } else {
            this.ungroupableQuestions.add(model.questionNumber);
          }
        }

        if (this.previewAsPerson) {
          // don't load responses in preview mode
          // generate a list of empty response box
          const formMode: QuestionSubmissionFormMode = this.getQuestionSubmissionFormModeInDefaultView(model);
          model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
            if (formMode === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT
                && model.recipientSubmissionForms.length >= model.customNumberOfEntitiesToGiveFeedbackTo) {
              return;
            }

            let recipientIdentifier: string = '';
            if (formMode !== QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT) {
              recipientIdentifier = recipient.recipientIdentifier;
            }

            model.recipientSubmissionForms.push({
              recipientIdentifier,
              responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
              responseId: '',
              isValid: true,
            });
          });
          model.isLoading = false;
          model.isLoaded = true;
        } else {
          this.loadFeedbackResponses(model);
        }
      },
      error: (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message),
    });
  }

  /**
   * Gets the form mode of the question submission form.
   */
  getQuestionSubmissionFormMode(model: QuestionSubmissionFormModel, recipientListLength: number):
    QuestionSubmissionFormMode {
    const isNumberOfEntitiesToGiveFeedbackToSettingLimited: boolean =
        (model.recipientType === FeedbackParticipantType.STUDENTS
            || model.recipientType === FeedbackParticipantType.STUDENTS_EXCLUDING_SELF
            || model.recipientType === FeedbackParticipantType.STUDENTS_IN_SAME_SECTION
            || model.recipientType === FeedbackParticipantType.TEAMS
            || model.recipientType === FeedbackParticipantType.TEAMS_EXCLUDING_SELF
            || model.recipientType === FeedbackParticipantType.TEAMS_IN_SAME_SECTION
            || model.recipientType === FeedbackParticipantType.INSTRUCTORS)
        && model.numberOfEntitiesToGiveFeedbackToSetting === NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM
        && recipientListLength > model.customNumberOfEntitiesToGiveFeedbackTo;

    return isNumberOfEntitiesToGiveFeedbackToSettingLimited
        ? QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT : QuestionSubmissionFormMode.FIXED_RECIPIENT;
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
  loadFeedbackResponses(model: QuestionSubmissionFormModel): void {
    this.feedbackResponsesService.getFeedbackResponse({
      questionId: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
    }).pipe(finalize(() => {
      model.isLoading = false;
      model.isLoaded = true;

      this.originalQuestionSubmissionForms.push({
        ...model,
        hasResponseChangedForRecipients: new Map(model.hasResponseChangedForRecipients),
        isTabExpandedForRecipients: new Map(model.isTabExpandedForRecipients),
        recipientList: model.recipientList.map((recipient) => ({ ...recipient })),
        recipientSubmissionForms: model.recipientSubmissionForms.map((form) => ({
          ...form,
          responseDetails: { ...form.responseDetails },
          commentByGiver: form.commentByGiver ? { ...form.commentByGiver } : undefined,
        })),
        questionDetails: { ...model.questionDetails },
      });

    }))
      .subscribe({
        next: (existingResponses: FeedbackResponsesResponse) => {
          if (this.getQuestionSubmissionFormModeInDefaultView(model) === QuestionSubmissionFormMode.FIXED_RECIPIENT) {
            // need to generate a full list of submission forms
            model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
              const matchedExistingResponse: FeedbackResponse | undefined =
                  existingResponses.responses.find(
                      (response: FeedbackResponse) => response.recipientIdentifier === recipient.recipientIdentifier);
              const submissionForm: FeedbackResponseRecipientSubmissionFormModel = {
                recipientIdentifier: recipient.recipientIdentifier,
                responseDetails: matchedExistingResponse
                    ? matchedExistingResponse.responseDetails
                    : this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
                responseId: matchedExistingResponse ? matchedExistingResponse.feedbackResponseId : '',
                isValid: true,
              };
              if (matchedExistingResponse && matchedExistingResponse.giverComment) {
                submissionForm.commentByGiver = this.getCommentModel(
                    matchedExistingResponse.giverComment, recipient.recipientIdentifier);
              }
              model.recipientSubmissionForms.push(submissionForm);
            });
          }

          if (this.getQuestionSubmissionFormModeInDefaultView(model)
            === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT) {
            // need to generate limited number of submission forms
            let numberOfRecipientSubmissionFormsNeeded: number =
                model.customNumberOfEntitiesToGiveFeedbackTo - existingResponses.responses.length;

            existingResponses.responses.forEach((response: FeedbackResponse) => {
              const submissionForm: FeedbackResponseRecipientSubmissionFormModel = {
                recipientIdentifier: response.recipientIdentifier,
                responseDetails: response.responseDetails,
                responseId: response.feedbackResponseId,
                isValid: true,
              };
              if (response.giverComment) {
                submissionForm.commentByGiver = this.getCommentModel(
                    response.giverComment, response.recipientIdentifier);
              }
              model.recipientSubmissionForms.push(submissionForm);
            });

            // generate empty submission forms
            while (numberOfRecipientSubmissionFormsNeeded > 0) {
              model.recipientSubmissionForms.push({
                recipientIdentifier: '',
                responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
                responseId: '',
                isValid: true,
              });
              numberOfRecipientSubmissionFormsNeeded -= 1;
            }
          }
        },
        error: (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message),
      });
  }

  /**
   * Gets the comment model for a given comment.
   */
  getCommentModel(comment: FeedbackResponseComment, recipientIdentifier: string): CommentRowModel {
    return {
      originalComment: comment,
      originalRecipientIdentifier: recipientIdentifier,
      commentEditFormModel: {
        commentText: comment.commentText,
        // the participant comment shall not use custom visibilities
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      timezone: this.feedbackSessionTimezone,
      isEditing: false,
    };
  }

  /**
   * Checks whether there is any submission forms in the current page.
   */
  get questionsNeedingSubmission(): QuestionSubmissionFormModel[] {
    return this.questionSubmissionForms
        .filter((model: QuestionSubmissionFormModel) => model.recipientSubmissionForms.length !== 0);
  }

  /**
   * Saves the feedback responses for the specific questions.
   *
   * <p>All empty feedback response will be deleted; For non-empty responses, update/create them if necessary.
   *
   * @param questionSubmissionForms An array of question submission forms to be saved
   * @param isSubmitAll Is the 'Submit Responses for All Questions' button clicked when saving responses
   * @param recipientId The recipient identifier of the selected recipient when saving responses for this recipient
   * only. This parameter will be null when saving responses for all questions or saving responses for one question.
   */
  saveFeedbackResponses(questionSubmissionForms: QuestionSubmissionFormModel[],
                        isSubmitAll: boolean, recipientId: string | null): void {
    if (isSubmitAll) {
      this.isSubmitAllClicked = true;
    }

    const notYetAnsweredQuestions: Set<number> = new Set();
    const requestIds: Record<string, string> = {};
    const answers: Record<string, FeedbackResponse[]> = {};
    const failToSaveQuestions: Record<number, string> = {}; // Map of question number to error message
    const savingRequests: Observable<any>[] = [];

    this.logService.createFeedbackSessionLog({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      studentEmail: this.personEmail,
      logType: FeedbackSessionLogType.SUBMISSION,
      feedbackSessionId: this.feedbackSessionId,
      studentId: this.studentId,
    }).subscribe();

    questionSubmissionForms.forEach((questionSubmissionFormModel: QuestionSubmissionFormModel) => {
      let isQuestionFullyAnswered: boolean = true;

      const responses: FeedbackResponseRequest[] = [];

      questionSubmissionFormModel.recipientSubmissionForms
          .forEach((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
            if (!recipientSubmissionFormModel.isValid) {
              failToSaveQuestions[questionSubmissionFormModel.questionNumber] =
                  'Invalid responses provided. Please check question constraints.';
              return;
            }
            const isFeedbackResponseDetailsEmpty: boolean =
                this.feedbackResponsesService.isFeedbackResponseDetailsEmpty(
                    questionSubmissionFormModel.questionType, recipientSubmissionFormModel.responseDetails);

            if (!isFeedbackResponseDetailsEmpty) {
              responses.push({
                recipient: recipientSubmissionFormModel.recipientIdentifier,
                responseDetails: recipientSubmissionFormModel.responseDetails,
              });
            }
          });

      isQuestionFullyAnswered = responses.length > 0;

      if (!failToSaveQuestions[questionSubmissionFormModel.questionNumber]) {
        savingRequests.push(
            this.feedbackResponsesService.submitFeedbackResponses(questionSubmissionFormModel.feedbackQuestionId, {
              responses,
            }, {
              intent: this.intent,
              key: this.regKey,
              moderatedperson: this.moderatedPerson,
              singlerecipientidforsubmission: recipientId?.toString() || '',
            }).pipe(
                tap((resp: FeedbackResponses) => {
                  const responsesMap: Record<string, FeedbackResponse> = {};
                  resp.responses.forEach((response: FeedbackResponse) => {
                    responsesMap[response.recipientIdentifier] = response;
                    answers[questionSubmissionFormModel.feedbackQuestionId] =
                        answers[questionSubmissionFormModel.feedbackQuestionId] || [];
                    answers[questionSubmissionFormModel.feedbackQuestionId].push(response);
                  });
                  requestIds[questionSubmissionFormModel.feedbackQuestionId] = resp.requestId || '';

                  questionSubmissionFormModel.recipientSubmissionForms
                      .forEach((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
                        if (responsesMap[recipientSubmissionFormModel.recipientIdentifier]) {
                          const correspondingResp: FeedbackResponse =
                              responsesMap[recipientSubmissionFormModel.recipientIdentifier];
                          recipientSubmissionFormModel.responseId = correspondingResp.feedbackResponseId;
                          recipientSubmissionFormModel.responseDetails = correspondingResp.responseDetails;
                          recipientSubmissionFormModel.recipientIdentifier = correspondingResp.recipientIdentifier;
                        } else {
                          recipientSubmissionFormModel.responseId = '';
                          recipientSubmissionFormModel.commentByGiver = undefined;
                        }
                      });

                  const savedData = this.getLocalStorageItem(this.AUTOSAVE_KEY);
                  delete savedData[questionSubmissionFormModel.feedbackQuestionId];
                  this.setLocalStorageItem(this.AUTOSAVE_KEY, savedData);

                  this.originalQuestionSubmissionForms.forEach((originalModel: QuestionSubmissionFormModel) => {
                    if (originalModel.feedbackQuestionId === questionSubmissionFormModel.feedbackQuestionId) {
                      originalModel.recipientSubmissionForms.forEach((originalRecipientSubmissionFormModel:
                        FeedbackResponseRecipientSubmissionFormModel) => {
                          if (responsesMap[originalRecipientSubmissionFormModel.recipientIdentifier]) {
                            const correspondingResp: FeedbackResponse =
                                responsesMap[originalRecipientSubmissionFormModel.recipientIdentifier];
                            originalRecipientSubmissionFormModel.responseId = correspondingResp.feedbackResponseId;
                            originalRecipientSubmissionFormModel.responseDetails = correspondingResp.responseDetails;
                            originalRecipientSubmissionFormModel.recipientIdentifier =
                              correspondingResp.recipientIdentifier;
                          } else {
                            originalRecipientSubmissionFormModel.responseId = '';
                            originalRecipientSubmissionFormModel.commentByGiver = undefined;
                          }
                        });
                    }

                  });
                }),
                switchMap(() =>
                    forkJoin(questionSubmissionFormModel.recipientSubmissionForms
                        .map((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) =>
                            this.createCommentRequest(recipientSubmissionFormModel))),
                ),
                catchError((error: ErrorMessageOutput) => {
                  failToSaveQuestions[questionSubmissionFormModel.questionNumber] = error.error.message;
                  return of(error);
                }),
            ),
        );
      }

      if (!isQuestionFullyAnswered) {
        notYetAnsweredQuestions.add(questionSubmissionFormModel.questionNumber);
      }
    });

    this.isSavingResponses = true;
    forkJoin(savingRequests).pipe(
        finalize(() => {
          this.isSavingResponses = false;

          const modalRef: NgbModalRef = this.ngbModal.open(SavingCompleteModalComponent);
          modalRef.componentInstance.requestIds = requestIds;
          modalRef.componentInstance.courseId = this.courseId;
          modalRef.componentInstance.feedbackSessionName = this.feedbackSessionName;
          modalRef.componentInstance.feedbackSessionTimezone = this.feedbackSessionTimezone;
          modalRef.componentInstance.personEmail = this.personEmail;
          modalRef.componentInstance.personName = this.personName;
          modalRef.componentInstance.questions = questionSubmissionForms;
          modalRef.componentInstance.answers = answers;
          modalRef.componentInstance.notYetAnsweredQuestions = Array.from(notYetAnsweredQuestions.values());
          modalRef.componentInstance.failToSaveQuestions = failToSaveQuestions;

          if (recipientId) {
            this.questionSubmissionForms.forEach((model: QuestionSubmissionFormModel) => {
              if (this.recipientQuestionMap.get(recipientId)!.has(model.questionNumber)) {
                model.hasResponseChangedForRecipients.set(recipientId, false);
              }
            });
          }
        }),
    ).subscribe();
  }

  /**
   * Creates comment request.
   */
  createCommentRequest(recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel): Observable<any> {
    if (!recipientSubmissionFormModel.responseId) {
      // responseId not set, cannot set comment
      return of({});
    }
    if (!recipientSubmissionFormModel.commentByGiver) {
      // comment not given, do nothing
      return of({});
    }

    const isSameRecipient = recipientSubmissionFormModel.recipientIdentifier
        === recipientSubmissionFormModel.commentByGiver.originalRecipientIdentifier;

    if (!recipientSubmissionFormModel.commentByGiver.originalComment || !isSameRecipient) {
      // comment is new or original comment deleted because recipient has changed

      if (recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText === '') {
        // new comment is empty
        recipientSubmissionFormModel.commentByGiver = undefined;
        return of({});
      }

      // create new comment
      return this.commentService.createComment({
        commentText: recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText,
        // we ignore the fields in comment edit model as participant comment
        // will follow visibilities from question by design
        showCommentTo: [],
        showGiverNameTo: [],
      }, recipientSubmissionFormModel.responseId, this.intent, {
        key: this.regKey,
        moderatedperson: this.moderatedPerson,
      }).pipe(
          tap((comment: FeedbackResponseComment) => {
            recipientSubmissionFormModel.commentByGiver = this.getCommentModel(
                comment, recipientSubmissionFormModel.recipientIdentifier);
          }),
      );
    }

    // existing comment

    if (recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText === '') {
      // comment is empty, create delete request
      return this.commentService.deleteComment(
          recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
            key: this.regKey,
            moderatedperson: this.moderatedPerson,
          })
          .pipe(
              tap(() => {
                recipientSubmissionFormModel.commentByGiver = undefined;
              }));
    }

    // update comment
    return this.commentService.updateComment({
      commentText: recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText,
      // we ignore the fields in comment edit model as participant comment
      // will follow visibilities from question by design
      showCommentTo: [],
      showGiverNameTo: [],
    }, recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
    }).pipe(
        tap((comment: FeedbackResponseComment) => {
          recipientSubmissionFormModel.commentByGiver = this.getCommentModel(
              comment, recipientSubmissionFormModel.recipientIdentifier);
        }),
    );
  }

  /**
   * Deletes a comment by participants.
   */
  deleteParticipantComment(questionIndex: number, responseIdx: number): void {
    const recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel =
        this.questionSubmissionForms[questionIndex].recipientSubmissionForms[responseIdx];

    if (!recipientSubmissionFormModel.commentByGiver || !recipientSubmissionFormModel.commentByGiver.originalComment) {
      return;
    }

    this.commentService.deleteComment(
        recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
          key: this.regKey,
          moderatedperson: this.moderatedPerson,
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
      report.componentInstance.requestId = resp.error.requestId;
      report.componentInstance.errorMessage = resp.error.message;
    } else {
      this.statusMessageService.showErrorToast(resp.error.message);
    }
  }

  loadRecipientsAndResponses(event: any, questionSubmissionForm: QuestionSubmissionFormModel): void {
    if (event && event.visible && !questionSubmissionForm.isLoaded && !questionSubmissionForm.isLoading) {
      questionSubmissionForm.isLoading = true;
      this.loadFeedbackQuestionRecipientsForQuestion(questionSubmissionForm);
      this.loadAutoSavedData(questionSubmissionForm.feedbackQuestionId);
    }
  }

  private getformattedSessionClosingTime(feedbackSession: FeedbackSession, TIME_FORMAT: string): string {
    const userSessionEndingTime = DeadlineExtensionHelper.getUserFeedbackSessionEndingTimestamp(feedbackSession);
    let formattedString = this.timezoneService.formatToString(
      userSessionEndingTime, feedbackSession.timeZone, TIME_FORMAT);
    if (DeadlineExtensionHelper.hasUserExtension(feedbackSession)) {
      formattedString += ' (Extension given)';
    }
    return formattedString;
  }

  private isFeedbackEndingLessThanFifteenMinutes(feedbackSession: FeedbackSession): boolean {
    const userSessionEndingTime = DeadlineExtensionHelper.getOngoingUserFeedbackSessionEndingTimestamp(feedbackSession);
    return (userSessionEndingTime - Date.now()) < Milliseconds.IN_FIFTEEN_MINUTES;
  }

  /**
   * Filter questions that we are submitting for intended recipient
   * when grouped session view is toggled and save the responses after.
   */
  saveResponsesForSelectedRecipientQuestions(recipientId: string,
    questionSubmissionForms: QuestionSubmissionFormModel[]): void {
    const questionsToRecipient: Set<number> | undefined = this.recipientQuestionMap.get(recipientId);
    if (!questionsToRecipient) {
      this.statusMessageService.showErrorToast('Failed to save response for this recipient. '
          + 'Please switch back to "Group by Question" view to save responses.');
    }
    const recipientQSForms = questionSubmissionForms
      .filter((questionSubmissionFormModel: QuestionSubmissionFormModel) =>
          questionsToRecipient!.has(questionSubmissionFormModel.questionNumber));

    this.saveFeedbackResponses(recipientQSForms, false, recipientId);
  }

  resetResponsesForSelectedRecipientQuestions(recipientId: string,
    questionSubmissionForms: QuestionSubmissionFormModel[]): void {

    const questionsToRecipient: Set<number> | undefined = this.recipientQuestionMap.get(recipientId);
    if (!questionsToRecipient) {
      this.statusMessageService.showErrorToast('Failed to reset response for this recipient. '
          + 'Please switch back to "Group by Question" view to reset responses.');
    }
    const recipientQSForms = questionSubmissionForms
      .filter((questionSubmissionFormModel: QuestionSubmissionFormModel) =>
          questionsToRecipient!.has(questionSubmissionFormModel.questionNumber));
    this.resetFeedbackResponses(recipientQSForms, recipientId);
  }

  resetFeedbackResponses(questionSubmissionForms: QuestionSubmissionFormModel[], recipientId: string | null): void {
    const savedData = this.getLocalStorageItem(this.AUTOSAVE_KEY);

    questionSubmissionForms.forEach((questionSubmissionFormModel: QuestionSubmissionFormModel) => {
      const originalSubmissionForm = this.originalQuestionSubmissionForms.find(
        (originalModel: QuestionSubmissionFormModel) =>
          originalModel.feedbackQuestionId === questionSubmissionFormModel.feedbackQuestionId,
      );

      if (originalSubmissionForm) {
        if (recipientId) {
          questionSubmissionFormModel.recipientSubmissionForms.forEach((form, index) => {
            if (form.recipientIdentifier === recipientId) {
              const originalForm = originalSubmissionForm.recipientSubmissionForms.find(
                (originalRecipientForm) => originalRecipientForm.recipientIdentifier === form.recipientIdentifier,
              );

              if (originalForm) {
                questionSubmissionFormModel.recipientSubmissionForms[index] = {
                  ...originalForm,
                  responseDetails: { ...originalForm.responseDetails },
                  commentByGiver: originalForm.commentByGiver ? { ...originalForm.commentByGiver } : undefined,
                };
              }
            }
          });

          questionSubmissionFormModel.hasResponseChangedForRecipients.set(
            recipientId, originalSubmissionForm.hasResponseChangedForRecipients.get(recipientId) ?? false,
          );
          questionSubmissionFormModel.isTabExpandedForRecipients.set(
            recipientId, originalSubmissionForm.isTabExpandedForRecipients.get(recipientId) ?? true,
          );

          if (savedData[questionSubmissionFormModel.feedbackQuestionId]) {
            const recipientIndex = savedData[questionSubmissionFormModel.feedbackQuestionId].recipientSubmissionForms
              .findIndex((form: FeedbackResponseRecipientSubmissionFormModel) =>
                  form.recipientIdentifier === recipientId);

            if (recipientIndex !== -1) {
              savedData[questionSubmissionFormModel.feedbackQuestionId]
                .recipientSubmissionForms.splice(recipientIndex, 1);
            }

            if (savedData[questionSubmissionFormModel.feedbackQuestionId].recipientSubmissionForms.length === 0) {
              delete savedData[questionSubmissionFormModel.feedbackQuestionId];
            }
          }
        } else {
          Object.assign(questionSubmissionFormModel, {
            ...originalSubmissionForm,
            recipientSubmissionForms: originalSubmissionForm.recipientSubmissionForms
              .map((form: FeedbackResponseRecipientSubmissionFormModel) => ({
                ...form,
                responseDetails: { ...form.responseDetails },
                commentByGiver: form.commentByGiver ? { ...form.commentByGiver } : undefined,
              })),
            hasResponseChangedForRecipients: new Map(originalSubmissionForm.hasResponseChangedForRecipients),
            isTabExpandedForRecipients: new Map(originalSubmissionForm.isTabExpandedForRecipients),
            questionDetails: { ...originalSubmissionForm.questionDetails },
          });

          delete savedData[questionSubmissionFormModel.feedbackQuestionId];
        }
      }
    });

    this.setLocalStorageItem(this.AUTOSAVE_KEY, savedData);
  }

  hasResponseChangedForRecipient(recipientId: string,
    questionSubmissionForms: QuestionSubmissionFormModel[]): boolean {
    const questionsToRecipient: Set<number> | undefined = this.recipientQuestionMap.get(recipientId);
    if (!questionsToRecipient) {
      return false;
    }
    return questionSubmissionForms.some((questionSubmissionFormModel: QuestionSubmissionFormModel) =>
      questionsToRecipient.has(questionSubmissionFormModel.questionNumber)
      && questionSubmissionFormModel.hasResponseChangedForRecipients.get(recipientId));
  }

  private addQuestionForRecipient(recipientId: string, questionId: any): void {
    if (this.recipientQuestionMap.has(recipientId)) {
      this.recipientQuestionMap.get(recipientId)!.add(questionId);
    } else {
      const feedbackQuestionIds: Set<any> = new Set<any>();
      feedbackQuestionIds.add(questionId);
      this.recipientQuestionMap.set(recipientId, feedbackQuestionIds);
    }
  }

  expandAllQuestions(): void {
    if (this.currentSelectedSessionView === this.allSessionViews.DEFAULT) {
      this.questionSubmissionForms.forEach((q) => {
        q.isTabExpanded = true;
      });
    } else {
      this.questionSubmissionForms.forEach((q) => {
        q.recipientList.forEach((recipient) => {
          q.isTabExpandedForRecipients.set(recipient.recipientIdentifier, true);
        });
      });
    }
  }

  collapseAllQuestions(): void {
    if (this.currentSelectedSessionView === this.allSessionViews.DEFAULT) {
      this.questionSubmissionForms.forEach((q) => {
        q.isTabExpanded = false;
      });
    } else {
      this.questionSubmissionForms.forEach((q) => {
        q.recipientList.forEach((recipient) => {
          q.isTabExpandedForRecipients.set(recipient.recipientIdentifier, false);
        });
      });
    }
  }

  toggleViewChange(selectedView: SessionView): void {
    if (selectedView === this.currentSelectedSessionView) {
      return;
    }

    if (selectedView === SessionView.DEFAULT) {
      this.currentSelectedSessionView = SessionView.DEFAULT;
    } else if (selectedView === SessionView.GROUP_RECIPIENTS) {
      this.currentSelectedSessionView = SessionView.GROUP_RECIPIENTS;
      this.groupQuestionsByRecipient();
    }
  }

  /**
   * Group questions by recipients in {@code GROUP_RECIPIENTS} view.
   */
  groupQuestionsByRecipient(): void {
    if (this.hasLoadedAllRecipients) {
      return;
    }
    // We first need to load the recipient for all the questions. This is because questions with
    // FIXED_RECIPIENT question submission mode are ungroupable and to know whether the question
    // submission mode of a question, we need to load the recipient list first.
    const recipientsObservables: Observable<FeedbackQuestionRecipients>[] = [];
    const questionsToBeLoaded: QuestionSubmissionFormModel[] = [];

    this.questionSubmissionForms.forEach((model: QuestionSubmissionFormModel) => {
      if (!model.isLoading && !model.isLoaded) {
        questionsToBeLoaded.push(model);
        recipientsObservables.push(this.feedbackQuestionsService.loadFeedbackQuestionRecipients({
          questionId: model.feedbackQuestionId,
          intent: this.intent,
          key: this.regKey,
          moderatedPerson: this.moderatedPerson,
          previewAs: this.previewAsPerson,
        }));
      }
    });

    // Find the groupable and ungroupable questions and construct the recipient to question mapping.
    forkJoin(recipientsObservables)
        .pipe(finalize(() => {
          this.ungroupableQuestionsSorted = Array.from(this.ungroupableQuestions).sort();
          this.hasLoadedAllRecipients = true;
        }))
        .subscribe({
          next: (feedbackQuestionRecipients: FeedbackQuestionRecipients[]) => {
              for (let i = 0; i < feedbackQuestionRecipients.length; i += 1) {
                const question: QuestionSubmissionFormModel = questionsToBeLoaded[i];
                // Only questions with question submission form mode being FIXED_RECIPIENT and with question type
                // not being CONSTSUM_RECIPIENTS, RANK_RECIPIENTS, and CONTRIB, are the groupable questions.
                if (this.getQuestionSubmissionFormMode(question, feedbackQuestionRecipients[i].recipients.length)
                    === QuestionSubmissionFormMode.FIXED_RECIPIENT
                    && question.questionType !== FeedbackQuestionType.CONSTSUM_RECIPIENTS
                    && question.questionType !== FeedbackQuestionType.RANK_RECIPIENTS
                    && question.questionType !== FeedbackQuestionType.CONTRIB) {

                  for (let j = 0; j < feedbackQuestionRecipients[i].recipients.length; j += 1) {
                    const recipient: FeedbackQuestionRecipient = feedbackQuestionRecipients[i].recipients[j];
                    this.addQuestionForRecipient(recipient.identifier, question.questionNumber);
                  }
                } else {
                  this.ungroupableQuestions.add(question.questionNumber);
                }
              }
            },
            error: () => {
              this.statusMessageService.showWarningToast('Failed to build groupable questions');
            },
          },
        );
  }

  /**
   * Gets recipient name in {@code FIXED_RECIPIENT} mode and in {@code GROUP_RECIPIENTS} view.
   */
  getRecipientName(recipientIdentifier: string): string {
    const question: QuestionSubmissionFormModel | undefined =
        this.questionSubmissionForms.find((model: QuestionSubmissionFormModel) =>
            model.questionNumber === this.recipientQuestionMap.get(recipientIdentifier)!.values().next().value);

    if (!question) {
      this.statusMessageService.showErrorToast('Failed to build groupable questions');
      return 'Unknown';
    }

    const recipient: FeedbackResponseRecipient | undefined =
        question!.recipientList.find(
            (r: FeedbackResponseRecipient) => r.recipientIdentifier === recipientIdentifier);

    return recipient ? recipient.recipientName : 'Unknown';
  }

  /**
   * Logs student activity after student/session details have been fetched.
   */
  logStudentAccess(): void {
    if (this.intent !== Intent.STUDENT_SUBMISSION) {
      return;
    }

    // dummy vars to check that both student and session has been loaded
    if (!this.personEmail || !this.feedbackSessionTimezone) {
      return;
    }

    this.logService.createFeedbackSessionLog({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      studentEmail: this.personEmail,
      logType: FeedbackSessionLogType.ACCESS,
      feedbackSessionId: this.feedbackSessionId,
      studentId: this.studentId,
    }).subscribe();
  }

  /**
   * Utility method to get item from local storage.
   */
  private getLocalStorageItem(key: string): any {
    return JSON.parse(localStorage.getItem(key) || '{}');
  }

  /**
   * Utility method to set item in local storage.
   */
  private setLocalStorageItem(key: string, data: any): void {
    localStorage.setItem(key, JSON.stringify(data));
  }
}
