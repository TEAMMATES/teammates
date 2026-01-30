import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Student } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { NgIf } from '@angular/common';
import { CourseRelatedInfoComponent } from '../../components/course-related-info/course-related-info.component';

/**
 * Instructor course student details page.
 */
@Component({
  selector: 'tm-instructor-course-student-details-page',
  templateUrl: './instructor-course-student-details-page.component.html',
  styleUrls: ['./instructor-course-student-details-page.component.scss'],
  imports: [
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    NgIf,
    CourseRelatedInfoComponent,
  ],
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
