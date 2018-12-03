import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { finalize, map, switchMap, tap } from 'rxjs/operators';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  LOCAL_DATE_TIME_FORMAT,
  LocalDateTimeAmbiguityStatus,
  LocalDateTimeInfo,
  TimezoneService,
} from '../../../services/timezone.service';
import {
  DateFormat,
  SessionEditFormMode, SessionEditFormModel, TimeFormat,
} from '../../components/session-edit-form/session-edit-form-model';
import { FeedbackSession, ResponseVisibleSetting, SessionVisibleSetting } from '../../feedback-session';
import { ErrorMessageOutput } from '../../message-output';

/**
 * Instructor feedback session edit page.
 */
@Component({
  selector: 'tm-instructor-session-edit-page',
  templateUrl: './instructor-session-edit-page.component.html',
  styleUrls: ['./instructor-session-edit-page.component.scss'],
})
export class InstructorSessionEditPageComponent implements OnInit {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;

  // url param
  user: string = '';
  courseId: string = '';
  feedbackSessionName: string = '';

  // models
  sessionEditFormModel: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTime: { hour: 0, minute: 0 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 0, minute: 0 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 0, minute: 0 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 0, minute: 0 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: '',
    publishStatus: '',

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    isSaving: false,
    isEditable: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  constructor(private route: ActivatedRoute, private router: Router, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService, private navigationService: NavigationService,
              private timezoneService: TimezoneService) {
    this.timezoneService.getTzVersion();
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;

      this.loadFeedbackSession();
    });
  }

  /**
   * Loads a feedback session.
   */
  loadFeedbackSession(): void {
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };
    this.httpRequestService.get('/session', paramMap)
        .subscribe((feedbackSession: FeedbackSession) => {
          this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Gets the {@code sessionEditFormModel} with {@link FeedbackSession} entity.
   */
  getSessionEditFormModel(feedbackSession: FeedbackSession): SessionEditFormModel {
    const submissionStart: {date: DateFormat; time: TimeFormat} =
        this.getDateTimeAtTimezone(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone);

    const submissionEnd: {date: DateFormat; time: TimeFormat} =
        this.getDateTimeAtTimezone(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone);

    const model: SessionEditFormModel = {
      courseId: feedbackSession.courseId,
      timeZone: feedbackSession.timeZone,
      courseName: 'USE COURSE API',
      feedbackSessionName: feedbackSession.feedbackSessionName,
      instructions: feedbackSession.instructions,

      submissionStartTime: submissionStart.time,
      submissionStartDate: submissionStart.date,
      submissionEndTime: submissionEnd.time,
      submissionEndDate: submissionEnd.date,
      gracePeriod: feedbackSession.gracePeriod,

      sessionVisibleSetting: feedbackSession.sessionVisibleSetting,
      customSessionVisibleTime: { hour: 0, minute: 0 },
      customSessionVisibleDate: { year: 0, month: 0, day: 0 },

      responseVisibleSetting: feedbackSession.responseVisibleSetting,
      customResponseVisibleTime: { hour: 0, minute: 0 },
      customResponseVisibleDate: { year: 0, month: 0, day: 0 },

      submissionStatus: feedbackSession.submissionStatus,
      publishStatus: feedbackSession.publishStatus,

      isClosingEmailEnabled: feedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: feedbackSession.isPublishedEmailEnabled,

      isSaving: false,
      isEditable: false,
      hasVisibleSettingsPanelExpanded: feedbackSession.sessionVisibleSetting !== SessionVisibleSetting.AT_OPEN
          || feedbackSession.responseVisibleSetting !== ResponseVisibleSetting.LATER,
      hasEmailSettingsPanelExpanded: !feedbackSession.isClosingEmailEnabled || !feedbackSession.isPublishedEmailEnabled
    };

    if (feedbackSession.customSessionVisibleTimestamp) {
      const customSessionVisible: {date: DateFormat; time: TimeFormat} =
          this.getDateTimeAtTimezone(feedbackSession.customSessionVisibleTimestamp, feedbackSession.timeZone);
      model.customSessionVisibleTime = customSessionVisible.time;
      model.customSessionVisibleDate = customSessionVisible.date;
    }

    if (feedbackSession.customResponseVisibleTimestamp) {
      const customResponseVisible: {date: DateFormat; time: TimeFormat} =
          this.getDateTimeAtTimezone(feedbackSession.customResponseVisibleTimestamp, feedbackSession.timeZone);
      model.customResponseVisibleTime = customResponseVisible.time;
      model.customResponseVisibleDate = customResponseVisible.date;
    }

    return model;
  }

  /**
   * Get the local date and time of timezone from timestamp.
   */
  private getDateTimeAtTimezone(timestamp: number, timeZone: string): {date: DateFormat; time: TimeFormat} {
    const momentInstance: any = moment(timestamp).tz(timeZone);
    const date: DateFormat = {
      year: momentInstance.year(),
      month: momentInstance.month() + 1, // moment return 0-11 for month
      day: momentInstance.date(),
    };
    const time: TimeFormat = {
      minute: momentInstance.minute(),
      hour: momentInstance.hour(),
    };
    return {
      date,
      time,
    };
  }

  /**
   * Handles editing existing session event.
   */
  editExistingSessionHandler(): void {
    this.sessionEditFormModel.isSaving = true;
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };

    forkJoin(
        this.resolveLocalDateTime(this.sessionEditFormModel.submissionStartDate,
            this.sessionEditFormModel.submissionStartTime, this.sessionEditFormModel.timeZone,
            'Submission opening time'),
        this.resolveLocalDateTime(this.sessionEditFormModel.submissionEndDate,
            this.sessionEditFormModel.submissionEndTime, this.sessionEditFormModel.timeZone,
            'Submission closing time'),
        this.sessionEditFormModel.sessionVisibleSetting === SessionVisibleSetting.CUSTOM ?
            this.resolveLocalDateTime(this.sessionEditFormModel.customSessionVisibleDate,
                this.sessionEditFormModel.customSessionVisibleTime, this.sessionEditFormModel.timeZone,
                'Session visible time')
            : of(0),
        this.sessionEditFormModel.responseVisibleSetting === ResponseVisibleSetting.CUSTOM ?
            this.resolveLocalDateTime(this.sessionEditFormModel.customResponseVisibleDate,
                this.sessionEditFormModel.customResponseVisibleTime, this.sessionEditFormModel.timeZone,
                'Response visible time')
            : of(0),
    ).pipe(
        switchMap((vals: number[]) => {
          return this.httpRequestService.put('/session', paramMap, {
            instructions: this.sessionEditFormModel.instructions,

            submissionStartTimestamp: vals[0],
            submissionEndTimestamp: vals[1],
            gracePeriod: this.sessionEditFormModel.gracePeriod,

            sessionVisibleSetting: this.sessionEditFormModel.sessionVisibleSetting,
            customSessionVisibleTimestamp: vals[2],

            responseVisibleSetting: this.sessionEditFormModel.responseVisibleSetting,
            customResponseVisibleTimestamp: vals[3],

            isClosingEmailEnabled: this.sessionEditFormModel.isClosingEmailEnabled,
            isPublishedEmailEnabled: this.sessionEditFormModel.isPublishedEmailEnabled,
          });
        }),
        finalize(() => {
          this.sessionEditFormModel.isSaving = false;
        }),
    ).subscribe((feedbackSession: FeedbackSession) => {
      this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession);

      this.statusMessageService.showSuccessMessage('The feedback session has been updated.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Resolves the local date time to an UNIX timestamp.
   */
  private resolveLocalDateTime(
      date: DateFormat, time: TimeFormat, timeZone: string, fieldName: string): Observable<number> {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);

    const localDateTime: string = inst.format(LOCAL_DATE_TIME_FORMAT);
    return this.timezoneService.getResolveLocalDateTime(localDateTime, timeZone).pipe(
        tap((info: LocalDateTimeInfo) => {
          const DATE_FORMAT_WITHOUT_ZONE_INFO: any = 'ddd, DD MMM, YYYY hh:mm A';
          const DATE_FORMAT_WITH_ZONE_INFO: any = "ddd, DD MMM, YYYY hh:mm A z ('UTC'Z)";

          switch (info.resolvedStatus) {
            case LocalDateTimeAmbiguityStatus.UNAMBIGUOUS:
              break;
            case LocalDateTimeAmbiguityStatus.GAP:
              this.statusMessageService.showWarningMessage(
                  `The ${fieldName}, ${moment.format(DATE_FORMAT_WITHOUT_ZONE_INFO)},
                   falls within the gap period when clocks spring forward at the start of DST.
                   It was resolved to ${moment(info.resolvedTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}.`);
              break;
            case LocalDateTimeAmbiguityStatus.OVERLAP:
              this.statusMessageService.showWarningMessage(
                  `The ${fieldName}, ${moment.format(DATE_FORMAT_WITHOUT_ZONE_INFO)},
                   falls within the overlap period when clocks fall back at the end of DST.
                   It can refer to ${moment(info.earlierInterpretationTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}
                   or ${moment(info.laterInterpretationTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)} .
                   It was resolved to %s.`,
              );
              break;
            default:
          }
        }),
        map((info: LocalDateTimeInfo) => info.resolvedTimestamp));
  }

  /**
   * Handles deleting current feedback session.
   */
  deleteExistingSessionHandler(): void {
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };
    this.httpRequestService.put('/bin/session', paramMap).subscribe(() => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/sessions',
          'The feedback session has been deleted. You can restore it from the deleted sessions table below.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
