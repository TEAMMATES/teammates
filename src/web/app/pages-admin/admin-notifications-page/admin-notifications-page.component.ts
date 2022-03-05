import { Component } from '@angular/core';
import {
  NotificationEditFormMode,
  NotificationEditFormModel,
} from './notification-edit-form/notification-edit-form-model';

@Component({
  selector: 'tm-admin-notifications-page',
  templateUrl: './admin-notifications-page.component.html',
  styleUrls: ['./admin-notifications-page.component.scss'],
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

  retryLoadingAllData(): void {
  }

  addNewNotificationHandler(): void {
  }
}
