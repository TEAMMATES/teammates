import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { finalize } from 'rxjs/operators';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { FeedbackSessionStats, OngoingSession, OngoingSessions } from '../../../types/api-output';
import { DateFormat, TimeFormat, getDefaultDateFormat, getLatestTimeFormat } from '../../../types/datetime-const';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface OngoingSessionModel {
  ongoingSession: OngoingSession;
  startTimeString: string;
  endTimeString: string;
  responseRate?: string;
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

  constructor(private timezoneService: TimezoneService,
              private statusMessageService: StatusMessageService,
              private feedbackSessionsService: FeedbackSessionsService) {}

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
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Gets the response rate of a feedback session.
   */
  getResponseRate(institute: string, courseId: string, feedbackSessionName: string, event: any): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.feedbackSessionsService.loadSessionStatistics(courseId, feedbackSessionName)
        .subscribe({
          next: (resp: FeedbackSessionStats) => {
            const sessions: OngoingSessionModel[] = this.sessions[institute].filter((session: OngoingSessionModel) =>
                session.ongoingSession.courseId === courseId
                && session.ongoingSession.feedbackSessionName === feedbackSessionName,
            );
            if (sessions.length) {
              sessions[0].responseRate = `${resp.submittedTotal} / ${resp.expectedTotal}`;
            }
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  updateDisplayedTimes(): void {
    for (const sessions of Object.values(this.sessions)) {
      for (const session of sessions) {
        session.startTimeString = this.showDateFromMillis(session.ongoingSession.startTime);
        session.endTimeString = this.showDateFromMillis(session.ongoingSession.endTime);
      }
    }
  }

}
