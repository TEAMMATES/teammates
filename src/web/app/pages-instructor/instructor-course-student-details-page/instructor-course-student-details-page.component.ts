import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentAttributes } from '../student-profile/student-attributes';
import { StudentProfile } from '../student-profile/student-profile';

interface StudentDetails {
  student: StudentAttributes;
  studentProfile: StudentProfile;
  hasSection: boolean;
}

/**
 * Instructor course student details page.
 */
@Component({
  selector: 'tm-instructor-course-student-details-page',
  templateUrl: './instructor-course-student-details-page.component.html',
  styleUrls: ['./instructor-course-student-details-page.component.scss'],
})
export class InstructorCourseStudentDetailsPageComponent implements OnInit {

  user: string = '';
  student?: StudentAttributes;
  studentProfile?: StudentProfile;

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudentDetails(queryParams.courseid, queryParams.studentemail);
    });
  }

  /**
   * Loads the student's details based on the given course ID and email.
   */
  loadStudentDetails(courseid: string, studentemail: string): void {
    const paramMap: { [key: string]: string } = { courseid, studentemail };
    this.httpRequestService.get('/courses/students/details', paramMap).subscribe((resp: StudentDetails) => {
      this.student = resp.student;
      this.studentProfile = resp.studentProfile;
      if (!this.student) {
        this.statusMessageService.showErrorMessage('Error retrieving student details');
      }
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
