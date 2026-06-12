import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { combineLatest, Observable } from 'rxjs';
import { finalize, map, mergeMap, tap } from 'rxjs/operators';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorCommentEventData, InstructorCommentService } from '../../../services/instructor-comment.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  FeedbackSession,
  FeedbackSessions,
  FeedbackVisibilityType,
  QuestionOutput,
  ResponseOutput,
  SessionResults,
  Student,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { CommentTableModel } from '../../components/comment-box/comment-table/comment-table.model';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import { GrqRgqViewResponsesComponent } from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.component';
import { areEmailsEqual } from '../../components/teammates-common/email-utils';
import { ErrorMessageOutput } from '../../error-message-output';
import { commentToReadOnlyComment } from '../../utils/comment-to-comment-table.util';

interface SessionTab {
  isCollapsed: boolean;
  feedbackSession: FeedbackSession;
  responsesGivenByStudent: QuestionOutput[];
  responsesReceivedByStudent: QuestionOutput[];
}

/**
 * Instructor student records page.
 */
@Component({
  selector: 'tm-instructor-student-records-page',
  templateUrl: './instructor-student-records-page.component.html',
  imports: [
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    PanelChevronComponent,
    GrqRgqViewResponsesComponent,
    NgbCollapse,
  ],
  providers: [CommentsToCommentTableModelPipe],
})
export class InstructorStudentRecordsPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private feedbackSessionsService = inject(FeedbackSessionsService);
  private studentService = inject(StudentService);
  private tableComparatorService = inject(TableComparatorService);
  private statusMessageService = inject(StatusMessageService);
  private commentService = inject(InstructorCommentService);

  courseId = '';
  studentName = '';
  studentEmail = '';
  studentId = '';
  studentTeam = '';

  sessionTabs: SessionTab[] = [];
  isStudentResultsLoading = false;
  hasStudentResultsLoadingFailed = false;

  instructorCommentTableModel: Record<string, CommentTableModel> = {};

  ngOnInit(): void {
    this.route.queryParams.subscribe({
      next: (queryParams: Params) => {
        this.courseId = queryParams['courseid'];
        this.studentId = queryParams['userid'];

        this.loadStudentResults(this.courseId, this.studentId);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Loads the student's feedback session results based on the given course ID and student user ID.
   * Fetches student records and feedback sessions in parallel, then loads results for each session.
   */
  loadStudentResults(courseId: string, studentId: string): void {
    this.sessionTabs = [];
    this.hasStudentResultsLoadingFailed = false;
    this.isStudentResultsLoading = true;

    combineLatest({
      feedbackSession: this.getFeedbackSessions(courseId),
      student: this.loadStudentRecords(studentId),
    })
      .pipe(
        mergeMap(({ feedbackSession, student }: { feedbackSession: FeedbackSession; student: Student }) => {
          return this.getFeedbackSessionResults(feedbackSession, student.sectionId);
        }),
        finalize(() => {
          this.isStudentResultsLoading = false;
        }),
      )
      .subscribe({
        next: ({ feedbackSession, results }: { results: SessionResults; feedbackSession: FeedbackSession }) => {
          this.sessionTabs.push(this.createSessionTab(feedbackSession, results));
          results.questions.forEach((questions: QuestionOutput) => {
            return this.preprocessComments(
              questions.allResponses,
              feedbackSession.timeZone,
              questions.feedbackQuestion.showResponsesTo,
            );
          });
        },
        error: (errorMessageOutput: ErrorMessageOutput) => {
          this.hasStudentResultsLoadingFailed = true;
          this.statusMessageService.showErrorToast(errorMessageOutput.error.message);
        },
        complete: () => this.sortFeedbackSessions(),
      });
  }

  /**
   * Loads the student's records based on the given course ID and user ID.
   */
  private loadStudentRecords(studentId: string): Observable<Student> {
    return this.studentService.getStudent({ userId: studentId }).pipe(
      tap((resp: Student) => {
        this.studentName = resp.name;
        this.studentEmail = resp.email;
        this.studentTeam = resp.teamName;
      }),
    );
  }

  /**
   * Fetches the feedback sessions for the given course ID.
   */
  private getFeedbackSessions(courseId: string): Observable<FeedbackSession> {
    return this.feedbackSessionsService
      .getFeedbackSessionsForInstructor(courseId)
      .pipe(
        mergeMap((feedbackSessions: FeedbackSessions) =>
          feedbackSessions.feedbackSessions.map((fs) => fs.feedbackSession),
        ),
      );
  }

  /**
   * Fetches the full detail result of the given feedback session in the current course
   * grouped by the student's section ID.
   */
  private getFeedbackSessionResults(
    feedbackSession: FeedbackSession,
    groupBySectionId: string,
  ): Observable<{ results: SessionResults; feedbackSession: FeedbackSession }> {
    return this.feedbackSessionsService
      .getCourseSessionResults({
        feedbackSessionId: feedbackSession.feedbackSessionId,
        groupBySection: groupBySectionId,
      })
      .pipe(
        map((results: SessionResults) => {
          this.sortQuestionsByNumber(results.questions);
          return { results, feedbackSession };
        }),
      );
  }

  private sortQuestionsByNumber(questions: QuestionOutput[]): void {
    questions.sort(
      (a: QuestionOutput, b: QuestionOutput) => a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber,
    );
  }

  private createSessionTab(feedbackSession: FeedbackSession, results: SessionResults): SessionTab {
    const giverQuestions: QuestionOutput[] = structuredClone(results.questions);
    giverQuestions.forEach((questions: QuestionOutput) => {
      questions.allResponses = questions.allResponses.filter(
        (response: ResponseOutput) =>
          !response.isMissingResponse && areEmailsEqual(response.giverEmail, this.studentEmail),
      );
    });
    const responsesGivenByStudent: QuestionOutput[] = giverQuestions.filter(
      (questions: QuestionOutput) => questions.allResponses.length > 0,
    );

    const recipientQuestions: QuestionOutput[] = structuredClone(results.questions);
    recipientQuestions.forEach((questions: QuestionOutput) => {
      questions.allResponses = questions.allResponses.filter(
        (response: ResponseOutput) =>
          !response.isMissingResponse && areEmailsEqual(response.recipientEmail, this.studentEmail),
      );
    });
    const responsesReceivedByStudent: QuestionOutput[] = recipientQuestions.filter(
      (questions: QuestionOutput) => questions.allResponses.length > 0,
    );

    return {
      feedbackSession,
      responsesGivenByStudent,
      responsesReceivedByStudent,
      isCollapsed: false,
    };
  }

  /**
   * Preprocesses the comments from instructor.
   *
   * <p>The instructor comment will be moved to map {@code instructorCommentTableModel}. The original
   * instructor comments associated with the response will be deleted.
   */
  preprocessComments(
    responses: ResponseOutput[],
    timezone: string,
    questionShowResponsesTo: FeedbackVisibilityType[],
  ): void {
    responses.forEach((response: ResponseOutput) => {
      this.instructorCommentTableModel[response.responseId] = commentToReadOnlyComment(
        response.instructorComments,
        false,
        timezone,
        questionShowResponsesTo,
      );
      this.commentService.sortComments(this.instructorCommentTableModel[response.responseId]);
      // clear the original comments for safe as instructorCommentTableModel will become the single point of truth
      response.instructorComments = [];
    });
  }

  /**
   * Handles saving a new instructor comment.
   */
  saveNewCommentEventHandler(responseId: string, timezone: string): void {
    this.commentService.saveNewComment({
      responseId,
      timezone,
      instructorCommentTableModel: this.instructorCommentTableModel,
    });
  }

  /**
   * Handles deleting an instructor comment.
   */
  deleteCommentEventHandler(data: InstructorCommentEventData): void {
    this.commentService.deleteComment({
      data,
      instructorCommentTableModel: this.instructorCommentTableModel,
    });
  }

  /**
   * Handles updating an instructor comment.
   */
  updateCommentEventHandler(data: InstructorCommentEventData, timezone: string): void {
    this.commentService.updateComment({
      data,
      timezone,
      instructorCommentTableModel: this.instructorCommentTableModel,
    });
  }

  /**
   * Sorts the student's feedback sessions according to name
   */
  private sortFeedbackSessions(): void {
    this.sessionTabs.sort((a: SessionTab, b: SessionTab) => {
      return this.tableComparatorService.compare(
        SortBy.SESSION_NAME,
        SortOrder.ASC,
        a.feedbackSession.feedbackSessionName,
        b.feedbackSession.feedbackSessionName,
      );
    });
  }
}
