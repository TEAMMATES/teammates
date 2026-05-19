import { NgClass, KeyValuePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDateParserFormatter, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { NotificationEditFormMode, NotificationEditFormModel } from './notification-edit-form-model';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { ApiConst } from '../../../../types/api-const';
import { NotificationTargetUser, NotificationStyle } from '../../../../types/api-request';
import { getDefaultTimeFormat, getDefaultDateFormat } from '../../../../types/datetime-const';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { DatePickerFormatter } from '../../../components/datepicker/datepicker-formatter';
import { DatepickerComponent } from '../../../components/datepicker/datepicker.component';
import { RichTextEditorComponent } from '../../../components/rich-text-editor/rich-text-editor.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { NotificationStyleClassPipe } from '../../../components/teammates-common/notification-style-class.pipe';
import { NotificationStyleDescriptionPipe } from '../../../components/teammates-common/notification-style-description.pipe';
import { TimepickerComponent } from '../../../components/timepicker/timepicker.component';

@Component({
  selector: 'tm-notification-edit-form',
  templateUrl: './notification-edit-form.component.html',
  styleUrls: ['./notification-edit-form.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: DatePickerFormatter }],
  imports: [
    NgbTooltip,
    FormsModule,
    NgClass,
    RichTextEditorComponent,
    DatepickerComponent,
    TimepickerComponent,
    AjaxLoadingComponent,
    KeyValuePipe,
    NotificationStyleDescriptionPipe,
    NotificationStyleClassPipe,
  ],
})
export class NotificationEditFormComponent {
  private simpleModalService = inject(SimpleModalService);

  NotificationEditFormMode = NotificationEditFormMode;
  NotificationStyle = NotificationStyle;
  NotificationTargetUser = NotificationTargetUser;

  NOTIFICATION_TITLE_MAX_LENGTH = ApiConst.NOTIFICATION_TITLE_MAX_LENGTH;

  @Input()
  guessTimezone = 'UTC';

  @Input()
  model: NotificationEditFormModel = {
    notificationId: '',

    startTime: getDefaultTimeFormat(),
    startDate: getDefaultDateFormat(),
    endTime: getDefaultTimeFormat(),
    endDate: getDefaultDateFormat(),

    style: NotificationStyle.SUCCESS,
    targetUser: NotificationTargetUser.GENERAL,

    title: '',
    message: '',

    isSaving: false,
    isDeleting: false,
  };

  @Output()
  modelChange: EventEmitter<NotificationEditFormModel> = new EventEmitter();

  @Input()
  formMode: NotificationEditFormMode = NotificationEditFormMode.ADD;

  @Input()
  courseCandidates: Notification[] = [];

  // event emission
  @Output()
  addNewNotificationEvent = new EventEmitter<void>();

  @Output()
  editExistingNotificationEvent = new EventEmitter<void>();

  @Output()
  cancelEditingNotificationEvent = new EventEmitter<void>();

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Handles submit button click event.
   */
  submitFormHandler(): void {
    // resolve local date time to timestamp
    if (this.formMode === NotificationEditFormMode.ADD) {
      this.addNewNotificationEvent.emit();
    }

    if (this.formMode === NotificationEditFormMode.EDIT) {
      this.editExistingNotificationEvent.emit();
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
      .result.then(() => {
        this.cancelEditingNotificationEvent.emit();
      });
  }
  /**
  * Check if notification is visible and been shown to users.
  * A notification is shown if current time is after its start time.
  */
  isNotificationActive(): boolean {
    const { startDate, startTime } = this.model;

    if (!startDate || !startTime) {
      return false;
    }

    try {
      const year = startDate.year;
      // Default date from getDefaultDateFormat() is { year: 0, month: 0, day: 0 }.
      // new Date(0, -1, 0) evaluates to the year 1899, which makes it seem active.
      if (year === 0) {
        return false;
      }

      const month = startDate.month - 1;
      const day = startDate.day;

      let hours: number;
      let minutes: number;

      if (typeof startTime === 'string') {
        const timeParts = (startTime as string).split(':');
        hours = parseInt(timeParts[0], 10);
        minutes = parseInt(timeParts[1], 10);
      } else {
        hours = (startTime as any).hour ?? (startTime as any).hours ?? 0;
        minutes = (startTime as any).minute ?? (startTime as any).minutes ?? 0;
      }

      if (isNaN(hours) || isNaN(minutes) || isNaN(year) || isNaN(month) || isNaN(day)) {
        console.warn('Invalid date/time values:', { year, month, day, hours, minutes });
        return false;
      }

      const startTimestamp = new Date(year, month, day, hours, minutes, 0, 0).getTime();
      const now = Date.now();

      return now > startTimestamp;
    } catch (error) {
      console.error('Error calculating notification active state:', error);
      return false;
    }
  }
}
