import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize, map, mergeMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { FeedbackResponseCommentService } from '../../../services/feedback-response-comment.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  FeedbackSession,
  FeedbackSessions,
  Gender,
  Instructor,
  QuestionOutput,
  ResponseOutput,
  SessionResults,
  Student,
  StudentProfile,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { CommentTableModel } from '../../components/comment-box/comment-table/comment-table.component';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorCommentsComponent } from '../instructor-comments.component';

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
  styleUrls: ['./instructor-student-records-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorStudentRecordsPageComponent extends InstructorCommentsComponent implements OnInit {

  courseId: string = '';
  studentName: string = '';
  studentEmail: string = '';
  studentTeam: string = '';
  studentSection: string = '';
  instructorCommentTableModel: Record<string, CommentTableModel> = {};

  studentProfile: StudentProfile = {
    name: '',
    shortName: '',
    email: '',
    institute: '',
    nationality: '',
    gender: Gender.OTHER,
    moreInfo: '',
  };
  sessionTabs: SessionTab[] = [];
  photoUrl: string = '';

  isStudentLoading: boolean = false;
  hasStudentLoadingFailed: boolean = false;
  isStudentProfileLoading: boolean = false;
  hasStudentProfileLoadingFailed: boolean = false;
  isStudentResultsLoading: boolean = false;
  hasStudentResultsLoadingFailed: boolean = false;

  constructor(private route: ActivatedRoute,
              private studentProfileService: StudentProfileService,
              private feedbackSessionsService: FeedbackSessionsService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private commentsToCommentTableModel: CommentsToCommentTableModelPipe,
              tableComparatorService: TableComparatorService,
              statusMessageService: StatusMessageService,
              commentService: FeedbackResponseCommentService,
              commentToCommentRowModel: CommentToCommentRowModelPipe) {
    super(commentToCommentRowModel, commentService, statusMessageService, tableComparatorService);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.studentEmail = queryParams.studentemail;

      this.loadStudentRecords();
      this.loadStudentResults();
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?`
            + `courseid=${this.courseId}&studentemail=${this.studentEmail}`;
      this.instructorService.getInstructor({
        courseId: queryParams.courseid,
        intent: Intent.FULL_DETAIL,
      }).subscribe((instructor: Instructor) => {
        this.currInstructorName = instructor.name;
      });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Loads the student's records based on the given course ID and email.
   */
  loadStudentRecords(): void {
    this.hasStudentLoadingFailed = false;
    this.hasStudentProfileLoadingFailed = false;
    this.isStudentLoading = true;
    this.isStudentProfileLoading = true;
    this.studentService.getStudent(
        this.courseId, this.studentEmail,
    ).pipe(finalize(() => this.isStudentLoading = false)).subscribe((resp: Student) => {
      this.studentName = resp.name;
      this.studentTeam = resp.teamName;
      this.studentSection = resp.sectionName;
    }, (resp: ErrorMessageOutput) => {
      this.hasStudentLoadingFailed = true;
      this.statusMessageService.showErrorToast(resp.error.message);
    });
    this.studentProfileService.getStudentProfile(
        this.studentEmail, this.courseId,
    ).pipe(finalize(() => this.isStudentProfileLoading = false)).subscribe((resp: StudentProfile) => {
      this.studentProfile = resp;
    }, (resp: ErrorMessageOutput) => {
      this.hasStudentProfileLoadingFailed = true;
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Loads the student's feedback session results based on the given course ID and student name.
   */
  loadStudentResults(): void {
    this.sessionTabs = [];
    this.hasStudentResultsLoadingFailed = false;
    this.isStudentResultsLoading = true;
    this.feedbackSessionsService.getFeedbackSessionsForInstructor(this.courseId).pipe(
        mergeMap((feedbackSessions: FeedbackSessions) => feedbackSessions.feedbackSessions),
        mergeMap((feedbackSession: FeedbackSession) => {
          return this.feedbackSessionsService.getFeedbackSessionResults({
            courseId: this.courseId,
            feedbackSessionName: feedbackSession.feedbackSessionName,
            groupBySection: this.studentSection,
            intent: Intent.INSTRUCTOR_RESULT,
          }).pipe(map((results: SessionResults) => {
            // sort questions by question number
            results.questions.sort((a: QuestionOutput, b: QuestionOutput) =>
                a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber);
            return { results, feedbackSession };
          }));
        }),
        finalize(() => this.isStudentResultsLoading = false),
    ).subscribe(
        ({ results, feedbackSession }: { results: SessionResults, feedbackSession: FeedbackSession }) => {
          const giverQuestions: QuestionOutput[] = JSON.parse(JSON.stringify(results.questions));
          giverQuestions.forEach((questions: QuestionOutput) => {
            questions.allResponses = questions.allResponses.filter((response: ResponseOutput) =>
                !response.isMissingResponse && response.giverEmail === this.studentEmail);
          });
          const responsesGivenByStudent: QuestionOutput[] =
              giverQuestions.filter((questions: QuestionOutput) => questions.allResponses.length > 0);

          const recipientQuestions: QuestionOutput[] = JSON.parse(JSON.stringify(results.questions));
          recipientQuestions.forEach((questions: QuestionOutput) => {
            questions.allResponses = questions.allResponses.filter((response: ResponseOutput) =>
                !response.isMissingResponse && response.recipientEmail === this.studentEmail);
          });
          const responsesReceivedByStudent: QuestionOutput[] =
              recipientQuestions.filter((questions: QuestionOutput) => questions.allResponses.length > 0);

          this.sessionTabs.push({
            feedbackSession,
            responsesGivenByStudent,
            responsesReceivedByStudent,
            isCollapsed: false,
          });
          results.questions.forEach((questions: QuestionOutput) => this.preprocessComments(questions.allResponses));
        }, (errorMessageOutput: ErrorMessageOutput) => {
          this.hasStudentResultsLoadingFailed = true;
          this.statusMessageService.showErrorToast(errorMessageOutput.error.message);
        }, () => this.sortFeedbackSessions());
  }

  /**
   * Preprocesses the comments from instructor.
   *
   * <p>The instructor comment will be moved to map {@code instructorCommentTableModel}. The original
   * instructor comments associated with the response will be deleted.
   */
  preprocessComments(responses: ResponseOutput[]): void {
    const timezone: string = this.sessionTabs[0] != null ? this.sessionTabs[0].feedbackSession.timeZone : '';
    responses.forEach((response: ResponseOutput) => {
      this.instructorCommentTableModel[response.responseId] =
          this.commentsToCommentTableModel.transform(response.instructorComments, false, timezone);
      this.sortComments(this.instructorCommentTableModel[response.responseId]);
      // clear the original comments for safe as instructorCommentTableModel will become the single point of truth
      response.instructorComments = [];
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
          b.feedbackSession.feedbackSessionName);
    });
  }
}
