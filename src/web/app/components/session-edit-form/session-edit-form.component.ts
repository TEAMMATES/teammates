import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbCalendar, NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { SessionEditFormMode, SessionEditFormModel } from './session-edit-form-model';
import { DateTimeService } from '../../../services/datetime.service';
import { TemplateSession } from '../../../services/feedback-sessions.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import {
  Course,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import {
  DateFormat,
  TimeFormat,
  getDefaultDateFormat,
  getDefaultTimeFormat,
  getLatestTimeFormat,
} from '../../../types/datetime-const';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../types/field-validator';
import { DatePickerFormatter } from '../datepicker/datepicker-formatter';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * Form to Add/Edit feedback sessions.
 */
@Component({
  selector: 'tm-session-edit-form',
  templateUrl: './session-edit-form.component.html',
  styleUrls: ['./session-edit-form.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: DatePickerFormatter }],
  animations: [collapseAnim],
})
export class SessionEditFormComponent {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  SessionVisibleSetting: typeof SessionVisibleSetting = SessionVisibleSetting;
  ResponseVisibleSetting: typeof ResponseVisibleSetting = ResponseVisibleSetting;

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH: number = FEEDBACK_SESSION_NAME_MAX_LENGTH;

  @Input()
  model: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTime: getDefaultTimeFormat(),
    submissionStartDate: getDefaultDateFormat(),
    submissionEndTime: getDefaultTimeFormat(),
    submissionEndDate: getDefaultDateFormat(),
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: getDefaultTimeFormat(),
    customSessionVisibleDate: getDefaultDateFormat(),

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: getDefaultTimeFormat(),
    customResponseVisibleDate: getDefaultDateFormat(),

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: true,
    isDeleting: false,
    isCopying: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  @Output()
  modelChange: EventEmitter<SessionEditFormModel> = new EventEmitter();

  @Input()
  formMode: SessionEditFormMode = SessionEditFormMode.ADD;

  // add mode specific
  @Input()
  courseCandidates: Course[] = [];

  @Input()
  templateSessions: TemplateSession[] = [];

  @Input()
  isCopyOtherSessionLoading: boolean = false;

  // event emission
  @Output()
  addNewSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  editExistingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  cancelEditingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  deleteExistingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  copyCurrentSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  copyOtherSessionsEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  closeEditFormEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(private simpleModalService: SimpleModalService,
              private datetimeService: DateTimeService,
              public calendar: NgbCalendar) { }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    if (field === 'submissionStartDate' || field === 'submissionStartTime') {
      this.adjustSessionVisibilityTime(data, field);
    }
    this.modelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Adjusts session visibility time to ensure it does not occur after submission opening time.
   */
  adjustSessionVisibilityTime(value: any, field: string): void {
    const submissionDateTime = this.combineDateAndTime(
      field === 'submissionStartDate' ? value : this.model.submissionStartDate,
      field === 'submissionStartTime' ? value : this.model.submissionStartTime,
    );

    const visibilityDateTime = this.combineDateAndTime(
      this.model.customSessionVisibleDate,
      this.model.customSessionVisibleTime,
    );

    if (submissionDateTime.isBefore(visibilityDateTime)) {
      if (field === 'submissionStartDate') {
        this.model.customSessionVisibleDate = value;
      } else {
        this.model.customSessionVisibleTime = value;
      }
    }
  }

  /**
   * Combines date and time into a single moment instance.
   */
  combineDateAndTime(date: DateFormat, time: TimeFormat): moment.Moment {
    return moment.tz({
      year: date.year,
      month: date.month - 1,
      day: date.day,
      hour: time.hour,
      minute: time.minute,
    }, this.model.timeZone);
  }

  /**
   * Triggers the change of the model when the submission opening date changes.
   */
  triggerSubmissionOpeningDateModelChange(field: string, date: DateFormat): void {
    const minDate: DateFormat = this.minDateForSubmissionStart;
    const minTime: TimeFormat = this.minTimeForSubmissionStart;

    // Case where date is same as earliest date and time is earlier than earliest possible time
    if (DateTimeService.compareDateFormat(date, minDate) === 0
    && DateTimeService.compareTimeFormat(this.model.submissionStartTime, minTime) === -1) {
      this.configureSubmissionOpeningTime(minTime);
      this.model.submissionStartTime = minTime;
    }

    this.adjustSessionVisibilityTime(date, field);
    this.triggerModelChange(field, date);
  }

  /**
   * Configures the time for the submission opening time.
   */
  configureSubmissionOpeningTime(time : TimeFormat) : void {
    if (time.hour === 23 && time.minute > 0) {
      time.minute = 59;
    } else if (time.hour < 23 && time.minute > 0) {
      // Case where minutes is not 0 since the earliest time with 0 minutes is the hour before
      time.hour += 1;
      time.minute = 0;
    }
  }

  /**
   * Triggers the change of the model when the submission opening time changes.
   */
  triggerSubmissionOpeningTimeModelChange(field: string, time: TimeFormat): void {
    const date: DateFormat = this.model.submissionStartDate;
    const sessionDate: DateFormat = this.model.customSessionVisibleDate;
    const sessionTime: TimeFormat = this.model.customSessionVisibleTime;

    if (DateTimeService.compareDateFormat(date, sessionDate) === 0
        && DateTimeService.compareTimeFormat(time, sessionTime) === -1) {
      this.configureSessionVisibleDateTime(date, time);
    }

    this.triggerModelChange(field, time);
  }

  /**
   * Configures the session visible date and time to ensure it is not after submission opening time.
   */
  configureSessionVisibleDateTime(date: DateFormat, time: TimeFormat): void {
    const sessionDate: DateFormat = this.model.customSessionVisibleDate;
    const sessionTime: TimeFormat = this.model.customSessionVisibleTime;

    if (DateTimeService.compareDateFormat(date, sessionDate) === -1) {
      this.model.customSessionVisibleDate = date;
    } else if (DateTimeService.compareDateFormat(date, sessionDate) === 0
               && DateTimeService.compareTimeFormat(time, sessionTime) === -1) {
      this.model.customSessionVisibleTime = time;
    }
  }

  /**
   * Handles course Id change event.
   *
   * <p>Used in ADD mode.
   */
  courseIdChangeHandler(newCourseId: string): void {
    const course: Course | undefined = this.courseCandidates.find((c: Course) => c.courseId === newCourseId);

    if (course) {
      this.modelChange.emit({
        ...this.model,
        courseId: newCourseId,
        courseName: course.courseName,
        timeZone: course.timeZone,
      });
    }
  }

  /**
   * Gets the minimum date for a session to be opened.
   *
   * <p> The minimum session opening datetime is 2 hours before now.
   */
  get minDateForSubmissionStart(): DateFormat {
    const twoHoursBeforeNow = moment().tz(this.model.timeZone).subtract(2, 'hours');
    return this.datetimeService.getDateInstance(twoHoursBeforeNow);
  }

  /**
   * Gets the minimum time for a session to be opened.
   *
   * <p> The minimum session opening datetime is 2 hours before now.
   */
  get minTimeForSubmissionStart(): TimeFormat {
    const twoHoursBeforeNow = moment().tz(this.model.timeZone).subtract(2, 'hours');
    return this.datetimeService.getTimeInstance(twoHoursBeforeNow);
  }

  /**
   * Gets the maximum date for a session to be opened.
   *
   * <p> The maximum session opening datetime is 12 months from now.
   */
  get maxDateForSubmissionStart(): DateFormat {
    const twelveMonthsFromNow = moment().tz(this.model.timeZone).add(12, 'months');
    return this.datetimeService.getDateInstance(twelveMonthsFromNow);
  }

  /**
   * Gets the maximum time for a session to be opened.
   *
   * <p> The maximum session opening time is 23:59h.
   */
  get maxTimeForSubmissionStart(): TimeFormat {
    return getLatestTimeFormat();
  }

  /**
   * Gets the minimum date for a session to be closed.
   *
   * <p> The minimum session closing datetime is on session opening datetime or 1 hour before now, whichever is later.
   */
  get minDateForSubmissionEnd(): DateFormat {
    const submissionStartDate: moment.Moment =
        this.datetimeService.getMomentInstanceFromDate(this.model.submissionStartDate);
    const oneHourBeforeNow = moment().tz(this.model.timeZone).subtract(1, 'hours');

    return submissionStartDate.isAfter(oneHourBeforeNow)
        ? this.model.submissionStartDate
        : this.datetimeService.getDateInstance(oneHourBeforeNow);
  }

  /**
   * Gets the minimum time for a session to be closed.
   *
   * <p> The minimum session closing datetime is on session opening datetime or 1 hour before now, whichever is later.
   */
  get minTimeForSubmissionEnd(): TimeFormat {
    const submissionStartDate: moment.Moment =
        this.datetimeService.getMomentInstanceFromDate(this.model.submissionStartDate);
    const submissionStartTime: moment.Moment =
        this.datetimeService.getMomentInstanceFromTime(this.model.submissionStartTime);

    const submissionStartDateTime: moment.Moment = submissionStartDate.clone()
    .hours(submissionStartTime.hour())
    .minutes(submissionStartTime.minute());

    const oneHourBeforeNow = moment().tz(this.model.timeZone).subtract(1, 'hours');

    if (submissionStartDateTime.isAfter(oneHourBeforeNow)) {
      return this.datetimeService.getTimeInstance(submissionStartDateTime);
    }
    return this.datetimeService.getTimeInstance(oneHourBeforeNow);
  }

  /**
   * Gets the maximum date for a session to be closed.
   *
   * <p> The maximum session closing datetime is 12 months from now.
   */
  get maxDateForSubmissionEnd(): DateFormat {
    const twelveMonthsFromNow = moment().tz(this.model.timeZone).add(12, 'months');
    return this.datetimeService.getDateInstance(twelveMonthsFromNow);
  }

  /**
   * Gets the maximum time for a session to be closed.
   *
   * <p> The maximum session closing time is 23:59H.
   */
  get maxTimeForSubmissionEnd(): TimeFormat {
    return getLatestTimeFormat();
  }

  /**
   * Gets the minimum date for a session to be visible based on the input model.
   *
   * <p> The minimum session visible datetime is 30 days before session opening datetime.
   */
  get minDateForSessionVisible(): DateFormat {
    const thirtyDaysBeforeSubmissionStartDate: moment.Moment =
        this.datetimeService.getMomentInstanceFromDate(this.model.submissionStartDate).subtract(30, 'days');
    return this.datetimeService.getDateInstance(thirtyDaysBeforeSubmissionStartDate);
  }

  /**
   * Gets the minimum time for a session to be visible based on the input model.
   *
   * <p> The minimum session visible datetime is 30 days before session opening datetime.
   */
  get minTimeForSessionVisible(): TimeFormat {
    const submissionStartDate: moment.Moment =
        this.datetimeService.getMomentInstanceFromDate(this.model.submissionStartDate);
    const submissionStartTime: moment.Moment =
        this.datetimeService.getMomentInstanceFromTime(this.model.submissionStartTime);
    const submissionStartDateTime: moment.Moment =
        submissionStartDate.add(submissionStartTime.hour()).add(submissionStartTime.minute());
    const thirtyDaysBeforeSubmissionStartDateTime: moment.Moment =
        submissionStartDateTime.subtract(30, 'days');
    return this.datetimeService.getTimeInstance(thirtyDaysBeforeSubmissionStartDateTime);
  }

  /**
   * Gets the maximum date for a session to be visible based on the input model.
   *
   * <p> The maximum session visible datetime is on response visible datetime.
   */
  get maxDateForSessionVisible(): DateFormat {
    switch (this.model.responseVisibleSetting) {
      case ResponseVisibleSetting.LATER:
      case ResponseVisibleSetting.AT_VISIBLE:
        return this.model.submissionStartDate;
      case ResponseVisibleSetting.CUSTOM: {
        const submissionStartDate: moment.Moment =
            this.datetimeService.getMomentInstanceFromDate(this.model.submissionStartDate);
        const responseVisibleDate: moment.Moment =
            this.datetimeService.getMomentInstanceFromDate(this.model.customResponseVisibleDate);
        if (submissionStartDate.isBefore(responseVisibleDate)) {
          return this.model.submissionStartDate;
        }
        return this.model.customResponseVisibleDate;
      }
      default:
        return getDefaultDateFormat();
    }
  }

  /**
   * Gets the maximum time for a session to be visible based on the input model.
   *
   * <p> The maximum session visible datetime is on response visible datetime.
   */
  get maxTimeForSessionVisible(): TimeFormat {
    switch (this.model.responseVisibleSetting) {
      case ResponseVisibleSetting.LATER:
      case ResponseVisibleSetting.AT_VISIBLE:
        return this.model.submissionStartTime;
      case ResponseVisibleSetting.CUSTOM: {
        const submissionStartDate: moment.Moment =
            this.datetimeService.getMomentInstanceFromDate(this.model.submissionStartDate);
        const responseVisibleDate: moment.Moment =
            this.datetimeService.getMomentInstanceFromDate(this.model.customResponseVisibleDate);
        if (submissionStartDate.isBefore(responseVisibleDate)) {
          return this.model.submissionStartTime;
        }
        return this.model.customResponseVisibleTime;
      }
      default:
        return getDefaultTimeFormat();
    }
  }

  /**
   * Gets the minimum date for responses to be visible based on the input model.
   *
   * <p> The minimum response visible datetime is on session visible datetime.
   */
  get minDateForResponseVisible(): DateFormat {
    switch (this.model.sessionVisibleSetting) {
      case SessionVisibleSetting.AT_OPEN:
        return this.model.submissionStartDate;
      case SessionVisibleSetting.CUSTOM:
        return this.model.customSessionVisibleDate;
      default:
        return getDefaultDateFormat();
    }
  }

  /**
   * Gets the minimum time for responses to be visible based on the input model.
   *
   * <p> The minimum response visible datetime is on session visible datetime.
   */
  get minTimeForResponseVisible(): TimeFormat {
    switch (this.model.sessionVisibleSetting) {
      case SessionVisibleSetting.AT_OPEN:
        return this.model.submissionStartTime;
      case SessionVisibleSetting.CUSTOM:
        return this.model.customSessionVisibleTime;
      default:
        return getDefaultTimeFormat();
    }
  }

  /**
   * Handles submit button click event.
   */
  submitFormHandler(): void {
    // resolve local date time to timestamp
    if (this.formMode === SessionEditFormMode.ADD) {
      this.addNewSessionEvent.emit();
    }

    if (this.formMode === SessionEditFormMode.EDIT) {
      this.editExistingSessionEvent.emit();
    }
  }

  /**
   * Handles cancel button click event.
   */
  cancelHandler(): void {
    this.simpleModalService.openConfirmationModal('Discard unsaved edit?',
        SimpleModalType.WARNING, 'Warning: Any unsaved changes will be lost.').result.then(() => {
          this.cancelEditingSessionEvent.emit();
        }, () => {});
  }

  /**
   * Handles delete current feedback session button click event.
   */
  deleteHandler(): void {
    this.simpleModalService.openConfirmationModal(
        `Delete the session <strong>${this.model.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING,
        'The session will be moved to the recycle bin. This action can be reverted '
        + 'by going to the "Sessions" tab and restoring the desired session(s).',
    ).result.then(() => {
      this.deleteExistingSessionEvent.emit();
    }, () => {});
  }

  /**
   * Handles copy current feedback session button click event.
   */
  copyHandler(): void {
    this.copyCurrentSessionEvent.emit();
  }

  /**
   * Handles copy from other feedback sessions button click event.
   */
  copyOthersHandler(): void {
    this.copyOtherSessionsEvent.emit();
  }

  /**
   * Handles closing of the edit form.
   */
  closeEditFormHandler(): void {
    this.closeEditFormEvent.emit();
  }
}
