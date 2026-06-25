import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import moment from 'moment-timezone';
import { SessionEditFormMode, SessionEditFormModel } from './session-edit-form-model';
import { SimpleModalService } from '../../../services/simple-modal.service';
import {
  Course,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../types/field-validator';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { DatetimepickerComponent } from '../datetimepicker/datetimepicker.component';
import { RichTextEditorComponent } from '../rich-text-editor/rich-text-editor.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { PublishStatusNamePipe } from '../teammates-common/publish-status-name.pipe';
import { SubmissionStatusNamePipe } from '../teammates-common/submission-status-name.pipe';
import { RouterLink } from '@angular/router';
import { TemplateSession } from '../../../data/template-sessions';

/**
 * Form to Add/Edit feedback sessions.
 */
@Component({
  selector: 'tm-session-edit-form',
  templateUrl: './session-edit-form.component.html',
  styleUrls: ['./session-edit-form.component.scss'],
  imports: [
    FormsModule,
    RouterLink,
    AjaxLoadingComponent,
    NgbTooltip,
    NgClass,
    RichTextEditorComponent,
    DatetimepickerComponent,
    SubmissionStatusNamePipe,
    PublishStatusNamePipe,
    NgbCollapse,
  ],
})
export class SessionEditFormComponent {
  private readonly simpleModalService = inject(SimpleModalService);

  private readonly nowMinute: number = moment().second(0).millisecond(0).valueOf();

  // enum
  SessionEditFormMode!: typeof SessionEditFormMode;
  SessionVisibleSetting!: typeof SessionVisibleSetting;
  ResponseVisibleSetting!: typeof ResponseVisibleSetting;

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH!: number;

  @Input()
  model: SessionEditFormModel = {
    feedbackSessionId: '',
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTimestamp: Date.now(),
    submissionEndTimestamp: Date.now(),
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTimestamp: Date.now(),

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTimestamp: Date.now(),

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
  isCopyOtherSessionLoading = false;

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

  constructor() {
    this.SessionEditFormMode = SessionEditFormMode;
    this.SessionVisibleSetting = SessionVisibleSetting;
    this.ResponseVisibleSetting = ResponseVisibleSetting;
    this.FEEDBACK_SESSION_NAME_MAX_LENGTH = FEEDBACK_SESSION_NAME_MAX_LENGTH;
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: unknown): void {
    this.modelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Triggers the change of the model when the submission opening time changes.
   *
   * Pulls the custom session visibility time back so that it never occurs after the submission opening
   * time. The picker guarantees the incoming value already satisfies the configured range.
   */
  triggerSubmissionOpeningTimestampChange(timestamp: number): void {
    const updatedModel: SessionEditFormModel = {
      ...this.model,
      submissionStartTimestamp: timestamp,
    };
    if (updatedModel.customSessionVisibleTimestamp > timestamp) {
      updatedModel.customSessionVisibleTimestamp = timestamp;
    }
    this.modelChange.emit(updatedModel);
  }

  /**
   * Handles course Id change event.
   *
   * Used in ADD mode.
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
   * Gets the minimum timestamp for a session to be opened.
   *
   * The minimum session opening datetime is 2 hours before now.
   */
  get minTimestampForSubmissionStart(): number {
    return moment(this.nowMinute).tz(this.model.timeZone).subtract(2, 'hours').valueOf();
  }

  /**
   * Gets the maximum timestamp for a session to be opened.
   *
   * The maximum session opening datetime is 23:59 of the day 12 months from now.
   */
  get maxTimestampForSubmissionStart(): number {
    return this.endOfDay(moment(this.nowMinute).tz(this.model.timeZone).add(12, 'months'));
  }

  /**
   * Gets the minimum timestamp for a session to be closed.
   *
   * The minimum session closing datetime is the session opening datetime or 1 hour before now, whichever
   * is later.
   */
  get minTimestampForSubmissionEnd(): number {
    const oneHourBeforeNow: number = moment(this.nowMinute).tz(this.model.timeZone).subtract(1, 'hours').valueOf();
    return Math.max(this.model.submissionStartTimestamp, oneHourBeforeNow);
  }

  /**
   * Gets the maximum timestamp for a session to be closed.
   *
   * The maximum session closing datetime is 23:59 of the day 12 months from now.
   */
  get maxTimestampForSubmissionEnd(): number {
    return this.endOfDay(moment(this.nowMinute).tz(this.model.timeZone).add(12, 'months'));
  }

  /**
   * Gets the minimum timestamp for a session to be visible based on the input model.
   *
   * The minimum session visible datetime is 30 days before the session opening datetime.
   */
  get minTimestampForSessionVisible(): number {
    return moment(this.model.submissionStartTimestamp).tz(this.model.timeZone).subtract(30, 'days').valueOf();
  }

  /**
   * Gets the maximum timestamp for a session to be visible based on the input model.
   *
   * The maximum session visible datetime is on the response visible datetime.
   */
  get maxTimestampForSessionVisible(): number {
    switch (this.model.responseVisibleSetting) {
      case ResponseVisibleSetting.CUSTOM:
        return Math.min(this.model.submissionStartTimestamp, this.model.customResponseVisibleTimestamp);
      default:
        return this.model.submissionStartTimestamp;
    }
  }

  /**
   * Gets the minimum timestamp for responses to be visible based on the input model.
   *
   * The minimum response visible datetime is on the session visible datetime.
   */
  get minTimestampForResponseVisible(): number {
    switch (this.model.sessionVisibleSetting) {
      case SessionVisibleSetting.AT_OPEN:
        return this.model.submissionStartTimestamp;
      case SessionVisibleSetting.CUSTOM:
        return this.model.customSessionVisibleTimestamp;
      default:
        return this.model.submissionStartTimestamp;
    }
  }

  private endOfDay(inst: moment.Moment): number {
    return inst.hour(23).minute(59).second(0).millisecond(0).valueOf();
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
    this.simpleModalService
      .openConfirmationModal(
        'Discard unsaved edit?',
        SimpleModalType.WARNING,
        'Warning: Any unsaved changes will be lost.',
      )
      .result.then(
        () => {
          this.cancelEditingSessionEvent.emit();
        },
        () => {},
      );
  }

  /**
   * Handles delete current feedback session button click event.
   */
  deleteHandler(): void {
    this.simpleModalService
      .openConfirmationModal(
        `Delete the session <strong>${this.model.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING,
        'The session will be moved to the recycle bin. This action can be reverted ' +
          'by going to the "Sessions" tab and restoring the desired session(s).',
      )
      .result.then(
        () => {
          this.deleteExistingSessionEvent.emit();
        },
        () => {},
      );
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
