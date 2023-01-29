import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionActionsService } from '../../../services/feedback-session-actions.service';
import { FeedbackSessionsService, TemplateSession } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  Courses,
  FeedbackQuestion,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { getDefaultDateFormat, getLatestTimeFormat } from '../../../types/datetime-const';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../../../types/default-instructor-privilege';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  SessionEditFormMode,
  SessionEditFormModel,
} from '../../components/session-edit-form/session-edit-form-model';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableHeaderColorScheme,
  SessionsTableRowModel,
} from '../../components/sessions-table/sessions-table-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionModalPageComponent } from '../instructor-session-modal-page.component';
import { CopyFromOtherSessionsResult } from './copy-from-other-sessions-modal/copy-from-other-sessions-modal-model';
import {
  CopyFromOtherSessionsModalComponent,
} from './copy-from-other-sessions-modal/copy-from-other-sessions-modal.component';
import {
  SessionPermanentDeletionConfirmModalComponent,
} from './session-permanent-deletion-confirm-modal/session-permanent-deletion-confirm-modal.component';
import {
  SessionsPermanentDeletionConfirmModalComponent,
} from './sessions-permanent-deletion-confirm-modal/sessions-permanent-deletion-confirm-modal.component';

interface RecycleBinFeedbackSessionRowModel {
  feedbackSession: FeedbackSession;
}
/**
 * Instructor feedback sessions list page.
 */
