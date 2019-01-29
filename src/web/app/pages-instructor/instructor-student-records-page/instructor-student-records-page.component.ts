import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
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
  sessions: Session[] = [];

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudentRecords(queryParams.courseid, queryParams.studentemail);
    });
  }

  /**
   * Loads the student's records based on the given course ID and email.
   */
  loadStudentRecords(courseid: string, studentemail: string): void {
    const paramMap: { [key: string]: string } = { courseid, studentemail };
    this.httpRequestService.get('/students/records', paramMap).subscribe((resp: StudentRecords) => {
      this.courseId = resp.courseId;
      this.studentName = resp.studentName;
      this.studentEmail = resp.studentEmail;
      this.studentProfile = resp.studentProfile;
      this.sessions = resp.sessionNames.map((sessionName: string) => ({ name: sessionName, isCollapsed: false }));
      if (!this.studentProfile) {
        this.statusMessageService.showWarningMessage(
                'Normally, we would show the student\'s profile here. '
                + 'However, either this student has not created a profile yet, '
                + 'or you do not have access to view this student\'s profile.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
