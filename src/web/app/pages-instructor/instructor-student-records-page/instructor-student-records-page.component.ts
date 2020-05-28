import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import {
  FeedbackSession,
  FeedbackSessions,
  Gender,
  QuestionOutput, ResponseOutput,
  SessionResults, Student,
  StudentProfile,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

interface Session {
  name: string;
  isCollapsed: boolean;
  questions: QuestionOutput[];
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

  studentProfile: StudentProfile = {
    name: '',
    shortName: '',
    email: '',
    institute: '',
    nationality: '',
    gender: Gender.OTHER,
    moreInfo: '',
  };
  sessions: Session[] = [];
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
        }).subscribe((res: SessionResults) => {
          res.questions.forEach((q: QuestionOutput) => {
            q.allResponses = q.allResponses.filter((response: ResponseOutput) =>
                response.giverEmail === this.studentProfile.email ||
                response.recipientEmail === this.studentProfile.email);
          });
          this.sessions.push({
            name: feedbackSession.feedbackSessionName,
            isCollapsed: true,
            questions: res.questions.filter((q: QuestionOutput) => q.allResponses.length > 0),
          });
        });
      });
    }, (errorMessageOutput: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(errorMessageOutput.error.message);
    });
  }
}
