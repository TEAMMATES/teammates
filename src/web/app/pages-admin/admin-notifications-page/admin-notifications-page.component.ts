import { Component, OnInit } from '@angular/core';
import moment from 'moment-timezone';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Notification, Notifications } from '../../../types/api-output';
import { NotificationType, NotificationTargetUser } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  NotificationEditFormMode,
  NotificationEditFormModel,
} from './notification-edit-form/notification-edit-form-model';
import { NotificationsTableRowModel } from './notifications-table/notifications-table-model';

@Component({
  selector: 'tm-admin-notifications-page',
  templateUrl: './admin-notifications-page.component.html',
  styleUrls: ['./admin-notifications-page.component.scss'],
  animations: [collapseAnim],
})
export class AdminNotificationsPageComponent implements OnInit {

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

    type: NotificationType.MAINTENANCE,
    targetUser: NotificationTargetUser.GENERAL,

    title: '',
    message: '',

    isSaving: false,
    isEditable: true,
    isDeleting: false,
  };

  notificationsTableRowModels: NotificationsTableRowModel[] = [];
  notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
  notificationsTableRowModelsSortOrder = SortOrder.DESC;

  constructor(
    private notificationService: NotificationService,
    private statusMessageService: StatusMessageService,
    private tableComparatorService: TableComparatorService,
    private timezoneService: TimezoneService,
  ) {}

  ngOnInit(): void {
    this.initNotificationEditFormModel();
    this.loadNotifications();
  }

  /**
   * Initializes values for the notification edit form model.
   */
  initNotificationEditFormModel(): void {
    const nearFuture: moment.Moment = moment().add(1, 'hours');
    const tomorrow: moment.Moment = moment().add(1, 'days');
    this.notificationEditFormModel = {
      notificationId: '',
      shown: false,

      startTime: {
        minute: nearFuture.hour() === 0 ? 59 : 0, // for 00:00 midnight, we use 23:59
        hour: nearFuture.hour() === 0 ? 23 : nearFuture.hour(),
      },
      startDate: {
        year: nearFuture.year(),
        month: nearFuture.month() + 1, // moment return 0-11 for month
        day: nearFuture.date(),
      },
      endTime: { hour: 23, minute: 59 },
      endDate: {
        year: tomorrow.year(),
        month: tomorrow.month() + 1, // moment return 0-11 for month
        day: tomorrow.date(),
      },

      type: NotificationType.MAINTENANCE,
      targetUser: NotificationTargetUser.GENERAL,

      title: '',
      message: '',

      isSaving: false,
      isEditable: true,
      isDeleting: false,
    };
  }

  /**
   * Loads all notifications from backend.
   */
  loadNotifications(): void {
    this.isNotificationLoading = true;
    this.notificationService.getNotifications()
      .pipe(finalize(() => { this.isNotificationLoading = false; }))
      .subscribe(
        (notifications: Notifications) => {
          notifications.notifications.forEach((notification: Notification) => {
            this.notificationsTableRowModels.push({
              index: -1,
              isHighlighted: false,
              notification,
            });
          });
          // sort the list using create time, and allocate the index in ascending order
          // note: order is set to be descending here as it will be reversed later
          this.notificationsTableRowModelsSortOrder = SortOrder.ASC;
          this.sortNotificationsTableRowModelsHandler(SortBy.NOTIFICATION_CREATE_TIME);

          const size = this.notificationsTableRowModels.length;
          this.notificationsTableRowModels.forEach(
            (notificationsTableRowModel: NotificationsTableRowModel, index: number) => {
              notificationsTableRowModel.index = size - index;
            },
          );
        },
        (resp: ErrorMessageOutput) => {
          this.hasNotificationLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      );
  }

  /**
   * Resets error state and retry loading data.
   */
  retryLoadingAllData(): void {
    this.isNotificationLoading = false;
    this.hasNotificationLoadingFailed = false;
    this.loadNotifications();
  }

  /**
   * Adds a new notification.
   */
  addNewNotificationHandler(): void {
    this.notificationEditFormModel.isSaving = true;

    // Timezone is not specified here so it will be guessed from browser's request.
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
      (notification: Notification) => {
        this.statusMessageService.showSuccessToast('Notification created successfully.');

        // get the max index of the existing notifications
        const maxIndex = this.notificationsTableRowModels.reduce(
          (prevMax: number, notificationsTableRowModel: NotificationsTableRowModel) => {
            return Math.max(prevMax, notificationsTableRowModel.index);
          },
          0,
        );

        this.notificationsTableRowModels.unshift({
          index: maxIndex + 1,
          isHighlighted: true,
          notification,
        });

        this.isNotificationEditFormExpanded = false;
        this.initNotificationEditFormModel();
      },
      (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    );
  }

  /**
   * Handles sorting event from the table sub-component.
   */
  sortNotificationsTableRowModelsHandler(sortBy: SortBy): void {
    if (this.notificationsTableRowModelsSortBy === sortBy) {
      // if sorting the same column, reverse the order
      this.notificationsTableRowModelsSortOrder =
        this.notificationsTableRowModelsSortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;
    } else {
      // if different column, change the sortBy value and but sort order will not be changed
      this.notificationsTableRowModelsSortBy = sortBy;
    }
    // before sorting, remove highlights from all rows
    this.notificationsTableRowModels.forEach(
      (notificationsTableRowModel: NotificationsTableRowModel) => {
        notificationsTableRowModel.isHighlighted = false;
      },
    );

    this.notificationsTableRowModels.sort(this.getNotificationsTableRowModelsComparator());
  }

  /**
   * Gets a comparator to sort the row models based on sortBy and sortOrder.
   */
  getNotificationsTableRowModelsComparator():
    ((a: NotificationsTableRowModel, b: NotificationsTableRowModel) => number) {
      return (a: NotificationsTableRowModel, b: NotificationsTableRowModel) => {
        let strA: string;
        let strB: string;
        switch (this.notificationsTableRowModelsSortBy) {
          case SortBy.NOTIFICATION_CREATE_TIME:
            strA = String(a.notification.createdAt);
            strB = String(b.notification.createdAt);
            break;
          case SortBy.NOTIFICATION_TITLE:
            strA = a.notification.title;
            strB = b.notification.title;
            break;
          case SortBy.NOTIFICATION_START_TIME:
            strA = String(a.notification.startTimestamp);
            strB = String(b.notification.startTimestamp);
            break;
          case SortBy.NOTIFICATION_END_TIME:
            strA = String(a.notification.endTimestamp);
            strB = String(b.notification.endTimestamp);
            break;
          case SortBy.NOTIFICATION_TARGET_USER:
            strA = String(a.notification.targetUser);
            strB = String(b.notification.targetUser);
            break;
          case SortBy.NOTIFICATION_TYPE:
            strA = String(a.notification.notificationType);
            strB = String(b.notification.notificationType);
            break;
          default:
            strA = '';
            strB = '';
        }

        return this.tableComparatorService.compare(
          this.notificationsTableRowModelsSortBy, this.notificationsTableRowModelsSortOrder, strA, strB);
      };
  }
}
