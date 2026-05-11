import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest, Observable } from 'rxjs';
import { finalize, map, mergeMap, tap } from 'rxjs/operators';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorCommentEventData, InstructorCommentService } from '../../../services/instructor-comment.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  FeedbackSession,
  FeedbackSessions,
  Instructor,
  QuestionOutput,
  ResponseOutput,
  SessionResults,
  Student,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { CommentTableModel } from '../../components/comment-box/comment-table/comment-table.model';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import { GrqRgqViewResponsesComponent } from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.component';
import { areEmailsEqual } from '../../components/teammates-common/email-utils';
import { ErrorMessageOutput } from '../../error-message-output';

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
  courseId = '';
  studentName = '';
  studentEmail = '';
  studentTeam = '';

  sessionTabs: SessionTab[] = [];
  isStudentResultsLoading = false;
  hasStudentResultsLoadingFailed = false;

  currInstructorName?: string;
  instructorCommentTableModel: Record<string, CommentTableModel> = {};

  constructor(
    private route: ActivatedRoute,
    private feedbackSessionsService: FeedbackSessionsService,
    private studentService: StudentService,
    private instructorService: InstructorService,
    private commentsToCommentTableModel: CommentsToCommentTableModelPipe,
    private tableComparatorService: TableComparatorService,
    private statusMessageService: StatusMessageService,
    private commentService: InstructorCommentService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe({
      next: (queryParams: any) => {
        this.courseId = queryParams.courseid;
        this.studentEmail = queryParams.studentemail;

        this.loadInstructorRecords(this.courseId);
        this.loadStudentResults(this.courseId, this.studentEmail);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Loads the instructor's records based on the given course ID.
   */
  loadInstructorRecords(courseId: string): void {
    this.instructorService
      .getInstructor({
        courseId,
        intent: Intent.FULL_DETAIL,
      })
      .subscribe((instructor: Instructor) => {
        this.currInstructorName = instructor.name;
      });
  }

  /**
   * Loads the student's feedback session results based on the given course ID and student email.
   * Fetches student records and feedback sessions in parallel, then loads results for each session.
   */
  loadStudentResults(courseId: string, studentEmail: string): void {
    this.sessionTabs = [];
    this.hasStudentResultsLoadingFailed = false;
    this.isStudentResultsLoading = true;

    combineLatest({
      feedbackSession: this.getFeedbackSessions(this.courseId),
      student: this.loadStudentRecords(courseId, studentEmail),
    })
      .pipe(
        mergeMap(({ feedbackSession, student }: { feedbackSession: FeedbackSession; student: Student }) => {
          return this.getFeedbackSessionResults(feedbackSession, student.sectionName);
        }),
        finalize(() => {
          this.isStudentResultsLoading = false;
        }),
      )
      .subscribe({
        next: ({ feedbackSession, results }: { results: SessionResults; feedbackSession: FeedbackSession }) => {
          this.sessionTabs.push(this.createSessionTab(feedbackSession, results));
          results.questions.forEach((questions: QuestionOutput) => {
            return this.preprocessComments(questions.allResponses, feedbackSession.timeZone);
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
   * Loads the student's records based on the given course ID and email.
   */
  private loadStudentRecords(courseId: string, studentEmail: string): Observable<Student> {
    return this.studentService.getStudent(courseId, studentEmail).pipe(
      tap((resp: Student) => {
        this.studentName = resp.name;
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
      .pipe(mergeMap((feedbackSessions: FeedbackSessions) => feedbackSessions.feedbackSessions));
  }

  /**
   * Fetches the full detail result of the given feedback session in the current course
   * grouped by the student's section.
   */
  private getFeedbackSessionResults(
    feedbackSession: FeedbackSession,
    groupBySection: string,
  ): Observable<{ results: SessionResults; feedbackSession: FeedbackSession }> {
    return this.feedbackSessionsService
      .getFeedbackSessionResults({
        feedbackSessionId: feedbackSession.feedbackSessionId,
        groupBySection,
        intent: Intent.FULL_DETAIL,
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
  preprocessComments(responses: ResponseOutput[], timezone: string): void {
    responses.forEach((response: ResponseOutput) => {
      this.instructorCommentTableModel[response.responseId] = this.commentsToCommentTableModel.transform(
        response.instructorComments,
        false,
        timezone,
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
      currInstructorName: this.currInstructorName,
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
      currInstructorName: this.currInstructorName,
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
