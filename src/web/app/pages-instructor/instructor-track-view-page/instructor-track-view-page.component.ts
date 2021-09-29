import { Component, OnInit } from '@angular/core';
import { NgbDateParserFormatter, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { concatMap, finalize, mergeAll } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { ApiConst } from '../../../types/api-const';
import {
  Course,
  Courses,
  FeedbackSession,
  FeedbackSessionLog,
  FeedbackSessionLogEntry,
  FeedbackSessionLogs,
  FeedbackSessionLogType,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { SortBy } from '../../../types/sort-properties';
import { DateFormat } from '../../components/datepicker/datepicker.component';
import { SessionEditFormDatePickerFormatter } from '../../components/session-edit-form/session-edit-form-datepicker-formatter';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
import { TimeFormat } from '../../components/timepicker/timepicker.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Model for searching of logs
 */
interface SearchLogsFormModel {
  courseId: string;
  feedbackSessionName: string;
}

/**
 * Model for displaying of feedback session logs
 */
interface FeedbackSessionLogModel {
  courseId: string;
  feedbackSessionName: string;
  publishedDate: string;
  logColumnsData: ColumnData[];
  logRowsData: SortableTableCellData[][];
}

/**
 * Component for tracking student results view
 */
@Component({
  selector: 'tm-instructor-track-view-page',
  templateUrl: './instructor-track-view-page.component.html',
  providers: [{ provide: NgbDateParserFormatter, useClass: SessionEditFormDatePickerFormatter }],
  styleUrls: ['./instructor-track-view-page.component.scss'],
})
export class InstructorTrackViewPageComponent implements OnInit {
  LOGS_DATE_TIME_FORMAT: string = 'ddd, DD MMM YYYY hh:mm:ss A';
  LOGS_RETENTION_PERIOD_IN_DAYS: number = ApiConst.LOGS_RETENTION_PERIOD;
  LOGS_RETENTION_PERIOD_IN_MILLISECONDS: number = this.LOGS_RETENTION_PERIOD_IN_DAYS * 24 * 60 * 60 * 1000;
  ONE_MINUTE_IN_MILLISECONDS: number = 60 * 1000;

  // enum
  SortBy: typeof SortBy = SortBy;

  formModel: SearchLogsFormModel = {
    courseId: '',
    feedbackSessionName: '',
  };
  courses: Course[] = [];
  courseToFeedbackSession: Record<string, FeedbackSession[]> = {};
  searchResult: FeedbackSessionLogModel = {
    courseId: '',
    feedbackSessionName: '',
    publishedDate: '',
    logColumnsData: [],
    logRowsData: [],
  };
  students: Student[] = [];
  studentToLog: Record<string, FeedbackSessionLogEntry> = {};
  isLoading: boolean = true;
  isSearching: boolean = false;
  hasResult: boolean = false;
  publishedTime: number = 0;
  notViewedSince: number = 0;
  logsDateFrom: DateFormat = { year: 0, month: 0, day: 0 };
  logsTimeFrom: TimeFormat = { hour: 23, minute: 59 };
  logsDateTo: DateFormat = { year: 0, month: 0, day: 0 };
  logsTimeTo: TimeFormat = { hour: 23, minute: 59 };

  constructor(private courseService: CourseService,
    private feedbackSessionsService: FeedbackSessionsService,
    private logsService: LogService,
    private simpleModalService: SimpleModalService,
    private statusMessageService: StatusMessageService,
    private studentService: StudentService,
    private timezoneService: TimezoneService) { }

  ngOnInit(): void {
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
      .subscribe(((feedbackSessions: FeedbackSessions) => {
        if (feedbackSessions.feedbackSessions.length > 0) {
          this.courseToFeedbackSession[feedbackSessions.feedbackSessions[0].courseId]
            = [...feedbackSessions.feedbackSessions];
        }
      }),
      (e: ErrorMessageOutput) => this.statusMessageService.showErrorToast(e.error.message));
  }

  /**
   * Get the selected feedback session
   */
  getFeedbackSession(): void {
    this.isSearching = true;

    // Get the publish status and time of feedback session
    this.feedbackSessionsService
      .getFeedbackSession({
        courseId: this.formModel.courseId,
        feedbackSessionName: this.formModel.feedbackSessionName,
        intent: Intent.INSTRUCTOR_RESULT,
      })
      .subscribe((feedbackSession: FeedbackSession) => {
        this.publishedTime = feedbackSession.resultVisibleFromTimestamp || this.publishedTime;

        // Feedback session is not published, do not need to search.
        if (feedbackSession.publishStatus !== FeedbackSessionPublishStatus.PUBLISHED) {
          this.isSearching = false;
          this.statusMessageService.showErrorToast('This feedback session is not published');
          return;
        }

        const earliestSearchTime: number = Date.now() - this.LOGS_RETENTION_PERIOD_IN_MILLISECONDS;
        // Feedback session is published more than 30 days ago, open a dialog.
        if (this.publishedTime < earliestSearchTime) {
          this.openModal();
          return;
        }

        // Published time of the feedback session is less than 30 days ago, update the form model to
        // search only up till the publish date.
        const publishedDate: Date = new Date(this.publishedTime);
        this.notViewedSince = publishedDate.getTime();
        this.logsDateFrom = {
          year: publishedDate.getFullYear(),
          month: publishedDate.getMonth() + 1,
          day: publishedDate.getDate(),
        };
        this.logsTimeFrom = {
          hour: publishedDate.getHours(),
          minute: publishedDate.getMinutes(),
        };
        this.search();
      });
  }

  /**
   * Search for logs of student activity
   */
  private search(): void {
    this.students = [];
    this.studentToLog = {};

    const today: Date = new Date();
    this.logsDateTo = {
      year: today.getFullYear(),
      month: today.getMonth() + 1,
      day: today.getDate(),
    };

    const logsDateFrom: number = this.timezoneService.resolveLocalDateTime(this.logsDateFrom, this.logsTimeFrom);
    const logsDateTo: number = this.timezoneService.resolveLocalDateTime(this.logsDateTo, this.logsTimeTo);

    this.logsService.searchFeedbackSessionLog({
      courseId: this.formModel.courseId,
      searchFrom: logsDateFrom.toString(),
      searchUntil: logsDateTo.toString(),
      sessionName: this.formModel.feedbackSessionName,
    }).pipe(
        finalize(() => {
          this.isSearching = false;
          this.hasResult = true;
        }),
    ).subscribe((logs: FeedbackSessionLogs) => {
      this.studentService
          .getStudentsFromCourse({ courseId: this.formModel.courseId })
          .subscribe((students: Students) => {
            this.students.push(...students.students);

            const targetFeedbackSessionLog: FeedbackSessionLog | undefined = logs.feedbackSessionLogs
                .find((fsLog: FeedbackSessionLog) =>
                    fsLog.feedbackSessionData.feedbackSessionName === this.formModel.feedbackSessionName);
            if (!targetFeedbackSessionLog) {
              return;
            }

            targetFeedbackSessionLog.feedbackSessionLogEntries
                .filter((entry: FeedbackSessionLogEntry) =>
                    entry.feedbackSessionLogType.toString() as keyof typeof FeedbackSessionLogType
                    === 'VIEW_RESULT')
                .filter((entry: FeedbackSessionLogEntry) =>
                    !(entry.studentData.email in this.studentToLog)
                    || this.studentToLog[entry.studentData.email].timestamp < entry.timestamp)
                .forEach((entry: FeedbackSessionLogEntry) => this.studentToLog[entry.studentData.email] = entry);

            this.searchResult = this.toFeedbackSessionLogModel(targetFeedbackSessionLog);
          });
    }, (e: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(e.error.message);
    });
  }

  private openModal(): void {
    const modalContent: string = 'Published date of selected feedback session is more than 30 days ago. '
      + 'Only activities within the last 30 days will be shown.';
    const modalRef: NgbModalRef =
      this.simpleModalService.openConfirmationModal('Continue the operation?', SimpleModalType.WARNING, modalContent);
    modalRef.result.then(
      () => {
        const earliestSearchDate: Date =
          new Date(Date.now() - this.LOGS_RETENTION_PERIOD_IN_MILLISECONDS + this.ONE_MINUTE_IN_MILLISECONDS);
        this.logsDateFrom = {
          year: earliestSearchDate.getFullYear(),
          month: earliestSearchDate.getMonth() + 1,
          day: earliestSearchDate.getDate(),
        };
        this.logsTimeFrom = {
          hour: earliestSearchDate.getHours(),
          minute: earliestSearchDate.getMinutes(),
        };

        this.notViewedSince = earliestSearchDate.getTime();
        this.search();
      },
      () => { this.isSearching = false; },
    );
  }

  private toFeedbackSessionLogModel(log: FeedbackSessionLog): FeedbackSessionLogModel {
    return {
      courseId: this.formModel.courseId,
      feedbackSessionName: this.formModel.feedbackSessionName,
      publishedDate: this.timezoneService.formatToString(
          this.publishedTime, log.feedbackSessionData.timeZone, this.LOGS_DATE_TIME_FORMAT),
      logColumnsData: [
        { header: 'Status', sortBy: SortBy.RESULT_VIEW_STATUS },
        { header: 'Name', sortBy: SortBy.GIVER_NAME },
        { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
        { header: 'Section', sortBy: SortBy.SECTION_NAME },
        { header: 'Team', sortBy: SortBy.TEAM_NAME },
      ],
      logRowsData: this.students
        .map((student: Student) => {
          let status: string;
          let dataStyle: string = 'font-family:monospace; white-space:pre;';
          if (student.email in this.studentToLog) {
            const entry: FeedbackSessionLogEntry = this.studentToLog[student.email];
            status = `Viewed last at   ${this.timezoneService.formatToString(entry.timestamp, log.feedbackSessionData.timeZone, this.LOGS_DATE_TIME_FORMAT)}`;
          } else {
            status = `Not viewed since ${this.timezoneService.formatToString(this.notViewedSince, log.feedbackSessionData.timeZone, this.LOGS_DATE_TIME_FORMAT)}`;
            dataStyle += 'color:red;';
          }
          return [
            { value: status,
              style: dataStyle },
            { value: student.name },
            { value: student.email },
            { value: student.sectionName },
            { value: student.teamName },
          ];
        }),
    };
  }
}
