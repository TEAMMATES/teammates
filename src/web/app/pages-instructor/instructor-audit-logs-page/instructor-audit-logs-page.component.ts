import { Component, OnInit } from '@angular/core';
import { concatMap, finalize, mergeAll } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Course, Courses, Student, Students } from '../../../types/api-output';
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
  courseToStudents: Record<string, Student[]> = {};
  isLoading: boolean = true;

  constructor(private courseService: CourseService,
              private studentService: StudentService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.loadData();
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    // TODO: Call endpoint to retrieve logs
  }

  /**
   * Load all courses and students that the instructor have
   */
  private loadData(): void {
    const emptyStudent: Student = {
      courseId: '', email: '', name: '', sectionName: '', teamName: '',
    };
    this.courseService
        .getAllCoursesAsInstructor('active')
        .pipe(
            concatMap((courses: Courses) => courses.courses.map((course: Course) => {
              this.courses.push(course);
              return this.studentService.getStudentsFromCourse({ courseId: course.courseId });
            })),
            mergeAll(),
            finalize(() => this.isLoading = false))
        .subscribe(((student: Students) =>
                // Student with no name is selectable to search for all students since the field is optional
                this.courseToStudents[student.students[0].courseId] = [emptyStudent, ...student.students]),
            (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }
}
