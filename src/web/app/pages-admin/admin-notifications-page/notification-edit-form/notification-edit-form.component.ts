import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import { NotificationEditFormMode, NotificationEditFormModel } from './notification-edit-form-model';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { ApiConst } from '../../../../types/api-const';
import { NotificationTargetUser, NotificationStyle } from '../../../../types/api-request';
import { getDefaultTimeFormat, getDefaultDateFormat } from '../../../../types/datetime-const';
import { DatePickerFormatter } from '../../../components/datepicker/datepicker-formatter';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';

@Component({
  selector: 'tm-notification-edit-form',
  templateUrl: './notification-edit-form.component.html',
  styleUrls: ['./notification-edit-form.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: DatePickerFormatter }],
  animations: [collapseAnim],
})
export class NotificationEditFormComponent {

  NotificationEditFormMode = NotificationEditFormMode;
  NotificationStyle = NotificationStyle;
  NotificationTargetUser = NotificationTargetUser;

  NOTIFICATION_TITLE_MAX_LENGTH = ApiConst.NOTIFICATION_TITLE_MAX_LENGTH;

  @Input()
  guessTimezone = 'UTC';

  @Input()
  model: NotificationEditFormModel = {
    notificationId: '',
    shown: false,

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

  constructor(private simpleModalService: SimpleModalService) { }

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
    this.simpleModalService.openConfirmationModal('Discard unsaved edit?',
        SimpleModalType.WARNING, 'Warning: Any unsaved changes will be lost.').result.then(() => {
          this.cancelEditingNotificationEvent.emit();
        });
  }

}
