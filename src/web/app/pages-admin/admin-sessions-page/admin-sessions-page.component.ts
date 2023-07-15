import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { finalize } from 'rxjs/operators';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { FeedbackSessionStats, OngoingSession, OngoingSessions } from '../../../types/api-output';
import { DateFormat, TimeFormat, getDefaultDateFormat, getLatestTimeFormat } from '../../../types/datetime-const';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface OngoingSessionModel {
  ongoingSession: OngoingSession;
  startTimeString: string;
  endTimeString: string;
  responseRate?: string;
}
interface SortableTable {
  institute: string;
  columns: ColumnData[];
  rows: SortableTableCellData[][];
}
/**
 * Admin sessions page.
 */
@Component({
  selector: 'tm-admin-sessions-page',
  templateUrl: './admin-sessions-page.component.html',
  styleUrls: ['./admin-sessions-page.component.scss'],
  animations: [collapseAnim],
})
export class AdminSessionsPageComponent implements OnInit {

  totalOngoingSessions: number = 0;
  totalOpenSessions: number = 0;
  totalClosedSessions: number = 0;
  totalAwaitingSessions: number = 0;
  totalInstitutes: number = 0;
  sessions: Record<string, OngoingSessionModel[]> = {};

  // Tracks the whether the panel of an institute has been opened
  institutionPanelsStatus: Record<string, boolean> = {};

  showFilter: boolean = false;
  timezones: string[] = [];
  filterTimezone: string = '';
  tableTimezone: string = '';
  startDate: DateFormat = getDefaultDateFormat();
  startTime: TimeFormat = getLatestTimeFormat();
  endDate: DateFormat = getDefaultDateFormat();
  endTime: TimeFormat = getLatestTimeFormat();

  timezoneString: string = '';
  startTimeString: string = '';
  endTimeString: string = '';

  isLoadingOngoingSessions: boolean = false;

  SortBy: typeof SortBy = SortBy;
  sortableTable: SortableTable[] = [];
  column: ColumnData[] = [
    { header: 'Status' },
    { header: '[Course ID] Session Name' },
    { header: 'Response Rate' },
    { header: 'Start Time', sortBy: SortBy.SESSION_START_DATE },
    { header: 'End Time', sortBy: SortBy.SESSION_END_DATE },
    { header: 'Creator' },
];

  selectedSort:SortBy = SortBy.NONE;
  constructor(private timezoneService: TimezoneService,
              private statusMessageService: StatusMessageService,
              private feedbackSessionsService: FeedbackSessionsService,
              private tableComparatorService: TableComparatorService) {}

  ngOnInit(): void {
    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
    this.filterTimezone = this.timezoneService.guessTimezone();
    this.tableTimezone = this.timezoneService.guessTimezone();

    const now: moment.Moment = moment();
    this.startDate = {
      year: now.year(),
      month: now.month() + 1,
      day: now.date(),
    };
    this.startTime = {
      hour: now.hour(),
      minute: now.minute(),
    };
    this.endTime = {
      hour: now.hour(),
      minute: now.minute(),
    };

    const nextWeek: moment.Moment = moment(now).add(1, 'weeks');
    this.endDate = {
      year: nextWeek.year(),
      month: nextWeek.month() + 1,
      day: nextWeek.date(),
    };

    this.getFeedbackSessions();
  }
  /**
   * Populates the Sortable Table Data to be displayed
   */
  populateSortableTable(): void {
  this.sortableTable = [];
  Object.entries(this.sessions).forEach((kvp) => {
    this.sortableTable.push({
      institute:kvp[0],
      columns:this.column,
      rows: kvp[1].map((session):SortableTableCellData[]=>{
        return [
          { displayValue: session.ongoingSession.sessionStatus },
          { displayValue: '['+session.ongoingSession.courseId+'] '+session.ongoingSession.feedbackSessionName },
          { displayValue: ''},
          { value: session.startTimeString },
          { value: session.endTimeString },
          { displayValue: session.ongoingSession.creatorEmail },
        ]
      })
    });
    this.getResponseRate(kvp[0]);
    
  })
}

  /**
   * Opens all institution panels.
   */
  openAllInstitutions(): void {
    for (const institution of Object.keys(this.institutionPanelsStatus)) {
      this.institutionPanelsStatus[institution] = true;
    }
  }

  /**
   * Closes all institution panels.
   */
  closeAllInstitutions(): void {
    for (const institution of Object.keys(this.institutionPanelsStatus)) {
      this.institutionPanelsStatus[institution] = false;
    }
  }

