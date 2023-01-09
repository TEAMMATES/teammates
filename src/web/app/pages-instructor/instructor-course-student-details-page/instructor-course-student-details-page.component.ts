import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Student } from '../../../types/api-output';
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

  courseId: string = '';
  studentEmail: string = '';

  isStudentLoading: boolean = false;
  hasStudentLoadingFailed: boolean = false;

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private studentService: StudentService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.studentEmail = queryParams.studentemail;

      this.loadStudentDetails(this.courseId, this.studentEmail);
    });
  }

  /**
   * Loads the student's details based on the given course ID and email.
   */
  loadStudentDetails(courseId: string, studentEmail: string): void {
    this.hasStudentLoadingFailed = false;
    this.isStudentLoading = true;
    this.studentService.getStudent(
        courseId, studentEmail,
    ).pipe(finalize(() => {
      this.isStudentLoading = false;
    })).subscribe({
      next: (student: Student) => {
        this.student = student;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }
}
