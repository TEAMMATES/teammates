import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize, flatMap, map, switchMap, tap } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import {
  CommonVisibilitySetting,
  FeedbackQuestionsService,
  NewQuestionModel,
} from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from '../../../services/timezone.service';
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
import { VisibilityControl } from '../../../types/visibility-control';
import { CopySessionModalResult } from '../../components/copy-session-modal/copy-session-modal-model';
import { CopySessionModalComponent } from '../../components/copy-session-modal/copy-session-modal.component';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../components/question-edit-form/question-edit-form-model';
import {
  DateFormat,
  SessionEditFormMode,
  SessionEditFormModel,
  TimeFormat,
} from '../../components/session-edit-form/session-edit-form-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionBasePageComponent } from '../instructor-session-base-page.component';
import { QuestionToCopyCandidate } from './copy-questions-from-other-sessions-modal/copy-questions-from-other-sessions-modal-model';
import { CopyQuestionsFromOtherSessionsModalComponent } from './copy-questions-from-other-sessions-modal/copy-questions-from-other-sessions-modal.component';
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

    submissionStartTime: { hour: 23, minute: 59 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 23, minute: 59 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 23, minute: 59 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 23, minute: 59 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

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
    recipientType: FeedbackParticipantType.STUDENTS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 1,

    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],

    isEditable: true,
    isSaving: false,
    isCollapsed: false,
    isVisibilityChanged: false,
    isFeedbackPathChanged: false,
    isQuestionDetailsChanged: false,
  };

  isAddingQuestionPanelExpanded: boolean = false;
  isLoadingFeedbackSession: boolean = false;
  hasLoadingFeedbackSessionFailed: boolean = false;
  isLoadingFeedbackQuestions: boolean = false;
  hasLoadingFeedbackQuestionsFailed: boolean = false;
  isCopyingQuestion: boolean = false;

  // all students of the course
  studentsOfCourse: Student[] = [];
  emailOfStudentToPreview: string = '';
  // instructors which can be previewed as
  instructorsCanBePreviewedAs: Instructor[] = [];
  emailOfInstructorToPreview: string = '';

  get isAllCollapsed(): boolean {
    return this.questionEditFormModels.some((model: QuestionEditFormModel) => {
      return model.isCollapsed;
    });
  }

  constructor(router: Router,
              instructorService: InstructorService,
              statusMessageService: StatusMessageService,
              navigationService: NavigationService,
              feedbackSessionsService: FeedbackSessionsService,
              feedbackQuestionsService: FeedbackQuestionsService,
              tableComparatorService: TableComparatorService,
              ngbModal: NgbModal,
              private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute,
              private timezoneService: TimezoneService,
              private simpleModalService: SimpleModalService,
              private changeDetectorRef: ChangeDetectorRef) {
    super(router, instructorService, statusMessageService, navigationService,
        feedbackSessionsService, feedbackQuestionsService, tableComparatorService, ngbModal);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.isEditingMode = queryParams.editingMode === 'true';

      this.loadFeedbackSession();
      this.loadFeedbackQuestions();
      this.getAllStudentsOfCourse();
      this.getAllInstructorsCanBePreviewedAs();
    });
  }

  /**
   * Loads a feedback session.
   */
  loadFeedbackSession(): void {
    this.hasLoadingFeedbackSessionFailed = false;
    this.isLoadingFeedbackSession = true;
    // load the course of the feedback session first
    this.courseService.getCourseAsInstructor(this.courseId).subscribe((course: Course) => {
      this.courseName = course.courseName;

      this.feedbackSessionsService.getFeedbackSession({
        courseId: this.courseId,
        feedbackSessionName: this.feedbackSessionName,
        intent: Intent.FULL_DETAIL,
      }).pipe(finalize(() => this.isLoadingFeedbackSession = false))
      .subscribe((feedbackSession: FeedbackSession) => {
        this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession, this.isEditingMode);
        this.feedbackSessionModelBeforeEditing = this.getSessionEditFormModel(feedbackSession);
      }, (resp: ErrorMessageOutput) => {
        this.hasLoadingFeedbackSessionFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
      this.isLoadingFeedbackSession = false;
      this.hasLoadingFeedbackSessionFailed = true;
    });
  }

  /**
   * Copies the feedback session.
   */
  copyCurrentSession(): void {
    // load course candidates first
    this.courseService.getInstructorCoursesThatAreActive()
    .subscribe((courses: Courses) => {
      const modalRef: NgbModalRef = this.ngbModal.open(CopySessionModalComponent);
      modalRef.componentInstance.newFeedbackSessionName = this.feedbackSessionName;
      modalRef.componentInstance.courseCandidates = courses.courses;
      modalRef.componentInstance.sessionToCopyCourseId = this.courseId;

      modalRef.result.then((result: CopySessionModalResult) => {
        this.failedToCopySessions = {};
        const requestList: Observable<FeedbackSession>[] = this.createSessionCopyRequestsFromModal(
            result, this.courseId, this.feedbackSessionName);
        if (requestList.length === 1) {
          this.copySingleSession(requestList[0]);
        }
        if (requestList.length > 1) {
          forkJoin(requestList).subscribe(() => {
            this.showCopyStatusMessage();
          });
        }
      }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); })
      .catch(() => {});
    });
  }

  /**
   * Gets the {@code sessionEditFormModel} with {@link FeedbackSession} entity.
   */
  getSessionEditFormModel(feedbackSession: FeedbackSession, isEditable: boolean = false): SessionEditFormModel {
    const submissionStart: {date: DateFormat; time: TimeFormat} =
        this.getDateTimeAtTimezone(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone);

    const submissionEnd: {date: DateFormat; time: TimeFormat} =
        this.getDateTimeAtTimezone(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone);

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
      customSessionVisibleTime: { hour: 23, minute: 59 },
      customSessionVisibleDate: { year: 0, month: 0, day: 0 },

      responseVisibleSetting: feedbackSession.responseVisibleSetting,
      customResponseVisibleTime: { hour: 23, minute: 59 },
      customResponseVisibleDate: { year: 0, month: 0, day: 0 },

      submissionStatus: feedbackSession.submissionStatus,
      publishStatus: feedbackSession.publishStatus,

      templateSessionName: '',

      isClosingEmailEnabled: feedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: feedbackSession.isPublishedEmailEnabled,

      isSaving: false,
      hasVisibleSettingsPanelExpanded: feedbackSession.sessionVisibleSetting !== SessionVisibleSetting.AT_OPEN
          || feedbackSession.responseVisibleSetting !== ResponseVisibleSetting.LATER,
      hasEmailSettingsPanelExpanded: !feedbackSession.isClosingEmailEnabled || !feedbackSession.isPublishedEmailEnabled,
    };

    if (feedbackSession.customSessionVisibleTimestamp) {
      const customSessionVisible: {date: DateFormat; time: TimeFormat} =
          this.getDateTimeAtTimezone(feedbackSession.customSessionVisibleTimestamp, feedbackSession.timeZone);
      model.customSessionVisibleTime = customSessionVisible.time;
      model.customSessionVisibleDate = customSessionVisible.date;
    }

    if (feedbackSession.customResponseVisibleTimestamp) {
      const customResponseVisible: {date: DateFormat; time: TimeFormat} =
          this.getDateTimeAtTimezone(feedbackSession.customResponseVisibleTimestamp, feedbackSession.timeZone);
      model.customResponseVisibleTime = customResponseVisible.time;
      model.customResponseVisibleDate = customResponseVisible.date;
    }

    return model;
  }

  /**
   * Get the local date and time of timezone from timestamp.
   */
  private getDateTimeAtTimezone(timestamp: number, timeZone: string): {date: DateFormat; time: TimeFormat} {
    const momentInstance: any = this.timezoneService.getMomentInstance(timestamp, timeZone);
    const date: DateFormat = {
      year: momentInstance.year(),
      month: momentInstance.month() + 1, // moment return 0-11 for month
      day: momentInstance.date(),
    };
    const time: TimeFormat = {
      minute: momentInstance.minute(),
      hour: momentInstance.hour(),
    };
    return {
      date,
      time,
    };
  }

  /**
   * Handles editing existing session event.
   */
  editExistingSessionHandler(): void {
    this.sessionEditFormModel.isEditable = false;
    this.feedbackSessionModelBeforeEditing = JSON.parse(JSON.stringify(this.sessionEditFormModel));
    this.sessionEditFormModel.isSaving = true;

    forkJoin([
      this.resolveLocalDateTime(this.sessionEditFormModel.submissionStartDate,
          this.sessionEditFormModel.submissionStartTime, this.sessionEditFormModel.timeZone,
          'Submission opening time'),
      this.resolveLocalDateTime(this.sessionEditFormModel.submissionEndDate,
          this.sessionEditFormModel.submissionEndTime, this.sessionEditFormModel.timeZone,
          'Submission closing time'),
      this.sessionEditFormModel.sessionVisibleSetting === SessionVisibleSetting.CUSTOM ?
          this.resolveLocalDateTime(this.sessionEditFormModel.customSessionVisibleDate,
              this.sessionEditFormModel.customSessionVisibleTime, this.sessionEditFormModel.timeZone,
              'Session visible time')
          : of(0),
      this.sessionEditFormModel.responseVisibleSetting === ResponseVisibleSetting.CUSTOM ?
          this.resolveLocalDateTime(this.sessionEditFormModel.customResponseVisibleDate,
              this.sessionEditFormModel.customResponseVisibleTime, this.sessionEditFormModel.timeZone,
              'Response visible time')
          : of(0),
    ]).pipe(
        switchMap((vals: number[]) => {
          return this.feedbackSessionsService.updateFeedbackSession(this.courseId, this.feedbackSessionName, {
            instructions: this.sessionEditFormModel.instructions,

            submissionStartTimestamp: vals[0],
            submissionEndTimestamp: vals[1],
            gracePeriod: this.sessionEditFormModel.gracePeriod,

            sessionVisibleSetting: this.sessionEditFormModel.sessionVisibleSetting,
            customSessionVisibleTimestamp: vals[2],

            responseVisibleSetting: this.sessionEditFormModel.responseVisibleSetting,
            customResponseVisibleTimestamp: vals[3],

            isClosingEmailEnabled: this.sessionEditFormModel.isClosingEmailEnabled,
            isPublishedEmailEnabled: this.sessionEditFormModel.isPublishedEmailEnabled,
          });
        }),
        finalize(() => {
          this.sessionEditFormModel.isSaving = false;
        }),
    ).subscribe((feedbackSession: FeedbackSession) => {
      this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession);

      this.statusMessageService.showSuccessToast('The feedback session has been updated.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Handles canceling existing session event without saving changes.
   */
  cancelEditingSessionHandler(): void {
    this.sessionEditFormModel = JSON.parse(JSON.stringify(this.feedbackSessionModelBeforeEditing));
  }

  /**
   * Resolves the local date time to an UNIX timestamp.
   */
  private resolveLocalDateTime(
      date: DateFormat, time: TimeFormat, timeZone: string, fieldName: string): Observable<number> {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);

    const localDateTime: string = inst.format(LOCAL_DATE_TIME_FORMAT);
    return this.timezoneService.getResolvedTimestamp(localDateTime, timeZone, fieldName).pipe(
        tap((result: TimeResolvingResult) => {
          if (result.message.length !== 0) {
            this.statusMessageService.showWarningToast(result.message);
          }
        }),
        map((result: TimeResolvingResult) => result.timestamp));
  }

  /**
   * Handles deleting current feedback session.
   */
  deleteExistingSessionHandler(): void {
    this.feedbackSessionsService.moveSessionToRecycleBin(this.courseId, this.feedbackSessionName).subscribe(() => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/sessions',
          'The feedback session has been deleted. You can restore it from the deleted sessions table below.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
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
        .pipe(finalize(() => this.isLoadingFeedbackQuestions = false))
        .subscribe((response: FeedbackQuestions) => {
          response.questions.forEach((feedbackQuestion: FeedbackQuestion) => {
            const addedQuestionEditFormModel: QuestionEditFormModel = this.getQuestionEditFormModel(feedbackQuestion);
            this.questionEditFormModels.push(addedQuestionEditFormModel);
            this.loadResponseStatusForQuestion(addedQuestionEditFormModel);
            this.feedbackQuestionModels.set(feedbackQuestion.feedbackQuestionId, feedbackQuestion);
          });
        }, (resp: ErrorMessageOutput) => {
          this.hasLoadingFeedbackQuestionsFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
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
        .subscribe((resp: HasResponses) => {
          model.isQuestionHasResponses = resp.hasResponses;
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }

  /**
   * Saves the existing question.
   */
  saveExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const originalQuestionNumber: number =
        // tslint:disable-next-line:no-non-null-assertion
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
        .subscribe((updatedQuestion: FeedbackQuestion) => {
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
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
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
    movedExpandedQuestions.forEach((model: QuestionEditFormModel) => model.isCollapsed = true);
    this.changeDetectorRef.detectChanges();
    movedExpandedQuestions.forEach((model: QuestionEditFormModel) => model.isCollapsed = false);
  }

  /**
   * Normalizes question number in question forms by setting question number in sequence (i.e. 1, 2, 3, 4 ...).
   */
  private normalizeQuestionNumberInQuestionForms(): void {
    for (let i: number = 1; i <= this.questionEditFormModels.length; i += 1) {
      const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[i - 1];
      questionEditFormModel.questionNumber = i;
      // tslint:disable-next-line:no-non-null-assertion
      this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!.questionNumber = i;
    }
  }

  /**
   * Discards the changes made to the existing question.
   */
  discardExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const feedbackQuestion: FeedbackQuestion =
        // tslint:disable-next-line:no-non-null-assertion
        this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!;
    this.questionEditFormModels[index] = this.getQuestionEditFormModel(feedbackQuestion);
  }

  /**
   * Duplicates the question.
   */
  duplicateCurrentQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];

    questionEditFormModel.isSaving = true;
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
              questionEditFormModel.isSaving = false;
            }),
        )
        .subscribe((newQuestion: FeedbackQuestion) => {
          this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
          this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
          this.statusMessageService.showSuccessToast('The question has been duplicated below.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }

  /**
   * Deletes the existing question.
   */
  deleteExistingQuestionHandler(index: number): void {
    const modalRef: NgbModalRef = this.simpleModalService
        .openConfirmationModal('Delete the question?', SimpleModalType.DANGER,
            'Warning: Deleted question cannot be recovered. <b>All existing responses for this question to be deleted.</b>');
    modalRef.result.then(() => {
      const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
      this.feedbackQuestionsService.deleteFeedbackQuestion(questionEditFormModel.feedbackQuestionId).subscribe(
          () => {
            // remove form model
            this.feedbackQuestionModels.delete(questionEditFormModel.feedbackQuestionId);
            this.questionEditFormModels.splice(index, 1);
            this.normalizeQuestionNumberInQuestionForms();

            this.statusMessageService.showSuccessToast('The question has been deleted.');
          }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
    }, () => {});
  }

  /**
   * Handles display of template question modal.
   */
  templateQuestionModalHandler(): void {
    const windowClass: string = 'modal-large';
    this.ngbModal.open(TemplateQuestionModalComponent, { windowClass }).result.then((questions: FeedbackQuestion[]) => {
      let questionNumber: number = this.questionEditFormModels.length; // append the questions at the end
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
      ).subscribe((newQuestion: FeedbackQuestion) => {
        this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
        this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
      }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); }, () => {
        if (questions.length === 1) {
          this.statusMessageService.showSuccessToast('The question has been added to this feedback session.');
        } else {
          this.statusMessageService.showSuccessToast('The questions have been added to this feedback session.');
        }
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
          // tslint:disable-next-line:no-non-null-assertion
          && newQuestionAllowedFeedbackPaths.get(lastQuestionEditFormModel.giverType)!
              .indexOf(lastQuestionEditFormModel.recipientType) !== -1) {
        this.newQuestionEditFormModel.giverType = lastQuestionEditFormModel.giverType;
        this.newQuestionEditFormModel.recipientType = lastQuestionEditFormModel.recipientType;
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
      const newQuestionShowResponsesTo: FeedbackVisibilityType[]  =
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
        .subscribe((newQuestion: FeedbackQuestion) => {
          this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
          this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);

          this.moveQuestionForm(
              this.questionEditFormModels.length - 1, newQuestion.questionNumber - 1);
          this.normalizeQuestionNumberInQuestionForms();
          this.isAddingQuestionPanelExpanded = false;

          this.statusMessageService.showSuccessToast('The question has been added to this feedback session.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }

  /**
   * Handles 'Copy Question' click event.
   */
  copyQuestionsFromOtherSessionsHandler(): void {
    this.isCopyingQuestion = true;
    const questionToCopyCandidates: QuestionToCopyCandidate[] = [];

    this.feedbackSessionsService.getFeedbackSessionsForInstructor().pipe(
        switchMap((sessions: FeedbackSessions) => of(...sessions.feedbackSessions)),
        flatMap((session: FeedbackSession) => {
          return this.feedbackQuestionsService.getFeedbackQuestions({
            courseId: session.courseId,
            feedbackSessionName: session.feedbackSessionName,
            intent: Intent.FULL_DETAIL,
          },
          )
              .pipe(
                  map((questions: FeedbackQuestions) => {
                    return questions.questions.map((q: FeedbackQuestion) => ({
                      courseId: session.courseId,
                      feedbackSessionName: session.feedbackSessionName,
                      question: q,

                      isSelected: false,
                    } as QuestionToCopyCandidate));
                  }),
              );
        }),
        finalize(() => this.isCopyingQuestion = false),
    ).subscribe((questionToCopyCandidate: QuestionToCopyCandidate[]) => {
      questionToCopyCandidates.push(...questionToCopyCandidate);
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); }, () => {
      const ref: NgbModalRef = this.ngbModal.open(CopyQuestionsFromOtherSessionsModalComponent);
      ref.componentInstance.questionToCopyCandidates = questionToCopyCandidates;

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
            finalize(() => this.isCopyingQuestion = false),
        ).subscribe((newQuestion: FeedbackQuestion) => {
          this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
          this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
          this.statusMessageService.showSuccessToast('The question has been added to this feedback session.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
      });
    });
  }

  /**
   * Gets all students of a course.
   */
  getAllStudentsOfCourse(): void {
    this.studentService.getStudentsFromCourse({ courseId: this.courseId })
        .subscribe((students: Students) => {
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
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }

  /**
   * Gets all instructors of a course which can be previewed as.
   */
  getAllInstructorsCanBePreviewedAs(): void {
    this.instructorService.loadInstructors({
      courseId: this.courseId,
      intent: Intent.FULL_DETAIL,
    })
        .subscribe((instructors: Instructors) => {
          this.instructorsCanBePreviewedAs = instructors.instructors;

          // TODO use privilege API to filter instructors who has INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
          // in the feedback session

          // sort the instructor list based on name
          this.instructorsCanBePreviewedAs.sort((a: Instructor, b: Instructor): number => {
            return a.name.localeCompare(b.name);
          });

          // select the first instructor
          if (this.instructorsCanBePreviewedAs.length >= 1) {
            this.emailOfInstructorToPreview = this.instructorsCanBePreviewedAs[0].email;
          }
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
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
      const y: number = newEditForm.getBoundingClientRect().top + window.pageYOffset + yOffset;
      window.scrollTo({ top: y, behavior: 'smooth' });
    }, 0);
  }
}
