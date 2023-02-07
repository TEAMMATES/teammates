import { ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { DateTimeService } from '../../../services/datetime.service';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import {
  CommonVisibilitySetting,
  FeedbackQuestionsService,
  NewQuestionModel,
} from '../../../services/feedback-questions.service';
import { FeedbackSessionActionsService } from '../../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  Course,
  Courses,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  FeedbackTextQuestionDetails, FeedbackVisibilityType,
  HasResponses,
  Instructor,
  Instructors,
  NumberOfEntitiesToGiveFeedbackToSetting,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { DateFormat, TimeFormat, getDefaultDateFormat, getLatestTimeFormat } from '../../../types/datetime-const';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { VisibilityControl } from '../../../types/visibility-control';
import { CopySessionModalResult } from '../../components/copy-session-modal/copy-session-modal-model';
import { CopySessionModalComponent } from '../../components/copy-session-modal/copy-session-modal.component';
import {
  ExtensionConfirmModalComponent,
  ExtensionModalType,
} from '../../components/extension-confirm-modal/extension-confirm-modal.component';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../components/question-edit-form/question-edit-form-model';
import {
  SessionEditFormMode,
  SessionEditFormModel,
} from '../../components/session-edit-form/session-edit-form-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionBasePageComponent } from '../instructor-session-base-page.component';
import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../instructor-session-individual-extension-page/extension-table-column-model';
import {
  FeedbackSessionTabModel,
} from './copy-questions-from-other-sessions-modal/copy-questions-from-other-sessions-modal-model';
import {
  CopyQuestionsFromOtherSessionsModalComponent,
} from './copy-questions-from-other-sessions-modal/copy-questions-from-other-sessions-modal.component';
import { TemplateQuestionModalComponent } from './template-question-modal/template-question-modal.component';

/**
 * Instructor feedback session edit page.
 */
@Component({
  selector: 'tm-instructor-session-edit-page',
  templateUrl: './instructor-session-edit-page.component.html',
  styleUrls: ['./instructor-session-edit-page.component.scss'],
})
export class InstructorSessionEditPageComponent extends InstructorSessionBasePageComponent implements OnInit {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;

  // url param
  courseId: string = '';
  feedbackSessionName: string = '';
  isEditingMode: boolean = false;

  courseName: string = '';

  // models
  sessionEditFormModel: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTime: getLatestTimeFormat(),
    submissionStartDate: getDefaultDateFormat(),
    submissionEndTime: getLatestTimeFormat(),
    submissionEndDate: getDefaultDateFormat(),
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: getLatestTimeFormat(),
    customSessionVisibleDate: getDefaultDateFormat(),

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: getLatestTimeFormat(),
    customResponseVisibleDate: getDefaultDateFormat(),

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: false,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };
  studentDeadlines: Record<string, number> = {};
  instructorDeadlines: Record<string, number> = {};

  // to get the original session model on discard changes
  feedbackSessionModelBeforeEditing: SessionEditFormModel = JSON.parse(JSON.stringify(this.sessionEditFormModel));

  // to get the original question model
  feedbackQuestionModels: Map<string, FeedbackQuestion> = new Map();

  questionEditFormModels: QuestionEditFormModel[] = [];

  newQuestionEditFormModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    isQuestionHasResponses: false,

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    } as FeedbackTextQuestionDetails,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 1,

    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],

    isDeleting: false,
    isDuplicating: false,
    isEditable: true,
    isSaving: false,
    isCollapsed: false,
    isVisibilityChanged: false,
    isFeedbackPathChanged: false,
    isQuestionDetailsChanged: false,
  };

  isAddingFromTemplate: boolean = false;
  isAddingQuestionPanelExpanded: boolean = false;
  isLoadingFeedbackSession: boolean = false;
  hasLoadingFeedbackSessionFailed: boolean = false;
  isLoadingFeedbackQuestions: boolean = false;
  hasLoadingFeedbackQuestionsFailed: boolean = false;
  isCopyingQuestion: boolean = false;

  // all students of the course
  studentsOfCourse: Student[] = [];
  emailOfStudentToPreview: string = '';
  // all instructors of the course
  instructorsOfCourse: Instructor[] = [];
  emailOfInstructorToPreview: string = '';

  get isAllCollapsed(): boolean {
    return this.questionEditFormModels.some((model: QuestionEditFormModel) => {
      return model.isCollapsed;
    });
  }

  @ViewChild('modifiedTimestampsModal') modifiedTimestampsModal!: TemplateRef<any>;

  constructor(instructorService: InstructorService,
              statusMessageService: StatusMessageService,
              navigationService: NavigationService,
              feedbackSessionsService: FeedbackSessionsService,
              feedbackQuestionsService: FeedbackQuestionsService,
              tableComparatorService: TableComparatorService,
              ngbModal: NgbModal,
              simpleModalService: SimpleModalService,
              progressBarService: ProgressBarService,
              feedbackSessionActionsService: FeedbackSessionActionsService,
              timezoneService: TimezoneService,
              private datetimeService: DateTimeService,
              private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute,
              private changeDetectorRef: ChangeDetectorRef) {
    super(instructorService, statusMessageService, navigationService,
        feedbackSessionsService, feedbackQuestionsService, tableComparatorService,
        ngbModal, simpleModalService, progressBarService, feedbackSessionActionsService, timezoneService);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.isEditingMode = queryParams.editingMode === 'true';

      this.loadFeedbackSession();
      this.loadFeedbackQuestions();
      this.getAllStudentsOfCourse();
      this.getAllInstructors();
    });
  }

  /**
   * Loads a feedback session.
   */
  loadFeedbackSession(): void {
    this.hasLoadingFeedbackSessionFailed = false;
    this.isLoadingFeedbackSession = true;
    // load the course of the feedback session first
    this.courseService.getCourseAsInstructor(this.courseId).subscribe({
      next: (course: Course) => {
        this.courseName = course.courseName;

        this.feedbackSessionsService.getFeedbackSession({
          courseId: this.courseId,
          feedbackSessionName: this.feedbackSessionName,
          intent: Intent.FULL_DETAIL,
        }).pipe(finalize(() => {
          this.isLoadingFeedbackSession = false;
        }))
            .subscribe({
              next: (feedbackSession: FeedbackSession) => {
                this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession, this.isEditingMode);
                this.feedbackSessionModelBeforeEditing = this.getSessionEditFormModel(feedbackSession);
              },
              error: (resp: ErrorMessageOutput) => {
                this.hasLoadingFeedbackSessionFailed = true;
                this.statusMessageService.showErrorToast(resp.error.message);
              },
            });
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isLoadingFeedbackSession = false;
        this.hasLoadingFeedbackSessionFailed = true;
      },
    });
  }

  /**
   * Copies the feedback session.
   */
  copyCurrentSession(): Promise<void> {
    // load course candidates first
    return new Promise<void>((_resolve: any, reject: any) => {
      this.courseService.getInstructorCoursesThatAreActive()
      .pipe(finalize(() => {
        this.sessionEditFormModel.isCopying = false;
      }))
      .subscribe((courses: Courses) => {
        this.failedToCopySessions = {};
        this.coursesOfModifiedSession = [];
        this.modifiedSession = {};
        const modalRef: NgbModalRef = this.ngbModal.open(CopySessionModalComponent);
        modalRef.componentInstance.newFeedbackSessionName = this.feedbackSessionName;
        modalRef.componentInstance.courseCandidates = courses.courses;
        modalRef.componentInstance.sessionToCopyCourseId = this.courseId;

        modalRef.result.then((result: CopySessionModalResult) => {
          const requestList: Observable<FeedbackSession>[] = this.createSessionCopyRequestsFromModal(
              result, this.courseId, this.feedbackSessionName);
          this.sessionEditFormModel.isCopying = true;
          if (requestList.length === 1) {
            this.copySingleSession(requestList[0].pipe(finalize(() => {
              this.sessionEditFormModel.isCopying = false;
            })), this.modifiedTimestampsModal);
          }
          if (requestList.length > 1) {
            forkJoin(requestList)
            .pipe(finalize(() => {
              this.sessionEditFormModel.isCopying = false;
            }))
            .subscribe(() => {
              this.showCopyStatusMessage(this.modifiedTimestampsModal);
            });
          }
        }, (resp: ErrorMessageOutput) => {
          reject(resp);
          this.statusMessageService.showErrorToast(resp.error.message);
        })
        .catch(() => {
          this.sessionEditFormModel.isCopying = false;
        });
      });
    });
  }

  /**
   * Gets the {@code sessionEditFormModel} with {@link FeedbackSession} entity.
   */
  getSessionEditFormModel(feedbackSession: FeedbackSession, isEditable: boolean = false): SessionEditFormModel {
    const submissionStart: { date: DateFormat, time: TimeFormat } =
        this.datetimeService.getDateTimeAtTimezone(feedbackSession.submissionStartTimestamp,
          feedbackSession.timeZone, true);

    const submissionEnd: { date: DateFormat, time: TimeFormat } =
        this.datetimeService.getDateTimeAtTimezone(feedbackSession.submissionEndTimestamp,
          feedbackSession.timeZone, true);

    const model: SessionEditFormModel = {
      isEditable,
      courseId: feedbackSession.courseId,
      timeZone: feedbackSession.timeZone,
      courseName: this.courseName,
      feedbackSessionName: feedbackSession.feedbackSessionName,
      instructions: feedbackSession.instructions,

      submissionStartTime: submissionStart.time,
      submissionStartDate: submissionStart.date,
      submissionEndTime: submissionEnd.time,
      submissionEndDate: submissionEnd.date,
      gracePeriod: feedbackSession.gracePeriod,

      sessionVisibleSetting: feedbackSession.sessionVisibleSetting,
      customSessionVisibleTime: getLatestTimeFormat(),
      customSessionVisibleDate: getDefaultDateFormat(),

      responseVisibleSetting: feedbackSession.responseVisibleSetting,
      customResponseVisibleTime: getLatestTimeFormat(),
      customResponseVisibleDate: getDefaultDateFormat(),

      submissionStatus: feedbackSession.submissionStatus,
      publishStatus: feedbackSession.publishStatus,

      templateSessionName: '',

      isClosingEmailEnabled: feedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: feedbackSession.isPublishedEmailEnabled,

      isSaving: false,
      isDeleting: false,
      isCopying: false,
      hasVisibleSettingsPanelExpanded: feedbackSession.sessionVisibleSetting !== SessionVisibleSetting.AT_OPEN
          || feedbackSession.responseVisibleSetting !== ResponseVisibleSetting.LATER,
      hasEmailSettingsPanelExpanded: !feedbackSession.isClosingEmailEnabled || !feedbackSession.isPublishedEmailEnabled,
    };

    this.studentDeadlines = feedbackSession.studentDeadlines;
    this.instructorDeadlines = feedbackSession.instructorDeadlines;

    if (feedbackSession.customSessionVisibleTimestamp) {
      const customSessionVisible: { date: DateFormat, time: TimeFormat } =
          this.datetimeService.getDateTimeAtTimezone(feedbackSession.customSessionVisibleTimestamp,
              feedbackSession.timeZone, true);
      model.customSessionVisibleTime = customSessionVisible.time;
      model.customSessionVisibleDate = customSessionVisible.date;
    }

    if (feedbackSession.customResponseVisibleTimestamp) {
      const customResponseVisible: { date: DateFormat, time: TimeFormat } =
          this.datetimeService.getDateTimeAtTimezone(feedbackSession.customResponseVisibleTimestamp,
              feedbackSession.timeZone, true);
      model.customResponseVisibleTime = customResponseVisible.time;
      model.customResponseVisibleDate = customResponseVisible.date;
    }

    return model;
  }

  /**
   * Handles editing existing session event.
   */
  editExistingSessionHandler(): void {
    this.feedbackSessionModelBeforeEditing = JSON.parse(JSON.stringify(this.sessionEditFormModel));

    const submissionStartTime: number = this.timezoneService.resolveLocalDateTime(
        this.sessionEditFormModel.submissionStartDate, this.sessionEditFormModel.submissionStartTime,
        this.sessionEditFormModel.timeZone, true);
    const submissionEndTime: number = this.timezoneService.resolveLocalDateTime(
        this.sessionEditFormModel.submissionEndDate, this.sessionEditFormModel.submissionEndTime,
        this.sessionEditFormModel.timeZone, true);
    let sessionVisibleTime: number = 0;
    if (this.sessionEditFormModel.sessionVisibleSetting === SessionVisibleSetting.CUSTOM) {
      sessionVisibleTime = this.timezoneService.resolveLocalDateTime(
          this.sessionEditFormModel.customSessionVisibleDate, this.sessionEditFormModel.customSessionVisibleTime,
          this.sessionEditFormModel.timeZone, true);
    }
    let responseVisibleTime: number = 0;
    if (this.sessionEditFormModel.responseVisibleSetting === ResponseVisibleSetting.CUSTOM) {
      responseVisibleTime = this.timezoneService.resolveLocalDateTime(
          this.sessionEditFormModel.customResponseVisibleDate, this.sessionEditFormModel.customResponseVisibleTime,
          this.sessionEditFormModel.timeZone, true);
    }

    this.deleteDeadlineExtensionsHandler(submissionEndTime).subscribe((isUpdateSession) => {
      if (isUpdateSession) {
        this.updateFeedbackSession(submissionStartTime, submissionEndTime, sessionVisibleTime, responseVisibleTime);
      }
    });
  }

  updateFeedbackSession(submissionStartTime: number, submissionEndTime: number, sessionVisibleTime: number,
    responseVisibleTime: number): void {
    this.sessionEditFormModel.isSaving = true;
    this.sessionEditFormModel.isEditable = false;
    this.feedbackSessionsService.updateFeedbackSession(this.courseId, this.feedbackSessionName, {
      instructions: this.sessionEditFormModel.instructions,

      submissionStartTimestamp: submissionStartTime,
      submissionEndTimestamp: submissionEndTime,
      gracePeriod: this.sessionEditFormModel.gracePeriod,

      sessionVisibleSetting: this.sessionEditFormModel.sessionVisibleSetting,
      customSessionVisibleTimestamp: sessionVisibleTime,

      responseVisibleSetting: this.sessionEditFormModel.responseVisibleSetting,
      customResponseVisibleTimestamp: responseVisibleTime,

      isClosingEmailEnabled: this.sessionEditFormModel.isClosingEmailEnabled,
      isPublishedEmailEnabled: this.sessionEditFormModel.isPublishedEmailEnabled,

      studentDeadlines: this.studentDeadlines,
      instructorDeadlines: this.instructorDeadlines,
    }).pipe(finalize(() => {
      this.sessionEditFormModel.isSaving = false;
    })).subscribe({
      next: (feedbackSession: FeedbackSession) => {
        this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession);

        this.statusMessageService.showSuccessToast('The feedback session has been updated.');
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Prompts the user to delete individual extensions that are before or equal to the new session end time.
   */
  deleteDeadlineExtensionsHandler(submissionEndTimestamp: number): Observable<boolean> {
    const [studentDeadlinesToDelete, instructorDeadlinesToDelete] = this
      .getIndividualDeadlinesToDelete(submissionEndTimestamp);

    const isAllDeadlinesAfterUpdatedEndTime = Object.values(studentDeadlinesToDelete).length === 0
      && Object.values(instructorDeadlinesToDelete).length === 0;

    if (isAllDeadlinesAfterUpdatedEndTime) {
      return of(true); // no need to prompt for deletion
    }

    const [affectedStudentModels, affectedInstructorModels] = this
      .getAffectedIndividualModels(submissionEndTimestamp, studentDeadlinesToDelete, instructorDeadlinesToDelete);

    const modalRef: NgbModalRef = this.ngbModal.open(ExtensionConfirmModalComponent);
    modalRef.componentInstance.modalType = ExtensionModalType.SESSION_DELETE;
    modalRef.componentInstance.selectedStudents = affectedStudentModels;
    modalRef.componentInstance.selectedInstructors = affectedInstructorModels;
    modalRef.componentInstance.extensionTimestamp = submissionEndTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.sessionEditFormModel.timeZone;

    return new Observable((subscribeIsUserAccept) => {
      modalRef.componentInstance.confirmExtensionCallbackEvent.subscribe(() => {
        this.removeDeadlines(affectedStudentModels, affectedInstructorModels);
        modalRef.componentInstance.isSubmitting = false;
        modalRef.close();
        subscribeIsUserAccept.next(true);
      }, () => {
        subscribeIsUserAccept.next(false);
      });
    });
  }

  private getIndividualDeadlinesToDelete(submissionEndTimestamp: number): [
    Record<string, number>, Record<string, number>,
  ] {
    const studentDeadlinesToDelete = DeadlineExtensionHelper.getDeadlinesBeforeOrEqualToEndTime(
      this.studentDeadlines, submissionEndTimestamp);
    const instructorDeadlinesToDelete = DeadlineExtensionHelper.getDeadlinesBeforeOrEqualToEndTime(
      this.instructorDeadlines, submissionEndTimestamp);
    return [studentDeadlinesToDelete, instructorDeadlinesToDelete];
  }

  /**
   * Get models for individuals whose deadline extensions are before or equal to the new session end time.
   */
  private getAffectedIndividualModels(
    submissionEndTimestamp: number,
    affectedStudentDeadlines: Record<string, number>,
    affectedInstructorDeadlines: Record<string, number>,
  ): [StudentExtensionTableColumnModel[], InstructorExtensionTableColumnModel[]] {
    const affectedStudents = this.studentsOfCourse.filter((student) => affectedStudentDeadlines[student.email]);
    const affectedInstructors = this.instructorsOfCourse
      .filter((instructor) => affectedInstructorDeadlines[instructor.email]);

    const affectedStudentModels = DeadlineExtensionHelper.mapStudentsToStudentModels(
      affectedStudents, affectedStudentDeadlines, submissionEndTimestamp,
    );
    const affectedInstructorModels = DeadlineExtensionHelper.mapInstructorsToInstructorModels(
      affectedInstructors, affectedInstructorDeadlines, submissionEndTimestamp,
    );
    return [affectedStudentModels, affectedInstructorModels];
  }

  private removeDeadlines(students: StudentExtensionTableColumnModel[],
    instructors: InstructorExtensionTableColumnModel[],
  ): void {
    this.studentDeadlines = DeadlineExtensionHelper
      .getUpdatedDeadlinesForDeletion(students, this.studentDeadlines);
    this.instructorDeadlines = DeadlineExtensionHelper
      .getUpdatedDeadlinesForDeletion(instructors, this.instructorDeadlines);
  }
  /**
   * Handles canceling existing session event without saving changes.
   */
  cancelEditingSessionHandler(): void {
    this.sessionEditFormModel = JSON.parse(JSON.stringify(this.feedbackSessionModelBeforeEditing));
  }

  /**
   * Handles deleting current feedback session.
   */
  deleteExistingSessionHandler(): void {
    this.sessionEditFormModel.isDeleting = true;
    this.feedbackSessionsService.moveSessionToRecycleBin(this.courseId, this.feedbackSessionName)
      .pipe(finalize(() => {
        this.sessionEditFormModel.isDeleting = false;
      }))
      .subscribe({
        next: () => {
          this.navigationService.navigateWithSuccessMessage('/web/instructor/sessions',
              'The feedback session has been deleted. You can restore it from the deleted sessions table below.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Loads feedback questions.
   */
  loadFeedbackQuestions(): void {
    this.questionEditFormModels = [];
    this.hasLoadingFeedbackQuestionsFailed = false;
    this.isLoadingFeedbackQuestions = true;
    this.feedbackQuestionsService.getFeedbackQuestions({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: Intent.FULL_DETAIL,
    })
        .pipe(finalize(() => {
          this.isLoadingFeedbackQuestions = false;
        }))
        .subscribe({
          next: (response: FeedbackQuestions) => {
            response.questions.forEach((feedbackQuestion: FeedbackQuestion) => {
              const addedQuestionEditFormModel: QuestionEditFormModel = this.getQuestionEditFormModel(feedbackQuestion);
              this.questionEditFormModels.push(addedQuestionEditFormModel);
              this.loadResponseStatusForQuestion(addedQuestionEditFormModel);
              this.feedbackQuestionModels.set(feedbackQuestion.feedbackQuestionId, feedbackQuestion);
            });
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasLoadingFeedbackQuestionsFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Tracks the question edit form by feedback question id.
   *
   * @see https://angular.io/api/common/NgForOf#properties
   */
  trackQuestionEditFormByFn(_: any, item: QuestionEditFormModel): any {
    return item.feedbackQuestionId;
  }

  /**
   * Converts feedback question to the question edit form model.
   */
  private getQuestionEditFormModel(feedbackQuestion: FeedbackQuestion): QuestionEditFormModel {
    return {
      feedbackQuestionId: feedbackQuestion.feedbackQuestionId,

      questionNumber: feedbackQuestion.questionNumber,
      questionBrief: feedbackQuestion.questionBrief,
      questionDescription: feedbackQuestion.questionDescription,

      isQuestionHasResponses: false,

      questionType: feedbackQuestion.questionType,
      questionDetails: this.deepCopy(feedbackQuestion.questionDetails),

      giverType: feedbackQuestion.giverType,
      recipientType: feedbackQuestion.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: feedbackQuestion.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo
          ? feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo : 1,

      showResponsesTo: feedbackQuestion.showResponsesTo,
      showGiverNameTo: feedbackQuestion.showGiverNameTo,
      showRecipientNameTo: feedbackQuestion.showRecipientNameTo,

      isDeleting: false,
      isDuplicating: false,
      isEditable: false,
      isSaving: false,
      isCollapsed: false,

      isVisibilityChanged: false,
      isFeedbackPathChanged: false,
      isQuestionDetailsChanged: false,
    };
  }

  /**
   * Loads the isQuestionHasResponses value for a question edit for model.
   */
  private loadResponseStatusForQuestion(model: QuestionEditFormModel): void {
    this.feedbackSessionsService.hasResponsesForQuestion(model.feedbackQuestionId)
        .subscribe({
          next: (resp: HasResponses) => {
            model.isQuestionHasResponses = resp.hasResponses;
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Saves the existing question.
   */
  saveExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const originalQuestionNumber: number =
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!.questionNumber;

    questionEditFormModel.isSaving = true;
    this.feedbackQuestionsService.saveFeedbackQuestion(questionEditFormModel.feedbackQuestionId, {
      questionNumber: questionEditFormModel.questionNumber,
      questionBrief: questionEditFormModel.questionBrief,
      questionDescription: questionEditFormModel.questionDescription,

      questionDetails: questionEditFormModel.questionDetails,
      questionType: questionEditFormModel.questionType,

      giverType: questionEditFormModel.giverType,
      recipientType: questionEditFormModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: questionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: questionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo,

      showResponsesTo: questionEditFormModel.showResponsesTo,
      showGiverNameTo: questionEditFormModel.showGiverNameTo,
      showRecipientNameTo: questionEditFormModel.showRecipientNameTo,
    })
        .pipe(
            finalize(() => {
              questionEditFormModel.isSaving = false;
            }),
        )
        .subscribe({
          next: (updatedQuestion: FeedbackQuestion) => {
            this.questionEditFormModels[index] = this.getQuestionEditFormModel(updatedQuestion);
            this.feedbackQuestionModels.set(updatedQuestion.feedbackQuestionId, updatedQuestion);
            this.loadResponseStatusForQuestion(this.questionEditFormModels[index]);

            // shift question if needed
            if (originalQuestionNumber !== updatedQuestion.questionNumber) {
              // move question form
              this.moveQuestionForm(
                  originalQuestionNumber - 1, updatedQuestion.questionNumber - 1);
              this.normalizeQuestionNumberInQuestionForms();
            }

            this.statusMessageService.showSuccessToast('The changes to the question have been updated.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Moves question edit form from the original position to the new position.
   */
  private moveQuestionForm(originalPosition: number, newPosition: number): void {
    this.questionEditFormModels.splice(newPosition, 0,
        this.questionEditFormModels.splice(originalPosition, 1)[0]);

    // all expanded questions that were moved upwards must be re-expanded to reload rich text editor
    const start: number = Math.min(originalPosition, newPosition);
    const movedExpandedQuestions: QuestionEditFormModel[] = this.questionEditFormModels
      .slice(start, newPosition + 1)
      .filter((model: QuestionEditFormModel) => !model.isCollapsed);
    movedExpandedQuestions.forEach((model: QuestionEditFormModel) => {
      model.isCollapsed = true;
    });
    this.changeDetectorRef.detectChanges();
    movedExpandedQuestions.forEach((model: QuestionEditFormModel) => {
      model.isCollapsed = false;
    });
  }

  /**
   * Normalizes question number in question forms by setting question number in sequence (i.e. 1, 2, 3, 4 ...).
   */
  private normalizeQuestionNumberInQuestionForms(): void {
    for (let i: number = 1; i <= this.questionEditFormModels.length; i += 1) {
      const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[i - 1];
      questionEditFormModel.questionNumber = i;
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!.questionNumber = i;
    }
  }

  /**
   * Discards the changes made to the existing question.
   */
  discardExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const feedbackQuestion: FeedbackQuestion =
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!;
    this.questionEditFormModels[index] = this.getQuestionEditFormModel(feedbackQuestion);
  }

  /**
   * Duplicates the question.
   */
  duplicateCurrentQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];

    questionEditFormModel.isDuplicating = true;
    this.feedbackQuestionsService.createFeedbackQuestion(this.courseId, this.feedbackSessionName, {
      questionNumber: this.questionEditFormModels.length + 1, // add the duplicated question at the end
      questionBrief: questionEditFormModel.questionBrief,
      questionDescription: questionEditFormModel.questionDescription,

      questionDetails: questionEditFormModel.questionDetails,
      questionType: questionEditFormModel.questionType,

      giverType: questionEditFormModel.giverType,
      recipientType: questionEditFormModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: questionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: questionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo,

      showResponsesTo: questionEditFormModel.showResponsesTo,
      showGiverNameTo: questionEditFormModel.showGiverNameTo,
      showRecipientNameTo: questionEditFormModel.showRecipientNameTo,
    })
        .pipe(
            finalize(() => {
              questionEditFormModel.isDuplicating = false;
            }),
        )
        .subscribe({
          next: (newQuestion: FeedbackQuestion) => {
            this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
            this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
            this.statusMessageService.showSuccessToast('The question has been duplicated below.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Deletes the existing question.
   */
  deleteExistingQuestionHandler(index: number): void {
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Delete the question?', SimpleModalType.DANGER,
        'Warning: Deleted question cannot be recovered. '
        + '<b>All existing responses for this question to be deleted.</b>');
    modalRef.result.then(() => {
      const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
      questionEditFormModel.isDeleting = true;
      this.feedbackQuestionsService.deleteFeedbackQuestion(questionEditFormModel.feedbackQuestionId)
          .pipe(finalize(() => {
            questionEditFormModel.isDeleting = false;
          }))
          .subscribe({
            next: () => {
              // remove form model
              this.feedbackQuestionModels.delete(questionEditFormModel.feedbackQuestionId);
              this.questionEditFormModels.splice(index, 1);
              this.normalizeQuestionNumberInQuestionForms();

              this.statusMessageService.showSuccessToast('The question has been deleted.');
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
    }, () => {});
  }

  /**
   * Handles display of template question modal.
   */
  templateQuestionModalHandler(): void {
    const windowClass: string = 'modal-large';
    this.ngbModal.open(TemplateQuestionModalComponent, { windowClass }).result.then((questions: FeedbackQuestion[]) => {
      let questionNumber: number = this.questionEditFormModels.length; // append the questions at the end
      this.isAddingFromTemplate = true;
      of(...questions).pipe(
          concatMap((question: FeedbackQuestion) => {
            questionNumber += 1;
            return this.feedbackQuestionsService.createFeedbackQuestion(this.courseId, this.feedbackSessionName, {
              questionNumber,
              questionBrief: question.questionBrief,
              questionDescription: question.questionDescription,

              questionDetails: question.questionDetails,
              questionType: question.questionType,

              giverType: question.giverType,
              recipientType: question.recipientType,

              numberOfEntitiesToGiveFeedbackToSetting: question.numberOfEntitiesToGiveFeedbackToSetting,
              customNumberOfEntitiesToGiveFeedbackTo: question.customNumberOfEntitiesToGiveFeedbackTo,

              showResponsesTo: question.showResponsesTo,
              showGiverNameTo: question.showGiverNameTo,
              showRecipientNameTo: question.showRecipientNameTo,
            });
          }),
      ).pipe(
        finalize(() => {
          this.isAddingFromTemplate = false;
        }),
      ).subscribe({
        next: (newQuestion: FeedbackQuestion) => {
          this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
          this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
        complete: () => {
          if (questions.length === 1) {
            this.statusMessageService.showSuccessToast('The question has been added to this feedback session.');
          } else {
            this.statusMessageService.showSuccessToast('The questions have been added to this feedback session.');
          }
        },
      });
    }, () => {});
  }

  /**
   * Populates and shows new question edit form.
   */
  populateAndShowNewQuestionForm(type: FeedbackQuestionType): void {
    this.isAddingQuestionPanelExpanded = true;

    const newQuestionModel: NewQuestionModel =
        this.feedbackQuestionsService.getNewQuestionModel(type);

    this.newQuestionEditFormModel = {
      feedbackQuestionId: '',
      questionNumber: this.questionEditFormModels.length + 1,
      questionBrief: newQuestionModel.questionBrief,
      questionDescription: newQuestionModel.questionDescription,

      isQuestionHasResponses: false,

      questionType: newQuestionModel.questionType,
      questionDetails: newQuestionModel.questionDetails,

      giverType: newQuestionModel.giverType,
      recipientType: newQuestionModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: newQuestionModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: newQuestionModel.customNumberOfEntitiesToGiveFeedbackTo
          ? newQuestionModel.customNumberOfEntitiesToGiveFeedbackTo : 1,

      showResponsesTo: newQuestionModel.showResponsesTo,
      showGiverNameTo: newQuestionModel.showGiverNameTo,
      showRecipientNameTo: newQuestionModel.showRecipientNameTo,

      isDeleting: false,
      isDuplicating: false,
      isEditable: true,
      isSaving: false,
      isCollapsed: false,

      isVisibilityChanged: false,
      isFeedbackPathChanged: false,
      isQuestionDetailsChanged: false,
    };

    // inherit some settings from the last question
    if (this.questionEditFormModels.length > 0) {
      const lastQuestionEditFormModel: QuestionEditFormModel =
          this.questionEditFormModels[this.questionEditFormModels.length - 1];

      const newQuestionAllowedFeedbackPaths: Map<FeedbackParticipantType, FeedbackParticipantType[]> =
          this.feedbackQuestionsService.getAllowedFeedbackPaths(type);
      // inherit feedback path if applicable
      if (newQuestionAllowedFeedbackPaths.has(lastQuestionEditFormModel.giverType)
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          && newQuestionAllowedFeedbackPaths.get(lastQuestionEditFormModel.giverType)!
              .indexOf(lastQuestionEditFormModel.recipientType) !== -1) {
        this.newQuestionEditFormModel.giverType = lastQuestionEditFormModel.giverType;
        this.newQuestionEditFormModel.recipientType = lastQuestionEditFormModel.recipientType;
        this.newQuestionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting =
          lastQuestionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting;
        this.newQuestionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo =
          lastQuestionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo;
      }

      const newQuestionVisibilityStateMachine: VisibilityStateMachine =
          this.feedbackQuestionsService.getNewVisibilityStateMachine(
              this.newQuestionEditFormModel.giverType, this.newQuestionEditFormModel.recipientType);
      // inherit visibility settings if applicable, the state machine will automatically filter out invalid choices
      newQuestionVisibilityStateMachine.applyVisibilitySettings({
        SHOW_RESPONSE: lastQuestionEditFormModel.showResponsesTo,
        SHOW_GIVER_NAME: lastQuestionEditFormModel.showGiverNameTo,
        SHOW_RECIPIENT_NAME: lastQuestionEditFormModel.showRecipientNameTo,
      });
      const newQuestionShowResponsesTo: FeedbackVisibilityType[] =
          newQuestionVisibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RESPONSE);
      const newQuestionShowGiverNameTo: FeedbackVisibilityType[] =
          newQuestionVisibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_GIVER_NAME);
      const newQuestionShowRecipientNameTo: FeedbackVisibilityType[] =
          newQuestionVisibilityStateMachine
              .getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RECIPIENT_NAME);

      let isAllowedToUseInheritedVisibility: boolean = false;
      if (this.feedbackQuestionsService
          .isCustomFeedbackVisibilitySettingAllowed(this.newQuestionEditFormModel.questionType)) {
        isAllowedToUseInheritedVisibility = true;
      } else {
        const commonFeedbackVisibilitySettings: CommonVisibilitySetting[] =
            this.feedbackQuestionsService.getCommonFeedbackVisibilitySettings(
                newQuestionVisibilityStateMachine, this.newQuestionEditFormModel.questionType);
        // new question is only allowed to have common visibility settings
        // check whether the inherited settings fall into that or not
        for (const commonVisibilityOption of commonFeedbackVisibilitySettings) {
          if (this.isSameSet(newQuestionShowResponsesTo, commonVisibilityOption.visibilitySettings.SHOW_RESPONSE)
              && this.isSameSet(newQuestionShowGiverNameTo,
                  commonVisibilityOption.visibilitySettings.SHOW_GIVER_NAME)
              && this.isSameSet(newQuestionShowRecipientNameTo,
                  commonVisibilityOption.visibilitySettings.SHOW_RECIPIENT_NAME)) {
            isAllowedToUseInheritedVisibility = true;
            break;
          }
        }
      }

      if (isAllowedToUseInheritedVisibility) {
        this.newQuestionEditFormModel.showResponsesTo = newQuestionShowResponsesTo;
        this.newQuestionEditFormModel.showGiverNameTo = newQuestionShowGiverNameTo;
        this.newQuestionEditFormModel.showRecipientNameTo = newQuestionShowRecipientNameTo;
      }
    }

    this.scrollToNewEditForm();
  }

  private isSameSet(setA: FeedbackVisibilityType[], setB: FeedbackVisibilityType[]): boolean {
    return setA.length === setB.length && setA.every((ele: FeedbackVisibilityType) => setB.includes(ele));
  }

  /**
   * Creates a new question.
   */
  createNewQuestionHandler(): void {
    this.newQuestionEditFormModel.isSaving = true;
    this.feedbackQuestionsService.createFeedbackQuestion(this.courseId, this.feedbackSessionName, {
      questionNumber: this.newQuestionEditFormModel.questionNumber,
      questionBrief: this.newQuestionEditFormModel.questionBrief,
      questionDescription: this.newQuestionEditFormModel.questionDescription,

      questionDetails: this.newQuestionEditFormModel.questionDetails,
      questionType: this.newQuestionEditFormModel.questionType,

      giverType: this.newQuestionEditFormModel.giverType,
      recipientType: this.newQuestionEditFormModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: this.newQuestionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: this.newQuestionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo,

      showResponsesTo: this.newQuestionEditFormModel.showResponsesTo,
      showGiverNameTo: this.newQuestionEditFormModel.showGiverNameTo,
      showRecipientNameTo: this.newQuestionEditFormModel.showRecipientNameTo,
    })
        .pipe(
            finalize(() => {
              this.newQuestionEditFormModel.isSaving = false;
            }),
        )
        .subscribe({
          next: (newQuestion: FeedbackQuestion) => {
            this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
            this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);

            this.moveQuestionForm(
                this.questionEditFormModels.length - 1, newQuestion.questionNumber - 1);
            this.normalizeQuestionNumberInQuestionForms();
            this.isAddingQuestionPanelExpanded = false;

            this.statusMessageService.showSuccessToast('The question has been added to this feedback session.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Handles 'Copy Question' click event.
   */
  copyQuestionsFromOtherSessionsHandler(): void {
    this.isCopyingQuestion = true;
    const feedbackSessionTabModels: FeedbackSessionTabModel[] = [];

    this.feedbackSessionsService.getFeedbackSessionsForInstructor().pipe(
      finalize(() => {
        this.isCopyingQuestion = false;
      }),
    ).subscribe({
      next: (response: FeedbackSessions) => {
        response.feedbackSessions.forEach((feedbackSession: FeedbackSession) => {
          const model: FeedbackSessionTabModel = {
            courseId: feedbackSession.courseId,
            feedbackSessionName: feedbackSession.feedbackSessionName,
            createdAtTimestamp: feedbackSession.createdAtTimestamp,
            questionsTableRowModels: [],
            isTabExpanded: false,
            hasQuestionsLoaded: false,
            hasLoadingFailed: false,
            questionsTableRowModelsSortBy: SortBy.NONE,
            questionsTableRowModelsSortOrder: SortOrder.ASC,
          };
          feedbackSessionTabModels.push(model);
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
      complete: () => {
        const ref: NgbModalRef = this.ngbModal.open(CopyQuestionsFromOtherSessionsModalComponent);
        ref.componentInstance.feedbackSessionTabModels = feedbackSessionTabModels;

        ref.result.then((questionsToCopy: FeedbackQuestion[]) => {
          this.isCopyingQuestion = true;
          of(...questionsToCopy).pipe(
              concatMap((questionToCopy: FeedbackQuestion) => {
                return this.feedbackQuestionsService.createFeedbackQuestion(this.courseId, this.feedbackSessionName, {
                  questionNumber: this.questionEditFormModels.length + 1, // add the copied question at the end
                  questionBrief: questionToCopy.questionBrief,
                  questionDescription: questionToCopy.questionDescription,

                  questionDetails: questionToCopy.questionDetails,
                  questionType: questionToCopy.questionType,

                  giverType: questionToCopy.giverType,
                  recipientType: questionToCopy.recipientType,

                  numberOfEntitiesToGiveFeedbackToSetting: questionToCopy.numberOfEntitiesToGiveFeedbackToSetting,
                  customNumberOfEntitiesToGiveFeedbackTo: questionToCopy.customNumberOfEntitiesToGiveFeedbackTo,

                  showResponsesTo: questionToCopy.showResponsesTo,
                  showGiverNameTo: questionToCopy.showGiverNameTo,
                  showRecipientNameTo: questionToCopy.showRecipientNameTo,
                });
              }),
              finalize(() => {
                this.isCopyingQuestion = false;
              }),
          ).subscribe({
            next: (newQuestion: FeedbackQuestion) => {
              this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
              this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
              this.statusMessageService.showSuccessToast(
                  'The selected question(s) have been added to this feedback session.',
              );
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
        });
      },
    });
  }

  /**
   * Gets all students of a course.
   */
  getAllStudentsOfCourse(): void {
    this.studentService.getStudentsFromCourse({ courseId: this.courseId })
        .subscribe({
          next: (students: Students) => {
            this.studentsOfCourse = students.students;

            // sort the student list based on team name and student name
            this.studentsOfCourse.sort((a: Student, b: Student): number => {
              const teamNameCompare: number = a.teamName.localeCompare(b.teamName);
              if (teamNameCompare === 0) {
                return a.name.localeCompare(b.name);
              }
              return teamNameCompare;
            });

            // select the first student
            if (this.studentsOfCourse.length >= 1) {
              this.emailOfStudentToPreview = this.studentsOfCourse[0].email;
            }
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Gets all instructors of a course.
   */
  getAllInstructors(): void {
    this.instructorService.loadInstructors({
      courseId: this.courseId,
      intent: Intent.FULL_DETAIL,
    })
        .subscribe({
          next: (instructors: Instructors) => {
            this.instructorsOfCourse = instructors.instructors;
            // TODO use privilege API to filter instructors who has INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
            // in the feedback session

            // sort the instructor list based on name
            this.instructorsOfCourse.sort((a: Instructor, b: Instructor): number => {
              return a.name.localeCompare(b.name);
            });

            // select the first instructor
            if (this.instructorsOfCourse.length >= 1) {
              this.emailOfInstructorToPreview = this.instructorsOfCourse[0].email;
            }
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  expandAll(): void {
    this.questionEditFormModels.forEach(((model: QuestionEditFormModel): void => {
      model.isCollapsed = false;
    }));
  }

  collapseAll(): void {
    this.questionEditFormModels.forEach(((model: QuestionEditFormModel): void => {
      model.isCollapsed = true;
    }));
  }

  private deepCopy<T>(obj: T): T {
    return JSON.parse(JSON.stringify(obj));
  }

  private scrollToNewEditForm(): void {
    setTimeout(() => {
      const allEditForms: NodeListOf<Element> = document.querySelectorAll('tm-question-edit-form');
      const newEditForm: Element = allEditForms[allEditForms.length - 1];
      const yOffset: number = -70; // Need offset because of the navBar
      const y: number = newEditForm.getBoundingClientRect().top + window.scrollY + yOffset;
      window.scrollTo({ top: y, behavior: 'smooth' });
    }, 0);
  }

  scrollToTopOfPage(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
