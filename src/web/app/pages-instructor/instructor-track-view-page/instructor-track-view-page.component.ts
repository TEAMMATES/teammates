import { Component, OnInit } from '@angular/core';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { concatMap, finalize, map, mergeAll } from 'rxjs/operators';
import { FeedbackSessionsService } from 'src/web/services/feedback-sessions.service';
import { CourseService } from '../../../services/course.service';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { LOCAL_DATE_TIME_FORMAT, TimeResolvingResult, TimezoneService } from '../../../services/timezone.service';
import { ApiConst } from '../../../types/api-const';
import {
  Course,
  Courses,
  FeedbackSession,
  FeedbackSessionLog, 
  FeedbackSessionLogEntry,
  FeedbackSessionLogs, 
  FeedbackSessions, 
  LogType,
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
  courseId: string;
  sessionName: string;
  logsDateFrom: DateFormat;
  logsDateTo: DateFormat;
  logsTimeFrom: TimeFormat;
  logsTimeTo: TimeFormat;
}

/**
 * Model for displaying of feedback session logs
 */
interface FeedbackSessionLogModel {
  feedbackSessionName: string;
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
}

@Component({
  selector: 'tm-instructor-track-view-page',
  templateUrl: './instructor-track-view-page.component.html',
  providers: [{ provide: NgbDateParserFormatter, useClass: SessionEditFormDatePickerFormatter }],
  styleUrls: ['./instructor-track-view-page.component.scss']
})
export class InstructorTrackViewPageComponent implements OnInit {
  LOGS_RETENTION_PERIOD: number = ApiConst.LOGS_RETENTION_PERIOD;

  // enum
  SortBy: typeof SortBy = SortBy;

  formModel: SearchLogsFormModel = {
    logsDateFrom: { year: 0, month: 0, day: 0 },
    logsTimeFrom: { hour: 23, minute: 59 },
    logsDateTo: { year: 0, month: 0, day: 0 },
    logsTimeTo: { hour: 23, minute: 59 },
    courseId: '',
    sessionName: '',
  };
  courses: Course[] = [];
  courseToFeedbackSession: Record<string, FeedbackSession[]> = {};
  searchResult: FeedbackSessionLogModel = {
    feedbackSessionName: '',
    logColumnsData: [],
    logRowsData: [],
  };
  students: Student[] = [];
  isLoading: boolean = true;
  isSearching: boolean = false;
  isStudentsLoading: boolean = false;
  hasLoadingStudentsFailed: boolean = false;
  hasResult: boolean = false;

  constructor(private courseService: CourseService,
    private feedbackSessionsService: FeedbackSessionsService,
    private logsService: LogService,
    private statusMessageService: StatusMessageService,
    private studentService: StudentService,
    private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    const today: Date = new Date();

    this.formModel.logsDateTo.year = today.getFullYear();
    this.formModel.logsDateTo.month = today.getMonth() + 1;
    this.formModel.logsDateTo.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now() - this.LOGS_RETENTION_PERIOD * 24 * 60 * 60 * 1000);
    this.formModel.logsDateFrom.year = earliestSearchDate.getFullYear();
    this.formModel.logsDateFrom.month = earliestSearchDate.getMonth() + 1;
    this.formModel.logsDateFrom.day = earliestSearchDate.getDate();

    this.loadData();
  }

  /**
   * Load all courses that the instructor have
   */
  private loadData(): void {
    this.courseService
        .getAllCoursesAsInstructor('active')
        .pipe(
            concatMap((courses: Courses) => courses.courses.map((course: Course) => {
              this.courses.push(course);
              return this.feedbackSessionsService.getFeedbackSessionsForInstructor(course.courseId);
            })),
            mergeAll(),
            finalize(() => this.isLoading = false))
        .subscribe(((feedbackSession: FeedbackSessions) =>
                this.courseToFeedbackSession[feedbackSession.feedbackSessions[0].courseId] = [...feedbackSession.feedbackSessions]),
            (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    this.isSearching = true;
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
                sessionName: this.formModel.sessionName,
              });
            }),
            finalize(() => {
              this.isSearching = false;
              this.hasResult = true; 
            }))
        .subscribe((logs: FeedbackSessionLogs) => {
          logs.feedbackSessionLogs.map((log: FeedbackSessionLog) =>
              this.searchResult = this.toFeedbackSessionLogModel(log));
        }, (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
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
    this.loadStudents(this.formModel.courseId);
    return {
      feedbackSessionName: log.feedbackSessionData.feedbackSessionName,
      logColumnsData: [
        { header: 'Status', sortBy: SortBy.LOG_DATE },
        { header: 'Name', sortBy: SortBy.GIVER_NAME },
        { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
        { header: 'Section', sortBy: SortBy.SECTION_NAME },
        { header: 'Team', sortBy: SortBy.TEAM_NAME },
      ],
      logRowsData: log.feedbackSessionLogEntries
        .filter(entry => LogType[entry.feedbackSessionLogType.toString() as keyof typeof LogType] === LogType.FEEDBACK_SESSION_VIEW)
        .map((entry: FeedbackSessionLogEntry) => {
          return [
            { value: this.timezoneService.formatToString(entry.timestamp, log.feedbackSessionData.timeZone, 'ddd, DD MMM, YYYY hh:mm:ss A') },
            { value: entry.studentData.name },
            { value: entry.studentData.email },
            { value: entry.studentData.sectionName },
            { value: entry.studentData.teamName },
          ];
        }),
    };
  }

  loadStudents(courseid: string): void {
    this.hasLoadingStudentsFailed = false;
    this.isStudentsLoading = true;
    this.students = [];
    this.studentService
        .getStudentsFromCourse({ courseId: courseid })
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
            (students: Students) => students.students.map((student: Student) => this.students.push(student)),
            (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }
}
