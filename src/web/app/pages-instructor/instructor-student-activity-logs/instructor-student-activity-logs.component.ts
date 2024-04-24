import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { ApiConst } from '../../../types/api-const';
import {
  Course, FeedbackSession,
  FeedbackSessionLog, FeedbackSessionLogEntry,
  FeedbackSessionLogs, FeedbackSessions,
  Student,
} from '../../../types/api-output';
import {
  getDefaultDateFormat,
  getDefaultTimeFormat,
  getLatestTimeFormat,
  DateFormat,
  TimeFormat,
  Milliseconds,
} from '../../../types/datetime-const';
import { SortBy } from '../../../types/sort-properties';
import { DatePickerFormatter } from '../../components/datepicker/datepicker-formatter';
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
  logType: string;
  selectedSession: SelectedSession;
  selectedStudent: SelectedStudent;
  showActions: boolean;
  showInactions: boolean;
}

interface LogType {
  label: string;
  value: string;
}

interface SelectedStudent {
    studentEmail?: string;
    studentId?: string;
}

interface SelectedSession {
    feedbackSessionName?: string;
    sessionId?: string;
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
 * Component for student activity and inactivity logs
 */
@Component({
  selector: 'tm-instructor-student-activity-logs',
  templateUrl: './instructor-student-activity-logs.component.html',
  providers: [{ provide: NgbDateParserFormatter, useClass: DatePickerFormatter }],
  styleUrls: ['./instructor-student-activity-logs.component.scss'],
})
export class InstructorStudentActivityLogsComponent implements OnInit {
  LOGS_DATE_TIME_FORMAT: string = 'ddd, DD MMM YYYY hh:mm:ss A';
  LOGS_RETENTION_PERIOD: number = ApiConst.LOGS_RETENTION_PERIOD;
  STUDENT_ACTIVITY_LOGS_UPDATE_INTERVAL: number = ApiConst.STUDENT_ACTIVITY_LOGS_UPDATE_INTERVAL;
  LOG_TYPES: LogType[] = [
    { label: 'session access', value: 'access' },
    { label: 'session submission', value: 'submission' },
    { label: 'session access and submission', value: 'access,submission' },
    { label: 'view session results', value: 'view result' },
  ];

  // enum
  SortBy: typeof SortBy = SortBy;

  formModel: SearchLogsFormModel = {
    logsDateFrom: getDefaultDateFormat(),
    logsTimeFrom: getDefaultTimeFormat(),
    logsDateTo: getDefaultDateFormat(),
    logsTimeTo: getDefaultTimeFormat(),
    logType: '',
      selectedStudent: { studentEmail: '', studentId: '' },
      selectedSession: { feedbackSessionName: '', sessionId: '' },
    showActions: false,
    showInactions: false,
  };
  course: Course = {
    courseId: '',
    courseName: '',
    institute: '',
    timeZone: '',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };
  dateToday: DateFormat = getDefaultDateFormat();
  earliestSearchDate: DateFormat = getDefaultDateFormat();
  studentLogsMap: Map<string, FeedbackSessionLogEntry[]> = new Map();
  students: Student[] = [];
  feedbackSessions: Map<string, FeedbackSession> = new Map();
  searchResults: FeedbackSessionLogModel[] = [];
  isLoading: boolean = true;
  isSearching: boolean = false;
  lastUpdated: string = '';

