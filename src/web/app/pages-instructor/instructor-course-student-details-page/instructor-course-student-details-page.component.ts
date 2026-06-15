import { Component, Input, OnInit, inject } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Student } from '../../../types/api-output';
import { CourseRelatedInfoComponent } from '../../components/course-related-info/course-related-info.component';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Instructor course student details page.
 */
@Component({
  selector: 'tm-instructor-course-student-details-page',
  templateUrl: './instructor-course-student-details-page.component.html',
  imports: [LoadingRetryComponent, LoadingSpinnerDirective, CourseRelatedInfoComponent],
})
export class InstructorCourseStudentDetailsPageComponent implements OnInit {
  private statusMessageService = inject(StatusMessageService);
  private studentService = inject(StudentService);

  student?: Student;

  @Input({ required: true }) courseId!: string;
  @Input({ required: true }) userId!: string;

  isStudentLoading = false;
  hasStudentLoadingFailed = false;

  ngOnInit(): void {
    this.loadStudentDetails(this.userId);
  }

  /**
   * Loads the student's details based on the given course ID and user ID.
   */
  loadStudentDetails(studentId: string): void {
    this.hasStudentLoadingFailed = false;
    this.isStudentLoading = true;
    this.studentService
      .getStudent({ userId: studentId })
      .pipe(
        finalize(() => {
          this.isStudentLoading = false;
        }),
      )
      .subscribe({
        next: (student: Student) => {
          this.student = student;
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
