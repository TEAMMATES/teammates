import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { QuestionOutput, ResponseOutput, SessionResults, Student } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { Intent } from '../../Intent';
import { StudentProfile } from '../student-profile/student-profile';

interface StudentRecords {
  courseId: string;
  studentName: string;
  studentEmail: string;
  studentProfile: StudentProfile;
  sessionNames: string[];
}

interface Session {
  name: string;
  questions: QuestionOutput[];
  isCollapsed: boolean;
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

  user: string = '';
  courseId: string = '';
  studentName: string = '';
  studentEmail: string = '';
  studentProfile?: StudentProfile ;
  studentSection: string = '';
  sessions: Session[] = [];
  photoUrl: string = '';

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const courseId: string = queryParams.courseid;
      const studentEmail: string = queryParams.studentemail;

      this.user = queryParams.user;
      this.loadStudentRecords(courseId, studentEmail);
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${studentEmail}`;
    });
  }

  /**
   * Loads the student's records based on the given course ID and email.
   */
  loadStudentRecords(courseid: string, studentemail: string): void {
    const paramMap: { [key: string]: string } = { courseid, studentemail };
    this.httpRequestService.get('/student', paramMap).subscribe((resp: Student) => {
      this.studentSection = resp.sectionName;
    });
    this.httpRequestService.get('/students/records', paramMap).subscribe((resp: StudentRecords) => {
      this.courseId = resp.courseId;
      this.studentName = resp.studentName;
      this.studentEmail = resp.studentEmail;
      this.studentProfile = resp.studentProfile;
      if (!this.studentProfile) {
        this.statusMessageService.showWarningMessage(
                'Normally, we would show the student\'s profile here. '
                + 'However, either this student has not created a profile yet, '
                + 'or you do not have access to view this student\'s profile.');
      }

      resp.sessionNames.forEach((fsname: string) => {
        const resultParamMap: { [key: string]: string } = {
          courseid,
          fsname,
          frgroupbysection: this.studentSection,
          intent: Intent.INSTRUCTOR_RESULT,
        };
        this.httpRequestService.get('/result', resultParamMap).subscribe((res: SessionResults) => {
          res.questions.forEach((q: QuestionOutput) => {
            q.allResponses = q.allResponses.filter((response: ResponseOutput) =>
                response.giver === this.studentName || response.recipient === this.studentName);
          });
          const questions: QuestionOutput[] = res.questions.filter((q: QuestionOutput) => q.allResponses.length > 0);
          this.sessions.push({ questions, name: fsname, isCollapsed: questions.length === 0 });
        }, (res: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(res.error.message);
        });
      });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
