import { Router } from '@angular/router';
import { from, Observable, of } from 'rxjs';
import { concatMap, last, switchMap } from 'rxjs/operators';
import { FeedbackQuestionsService } from '../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../services/feedback-sessions.service';
import { HttpRequestService } from '../../services/http-request.service';
import { NavigationService } from '../../services/navigation.service';
import { StatusMessageService } from '../../services/status-message.service';
import {
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackSession,
  FeedbackSessionStats,
  InstructorPrivilege,
} from '../../types/api-output';
import {
  CopySessionResult,
  SessionsTableRowModel,
  SortBy,
  SortOrder,
} from '../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../error-message-output';
import { Intent } from '../Intent';

/**
 * The base page for session related page.
 */
export abstract class InstructorSessionBasePageComponent {

  protected constructor(protected router: Router, protected httpRequestService: HttpRequestService,
                        protected statusMessageService: StatusMessageService,
                        protected navigationService: NavigationService,
                        protected feedbackSessionsService: FeedbackSessionsService,
                        protected feedbackQuestionsService: FeedbackQuestionsService) { }

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
          const param: { [key: string]: string } = {
            courseid: fromFeedbackSession.courseId,
            fsname: fromFeedbackSession.feedbackSessionName,
            intent: Intent.FULL_DETAIL,
          };
          return this.httpRequestService.get('/questions', param);
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
        case SortBy.FEEDBACK_SESSION_NAME:
          strA = a.feedbackSession.feedbackSessionName;
          strB = b.feedbackSession.feedbackSessionName;
          break;
        case SortBy.COURSE_ID:
          strA = a.feedbackSession.courseId;
          strB = b.feedbackSession.courseId;
          break;
        case SortBy.START_DATE:
          strA = String(a.feedbackSession.submissionStartTimestamp);
          strB = String(b.feedbackSession.submissionStartTimestamp);
          break;
        case SortBy.END_DATE:
          strA = String(a.feedbackSession.submissionEndTimestamp);
          strB = String(b.feedbackSession.submissionEndTimestamp);
          break;
        case SortBy.SESSION_CREATION_DATE:
          strA = String(a.feedbackSession.createdAtTimestamp);
          strB = String(b.feedbackSession.createdAtTimestamp);
          break;
        case SortBy.DELETION_DATE:
          strA = String(a.feedbackSession.deletedAtTimestamp);
          strB = String(b.feedbackSession.deletedAtTimestamp);
          break;
        default:
          strA = '';
          strB = '';
      }
      if (order === SortOrder.ASC) {
        return strA.localeCompare(strB);
      }
      if (order === SortOrder.DESC) {
        return strB.localeCompare(strA);
      }
      return 0;
    });
  }

  /**
   * Updates the instructor privilege in {@code SessionsTableRowModel}.
   */
  protected updateInstructorPrivilege(model: SessionsTableRowModel): void {
    this.httpRequestService.get('/instructor/privilege', {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    }).subscribe((instructorPrivilege: InstructorPrivilege) => {
      model.instructorPrivilege = instructorPrivilege;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads response rate of a feedback session.
   */
  loadResponseRate(model: SessionsTableRowModel): void {
    model.isLoadingResponseRate = true;
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };
    this.httpRequestService.get('/session/stats', paramMap).subscribe((resp: FeedbackSessionStats) => {
      model.isLoadingResponseRate = false;
      model.responseRate = `${resp.submittedTotal} / ${resp.expectedTotal}`;
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Edits the feedback session.
   */
  editSession(model: SessionsTableRowModel): void {
    this.router.navigateByUrl('/web/instructor/sessions/edit'
        + `?courseid=${model.feedbackSession.courseId}&fsname=${model.feedbackSession.feedbackSessionName}`);
  }

  /**
   * Copies the feedback session.
   */
  copySession(model: SessionsTableRowModel, result: CopySessionResult): void {
    this.copyFeedbackSession(model.feedbackSession, result.newFeedbackSessionName, result.copyToCourseId)
        .subscribe((createdSession: FeedbackSession) => {
          this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/sessions/edit'
              + `?courseid=${createdSession.courseId}&fsname=${createdSession.feedbackSessionName}`,
              'The feedback session has been copied. Please modify settings/questions as necessary.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructor(model: SessionsTableRowModel): void {
    this.router.navigateByUrl('/web/instructor/sessions/submission'
        + `?courseid=${model.feedbackSession.courseId}&fsname=${model.feedbackSession.feedbackSessionName}`);
  }

  /**
   * Views the result of a feedback session.
   */
  viewSessionResult(model: SessionsTableRowModel): void {
    this.router.navigateByUrl('/web/instructor/sessions/result'
        + `?courseid=${model.feedbackSession.courseId}&fsname=${model.feedbackSession.feedbackSessionName}`);
  }

  /**
   * Publishes a feedback session.
   */
  publishSession(model: SessionsTableRowModel): void {
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    this.httpRequestService.post('/session/publish', paramMap)
        .subscribe((feedbackSession: FeedbackSession) => {
          model.feedbackSession = feedbackSession;
          model.responseRate = '';

          this.statusMessageService.showSuccessMessage('The feedback session has been published. '
              + 'Please allow up to 1 hour for all the notification emails to be sent out.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSession(model: SessionsTableRowModel): void {
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    this.httpRequestService.delete('/session/publish', paramMap)
        .subscribe((feedbackSession: FeedbackSession) => {
          model.feedbackSession = feedbackSession;
          model.responseRate = '';

          this.statusMessageService.showSuccessMessage('The feedback session has been unpublished.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }
}
