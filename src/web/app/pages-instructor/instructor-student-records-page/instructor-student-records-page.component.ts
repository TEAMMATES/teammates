import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import {
  CommentOutput, FeedbackResponseComment,
  FeedbackSession,
  FeedbackSessions,
  Gender,
  QuestionOutput, ResponseOutput,
  SessionResults, Student,
  StudentProfile,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { CommentRowModel } from '../../components/comment-box/comment-row/comment-row.component';
import { CommentTableModel } from '../../components/comment-box/comment-table/comment-table.component';
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
  styleUrls: ['./instructor-student-records-page.component.scss'],
})
export class InstructorStudentRecordsPageComponent implements OnInit {

  courseId: string = '';
  studentEmail: string = '';
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

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private studentProfileService: StudentProfileService,
              private feedbackSessionsService: FeedbackSessionsService,
              private studentService: StudentService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.studentEmail = queryParams.studentemail;

      this.loadStudentRecords();
      this.loadStudentResults();
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?`
            + `courseid=${this.courseId}&studentemail=${this.studentEmail}`;
    });
  }

  /**
   * Loads the student's records based on the given course ID and email.
   */
  loadStudentRecords(): void {
    this.studentService.getStudent(this.courseId, this.studentEmail).subscribe((resp: Student) => {
      this.studentSection = resp.sectionName;
    });
    this.studentProfileService.getStudentProfile(this.studentEmail, this.courseId).subscribe((resp: StudentProfile) => {
      this.studentProfile = resp;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads the student's feedback session results based on the given course ID and student name.
   */
  loadStudentResults(): void {
    this.feedbackSessionsService.getFeedbackSessionsForInstructor(this.courseId).subscribe((resp: FeedbackSessions) => {
      resp.feedbackSessions.forEach((feedbackSession: FeedbackSession) => {
        this.feedbackSessionsService.getFeedbackSessionsResult({
          courseId: this.courseId,
          feedbackSessionName: feedbackSession.feedbackSessionName,
          groupBySection: this.studentSection,
          intent: Intent.INSTRUCTOR_RESULT,
        }).subscribe((results: SessionResults) => {

          // Rather than subscribe to the Observable, we simply grab the input so that we can filter
          const giverQuestions: QuestionOutput[] = JSON.parse(JSON.stringify(results.questions));
          giverQuestions.forEach((questions: QuestionOutput) => {
            questions.allResponses = questions.allResponses.filter((responses: ResponseOutput) =>
                responses.giver === this.studentProfile.name && responses.giverEmail === this.studentEmail);
          });
          const responsesGivenByStudent: QuestionOutput[] =
              giverQuestions.filter((questions: QuestionOutput) => questions.allResponses.length > 0);

          const recipientQuestions: QuestionOutput[] = JSON.parse(JSON.stringify(results.questions));
          recipientQuestions.forEach((questions: QuestionOutput) => {
            questions.allResponses = questions.allResponses.filter((responses: ResponseOutput) =>
                responses.recipient === this.studentProfile.name && responses.recipientEmail === this.studentEmail);
          });
          const responsesReceivedByStudent: QuestionOutput[] =
              recipientQuestions.filter((questions: QuestionOutput) => questions.allResponses.length > 0);

          this.sessionTabs.push({
            feedbackSession,
            responsesGivenByStudent,
            responsesReceivedByStudent,
            isCollapsed: responsesGivenByStudent.length === 0 && responsesReceivedByStudent.length === 0,
          });
          results.questions.forEach((questions: QuestionOutput) => this.preprocessComments(questions.allResponses));
        });
      });
    }, (errorMessageOutput: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(errorMessageOutput.error.message);
    });
  }

  /**
   * Preprocesses the comments from instructor.
   *
   * <p>The instructor comment will be moved to map {@code instructorCommentTableModel}. The original
   * instructor comments associated with the response will be deleted.
   */
  preprocessComments(responses: ResponseOutput[]): void {
    responses.forEach((response: ResponseOutput) => {
      this.instructorCommentTableModel[response.responseId] =
          this.getCommentTableModel(response.instructorComments);

      // clear the original comments for safe as instructorCommentTableModel will become the single point of truth
      response.instructorComments = [];
    });
  }

  /**
   * Transforms instructor comments to a comment table model.
   */
  getCommentTableModel(comments: CommentOutput[]): CommentTableModel {
    return {
      commentRows: comments.map((comment: FeedbackResponseComment) => this.getCommentRowModel(comment)),
      newCommentRow: {
        commentEditFormModel: {
          commentText: '',
          isUsingCustomVisibilities: false,
          showCommentTo: [],
          showGiverNameTo: [],
        },

        isEditing: false,
      },
      isAddingNewComment: false,
    };
  }

  /**
   * Transforms a comment to a comment row model.
   */
  getCommentRowModel(comment: CommentOutput): CommentRowModel {
    return {
      originalComment: comment,

      // Sessions in the same course will always have the same timezone
      timezone: this.sessionTabs[0] != null ? this.sessionTabs[0].feedbackSession.timeZone : '',

      commentGiverName: comment.commentGiverName,
      lastEditorName: comment.lastEditorName,

      commentEditFormModel: {
        commentText: comment.commentText,
        isUsingCustomVisibilities: !comment.isVisibilityFollowingFeedbackQuestion,
        showCommentTo: comment.showCommentTo,
        showGiverNameTo: comment.showGiverNameTo,
      },

      isEditing: false,
    };
  }
}