  /**
   * Converts milliseconds to readable date format.
   */
  showDateFromMillis(millis: number): string {
    return this.timezoneService.formatToString(millis, this.tableTimezone, 'ddd, DD MMM YYYY, hh:mm a');
  }

  /**
   * Gets the feedback sessions which have opening time satisfying the query range.
   */
  getFeedbackSessions(): void {
    const timezone: string = this.filterTimezone;
    const startTime: number = this.timezoneService.resolveLocalDateTime(
        { year: this.startDate.year, month: this.startDate.month, day: this.startDate.day },
        { hour: this.startTime.hour, minute: this.startTime.minute },
        timezone);
    const endTime: number = this.timezoneService.resolveLocalDateTime(
        { year: this.endDate.year, month: this.endDate.month, day: this.endDate.day },
        { hour: this.endTime.hour, minute: this.endTime.minute },
        timezone);
    const displayFormat: string = 'ddd, DD MMM YYYY, hh:mm a';
    this.startTimeString = this.timezoneService.formatToString(startTime, timezone, displayFormat);
    this.endTimeString = this.timezoneService.formatToString(endTime, timezone, displayFormat);
    this.timezoneString = this.filterTimezone;
    this.isLoadingOngoingSessions = true;

    this.feedbackSessionsService.getOngoingSessions(startTime, endTime)
        .pipe(finalize(() => {
          this.isLoadingOngoingSessions = false;
        }))
        .subscribe({
          next: (resp: OngoingSessions) => {
            this.totalOngoingSessions = resp.totalOngoingSessions;
            this.totalOpenSessions = resp.totalOpenSessions;
            this.totalClosedSessions = resp.totalClosedSessions;
            this.totalAwaitingSessions = resp.totalAwaitingSessions;
            this.totalInstitutes = resp.totalInstitutes;
            Object.keys(resp.sessions).forEach((key: string) => {
              this.sessions[key] = resp.sessions[key].map((ongoingSession: OngoingSession) => {
                return {
                  ongoingSession,
                  startTimeString: this.showDateFromMillis(ongoingSession.startTime),
                  endTimeString: this.showDateFromMillis(ongoingSession.endTime),
                };
              });
            });
            
            this.institutionPanelsStatus = {};
            for (const institution of Object.keys(resp.sessions)) {
              this.institutionPanelsStatus[institution] = true;
            }
            this.populateSortableTable();
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Gets the response rate of all ongoing sessions in a course.
   */
  getResponseRate(institute: string): void {
    this.sessions[institute].forEach((session)=>{
      this.feedbackSessionsService.loadSessionStatistics(session.ongoingSession.courseId,session.ongoingSession.feedbackSessionName)
      .subscribe({
        next: (resp: FeedbackSessionStats) => {
            this.sortableTable.forEach((data:SortableTable)=>{
              data.rows.forEach((cellData:SortableTableCellData[])=>{
                  if(cellData[1].displayValue==='['+session.ongoingSession.courseId+'] '+session.ongoingSession.feedbackSessionName)
                  cellData[2].displayValue=`${resp.submittedTotal} / ${resp.expectedTotal}`
              })
            })
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    })
  }

  updateDisplayedTimes(): void {
    for (const sessions of Object.values(this.sessions)) {
      for (const session of sessions) {
        session.startTimeString = this.showDateFromMillis(session.ongoingSession.startTime);
        session.endTimeString = this.showDateFromMillis(session.ongoingSession.endTime);
      }
    }
  }
  
  sortCoursesBy(by:SortBy):void{
    this.selectedSort=by;
    const copyTable:SortableTable[]=this.sortableTable;
    copyTable.sort(this.sortPanelsBy(this.selectedSort))
    this.sortableTable=copyTable;
  }

  sortPanelsBy(by: SortBy): ((a: SortableTable, b: SortableTable)
  => number) {
  return ((a: SortableTable, b: SortableTable): number => {
    let strA: string;
    let strB: string;
    let sortOrder: SortOrder; 
    switch (by) {
      case SortBy.INSTITUTION_NAME:
        strA = a.institute;
        strB = b.institute
        sortOrder = SortOrder.ASC
        break;
      case SortBy.INSTITUTION_SESSIONS_TOTAL:
        strA = String(a.rows.length);
        strB = String(b.rows.length);
        sortOrder = SortOrder.DESC
        break;
      default:
        strA = '';
        strB = '';
        sortOrder=SortOrder.ASC;
    }
    return this.tableComparatorService.compare(by, sortOrder, strA, strB);
  });
}
}
