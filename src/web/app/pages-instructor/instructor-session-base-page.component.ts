import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { forkJoin, from, Observable, of } from 'rxjs';
import { catchError, concatMap, finalize, last, switchMap, tap } from 'rxjs/operators';
import { FeedbackQuestionsService } from '../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../services/feedback-sessions.service';
import { InstructorService } from '../../services/instructor.service';
import { NavigationService } from '../../services/navigation.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TableComparatorService } from '../../services/table-comparator.service';
import {
    FeedbackQuestion,
    FeedbackQuestions,
    FeedbackSession,
    FeedbackSessionStats,
    InstructorPrivilege,
} from '../../types/api-output';
import { Intent } from '../../types/api-request';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../types/default-retry-attempts';
import { SortBy, SortOrder } from '../../types/sort-properties';
import { ErrorReportComponent } from '../components/error-report/error-report.component';
import {
    CopySessionResult,
    SessionsTableRowModel,
} from '../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../error-message-output';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionBasePageComponent {

  private publishUnpublishRetryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

  protected constructor(protected router: Router,
                        protected instructorService: InstructorService,
                        protected statusMessageService: StatusMessageService,
                        protected navigationService: NavigationService,
                        protected feedbackSessionsService: FeedbackSessionsService,
                        protected feedbackQuestionsService: FeedbackQuestionsService,
                        protected tableComparatorService: TableComparatorService,
                        protected ngbModal: NgbModal) { }

  /**
   * Copies a feedback session.
   */
  protected copyFeedbackSession(fromFeedbackSession: FeedbackSession, newSessionName: string, newCourseId: string):
      Observable<FeedbackSession> {
    let createdFeedbackSession!: FeedbackSession;
    return this.feedbackSessionsService.createFeedbackSession(newCourseId, {
      feedbackSessionName: newSessionName,
      instructions: fromFeedbackSession.instructions,

      submissionStartTimestamp: fromFeedbackSession.submissionStartTimestamp,
      submissionEndTimestamp: fromFeedbackSession.submissionEndTimestamp,
      gracePeriod: fromFeedbackSession.gracePeriod,

      sessionVisibleSetting: fromFeedbackSession.sessionVisibleSetting,
      customSessionVisibleTimestamp: fromFeedbackSession.customSessionVisibleTimestamp,

      responseVisibleSetting: fromFeedbackSession.responseVisibleSetting,
      customResponseVisibleTimestamp: fromFeedbackSession.customResponseVisibleTimestamp,

      isClosingEmailEnabled: fromFeedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: fromFeedbackSession.isPublishedEmailEnabled,
    }).pipe(
        switchMap((feedbackSession: FeedbackSession) => {
          createdFeedbackSession = feedbackSession;

          // copy questions
          return this.feedbackQuestionsService.getFeedbackQuestions({
            courseId: fromFeedbackSession.courseId,
            feedbackSessionName: fromFeedbackSession.feedbackSessionName,
            intent: Intent.FULL_DETAIL,
          },
          );
        }),
        switchMap((response: FeedbackQuestions) => {
          if (response.questions.length === 0) {
            // no questions to copy
            return of(createdFeedbackSession);
          }
          return from(response.questions).pipe(
              concatMap((feedbackQuestion: FeedbackQuestion) => {
                return this.feedbackQuestionsService.createFeedbackQuestion(
                    createdFeedbackSession.courseId, createdFeedbackSession.feedbackSessionName, {
                      questionNumber: feedbackQuestion.questionNumber,
                      questionBrief: feedbackQuestion.questionBrief,
                      questionDescription: feedbackQuestion.questionDescription,

                      questionDetails: feedbackQuestion.questionDetails,
                      questionType: feedbackQuestion.questionType,

                      giverType: feedbackQuestion.giverType,
                      recipientType: feedbackQuestion.recipientType,

                      numberOfEntitiesToGiveFeedbackToSetting: feedbackQuestion.numberOfEntitiesToGiveFeedbackToSetting,
                      customNumberOfEntitiesToGiveFeedbackTo: feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo,

                      showResponsesTo: feedbackQuestion.showResponsesTo,
                      showGiverNameTo: feedbackQuestion.showGiverNameTo,
                      showRecipientNameTo: feedbackQuestion.showRecipientNameTo,
                    });
              }),
              last(),
              switchMap(() => of(createdFeedbackSession)),
          );
        }),
    );
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
   * Updates the instructor privilege in {@code SessionsTableRowModel}.
   */
  protected updateInstructorPrivilege(model: SessionsTableRowModel): void {
    this.instructorService.loadInstructorPrivilege({
      courseId: model.feedbackSession.courseId,
      feedbackSessionName: model.feedbackSession.feedbackSessionName,
    },
    ).subscribe((instructorPrivilege: InstructorPrivilege) => {
      model.instructorPrivilege = instructorPrivilege;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
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
        .pipe(finalize(() => model.isLoadingResponseRate = false))
        .subscribe((resp: FeedbackSessionStats) => {
          model.responseRate = `${resp.submittedTotal} / ${resp.expectedTotal}`;
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
  }

  /**
   * Edits the feedback session.
   */
  editSession(model: SessionsTableRowModel): void {
    this.navigationService.navigateByURLWithParamEncoding(
        this.router,
        '/web/instructor/sessions/edit',
        { courseid: model.feedbackSession.courseId, fsname: model.feedbackSession.feedbackSessionName });
  }

  /**
   * Combines a {@link SessionsTableRowModel} and {@link CopySessionResult} to submit the copy session requests.
   */
  copySessionTransformer(model: SessionsTableRowModel, result: CopySessionResult): FeedbackSession[] {
    const copySessionRequests: Observable<FeedbackSession>[] = [];
    result.copyToCourseList.forEach((copyToCourseId: string) => {
      copySessionRequests.push(
          this.copyFeedbackSession(model.feedbackSession, result.newFeedbackSessionName, copyToCourseId));
    });
    return this.copySession(copySessionRequests);
  }

  /**
   * Submits the copy session requests.
   */
  copySession(copySessionRequests: Observable<FeedbackSession>[]): FeedbackSession[] {
    const successMessage: string =
        'The feedback session has been copied. Please modify settings/questions as necessary.';
    const sessionList: FeedbackSession[] = [];
    if (copySessionRequests.length === 1) {
      copySessionRequests[0].subscribe((createdSession: FeedbackSession) => {
        sessionList.push(createdSession);
        this.navigationService.navigateWithSuccessMessage(this.router,
            '/web/instructor/sessions/edit', successMessage,
            { courseid: createdSession.courseId, fsname: createdSession.feedbackSessionName });
      }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorToast(resp.error.message); });
    } else if (copySessionRequests.length > 1) {
      let isAnyFailed: boolean = false;
      forkJoin(copySessionRequests).pipe(
          tap((sessions: FeedbackSession[]) => {
            sessionList.concat(sessions);
          }),
          catchError((error: any) => {
            isAnyFailed = true;
            return of(error);
          }),
      ).subscribe(() => {
        if (isAnyFailed) {
          this.statusMessageService.showErrorToast('The session could not be copied into some courses.');
        } else {
          this.statusMessageService.showSuccessToast(successMessage);
        }
      });
    }
    return sessionList;
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructor(model: SessionsTableRowModel): void {
    this.navigationService.navigateByURLWithParamEncoding(
        this.router,
        '/web/instructor/sessions/submission',
        { courseid: model.feedbackSession.courseId, fsname: model.feedbackSession.feedbackSessionName });
  }

  /**
   * Views the result of a feedback session.
   */
  viewSessionResult(model: SessionsTableRowModel): void {
    this.navigationService.navigateByURLWithParamEncoding(
        this.router,
        '/web/instructor/sessions/result',
        { courseid: model.feedbackSession.courseId, fsname: model.feedbackSession.feedbackSessionName });
  }

  /**
   * Downloads the result of a feedback session in csv.
   */
  downloadSessionResult(model: SessionsTableRowModel): void {
    const filename: string =
        `${model.feedbackSession.courseId}_${model.feedbackSession.feedbackSessionName}_result.csv`;
    let blob: any;

    this.feedbackSessionsService.downloadSessionResults(
      model.feedbackSession.courseId,
      model.feedbackSession.feedbackSessionName,
      Intent.INSTRUCTOR_RESULT,
      true,
      true,
    ).subscribe((resp: string) => {
      blob = new Blob([resp], { type: 'text/csv' });
      saveAs(blob, filename);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Publishes a feedback session.
   */
  publishSession(model: SessionsTableRowModel): void {

    this.feedbackSessionsService.publishFeedbackSession(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .subscribe((feedbackSession: FeedbackSession) => {
          model.feedbackSession = feedbackSession;
          model.responseRate = '';

          this.statusMessageService.showSuccessToast('The feedback session has been published. '
              + 'Please allow up to 1 hour for all the notification emails to be sent out.');
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          if (!this.publishUnpublishRetryAttempts) {
            this.openErrorReportModal(resp);
          } else {
            this.publishUnpublishRetryAttempts -= 1;
          }
        });
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSession(model: SessionsTableRowModel): void {
    this.feedbackSessionsService.unpublishFeedbackSession(
        model.feedbackSession.courseId,
        model.feedbackSession.feedbackSessionName,
    )
        .subscribe((feedbackSession: FeedbackSession) => {
          model.feedbackSession = feedbackSession;
          model.responseRate = '';

          this.statusMessageService.showSuccessToast('The feedback session has been unpublished.');
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          if (!this.publishUnpublishRetryAttempts) {
            this.openErrorReportModal(resp);
          } else {
            this.publishUnpublishRetryAttempts -= 1;
          }
        });
  }

  openErrorReportModal(resp: ErrorMessageOutput): void {
    const modal: NgbModalRef = this.ngbModal.open(ErrorReportComponent);
    modal.componentInstance.requestId = resp.error.requestId;
    modal.componentInstance.errorMessage = resp.error.message;
  }
}
