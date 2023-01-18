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
  FeedbackSessionStats,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../types/api-output';
import { Intent } from '../../types/api-request';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../types/default-retry-attempts';
import { SortBy, SortOrder } from '../../types/sort-properties';
import { CopySessionModalResult } from '../components/copy-session-modal/copy-session-modal-model';
import { ErrorReportComponent } from '../components/error-report/error-report.component';
import { CopySessionResult, SessionsTableRowModel } from '../components/sessions-table/sessions-table-model';
import { SimpleModalType } from '../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionBasePageComponent {

  isResultActionLoading: boolean = false;

  protected failedToCopySessions: Record<string, string> = {}; // Map of failed session copy to error message
  protected coursesOfModifiedSession: Set<string> = new Set();
  protected modifiedTimestamps: Array<string> = [];

  private publishUnpublishRetryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

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
    const twoHoursBeforeNow = moment().tz(fromFeedbackSession.timeZone).subtract(2, 'hours')
        .valueOf();
    const twoDaysFromNowRoundedUp = moment().tz(fromFeedbackSession.timeZone).add(2, 'days').startOf('hour')
        .valueOf();
    const sevenDaysFromNowRoundedUp = moment().tz(fromFeedbackSession.timeZone).add(7, 'days').startOf('hour')
        .valueOf();
    const ninetyDaysFromNow = moment().tz(fromFeedbackSession.timeZone).add(90, 'days')
        .valueOf();
    const ninetyDaysFromNowRoundedUp = moment().tz(fromFeedbackSession.timeZone).add(90, 'days').startOf('hour')
        .valueOf();
    const oneHundredAndEightyDaysFromNow = moment().tz(fromFeedbackSession.timeZone).add(180, 'days')
        .valueOf();
    const oneHundredAndEightyDaysFromNowRoundedUp = moment().tz(fromFeedbackSession.timeZone).add(180, 'days')
        .startOf('hour')
        .valueOf();

    // Preprocess timestamps to adhere to feedback session timestamps constraints
    let isModified: boolean = false;

    let copiedSubmissionStartTimestamp = fromFeedbackSession.submissionStartTimestamp;
    if (copiedSubmissionStartTimestamp < twoHoursBeforeNow) {
      this.modifiedTimestamps.push(`The submission opening time has been modified from 
      ${this.formatTimestamp(copiedSubmissionStartTimestamp, fromFeedbackSession.timeZone)} to 
      ${this.formatTimestamp(twoDaysFromNowRoundedUp, fromFeedbackSession.timeZone)}.`);
      isModified = true;
      copiedSubmissionStartTimestamp = twoDaysFromNowRoundedUp;
    } else if (copiedSubmissionStartTimestamp > ninetyDaysFromNow) {
      this.modifiedTimestamps.push(`The submission opening time has been modified from 
      ${this.formatTimestamp(copiedSubmissionStartTimestamp, fromFeedbackSession.timeZone)} to 
      ${this.formatTimestamp(ninetyDaysFromNowRoundedUp, fromFeedbackSession.timeZone)}.`);
      isModified = true;
      copiedSubmissionStartTimestamp = ninetyDaysFromNowRoundedUp;
    }

    let copiedSubmissionEndTimestamp = fromFeedbackSession.submissionEndTimestamp;
    if (copiedSubmissionEndTimestamp < copiedSubmissionStartTimestamp) {
      this.modifiedTimestamps.push(`The submission closing time has been modified from 
      ${this.formatTimestamp(copiedSubmissionEndTimestamp, fromFeedbackSession.timeZone)} to 
      ${this.formatTimestamp(sevenDaysFromNowRoundedUp, fromFeedbackSession.timeZone)}.`);
      isModified = true;
      copiedSubmissionEndTimestamp = sevenDaysFromNowRoundedUp;
    } else if (copiedSubmissionEndTimestamp > oneHundredAndEightyDaysFromNow) {
      this.modifiedTimestamps.push(`The submission closing time has been modified from 
      ${this.formatTimestamp(copiedSubmissionEndTimestamp, fromFeedbackSession.timeZone)} to 
      ${this.formatTimestamp(oneHundredAndEightyDaysFromNowRoundedUp, fromFeedbackSession.timeZone)}.`);
      isModified = true;
      copiedSubmissionEndTimestamp = oneHundredAndEightyDaysFromNowRoundedUp;
    }

    let copiedSessionVisibleSetting = fromFeedbackSession.sessionVisibleSetting;
    let copiedCustomSessionVisibleTimestamp = fromFeedbackSession.customSessionVisibleTimestamp!;
    const thirtyDaysFromSubmissionStart = moment(copiedSubmissionStartTimestamp)
        .tz(fromFeedbackSession.timeZone).subtract(30, 'days')
        .valueOf();
    const thirtyDaysFromSubmissionStartRoundedUp = moment(copiedSubmissionStartTimestamp)
        .tz(fromFeedbackSession.timeZone).subtract(30, 'days').startOf('hour')
        .valueOf();
    if (copiedSessionVisibleSetting === SessionVisibleSetting.CUSTOM) {
      if (copiedCustomSessionVisibleTimestamp < thirtyDaysFromSubmissionStart) {
        this.modifiedTimestamps.push(`The session visible time has been modified from 
        ${this.formatTimestamp(copiedCustomSessionVisibleTimestamp, fromFeedbackSession.timeZone)} to 
        ${this.formatTimestamp(thirtyDaysFromSubmissionStartRoundedUp, fromFeedbackSession.timeZone)}.`);
        isModified = true;
        copiedCustomSessionVisibleTimestamp = thirtyDaysFromSubmissionStartRoundedUp;
      } else if (copiedCustomSessionVisibleTimestamp > copiedSubmissionStartTimestamp) {
        this.modifiedTimestamps.push(`The session visible time has been modified from 
        ${this.formatTimestamp(copiedCustomSessionVisibleTimestamp, fromFeedbackSession.timeZone)} to 
        session visible time.`);
        isModified = true;
        copiedSessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
      }
    }

    let copiedResponseVisibleSetting = fromFeedbackSession.responseVisibleSetting;
    const copiedCustomResponseVisibleTimestamp = fromFeedbackSession.customResponseVisibleTimestamp!;
    if (copiedResponseVisibleSetting === ResponseVisibleSetting.CUSTOM
        && ((copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
                && copiedCustomResponseVisibleTimestamp < copiedSubmissionStartTimestamp)
            || copiedCustomResponseVisibleTimestamp < copiedCustomSessionVisibleTimestamp)) {
      this.modifiedTimestamps.push(`The session visible time has been modified from 
      ${this.formatTimestamp(copiedCustomResponseVisibleTimestamp, fromFeedbackSession.timeZone)} to later.`);
      isModified = true;
      copiedResponseVisibleSetting = ResponseVisibleSetting.LATER;
    }

    if (isModified) {
      this.coursesOfModifiedSession.add(newCourseId);
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

      isClosingEmailEnabled: fromFeedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: fromFeedbackSession.isPublishedEmailEnabled,
    });
  }

  private formatTimestamp(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM h:mm A');
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
  loadResponseRate(model: SessionsTableRowModel): void {
    model.isLoadingResponseRate = true;
    this.feedbackSessionsService.loadSessionStatistics(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .pipe(finalize(() => {
          model.isLoadingResponseRate = false;
        }))
        .subscribe({
          next: (resp: FeedbackSessionStats) => {
            model.responseRate = `${resp.submittedTotal} / ${resp.expectedTotal}`;
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
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
  copySingleSession(copySessionRequest: Observable<FeedbackSession>): void {
    copySessionRequest.subscribe({
      next: (createdSession: FeedbackSession) => {
        if (Object.keys(this.failedToCopySessions).length > 0) {
          this.statusMessageService.showErrorToast(this.getCopyErrorMessage());
        } else if (this.coursesOfModifiedSession.size > 0) {
          this.simpleModalService.openInformationModal('Note On Tweaked Session Timestamps',
              SimpleModalType.WARNING, this.getTimestampsTweakedWarningMessage(),
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

  showCopyStatusMessage(): void {
    if (Object.keys(this.failedToCopySessions).length > 0) {
      this.statusMessageService.showErrorToast(this.getCopyErrorMessage());
    } else if (this.coursesOfModifiedSession.size > 0) {
      this.simpleModalService.openInformationModal('Note On Tweaked Session Timestamps',
          SimpleModalType.WARNING, this.getTimestampsTweakedWarningMessage());
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

  getTimestampsTweakedWarningMessage(): string {
    return `<p>The feedback session has been copied to all course(s). However, changes are made to some session 
            timestamps due to timestamp constraints in these courses: 
            ${Array.from(this.coursesOfModifiedSession).join(', ')}.</p>
            <p><ul>${this.modifiedTimestamps.reduce((prevChange, currChange) => `${prevChange}
            <li>${currChange}</li>`, '')}</ul></p>
            <p>Please modify the timestamps as necessary.</p>`;
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
  publishSession(model: SessionsTableRowModel): void {
    this.isResultActionLoading = true;
    this.feedbackSessionsService.publishFeedbackSession(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .pipe(finalize(() => {
          this.isResultActionLoading = false;
        }))
        .subscribe({
          next: (feedbackSession: FeedbackSession) => {
            model.feedbackSession = feedbackSession;
            model.responseRate = '';

            this.statusMessageService.showSuccessToast('The feedback session has been published. '
                + 'Please allow up to 1 hour for all the notification emails to be sent out.');
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
  unpublishSession(model: SessionsTableRowModel): void {
    this.isResultActionLoading = true;
    this.feedbackSessionsService.unpublishFeedbackSession(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .pipe(finalize(() => {
          this.isResultActionLoading = false;
        }))
        .subscribe({
          next: (feedbackSession: FeedbackSession) => {
            model.feedbackSession = feedbackSession;
            model.responseRate = '';

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
}
