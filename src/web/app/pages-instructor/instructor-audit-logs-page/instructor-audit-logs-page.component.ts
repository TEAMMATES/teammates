import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Course, Courses } from '../../../types/api-output';
import { DateFormat } from '../../components/session-edit-form/session-edit-form-model';
import { TimeFormat } from '../../components/session-edit-form/time-picker/time-picker.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Model for searching of logs
 */
interface SearchLogsFormModel {
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
  courseId: string;
  studentName: string;
}

/**
 * Component for instructor logs
 */
@Component({
  selector: 'tm-instructor-audit-logs-page',
  templateUrl: './instructor-audit-logs-page.component.html',
  styleUrls: ['./instructor-audit-logs-page.component.scss'],
})
export class InstructorAuditLogsPageComponent implements OnInit {

  formModel: SearchLogsFormModel = {
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
    courseId: '',
    studentName: '',
  };
  courses: Course[] = [];
  isLoading: boolean = true;

  constructor(private courseService: CourseService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.loadCourses();
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    // TODO: Call endpoint to retrieve logs
  }

  private loadCourses(): void {
    this.courseService
        .getAllCoursesAsInstructor('active')
        .pipe(finalize(() => this.isLoading = false))
        .subscribe((courses: Courses) => this.courses = courses.courses,
            (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }
}
