import { TemplateRef } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { Observable, of } from 'rxjs';
import { catchError, finalize, switchMap } from 'rxjs/operators';
import { FeedbackQuestionsService } from '../../services/feedback-questions.service';
import { FeedbackSessionActionsService } from '../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../services/feedback-sessions.service';
import { InstructorService } from '../../services/instructor.service';
import { NavigationService } from '../../services/navigation.service';
import { ProgressBarService } from '../../services/progress-bar.service';
import { SimpleModalService } from '../../services/simple-modal.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TableComparatorService } from '../../services/table-comparator.service';
import { TimezoneService } from '../../services/timezone.service';
import {
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionStats,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../types/api-output';
import { Intent } from '../../types/api-request';
import { getDefaultDateFormat, getLatestTimeFormat } from '../../types/datetime-const';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../types/default-retry-attempts';
import { SortBy, SortOrder } from '../../types/sort-properties';
import { CopySessionModalResult } from '../components/copy-session-modal/copy-session-modal-model';
import { ErrorReportComponent } from '../components/error-report/error-report.component';
import { SessionEditFormModel } from '../components/session-edit-form/session-edit-form-model';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableColumnNames,
  SessionsTableRowModel,
} from '../components/sessions-table/sessions-table-model';
import { SimpleModalType } from '../components/simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableTableCellData,
} from '../components/sortable-table/sortable-table.component';
import { PublishStatusNamePipe } from '../components/teammates-common/publish-status-name.pipe';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionBasePageComponent {

  isResultActionLoading: boolean = false;

  protected failedToCopySessions: Record<string, string> = {}; // Map of failed session copy to error message
  coursesOfModifiedSession: Array<string> = [];
  modifiedSession: Record<string, TweakedTimestampData> = {};

  private publishUnpublishRetryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

  private publishStatusName: PublishStatusNamePipe = new PublishStatusNamePipe();

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

    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: false,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  protected constructor(protected instructorService: InstructorService,
                        protected statusMessageService: StatusMessageService,
                        protected navigationService: NavigationService,
                        protected feedbackSessionsService: FeedbackSessionsService,
                        protected feedbackQuestionsService: FeedbackQuestionsService,
                        protected tableComparatorService: TableComparatorService,
                        protected ngbModal: NgbModal,
                        protected simpleModalService: SimpleModalService,
                        protected progressBarService: ProgressBarService,
                        protected feedbackSessionActionsService: FeedbackSessionActionsService,
                        protected timezoneService: TimezoneService) { }

  /**
   * Copies a feedback session.
   */
  protected copyFeedbackSession(fromFeedbackSession: FeedbackSession, newSessionName: string, newCourseId: string,
      oldCourseId: string): Observable<FeedbackSession> {
    // Local constants
    const startHour = moment.utc(fromFeedbackSession.submissionStartTimestamp).tz(fromFeedbackSession.timeZone).hours();
    const endHour = moment(fromFeedbackSession.submissionEndTimestamp).tz(fromFeedbackSession.timeZone).hours();
    const twoHoursBeforeNow = moment().subtract(2, 'hours')
        .valueOf();
    const twoDaysFromNowSameHour = moment().tz(fromFeedbackSession.timeZone).add(2, 'days')
        .set('hour', startHour)
        .startOf('hour')
        .valueOf();
    const sevenDaysFromNowSameHour = moment().tz(fromFeedbackSession.timeZone).add(7, 'days')
        .set('hour', endHour)
        .startOf('hour')
        .valueOf();
    const ninetyDaysFromNow = moment().tz(fromFeedbackSession.timeZone).add(90, 'days')
        .valueOf();
    const ninetyDaysFromNowRoundedDown = moment().tz(fromFeedbackSession.timeZone).add(90, 'days').startOf('hour')
        .valueOf();
    const oneHundredAndEightyDaysFromNow = moment().tz(fromFeedbackSession.timeZone).add(180, 'days')
        .valueOf();
    const oneHundredAndEightyDaysFromNowRoundedDown = moment().tz(fromFeedbackSession.timeZone).add(180, 'days')
        .startOf('hour')
        .valueOf();

    // Preprocess timestamps to adhere to feedback session timestamps constraints
    let isModified: boolean = false;

    let copiedSubmissionStartTimestamp = fromFeedbackSession.submissionStartTimestamp;
    if (copiedSubmissionStartTimestamp < twoHoursBeforeNow) {
      copiedSubmissionStartTimestamp = twoDaysFromNowSameHour;
      isModified = true;
    } else if (copiedSubmissionStartTimestamp > ninetyDaysFromNow) {
      copiedSubmissionStartTimestamp = ninetyDaysFromNowRoundedDown;
      isModified = true;
    }

    let copiedSubmissionEndTimestamp = fromFeedbackSession.submissionEndTimestamp;
    if (copiedSubmissionEndTimestamp < copiedSubmissionStartTimestamp) {
      copiedSubmissionEndTimestamp = sevenDaysFromNowSameHour;
      isModified = true;
    } else if (copiedSubmissionEndTimestamp > oneHundredAndEightyDaysFromNow) {
      copiedSubmissionEndTimestamp = oneHundredAndEightyDaysFromNowRoundedDown;
      isModified = true;
    }

    let copiedSessionVisibleSetting = fromFeedbackSession.sessionVisibleSetting;
    let copiedCustomSessionVisibleTimestamp = fromFeedbackSession.customSessionVisibleTimestamp!;
    const thirtyDaysBeforeSubmissionStart = moment(copiedSubmissionStartTimestamp)
        .tz(fromFeedbackSession.timeZone).subtract(30, 'days')
        .valueOf();
    const thirtyDaysBeforeSubmissionStartRoundedUp = moment(copiedSubmissionStartTimestamp)
        .tz(fromFeedbackSession.timeZone).subtract(30, 'days').startOf('hour')
        .valueOf();
    if (copiedSessionVisibleSetting === SessionVisibleSetting.CUSTOM) {
      if (copiedCustomSessionVisibleTimestamp < thirtyDaysBeforeSubmissionStart) {
        copiedCustomSessionVisibleTimestamp = thirtyDaysBeforeSubmissionStartRoundedUp;
        isModified = true;
      } else if (copiedCustomSessionVisibleTimestamp > copiedSubmissionStartTimestamp) {
        copiedSessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        isModified = true;
      }
    }

    let copiedResponseVisibleSetting = fromFeedbackSession.responseVisibleSetting;
    const copiedCustomResponseVisibleTimestamp = fromFeedbackSession.customResponseVisibleTimestamp!;
    if (copiedResponseVisibleSetting === ResponseVisibleSetting.CUSTOM
        && ((copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
                && copiedCustomResponseVisibleTimestamp < copiedSubmissionStartTimestamp)
            || copiedCustomResponseVisibleTimestamp < copiedCustomSessionVisibleTimestamp)) {
      copiedResponseVisibleSetting = ResponseVisibleSetting.LATER;
      isModified = true;
    }

    if (isModified) {
      this.coursesOfModifiedSession.push(newCourseId);

      this.modifiedSession[newSessionName] = {
        oldTimestamp: {
          submissionStartTimestamp: this.formatTimestamp(fromFeedbackSession.submissionStartTimestamp,
              fromFeedbackSession.timeZone),
          submissionEndTimestamp: this.formatTimestamp(fromFeedbackSession.submissionEndTimestamp,
              fromFeedbackSession.timeZone),
          sessionVisibleTimestamp: fromFeedbackSession.sessionVisibleSetting === SessionVisibleSetting.AT_OPEN
              ? 'On submission opening time'
              : this.formatTimestamp(fromFeedbackSession.customSessionVisibleTimestamp!, fromFeedbackSession.timeZone),
          responseVisibleTimestamp: '',
        },
        newTimestamp: {
          submissionStartTimestamp: this.formatTimestamp(copiedSubmissionStartTimestamp, fromFeedbackSession.timeZone),
          submissionEndTimestamp: this.formatTimestamp(copiedSubmissionEndTimestamp, fromFeedbackSession.timeZone),
          sessionVisibleTimestamp: copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
              ? 'On submission opening time'
              : this.formatTimestamp(copiedCustomSessionVisibleTimestamp!, fromFeedbackSession.timeZone),
          responseVisibleTimestamp: '',
        },
      };

      if (fromFeedbackSession.responseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
        this.modifiedSession[newSessionName].oldTimestamp.responseVisibleTimestamp =
            'On session visible time';
      } else if (fromFeedbackSession.responseVisibleSetting === ResponseVisibleSetting.LATER) {
        this.modifiedSession[newSessionName].oldTimestamp.responseVisibleTimestamp =
            'Not now (publish manually)';
      } else {
        this.modifiedSession[newSessionName].oldTimestamp.responseVisibleTimestamp =
            this.formatTimestamp(fromFeedbackSession.customResponseVisibleTimestamp!, fromFeedbackSession.timeZone);
      }

      if (copiedResponseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
        this.modifiedSession[newSessionName].newTimestamp.responseVisibleTimestamp =
            'On session visible time';
      } else if (copiedResponseVisibleSetting === ResponseVisibleSetting.LATER) {
        this.modifiedSession[newSessionName].newTimestamp.responseVisibleTimestamp =
            'Not now (publish manually)';
      } else {
        this.modifiedSession[newSessionName].newTimestamp.responseVisibleTimestamp =
            this.formatTimestamp(copiedCustomResponseVisibleTimestamp!, fromFeedbackSession.timeZone);
      }

    }

    return this.feedbackSessionsService.createFeedbackSession(newCourseId, {
      feedbackSessionName: newSessionName,
      instructions: fromFeedbackSession.instructions,
      toCopySessionName: fromFeedbackSession.feedbackSessionName,
      toCopyCourseId: oldCourseId,

      submissionStartTimestamp: copiedSubmissionStartTimestamp,
      submissionEndTimestamp: copiedSubmissionEndTimestamp,
      gracePeriod: fromFeedbackSession.gracePeriod,

      sessionVisibleSetting: copiedSessionVisibleSetting,
      customSessionVisibleTimestamp: copiedCustomSessionVisibleTimestamp,

      responseVisibleSetting: copiedResponseVisibleSetting,
      customResponseVisibleTimestamp: fromFeedbackSession.customResponseVisibleTimestamp,

      isClosingSoonEmailEnabled: fromFeedbackSession.isClosingSoonEmailEnabled,
      isPublishedEmailEnabled: fromFeedbackSession.isPublishedEmailEnabled,
    });
  }

  private formatTimestamp(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM YYYY h:mm A');
  }

  /**
   * Generates a sorting function.
   */
  protected sortModelsBy(by: SortBy, order: SortOrder):
      ((a: { feedbackSession: FeedbackSession }, b: { feedbackSession: FeedbackSession }) => number) {
    return ((a: { feedbackSession: FeedbackSession }, b: { feedbackSession: FeedbackSession }): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SESSION_NAME:
          strA = a.feedbackSession.feedbackSessionName;
          strB = b.feedbackSession.feedbackSessionName;
          break;
        case SortBy.COURSE_ID:
          strA = a.feedbackSession.courseId;
          strB = b.feedbackSession.courseId;
          break;
        case SortBy.SESSION_START_DATE:
          strA = String(a.feedbackSession.submissionStartTimestamp);
          strB = String(b.feedbackSession.submissionStartTimestamp);
          break;
        case SortBy.SESSION_END_DATE:
          strA = String(a.feedbackSession.submissionEndTimestamp);
          strB = String(b.feedbackSession.submissionEndTimestamp);
          break;
        case SortBy.SESSION_CREATION_DATE:
          strA = String(a.feedbackSession.createdAtTimestamp);
          strB = String(b.feedbackSession.createdAtTimestamp);
          break;
        case SortBy.SESSION_DELETION_DATE:
          strA = String(a.feedbackSession.deletedAtTimestamp);
          strB = String(b.feedbackSession.deletedAtTimestamp);
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }

  /**
   * Loads response rate of a feedback session.
   */
  loadResponseRate(cb: (
    models: SessionsTableRowModel[]) => void, models: SessionsTableRowModel[],
    idx: number,
  ): void {
    models[idx] = {
      ...models[idx],
      isLoadingResponseRate: true,
    };
    cb(models);

    this.feedbackSessionsService
      .loadSessionStatistics(models[idx].feedbackSession.courseId, models[idx].feedbackSession.feedbackSessionName)
      .pipe(
        finalize(() => {
          models[idx] = {
            ...models[idx],
            isLoadingResponseRate: false,
          };
          cb(models);
        }),
      )
      .subscribe({
        next: (resp: FeedbackSessionStats) => {
          models[idx] = {
            ...models[idx],
            responseRate: `${resp.submittedTotal} / ${resp.expectedTotal}`,
          };
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
        complete: () => {
          /* eslint no-param-reassign: "off" */
          models = [...models];
          cb(models);
        },
      });
  }

  /**
   * Creates list of copy session requests from params.
   *
   * @param model the source session model
   * @param result the result of the copy session modal
   * @returns the list of copy session requests
   */
  createSessionCopyRequestsFromRowModel(
      model: SessionsTableRowModel, result: CopySessionResult): Observable<FeedbackSession>[] {
    const copySessionRequests: Observable<FeedbackSession>[] = [];
    result.copyToCourseList.forEach((copyToCourseId: string) => {
      copySessionRequests.push(
          this.copyFeedbackSession(model.feedbackSession, result.newFeedbackSessionName, copyToCourseId,
            result.sessionToCopyCourseId)
              .pipe(catchError((err: any) => {
                this.failedToCopySessions[copyToCourseId] = err.error.message;
                return of(err);
              })),
      );
    });
    return copySessionRequests;
  }

  /**
   * Creates list of copy session requests from params.
   *
   * @param result the result of the copy session modal
   * @param courseId the source courseId
   * @param feedbackSessionName the source feedback session name
   * @returns the list of copy session requests
   */
  createSessionCopyRequestsFromModal(result: CopySessionModalResult, courseId: string, feedbackSessionName: string)
      : Observable<FeedbackSession>[] {
    const copySessionRequests: Observable<FeedbackSession>[] = [];
    result.copyToCourseList.forEach((copyToCourseId: string) => {
      copySessionRequests.push(this.feedbackSessionsService.getFeedbackSession({
        courseId,
        feedbackSessionName,
        intent: Intent.FULL_DETAIL,
      }).pipe(
          switchMap((feedbackSession: FeedbackSession) =>
              this.copyFeedbackSession(feedbackSession, result.newFeedbackSessionName, copyToCourseId,
                result.sessionToCopyCourseId)),
          catchError((err: any) => {
            this.failedToCopySessions[copyToCourseId] = err.error.message;
            return of(err);
          }),
      ));
    });
    return copySessionRequests;
  }

  /**
   * Submits a single copy session request.
   */
  copySingleSession(copySessionRequest: Observable<FeedbackSession>, modifiedTimestampsModal: TemplateRef<any>): void {
    copySessionRequest.subscribe({
      next: (createdSession: FeedbackSession) => {
        if (Object.keys(this.failedToCopySessions).length > 0) {
          this.statusMessageService.showErrorToast(this.getCopyErrorMessage());
        } else if (this.coursesOfModifiedSession.length > 0) {
          this.simpleModalService.openInformationModal('Note On Modified Session Timings',
              SimpleModalType.WARNING, modifiedTimestampsModal,
              {
                onClosed: () => this.navigationService.navigateByURLWithParamEncoding(
                    '/web/instructor/sessions/edit',
                    { courseid: createdSession.courseId, fsname: createdSession.feedbackSessionName }),
              });
        } else {
          this.navigationService.navigateWithSuccessMessage(
              '/web/instructor/sessions/edit',
              'The feedback session has been copied. Please modify settings/questions as necessary.',
              { courseid: createdSession.courseId, fsname: createdSession.feedbackSessionName });
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  showCopyStatusMessage(modifiedTimestampsModal: TemplateRef<any>): void {
    if (Object.keys(this.failedToCopySessions).length > 0) {
      this.statusMessageService.showErrorToast(this.getCopyErrorMessage());
    } else if (this.coursesOfModifiedSession.length > 0) {
      this.simpleModalService.openInformationModal('Note On Modified Session Timings',
          SimpleModalType.WARNING, modifiedTimestampsModal);
    } else {
      this.statusMessageService.showSuccessToast('Feedback session copied successfully to all courses.');
    }
  }

  getCopyErrorMessage(): string {
    return (Object.keys(this.failedToCopySessions).map((key: string) =>
        `Error copying to ${key}: ${this.failedToCopySessions[key]}`).join(' ')).concat(
        ` Tip: If you can't find such a session in that course, also check the 'Recycle bin'
         (shown at the bottom of the 'Sessions' page).`);
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructor(model: SessionsTableRowModel): void {
    this.navigationService.navigateByURLWithParamEncoding(
        '/web/instructor/sessions/submission',
        { courseid: model.feedbackSession.courseId, fsname: model.feedbackSession.feedbackSessionName });
  }

  /**
   * Downloads the result of a feedback session in csv.
   */
  downloadSessionResult(model: SessionsTableRowModel): void {
    this.feedbackQuestionsService.getFeedbackQuestions({
      courseId: model.feedbackSession.courseId,
      feedbackSessionName: model.feedbackSession.feedbackSessionName,
      intent: Intent.INSTRUCTOR_RESULT,
    }).pipe(
      switchMap((feedbackQuestions: FeedbackQuestions) => {
        const questions: FeedbackQuestion[] = feedbackQuestions.questions;
        this.isResultActionLoading = true;
        return of(this.feedbackSessionActionsService.downloadSessionResult(
          model.feedbackSession.courseId,
          model.feedbackSession.feedbackSessionName,
          Intent.FULL_DETAIL,
          true,
          true,
          questions,
        ));
      }),
      finalize(() => {
        this.isResultActionLoading = false;
      }),
    )
      .subscribe();
  }

  /**
   * Publishes a feedback session.
   */
  publishSession(model: SessionsTableRowModel, rowData: SortableTableCellData[], columnsData: ColumnData[]): void {
    this.isResultActionLoading = true;

    const colIdx = columnsData.findIndex(
      (colData) => colData.header === SessionsTableColumnNames.get(SessionsTableColumn.RESPONSES),
    );

    const actionsColIdx = columnsData.findIndex(
      (colData) => colData.header === SessionsTableColumnNames.get(SessionsTableColumn.ACTIONS),
    );

    this.feedbackSessionsService
      .publishFeedbackSession(model.feedbackSession.courseId, model.feedbackSession.feedbackSessionName)
      .pipe(finalize(() => {
          this.isResultActionLoading = false;
        }),
      )
      .subscribe({
        next: (feedbackSession: FeedbackSession) => {
          model.feedbackSession = feedbackSession;
          model.responseRate = '';

          rowData[colIdx].customComponent!.componentData! = () => {
            return {
              ...rowData[colIdx].customComponent!.componentData!,
              value: this.publishStatusName.transform(FeedbackSessionPublishStatus.PUBLISHED),
            };
          };

          rowData[actionsColIdx].customComponent!.componentData! = () => {
            return {
              ...rowData[actionsColIdx].customComponent!.componentData!,
              publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            };
          };

          this.statusMessageService.showSuccessToast(
            'The feedback session has been published. '
              + 'Please allow up to 1 hour for all the notification emails to be sent out.',
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          if (this.publishUnpublishRetryAttempts) {
            this.publishUnpublishRetryAttempts -= 1;
          } else {
            this.openErrorReportModal(resp);
          }
        },
      });
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSession(model: SessionsTableRowModel, rowData: SortableTableCellData[], columnsData: ColumnData[]): void {
    this.isResultActionLoading = true;

    const responseColIdx = columnsData.findIndex(
        (colData) => colData.header === SessionsTableColumnNames.get(SessionsTableColumn.RESPONSES),
    );

    const actionsColIdx = columnsData.findIndex(
      (colData) => colData.header === SessionsTableColumnNames.get(SessionsTableColumn.ACTIONS),
    );

    this.feedbackSessionsService
      .unpublishFeedbackSession(model.feedbackSession.courseId, model.feedbackSession.feedbackSessionName)
      .pipe(
        finalize(() => {
          this.isResultActionLoading = false;
        }),
      )
      .subscribe({
        next: (feedbackSession: FeedbackSession) => {
          model.feedbackSession = feedbackSession;
          model.responseRate = '';

          rowData[responseColIdx].customComponent!.componentData! = () => {
            return {
              ...rowData[responseColIdx].customComponent!.componentData!,
              value: this.publishStatusName.transform(FeedbackSessionPublishStatus.NOT_PUBLISHED),
            };
          };

          rowData[actionsColIdx].customComponent!.componentData! = () => {
            return {
              ...rowData[actionsColIdx].customComponent!.componentData!,
              publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
            };
          };

          this.statusMessageService.showSuccessToast('The feedback session has been unpublished.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          if (this.publishUnpublishRetryAttempts) {
            this.publishUnpublishRetryAttempts -= 1;
          } else {
            this.openErrorReportModal(resp);
          }
        },
      });
  }

  openErrorReportModal(resp: ErrorMessageOutput): void {
    const modal: NgbModalRef = this.ngbModal.open(ErrorReportComponent);
    modal.componentInstance.requestId = resp.error.requestId;
    modal.componentInstance.errorMessage = resp.error.message;
  }

  triggerModelChange(data: SessionEditFormModel): void {
    const { submissionStartDate, submissionEndDate, submissionStartTime, submissionEndTime } = data;

    const startDate = new Date(submissionStartDate.year, submissionStartDate.month, submissionStartDate.day);
    const endDate = new Date(submissionEndDate.year, submissionEndDate.month, submissionEndDate.day);

    if (startDate > endDate) {
      this.sessionEditFormModel = {
        ...data,
        submissionEndDate: submissionStartDate,
        submissionEndTime:
          submissionStartTime.hour > submissionEndTime.hour || (
            submissionStartTime.hour === submissionEndTime.hour
            && submissionStartTime.minute > submissionEndTime.minute
          )
            ? submissionStartTime
            : submissionEndTime,
      };
    } else if (startDate.toISOString() === endDate.toISOString() && submissionStartTime.hour > submissionEndTime.hour) {
      this.sessionEditFormModel = {
        ...data,
        submissionEndDate: submissionStartDate,
        submissionEndTime: {
          ...submissionStartTime,
          hour: submissionStartTime.hour,
        },
      };
    } else if (startDate.toISOString() === endDate.toISOString() && submissionStartTime.hour === submissionEndTime.hour
        && submissionStartTime.minute > submissionEndTime.minute) {
      this.sessionEditFormModel = {
        ...data,
        submissionEndDate: submissionStartDate,
        submissionEndTime: {
          ...submissionStartTime,
          minute: submissionStartTime.minute,
        },
      };
    }
  }
}

interface SessionTimestampData {
  submissionStartTimestamp: string;
  submissionEndTimestamp: string;
  sessionVisibleTimestamp: string;
  responseVisibleTimestamp: string;
}

export interface TweakedTimestampData {
  oldTimestamp: SessionTimestampData;
  newTimestamp: SessionTimestampData;
}
