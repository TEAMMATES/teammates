import { Component } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  NotificationEditFormMode,
  NotificationEditFormModel,
} from './notification-edit-form/notification-edit-form-model';

@Component({
  selector: 'tm-admin-notifications-page',
  templateUrl: './admin-notifications-page.component.html',
  styleUrls: ['./admin-notifications-page.component.scss'],
  animations: [collapseAnim],
})
export class AdminNotificationsPageComponent {

  NotificationEditFormMode = NotificationEditFormMode;

  isNotificationLoading = false;
  hasNotificationLoadingFailed = false;

  isNotificationEditFormExpanded = false;

  notificationEditFormModel: NotificationEditFormModel = {
    notificationId: '',
    shown: false,

    startTime: { hour: 0, minute: 0 },
    startDate: { year: 0, month: 0, day: 0 },
    endTime: { hour: 0, minute: 0 },
    endDate: { year: 0, month: 0, day: 0 },

    type: '',
    targetUser: '',

    title: '',
    message: '',

    isSaving: false,
    isEditable: true,
    isDeleting: false,
  };

  notifications: any[] = [];

  constructor(
    private notificationService: NotificationService,
    private statusMessageService: StatusMessageService,
    private timezoneService: TimezoneService,
  ) {}

  retryLoadingAllData(): void {
  }

  /**
   * Adds a new notification.
   */
  addNewNotificationHandler(): void {
    this.notificationEditFormModel.isSaving = true;

    // FIXME: Timezone is not specified here so it will be guessed from browser's request.
    // It is still to be discussed how to handle this.
    const startTime = this.timezoneService.resolveLocalDateTime(
      this.notificationEditFormModel.startDate, this.notificationEditFormModel.startTime,
    );
    const endTime = this.timezoneService.resolveLocalDateTime(
      this.notificationEditFormModel.endDate, this.notificationEditFormModel.endTime,
    );

    this.notificationService.createNotification({
      title: this.notificationEditFormModel.title,
      message: this.notificationEditFormModel.message,
      notificationType: this.notificationEditFormModel.type,
      targetUser: this.notificationEditFormModel.targetUser,
      startTimestamp: startTime,
      endTimestamp: endTime,
    })
    .pipe(finalize(() => { this.notificationEditFormModel.isSaving = false; }))
    .subscribe(
      () => {
        this.statusMessageService.showSuccessToast('Notification created successfully.');
      },
      (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    );
  }
}
