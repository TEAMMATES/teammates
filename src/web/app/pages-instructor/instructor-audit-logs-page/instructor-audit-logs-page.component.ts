import { Component, OnInit } from '@angular/core';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { concatMap, finalize, map, mergeAll } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from '../../../services/timezone.service';
import { ApiConst } from '../../../types/api-const';
import {
  Course,
  Courses,
  FeedbackSessionLog, FeedbackSessionLogEntry,
  FeedbackSessionLogs, LogType,
  Student,
  Students,
} from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { SessionEditFormDatePickerFormatter } from '../../components/session-edit-form/session-edit-form-datepicker-formatter';
import { DateFormat } from '../../components/session-edit-form/session-edit-form-model';
import { TimeFormat } from '../../components/session-edit-form/time-picker/time-picker.component';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
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
  studentEmail: string;
}

/**
 * Model for displaying of feedback session logs
 */
interface FeedbackSessionLogModel {
  feedbackSessionName: string;
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
  isTabExpanded: boolean;
}

/**
 * Component for instructor logs
 */
@Component({
  selector: 'tm-instructor-audit-logs-page',
  templateUrl: './instructor-audit-logs-page.component.html',
  providers: [{ provide: NgbDateParserFormatter, useClass: SessionEditFormDatePickerFormatter }],
  styleUrls: ['./instructor-audit-logs-page.component.scss'],
})
export class InstructorAuditLogsPageComponent implements OnInit {
  LOGS_RETENTION_PERIOD: number = ApiConst.LOGS_RETENTION_PERIOD;

  // enum
  SortBy: typeof SortBy = SortBy;

  formModel: SearchLogsFormModel = {
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 0, minute: 0 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 0, minute: 0 },
    courseId: '',
    studentEmail: '',
  };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 0, month: 0, day: 0 };
  courses: Course[] = [];
  courseToStudents: Record<string, Student[]> = {};
  searchResults: FeedbackSessionLogModel[] = [];
  isLoading: boolean = true;
  isSearching: boolean = false;

  constructor(private courseService: CourseService,
              private studentService: StudentService,
              private logsService: LogService,
              private timezoneService: TimezoneService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    const today: Date = new Date();
    this.dateToday.year = today.getFullYear();
    this.dateToday.month = today.getMonth() + 1;
    this.dateToday.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now() - this.LOGS_RETENTION_PERIOD * 24 * 60 * 60 * 1000);
    this.earliestSearchDate.year = earliestSearchDate.getFullYear();
    this.earliestSearchDate.month = earliestSearchDate.getMonth() + 1;
    this.earliestSearchDate.day = earliestSearchDate.getDate();

    this.formModel.logsDateFrom = { ...this.dateToday, day: today.getDate() - 1 };
    this.formModel.logsDateTo = { ...this.dateToday };
    this.formModel.logsTimeFrom = { hour: 23, minute: 59 };
    this.formModel.logsTimeTo = { hour: 23, minute: 59 };
    this.loadData();
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    this.isSearching = true;
    this.searchResults = [];
    const localDateTime: Observable<number>[] = [
      this.resolveLocalDateTime(this.formModel.logsDateFrom, this.formModel.logsTimeFrom, 'Search period from'),
      this.resolveLocalDateTime(this.formModel.logsDateTo, this.formModel.logsTimeTo, 'Search period until'),
    ];

    forkJoin(localDateTime)
        .pipe(
            concatMap((timestamp: number[]) => {
              return this.logsService.searchFeedbackSessionLog({
                courseId: this.formModel.courseId,
                searchFrom: timestamp[0].toString(),
                searchUntil: timestamp[1].toString(),
                studentEmail: this.formModel.studentEmail,
              });
            }),
            finalize(() => this.isSearching = false))
        .subscribe((logs: FeedbackSessionLogs) => {
          logs.feedbackSessionLogs.map((log: FeedbackSessionLog) =>
              this.searchResults.push(this.toFeedbackSessionLogModel(log)));
        }, (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
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
            concatMap((courses: Courses) => courses.courses
                .filter((course: Course) =>
                    course.privileges?.canModifyStudent
                    && course.privileges?.canModifySession
                    && course.privileges?.canModifySession)
                .map((course: Course) => {
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

  private resolveLocalDateTime(date: DateFormat, time: TimeFormat, fieldName: string): Observable<number> {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);
    const localDateTime: string = inst.format(LOCAL_DATE_TIME_FORMAT);

    return this.timezoneService.getResolvedTimestamp(localDateTime, this.timezoneService.guessTimezone(), fieldName)
        .pipe(map((result: TimeResolvingResult) => result.timestamp));
  }

  private toFeedbackSessionLogModel(log: FeedbackSessionLog): FeedbackSessionLogModel {
    return {
      isTabExpanded: log.feedbackSessionLogEntries.length === 0,
      feedbackSessionName: log.feedbackSessionData.feedbackSessionName,
      logColumnsData: [
        { header: 'Time', sortBy: SortBy.LOG_DATE },
        { header: 'Name', sortBy: SortBy.GIVER_NAME },
        { header: 'Activity', sortBy: SortBy.LOG_TYPE },
        { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
        { header: 'Section', sortBy: SortBy.SECTION_NAME },
        { header: 'Team', sortBy: SortBy.TEAM_NAME },
      ],
      logRowsData: log.feedbackSessionLogEntries.map((entry: FeedbackSessionLogEntry) => {
        return [
          { value: this.timezoneService.formatToString(entry.timestamp, log.feedbackSessionData.timeZone, 'ddd, DD MMM, YYYY hh:mm:ss A'),
            font: 'monospace' },
          { value: entry.studentData.name },
          { value: LogType[entry.feedbackSessionLogType.toString() as keyof typeof LogType]
            === LogType.FEEDBACK_SESSION_ACCESS ? 'Viewed the submission page' : 'Submitted responses' },
          { value: entry.studentData.email },
          { value: entry.studentData.sectionName },
          { value: entry.studentData.teamName },
        ];
      }),
    };
  }
}