@Component({
  selector: 'tm-instructor-sessions-page',
  templateUrl: './instructor-sessions-page.component.html',
  styleUrls: ['./instructor-sessions-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorSessionsPageComponent extends InstructorSessionModalPageComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  SessionsTableColumn: typeof SessionsTableColumn = SessionsTableColumn;
  SessionsTableHeaderColorScheme: typeof SessionsTableHeaderColorScheme = SessionsTableHeaderColorScheme;

  // url params
  courseId: string = '';

  // data
  courseCandidates: Course[] = [];
  templateSessions: TemplateSession[] = [];

  // models
  sessionEditFormModel: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: 'Please answer all the given questions.',

    submissionStartTime: getLatestTimeFormat(),
    submissionStartDate: getDefaultDateFormat(),
    submissionEndTime: getLatestTimeFormat(),
    submissionEndDate: getDefaultDateFormat(),
    gracePeriod: 15,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: getLatestTimeFormat(),
    customSessionVisibleDate: getDefaultDateFormat(),

    responseVisibleSetting: ResponseVisibleSetting.LATER,
    customResponseVisibleTime: getLatestTimeFormat(),
    customResponseVisibleDate: getDefaultDateFormat(),

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: true,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  isSessionEditFormExpanded: boolean = false;

  sessionsTableRowModels: SessionsTableRowModel[] = [];
  sessionsTableRowModelsSortBy: SortBy = SortBy.NONE;
  sessionsTableRowModelsSortOrder: SortOrder = SortOrder.ASC;

  isRecycleBinExpanded: boolean = false;
  recycleBinFeedbackSessionRowModels: RecycleBinFeedbackSessionRowModel[] = [];
  recycleBinFeedbackSessionRowModelsSortBy: SortBy = SortBy.NONE;
  recycleBinFeedbackSessionRowModelsSortOrder: SortOrder = SortOrder.ASC;

  isCopyOtherSessionLoading: boolean = false;
  isCoursesLoading: boolean = true;
  isFeedbackSessionsLoading: boolean = true;
  isMoveToRecycleBinLoading: boolean = false;
  isCopySessionLoading: boolean = false;
  isRecycleBinLoading: boolean = true;
  isRestoreFeedbackSessionLoading: boolean = false;
  isPermanentDeleteLoading: boolean = false;
  hasCourseLoadingFailed: boolean = false;
  hasFeedbackSessionLoadingFailed: boolean = false;

  @ViewChild('modifiedTimestampsModal') modifiedTimestampsModal!: TemplateRef<any>;

  constructor(statusMessageService: StatusMessageService,
              navigationService: NavigationService,
              feedbackSessionsService: FeedbackSessionsService,
              feedbackQuestionsService: FeedbackQuestionsService,
              ngbModalService: NgbModal,
              studentService: StudentService,
              instructorService: InstructorService,
              tableComparatorService: TableComparatorService,
              simpleModalService: SimpleModalService,
              progressBarService: ProgressBarService,
              feedbackSessionActionsService: FeedbackSessionActionsService,
              timezoneService: TimezoneService,
              private courseService: CourseService,
              private route: ActivatedRoute) {
    super(instructorService, statusMessageService, navigationService, feedbackSessionsService,
        feedbackQuestionsService, tableComparatorService, ngbModalService,
        simpleModalService, progressBarService, feedbackSessionActionsService, timezoneService, studentService);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
    });

    this.isSessionEditFormExpanded = !!this.courseId;
    this.templateSessions = this.feedbackSessionsService.getTemplateSessions();
    this.loadCandidatesCourse();
    this.loadFeedbackSessions();
    this.loadRecycleBinFeedbackSessions();
  }

  /**
   * Copies from other sessions.
   */
  copyFromOtherSessionsHandler(): void {
    this.isCopyOtherSessionLoading = true;
    const modalRef: NgbModalRef = this.ngbModal.open(CopyFromOtherSessionsModalComponent);
    // select the current course Id.
    modalRef.componentInstance.copyToCourseId = this.sessionEditFormModel.courseId;

    modalRef.componentInstance.courseCandidates = this.courseCandidates;
    modalRef.componentInstance.existingFeedbackSession =
        this.sessionsTableRowModels.map((model: SessionsTableRowModel) => model.feedbackSession);

    modalRef.result.then((result: CopyFromOtherSessionsResult) => {
      this.coursesOfModifiedSession = [];
      this.modifiedSession = {};
      this.copyFeedbackSession(result.fromFeedbackSession, result.newFeedbackSessionName, result.copyToCourseId,
        result.fromFeedbackSession.courseId)
          .pipe(finalize(() => {
            this.isCopyOtherSessionLoading = false;
          }))
          .subscribe({
            next: (createdFeedbackSession: FeedbackSession) => {
              if (this.coursesOfModifiedSession.length > 0) {
                this.simpleModalService.openInformationModal('Note On Tweaked Session Timestamps',
                    SimpleModalType.WARNING, this.modifiedTimestampsModal,
                    {
                      onClosed: () => this.navigationService.navigateByURLWithParamEncoding(
                          '/web/instructor/sessions/edit',
                          {
                            courseid: createdFeedbackSession.courseId,
                            fsname: createdFeedbackSession.feedbackSessionName,
                          }),
                    });
              } else {
                this.navigationService.navigateWithSuccessMessage('/web/instructor/sessions/edit',
                    'The feedback session has been copied. Please modify settings/questions as necessary.',
                    { courseid: createdFeedbackSession.courseId, fsname: createdFeedbackSession.feedbackSessionName });
              }
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(
                  this.formatErrorMessage(resp.error.message));
            },
          });
    }).catch(() => {
      this.isCopyOtherSessionLoading = false;
    });
  }

  /**
   * Loads courses owned by the current user.
   */
  loadCandidatesCourse(): void {
    this.isCoursesLoading = true;
    this.courseService.getInstructorCoursesThatAreActive()
        .pipe(finalize(() => {
          this.isCoursesLoading = false;
        }))
        .subscribe({
          next: (courses: Courses) => {
            this.courseCandidates = courses.courses;

            this.initDefaultValuesForSessionEditForm();
          },
          error: (resp: ErrorMessageOutput) => {
            this.resetAllModels();
            this.hasCourseLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
   }

  /**
   * Sets default values for the session edit form.
   */
  initDefaultValuesForSessionEditForm(): void {
    // if specified in the query and is valid, select that course
    // otherwise, select the first course by default
    const course: Course | undefined = this.courseCandidates.find((c: Course) => c.courseId === this.courseId);
    if (course) {
      this.sessionEditFormModel.courseId = course.courseId;
      this.sessionEditFormModel.courseName = course.courseName;
      this.sessionEditFormModel.timeZone = course.timeZone;
    } else if (this.courseCandidates.length !== 0) {
      this.sessionEditFormModel.courseId = this.courseCandidates[0].courseId;
      this.sessionEditFormModel.courseName = this.courseCandidates[0].courseName;
      this.sessionEditFormModel.timeZone = this.courseCandidates[0].timeZone;
    }

    // select the first template session
    if (this.templateSessions.length !== 0) {
      this.sessionEditFormModel.templateSessionName = this.templateSessions[0].name;
    }

    // set opening time to near future
    const nearFuture: moment.Moment = moment().tz(this.sessionEditFormModel.timeZone).add(1, 'hours');
    this.sessionEditFormModel.submissionStartDate = {
      year: nearFuture.year(),
      month: nearFuture.month() + 1, // moment return 0-11 for month
      day: nearFuture.date(),
    };
    this.sessionEditFormModel.submissionStartTime = {
      minute: nearFuture.hour() === 0 ? 59 : 0, // for 00:00 midnight, we use 23:59
      hour: nearFuture.hour() === 0 ? 23 : nearFuture.hour(),
    };
    // set the closing time to tomorrow
    const tomorrow: moment.Moment = moment().tz(this.sessionEditFormModel.timeZone).add(1, 'days');
    this.sessionEditFormModel.submissionEndDate = {
      year: tomorrow.year(),
      month: tomorrow.month() + 1, // moment return 0-11 for month
      day: tomorrow.date(),
    };
    this.sessionEditFormModel.submissionEndTime = {
      minute: 59,
      hour: 23,
    };
  }

  /**
   * Adds a new feedback session.
   */
  addNewSessionHandler(): void {
    this.sessionEditFormModel.isSaving = true;

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

    this.feedbackSessionsService.createFeedbackSession(this.sessionEditFormModel.courseId, {
      feedbackSessionName: this.sessionEditFormModel.feedbackSessionName,
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
    }).subscribe({
      next: (feedbackSession: FeedbackSession) => {

        // begin to populate session with template
        const templateSession: TemplateSession | undefined =
            this.feedbackSessionsService.getTemplateSessions().find(
                (t: TemplateSession) => t.name === this.sessionEditFormModel.templateSessionName);
        if (!templateSession) {
          return;
        }
        of(...templateSession.questions).pipe(
            concatMap((question: FeedbackQuestion) => {
              return this.feedbackQuestionsService.createFeedbackQuestion(
                  feedbackSession.courseId, feedbackSession.feedbackSessionName, {
                    questionNumber: question.questionNumber,
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
        ).subscribe({
          next: () => {},
          error: (resp: ErrorMessageOutput) => {
            this.sessionEditFormModel.isSaving = false;
            this.statusMessageService.showErrorToast(
                `The session is created but the template questions cannot be created: ${resp.error.message}`);
          },
          complete: () => {
            this.navigationService.navigateByURLWithParamEncoding(
                '/web/instructor/sessions/edit',
                { courseid: feedbackSession.courseId, fsname: feedbackSession.feedbackSessionName })
                .then(() => {
                  this.statusMessageService.showSuccessToast('The feedback session has been added.'
                      + 'Click the "Add New Question" button below to begin adding questions '
                      + 'for the feedback session.');
                });
          },
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.sessionEditFormModel.isSaving = false;
        this.statusMessageService.showErrorToast(
            this.formatErrorMessage(resp.error.message));
      },
    });
  }

  formatErrorMessage(errorMessage: string): string {
    if (errorMessage.match('exists already in the course')) {
      return `${errorMessage}
          Tip: If you can't find such a session in that course, also check the 'Recycle bin'
          (shown at the bottom of the 'Sessions' page).`;
    }
    return errorMessage;
  }

  /**
   * Loads all feedback sessions that can be accessed by current user.
   */
  loadFeedbackSessions(): void {
    this.isFeedbackSessionsLoading = true;
    this.feedbackSessionsService.getFeedbackSessionsForInstructor()
        .pipe(finalize(() => {
          this.isFeedbackSessionsLoading = false;
        }))
        .subscribe({
          next: (response: FeedbackSessions) => {
            response.feedbackSessions.forEach((session: FeedbackSession) => {
              const model: SessionsTableRowModel = {
                feedbackSession: session,
                responseRate: '',
                isLoadingResponseRate: false,
                instructorPrivilege: session.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
              };
              this.sessionsTableRowModels.push(model);
            });
          },
          error: (resp: ErrorMessageOutput) => {
            this.resetAllModels();
            this.hasFeedbackSessionLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
          complete: () => this.sortSessionsTableRowModelsEvent(SortBy.COURSE_ID),
        });
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortSessionsTableRowModelsEvent(by: SortBy): void {
    this.sessionsTableRowModelsSortBy = by;
    // reverse the sort order
    this.sessionsTableRowModelsSortOrder =
        this.sessionsTableRowModelsSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.sessionsTableRowModels.sort(this.sortModelsBy(by, this.sessionsTableRowModelsSortOrder));
  }

  /**
   * Sorts the list of feedback session rows in recycle bin table.
   */
  sortRecycleBinFeedbackSessionRowsEvent(by: SortBy): void {
    this.recycleBinFeedbackSessionRowModelsSortBy = by;
    // reverse the sort order
    this.recycleBinFeedbackSessionRowModelsSortOrder =
        this.recycleBinFeedbackSessionRowModelsSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.recycleBinFeedbackSessionRowModels.sort(
        this.sortModelsBy(by, this.recycleBinFeedbackSessionRowModelsSortOrder));
  }
  /**
   * Loads response rate of a feedback session.
   */
  loadResponseRateEventHandler(rowIndex: number): void {
    this.loadResponseRate(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Restores a recycle bin feedback session.
   */
  restoreRecycleBinFeedbackSession(model: RecycleBinFeedbackSessionRowModel): void {
    this.isRestoreFeedbackSessionLoading = true;
    this.feedbackSessionsService.deleteSessionFromRecycleBin(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .pipe(finalize(() => {
          this.isRestoreFeedbackSessionLoading = false;
        }))
        .subscribe({
          next: (feedbackSession: FeedbackSession) => {
            this.recycleBinFeedbackSessionRowModels.splice(
                this.recycleBinFeedbackSessionRowModels.indexOf(model), 1);
            const m: SessionsTableRowModel = {
              feedbackSession,
              responseRate: '',
              isLoadingResponseRate: false,
              instructorPrivilege: feedbackSession.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
            };
            this.sessionsTableRowModels.push(m);
            this.statusMessageService.showSuccessToast('The feedback session has been restored.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Moves the feedback session to the recycle bin.
   */
  moveSessionToRecycleBinEventHandler(rowIndex: number): void {
    this.isMoveToRecycleBinLoading = true;
    const model: SessionsTableRowModel = this.sessionsTableRowModels[rowIndex];
    this.feedbackSessionsService.moveSessionToRecycleBin(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .pipe(finalize(() => {
          this.isMoveToRecycleBinLoading = false;
        }))
        .subscribe({
          next: (feedbackSession: FeedbackSession) => {
            this.sessionsTableRowModels.splice(this.sessionsTableRowModels.indexOf(model), 1);
            this.recycleBinFeedbackSessionRowModels.push({
              feedbackSession,
            });
            this.statusMessageService.showSuccessToast('The feedback session has been deleted. '
                + 'You can restore it from the deleted sessions table below.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Edits the feedback session.
   */
  copySessionEventHandler(result: CopySessionResult): void {
    this.isCopySessionLoading = true;
    this.failedToCopySessions = {};
    this.coursesOfModifiedSession = [];
    this.modifiedSession = {};
    const requestList: Observable<FeedbackSession>[] = this.createSessionCopyRequestsFromRowModel(
        this.sessionsTableRowModels[result.sessionToCopyRowIndex], result);
    if (requestList.length === 1) {
      this.copySingleSession(requestList[0], this.modifiedTimestampsModal);
    }
    if (requestList.length > 1) {
      forkJoin(requestList).pipe(finalize(() => {
        this.isCopySessionLoading = false;
      }))
        .subscribe((newSessions: FeedbackSession[]) => {
          if (newSessions.length > 0) {
            newSessions.forEach((session: FeedbackSession) => {
              const model: SessionsTableRowModel = {
                feedbackSession: session,
                responseRate: '',
                isLoadingResponseRate: false,
                instructorPrivilege: session.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
              };
              this.sessionsTableRowModels.push(model);
            });
          }
          this.showCopyStatusMessage(this.modifiedTimestampsModal);
        });
    }
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructorEventHandler(rowIndex: number): void {
    this.submitSessionAsInstructor(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Publishes a feedback session.
   */
  publishSessionEventHandler(rowIndex: number): void {
    this.publishSession(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSessionEventHandler(rowIndex: number): void {
    this.unpublishSession(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Downloads the result of a feedback session in csv.
   */
  downloadSessionResultEventHandler(rowIndex: number): void {
    this.downloadSessionResult(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Loads all feedback sessions in recycle bin that can be accessed by current user.
   */
  loadRecycleBinFeedbackSessions(): void {
    this.isRecycleBinLoading = true;
    this.feedbackSessionsService.getFeedbackSessionsInRecycleBinForInstructor()
        .pipe(finalize(() => {
          this.isRecycleBinLoading = false;
        }))
        .subscribe({
          next: (response: FeedbackSessions) => {
            response.feedbackSessions.forEach((session: FeedbackSession) => {
              this.recycleBinFeedbackSessionRowModels.push({
                feedbackSession: session,
              });
            });
          },
          error: (resp: ErrorMessageOutput) => {
            this.resetAllModels();
            this.hasFeedbackSessionLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
          complete: () => this.sortRecycleBinFeedbackSessionRowsEvent(SortBy.SESSION_DELETION_DATE),
        });
  }

  /**
   * Restores all feedback sessions in recycle bin.
   */
  restoreAllRecycleBinFeedbackSession(): void {
    this.isRestoreFeedbackSessionLoading = true;
    const restoreRequests: Observable<FeedbackSession>[] = [];
    this.recycleBinFeedbackSessionRowModels.forEach((model: RecycleBinFeedbackSessionRowModel) => {
      restoreRequests.push(
          this.feedbackSessionsService.deleteSessionFromRecycleBin(
              model.feedbackSession.courseId,
              model.feedbackSession.feedbackSessionName,
          ));
    });

    forkJoin(restoreRequests).pipe(finalize(() => {
      this.isRestoreFeedbackSessionLoading = false;
    }))
      .subscribe({
        next: (restoredSessions: FeedbackSession[]) => {
          restoredSessions.forEach((session: FeedbackSession) => {
            this.recycleBinFeedbackSessionRowModels = [];
            const m: SessionsTableRowModel = {
              feedbackSession: session,
              responseRate: '',
              isLoadingResponseRate: false,
              instructorPrivilege: session.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE(),
            };
            this.sessionsTableRowModels.push(m);
          });
          this.statusMessageService.showSuccessToast('All sessions have been restored.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Deletes the feedback session permanently.
   */
  permanentDeleteSession(model: RecycleBinFeedbackSessionRowModel): void {
    this.isPermanentDeleteLoading = true;
    const modalRef: NgbModalRef = this.ngbModal.open(SessionPermanentDeletionConfirmModalComponent);
    modalRef.componentInstance.courseId = model.feedbackSession.courseId;
    modalRef.componentInstance.feedbackSessionName = model.feedbackSession.feedbackSessionName;

    modalRef.result.then(() => {
      this.feedbackSessionsService.deleteFeedbackSession(
          model.feedbackSession.courseId,
          model.feedbackSession.feedbackSessionName,
      )
        .pipe(finalize(() => {
          this.isPermanentDeleteLoading = false;
        }))
        .subscribe({
          next: () => {
            this.recycleBinFeedbackSessionRowModels.splice(
                this.recycleBinFeedbackSessionRowModels.indexOf(model), 1);
            this.statusMessageService.showSuccessToast('The feedback session has been permanently deleted.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
    }).catch(() => {
      this.isPermanentDeleteLoading = false;
    });
  }

  /**
   * Deletes all feedback sessions in the recycle bin permanently.
   */
  permanentDeleteAllSessions(): void {
    this.isPermanentDeleteLoading = true;
    const modalRef: NgbModalRef = this.ngbModal.open(SessionsPermanentDeletionConfirmModalComponent);
    modalRef.componentInstance.sessionsToDelete =
        this.recycleBinFeedbackSessionRowModels.map(
            (model: RecycleBinFeedbackSessionRowModel) => model.feedbackSession);

    modalRef.result.then(() => {
      const deleteRequests: Observable<any>[] = [];

      this.recycleBinFeedbackSessionRowModels.forEach((model: RecycleBinFeedbackSessionRowModel) => {
        deleteRequests.push(this.feedbackSessionsService.deleteFeedbackSession(
            model.feedbackSession.courseId,
            model.feedbackSession.feedbackSessionName,
        ));
      });

      forkJoin(deleteRequests).pipe(finalize(() => {
        this.isPermanentDeleteLoading = false;
      }))
        .subscribe({
          next: () => {
            this.recycleBinFeedbackSessionRowModels = [];
            this.statusMessageService.showSuccessToast('All sessions have been permanently deleted.');
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
    }).catch(() => {
      this.isPermanentDeleteLoading = false;
    });
  }

  /**
   * Attempts to load all data again.
   */
  retryLoadingAllData(): void {
    this.hasFeedbackSessionLoadingFailed = false;
    this.hasCourseLoadingFailed = false;
    this.loadCandidatesCourse();
    this.loadFeedbackSessions();
    this.loadRecycleBinFeedbackSessions();
  }

  resetAllModels(): void {
    this.courseCandidates = [];
    this.sessionsTableRowModels = [];
    this.recycleBinFeedbackSessionRowModels = [];
  }
}
