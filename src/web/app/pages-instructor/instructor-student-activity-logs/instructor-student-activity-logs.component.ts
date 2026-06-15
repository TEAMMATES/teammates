import { NgClass, KeyValuePipe } from '@angular/common';
import { Component, Input, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import moment from 'moment-timezone';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { ApiConst } from '../../../types/api-const';
import {
  Course,
  FeedbackSession,
  FeedbackSessionLog,
  FeedbackSessionLogs,
  FeedbackSessionLogType,
  FeedbackSessionView,
  Student,
} from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { DatetimepickerComponent } from '../../components/datetimepicker/datetimepicker.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import {
  ColumnData,
  SortableTableCellData,
  SortableTableComponent,
} from '../../components/sortable-table/sortable-table.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Model for searching of logs
 */
interface SearchLogsFormModel {
  logsStartTimestamp: number;
  logsEndTimestamp: number;
  logTypes: FeedbackSessionLogType[];
  selectedSessionId: string;
  selectedUserId: string;
  showActions: boolean;
  showInactions: boolean;
}

interface LogType {
  label: string;
  value: FeedbackSessionLogType;
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
  styleUrls: ['./instructor-student-activity-logs.component.scss'],
  imports: [
    LoadingSpinnerDirective,
    FormsModule,
    DatetimepickerComponent,
    NgClass,
    PanelChevronComponent,
    SortableTableComponent,
    KeyValuePipe,
  ],
})
export class InstructorStudentActivityLogsComponent implements OnInit {
  private readonly courseService = inject(CourseService);
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly studentService = inject(StudentService);
  private readonly logsService = inject(LogService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly statusMessageService = inject(StatusMessageService);

  LOGS_DATE_TIME_FORMAT = 'ddd, DD MMM YYYY hh:mm:ss A';
  STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD: number = ApiConst.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD;
  LOG_TYPES: LogType[] = [
    { label: 'Session Access', value: FeedbackSessionLogType.ACCESS },
    { label: 'Session Submission', value: FeedbackSessionLogType.SUBMISSION },
    { label: 'View Session Results', value: FeedbackSessionLogType.VIEW_RESULT },
  ];

  // enum
  SortBy!: typeof SortBy;

  formModel: SearchLogsFormModel = {
    logsStartTimestamp: 0,
    logsEndTimestamp: 0,
    logTypes: [FeedbackSessionLogType.ACCESS, FeedbackSessionLogType.SUBMISSION],
    selectedUserId: '',
    selectedSessionId: '',
    showActions: true,
    showInactions: false,
  };
  course: Course = {
    courseId: '',
    courseName: '',
    institute: '',
    country: '',
    instituteId: '',
    timeZone: '',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };
  earliestSearchTimestamp = 0;
  latestSearchTimestamp = 0;
  studentLogsMap: Map<string, FeedbackSessionLog[]> = new Map();
  students: Student[] = [];
  feedbackSessions: Map<string, FeedbackSession> = new Map();
  searchResults: FeedbackSessionLogModel[] = [];
  isLoading = true;

  constructor() {
    this.SortBy = SortBy;
  }

  @Input({ required: true }) courseId!: string;

  ngOnInit(): void {
    this.loadData(this.courseId);
  }

  /**
   * Initialises the search range bounds and default values in the course's timezone.
   */
  loadControlPanel(): void {
    const now: moment.Moment = moment.tz(this.course.timeZone || this.timezoneService.guessTimezone());

    this.earliestSearchTimestamp = now
      .clone()
      .subtract(this.STUDENT_ACTIVITY_LOGS_RETENTION_PERIOD, 'days')
      .startOf('day')
      .valueOf();
    this.latestSearchTimestamp = now.clone().add(1, 'day').startOf('day').valueOf();

    // The default search window spans from the start of the day to the end of the day.
    this.formModel.logsStartTimestamp = now.clone().startOf('day').valueOf();
    this.formModel.logsEndTimestamp = now.clone().add(1, 'day').startOf('day').valueOf();
  }

  /**
   * Search for logs of student activity
   */
  search(): void {
    if (!this.formModel.logTypes || this.formModel.logTypes.length === 0) {
      this.statusMessageService.showErrorToast('Please select an activity type');
      return;
    }

    this.studentLogsMap = new Map();
    this.searchResults = [];
    this.isLoading = true;

    this.logsService
      .searchFeedbackSessionLog({
        courseId: this.course.courseId,
        searchFrom: this.formModel.logsStartTimestamp,
        searchUntil: this.formModel.logsEndTimestamp,
        logTypes: this.formModel.logTypes,
        userId: this.formModel.selectedUserId,
        sessionId: this.formModel.selectedSessionId,
      })
      .pipe(
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: (logs: FeedbackSessionLogs) => {
          if (this.formModel.selectedSessionId === '') {
            this.feedbackSessions.forEach((_: FeedbackSession, feedbackSessionId: string) => {
              const entries: FeedbackSessionLog[] = logs.feedbackSessionLogs[feedbackSessionId] || [];
              entries.forEach((entry: FeedbackSessionLog) => {
                const arr: FeedbackSessionLog[] | undefined = this.studentLogsMap.get(
                  this.getStudentKey(feedbackSessionId, entry.user.userId),
                );
                if (arr) {
                  arr.push(entry);
                } else {
                  this.studentLogsMap.set(this.getStudentKey(feedbackSessionId, entry.user.userId), [entry]);
                }
              });
              this.searchResults.push(this.toFeedbackSessionLogModel(feedbackSessionId, entries));
            });
          } else {
            const selectedSessionId = this.formModel.selectedSessionId || '';
            const targetEntries: FeedbackSessionLog[] = logs.feedbackSessionLogs[selectedSessionId] || [];
            targetEntries.forEach((entry: FeedbackSessionLog) => {
              const arr: FeedbackSessionLog[] | undefined = this.studentLogsMap.get(
                this.getStudentKey(selectedSessionId, entry.user.userId),
              );
              if (arr) {
                arr.push(entry);
              } else {
                this.studentLogsMap.set(this.getStudentKey(selectedSessionId, entry.user.userId), [entry]);
              }
            });
            this.searchResults.push(this.toFeedbackSessionLogModel(selectedSessionId, targetEntries));
          }
        },
        error: (e: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(e.error.message);
        },
      });
  }