  constructor(private route: ActivatedRoute,
              private courseService: CourseService,
              private feedbackSessionsService: FeedbackSessionsService,
              private studentService: StudentService,
              private logsService: LogService,
              private timezoneService: TimezoneService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const courseId = queryParams.courseid;
      this.loadControlPanel();
      this.loadCourse(courseId);
      this.loadFeedbackSessions(courseId);
      this.loadStudents(courseId);
    });
  }

  /**
   * Loads the control panel based on the given course ID.
   */
  loadControlPanel(): void {
    const today: Date = new Date();
    this.dateToday.year = today.getFullYear();
    this.dateToday.month = today.getMonth() + 1;
    this.dateToday.day = today.getDate();

    const earliestSearchDate: Date = new Date(Date.now()
      - this.LOGS_RETENTION_PERIOD * Milliseconds.IN_ONE_DAY);
    this.earliestSearchDate.year = earliestSearchDate.getFullYear();
    this.earliestSearchDate.month = earliestSearchDate.getMonth() + 1;
    this.earliestSearchDate.day = earliestSearchDate.getDate();

    const fromDate: Date = new Date();
    fromDate.setDate(today.getDate() - 1);

    this.formModel.logsDateFrom = {
      year: fromDate.getFullYear(),
      month: fromDate.getMonth() + 1,
      day: fromDate.getDate(),
    };
    this.formModel.logsDateTo = { ...this.dateToday };
    this.formModel.logsTimeFrom = getLatestTimeFormat();
    this.formModel.logsTimeTo = getLatestTimeFormat();
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    if (this.formModel.logType === '') {
      this.statusMessageService.showErrorToast('Please select an activity type');
      return;
    }

    this.studentLogsMap = new Map();
    this.searchResults = [];
    this.isSearching = true;

    const timeZone: string = this.course.timeZone;
    const searchFrom: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateFrom, this.formModel.logsTimeFrom, timeZone, true);
    const searchUntil: number = this.timezoneService.resolveLocalDateTime(
        this.formModel.logsDateTo, this.formModel.logsTimeTo, timeZone, true);

    this.logsService.searchFeedbackSessionLog({
      courseId: this.course.courseId,
      searchFrom: searchFrom.toString(),
      searchUntil: searchUntil.toString(),
      studentEmail: this.formModel.selectedStudent.studentEmail,
      logType: this.formModel.logType,
      sessionName: this.formModel.selectedSession.feedbackSessionName,
      studentId: this.formModel.selectedStudent.studentId,
      sessionId: this.formModel.selectedSession.sessionId,
    }).pipe(
        finalize(() => {
          this.isSearching = false;
        }),
    ).subscribe({
      next: (logs: FeedbackSessionLogs) => {
        if (this.formModel.selectedSession.feedbackSessionName === '') {
          logs.feedbackSessionLogs.forEach((log: FeedbackSessionLog) => {
            log.feedbackSessionLogEntries.forEach((entry: FeedbackSessionLogEntry) => {
              const arr: FeedbackSessionLogEntry[] | undefined =
                this.studentLogsMap.get(this.getStudentKey(log, entry.studentData.email));
              if (arr) {
                arr.push(entry);
              } else {
                this.studentLogsMap.set(this.getStudentKey(log, entry.studentData.email), [entry]);
              }
            });
            this.searchResults.push(this.toFeedbackSessionLogModel(log));
          });
        } else {
          const targetFeedbackSessionLog = logs.feedbackSessionLogs.find((log: FeedbackSessionLog) =>
              log.feedbackSessionData.feedbackSessionName === this.formModel.selectedSession.feedbackSessionName);

          if (targetFeedbackSessionLog) {
            targetFeedbackSessionLog.feedbackSessionLogEntries.forEach((entry: FeedbackSessionLogEntry) => {
              const arr: FeedbackSessionLogEntry[] | undefined =
                this.studentLogsMap.get(this.getStudentKey(targetFeedbackSessionLog, entry.studentData.email));
              if (arr) {
                arr.push(entry);
              } else {
                this.studentLogsMap.set(
                  this.getStudentKey(targetFeedbackSessionLog, entry.studentData.email), [entry]);
              }
            });
            this.searchResults.push(this.toFeedbackSessionLogModel(targetFeedbackSessionLog));
          }
        }
      },
      error: (e: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(e.error.message);
      },
    });
  }

  /**
   * Load the course based on the course id
   */
  private loadCourse(courseId: string): void {
    this.courseService
        .getCourseAsInstructor(courseId)
        .pipe(finalize(() => {
          this.isLoading = false;
        }))
        .subscribe({
          next: (course: Course) => {
            this.course = course;
            this.setLastUpdated();
          },
          error: (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message),
        });
  }

  private loadFeedbackSessions(courseId: string): void {
    this.feedbackSessionsService
        .getFeedbackSessionsForInstructor(courseId)
        .subscribe({
          next: (feedbackSessions: FeedbackSessions) => {
            feedbackSessions.feedbackSessions.forEach((fs: FeedbackSession) => {
              this.feedbackSessions.set(fs.feedbackSessionName, fs);
            });
          },
          error: (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message),
        });
  }

  /**
   * Load all students for the selected course
   */
  loadStudents(courseId: string): void {
    if (this.students.length === 0) {
      this.isLoading = true;
      this.studentService.getStudentsFromCourse({ courseId })
          .pipe(finalize(() => { this.isLoading = false; }))
          .subscribe(({ students }: { students: Student[] }) => {
            const emptyStudent: Student = {
              courseId: '', email: '', name: '', sectionName: '', teamName: '',
            };
            students.sort((a: Student, b: Student): number => a.name.localeCompare(b.name));

            // Student with no name is selectable to search for all students since the field is optional
            this.students = [emptyStudent, ...students];
          });
    }
  }

  private toFeedbackSessionLogModel(log: FeedbackSessionLog): FeedbackSessionLogModel {
    const fsName = log.feedbackSessionData.feedbackSessionName;

    return {
      feedbackSessionName: fsName,
      logColumnsData: [
        { header: 'Status', sortBy: SortBy.RESULT_VIEW_STATUS },
        { header: 'Name', sortBy: SortBy.GIVER_NAME },
        { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
        { header: 'Section', sortBy: SortBy.SECTION_NAME },
        { header: 'Team', sortBy: SortBy.TEAM_NAME },
      ],
      logRowsData: this.students
          .filter((student: Student) => {
            if (student.email === '') {
              return false;
            }

            if (
              this.formModel.selectedStudent.studentEmail !== ''
              && student.email !== this.formModel.selectedStudent.studentEmail
            ) {
              return false;
            }

            if (this.formModel.showInactions && this.formModel.showActions) {
              return true;
            }

            const studentKey = this.getStudentKey(log, student.email);

            if (this.studentLogsMap.has(studentKey)) {
              if (this.formModel.showInactions) {
                return false;
              }
            } else if (this.formModel.showActions) {
              return false;
            }

            return true;
          })
          .flatMap((student: Student) => {
            let status: string;
            let dataStyle: string = 'font-family:monospace; white-space:pre;';
            const studentKey = this.getStudentKey(log, student.email);

            const entries: FeedbackSessionLogEntry[] | undefined = this.studentLogsMap.get(studentKey);
            const rows: any[] = [];
            if (entries) {
              entries.forEach((entry: FeedbackSessionLogEntry) => {
                const timestamp: string = this.timezoneService.formatToString(
                  entry.timestamp, log.feedbackSessionData.timeZone, this.LOGS_DATE_TIME_FORMAT);
                status = `${this.logTypeToActivityDisplay(entry.feedbackSessionLogType)} at ${timestamp}`;
                status = status.charAt(0).toUpperCase() + status.slice(1);
                rows.push([{
                  value: status,
                  style: dataStyle,
                },
                { value: student.name },
                { value: student.email },
                { value: student.sectionName },
                { value: student.teamName }]);
              });
            } else {
              status = `Not ${this.logTypeToActivityDisplay(this.formModel.logType)} within the query range`;
              dataStyle += 'color:red;';
              rows.push([
                {
                  value: status,
                  style: dataStyle,
                },
                { value: student.name },
                { value: student.email },
                { value: student.sectionName },
                { value: student.teamName },
              ]);
            }
            return rows;
          }),
      isTabExpanded: (log.feedbackSessionLogEntries.length !== 0 && this.formModel.showActions)
          || (log.feedbackSessionLogEntries.length === 0 && this.formModel.showInactions),
    };
  }

  private logTypeToActivityDisplay(logType: string): string {
    switch (logType.toUpperCase()) {
      case 'ACCESS':
        return 'viewed the submission page';
      case 'SUBMISSION':
        return 'submitted responses';
      case 'VIEW_RESULT':
      case 'VIEW RESULT':
        return 'viewed the session results';
      case 'ACCESS,SUBMISSION':
        return 'viewed the submission page or submitted responses';
      default:
        return 'unknown activity';
    }
  }

  private getStudentKey(log: FeedbackSessionLog, studentEmail: string): string {
    return `${log.feedbackSessionData.feedbackSessionName}-${studentEmail}`;
  }

  triggerDefaultLogActivityTypeChange(logType: string): void {
    if (logType === 'view result') {
      this.formModel.showInactions = true;
      this.formModel.showActions = false;
    } else {
      this.formModel.showInactions = false;
      this.formModel.showActions = true;
    }
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.formModel = {
      ...this.formModel,
      [field]: data,
    };
  }

  private setLastUpdated(): void {
    // 15 mins buffer to allow cron job to finish adding all logs to the database
    const CRON_JOB_BUFFER = 15;
    const now: Date = new Date();
    const minsPastQuarter = now.getMinutes() % 15;

    const lastUpdated = now.getTime() - ((CRON_JOB_BUFFER + minsPastQuarter) * Milliseconds.IN_ONE_MINUTE);

    this.lastUpdated = this.timezoneService.formatToString(lastUpdated, this.course.timeZone, 'DD MMM YYYY, hh:mm A');
  }
}
