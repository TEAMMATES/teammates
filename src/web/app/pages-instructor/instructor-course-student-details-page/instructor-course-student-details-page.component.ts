import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import { Student, StudentProfile } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

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

  courseId: string = '';
  studentEmail: string = '';

  isStudentLoading: boolean = false;
  hasStudentLoadingFailed: boolean = false;
  isStudentProfileLoading: boolean = false;
  hasStudentProfileLoadingFailed: boolean = false;

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private studentService: StudentService,
              private studentProfileService: StudentProfileService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.studentEmail = queryParams.studentemail;

      this.loadStudentDetails(this.courseId, this.studentEmail);
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?courseid=${this.courseId}&studentemail=${this.studentEmail}`;
    });
  }

  /**
   * Loads the student's details based on the given course ID and email.
   */
  loadStudentDetails(courseId: string, studentEmail: string): void {
    this.hasStudentLoadingFailed = false;
    this.hasStudentProfileLoadingFailed = false;
    this.isStudentProfileLoading = true;
    this.isStudentLoading = true;
    this.studentProfileService.getStudentProfile(
        studentEmail, courseId,
    ).pipe(finalize(() => this.isStudentProfileLoading = false)).subscribe((studentProfile: StudentProfile) => {
      this.studentProfile = studentProfile;
    }, (resp: ErrorMessageOutput) => {
      this.hasStudentLoadingFailed = true;
      this.statusMessageService.showErrorToast(resp.error.message);
    });
    this.studentService.getStudent(
        courseId, studentEmail,
    ).pipe(finalize(() => this.isStudentLoading = false)).subscribe((student: Student) => {
      this.student = student;
    }, (resp: ErrorMessageOutput) => {
      this.hasStudentProfileLoadingFailed = true;
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }
}