  private loadData(courseId: string): void {
    this.isLoading = true;
    this.course = {
      courseId: '',
      courseName: '',
      institute: '',
      country: '',
      instituteId: '',
      timeZone: '',
      creationTimestamp: 0,
      deletionTimestamp: 0,
    };
    this.feedbackSessions = new Map();
    this.students = [];

    forkJoin({
      course: this.courseService.getCourseAsInstructor(courseId),
      feedbackSessions: this.feedbackSessionsService.getFeedbackSessionsForInstructor(courseId),
      students: this.studentService.getStudentsFromCourse({ courseId }),
    })
      .pipe(
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: ({ course, feedbackSessions, students }) => {
          this.course = course.course;
          this.loadControlPanel();
          this.feedbackSessions = new Map(
            feedbackSessions.feedbackSessions.map((fsView: FeedbackSessionView) => [
              fsView.feedbackSession.feedbackSessionId,
              fsView.feedbackSession,
            ]),
          );
          this.students = this.toStudentSelectionList(students.students);
        },
        error: (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message),
      });
  }

  private toStudentSelectionList(students: Student[]): Student[] {
    const emptyStudent: Student = {
      userId: '',
      courseId: '',
      email: '',
      name: '',
      teamId: '',
      sectionName: '',
      teamName: '',
      sectionId: '',
      institute: '',
      courseName: '',
    };

    const sortedStudents = [...students].sort((a: Student, b: Student): number => a.name.localeCompare(b.name));

    // Student with no name is selectable to search for all students since the field is optional
    return [emptyStudent, ...sortedStudents];
  }

  private toFeedbackSessionLogModel(
    feedbackSessionId: string,
    feedbackSessionLogEntries: FeedbackSessionLog[],
  ): FeedbackSessionLogModel {
    const feedbackSession = this.feedbackSessions.get(feedbackSessionId);
    const fsName = feedbackSession ? feedbackSession.feedbackSessionName : feedbackSessionId;
    const timeZone = feedbackSession ? feedbackSession.timeZone : this.course.timeZone;

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
          if (this.formModel.selectedUserId && this.formModel.selectedUserId != student.userId) {
            return false;
          }

          if (this.formModel.showInactions && this.formModel.showActions) {
            return true;
          }

          const studentKey = this.getStudentKey(feedbackSessionId, student.userId);

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
          let dataStyle = 'font-family:monospace; white-space:pre;';
          const studentKey = this.getStudentKey(feedbackSessionId, student.userId);

          const entries: FeedbackSessionLog[] | undefined = this.studentLogsMap.get(studentKey);
          const rows: { value: string; style?: string }[][] = [];
          if (entries) {
            entries.forEach((entry: FeedbackSessionLog) => {
              const timestamp: string = this.timezoneService.formatToString(
                entry.timestamp,
                timeZone,
                this.LOGS_DATE_TIME_FORMAT,
              );
              status = `${this.logTypeToActivityDisplay(entry.feedbackSessionLogType)} at ${timestamp}`;
              status = status.charAt(0).toUpperCase() + status.slice(1);
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
            });
          } else {
            status = 'No results within the query range';
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
      isTabExpanded:
        (feedbackSessionLogEntries.length !== 0 && this.formModel.showActions) ||
        (feedbackSessionLogEntries.length === 0 && this.formModel.showInactions),
    };
  }

  isLogTypeSelected(logType: FeedbackSessionLogType): boolean {
    return this.formModel.logTypes.includes(logType);
  }

  onLogTypeToggle(logType: FeedbackSessionLogType, checked: boolean): void {
    const current = this.formModel.logTypes;
    if (checked) {
      if (current.includes(logType)) {
        return;
      }
      this.triggerModelChange('logTypes', [...current, logType]);
    } else {
      this.triggerModelChange(
        'logTypes',
        current.filter((t) => t !== logType),
      );
    }
  }

  private logTypeToActivityDisplay(logType: FeedbackSessionLogType): string {
    switch (logType) {
      case FeedbackSessionLogType.ACCESS:
        return 'viewed the submission page';
      case FeedbackSessionLogType.SUBMISSION:
        return 'submitted responses';
      case FeedbackSessionLogType.VIEW_RESULT:
        return 'viewed the session results';
      default:
        return '';
    }
  }

  private getStudentKey(feedbackSessionId: string, userId: string): string {
    return `${feedbackSessionId}-${userId}`;
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: unknown): void {
    this.formModel = {
      ...this.formModel,
      [field]: data,
    };
  }
}
