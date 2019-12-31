import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Student, StudentProfile } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentDetails {
  student: Student;
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

  student?: Student;
  studentProfile?: StudentProfile;
  photoUrl: string = '';

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
    private statusMessageService: StatusMessageService, private studentService: StudentService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const courseId: string = queryParams.courseid;
      const studentEmail: string = queryParams.studentemail;

      this.loadStudentDetails(courseId, studentEmail);
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${studentEmail}`;
    });
  }

  /**
   * Loads the student's details based on the given course ID and email.
   */
  loadStudentDetails(courseid: string, studentemail: string): void {
    const paramMap: { [key: string]: string } = { courseid, studentemail };
    this.httpRequestService.get('/courses/students/details', paramMap).subscribe((resp: StudentDetails) => {
      this.studentProfile = resp.studentProfile;
      if (!this.studentProfile) {
        this.statusMessageService.showWarningMessage(
                'Normally, we would show the student\'s profile here. '
                + 'However, either this student has not created a profile yet, '
                + 'or you do not have access to view this student\'s profile.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
    this.studentService.getStudentFromCourse(studentemail, courseid).subscribe((student: Student) => {
      this.student = student;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
