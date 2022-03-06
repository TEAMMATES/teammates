import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
// FIXME: This Notification is to be imported from api-output after GET route PR is merged
import { NotificationService, Notification, Notifications } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  NotificationEditFormMode,
  NotificationEditFormModel,
} from './notification-edit-form/notification-edit-form-model';
import { NotificationsTableRowModel } from './notifications-table/notifications-table-model';

// default sorting order for rows
const defaultNotificationsTableRowModelsSortOrder = SortOrder.ASC;

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

    type: '',
    targetUser: '',

    title: '',
    message: '',

    isSaving: false,
    isEditable: true,
    isDeleting: false,
  };

  notificationsTableRowModels: NotificationsTableRowModel[] = [];
  notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
  notificationsTableRowModelsSortOrder = defaultNotificationsTableRowModelsSortOrder;

  constructor(
    private notificationService: NotificationService,
    private statusMessageService: StatusMessageService,
    private tableComparatorService: TableComparatorService,
    private timezoneService: TimezoneService,
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
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
              notification,
            });
          });
          // sort the list using create time, and allocate the index in ascending order
          this.sortNotificationsTableRowModelsHandler(SortBy.NOTIFICATION_CREATE_TIME);
          this.notificationsTableRowModels.forEach(
            (notificationsTableRowModel: NotificationsTableRowModel, index: number) => {
              notificationsTableRowModel.index = index;
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

  /**
   * Handles sorting event from the table sub-component.
   */
  sortNotificationsTableRowModelsHandler(sortBy: SortBy): void {
    if (this.notificationsTableRowModelsSortBy === sortBy) {
      // if sorting the same column, reverse the order
      this.notificationsTableRowModelsSortOrder =
        this.notificationsTableRowModelsSortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;
    } else {
      // if different column, change the sortBy value and go back to default order
      this.notificationsTableRowModelsSortBy = sortBy;
      this.notificationsTableRowModelsSortOrder = defaultNotificationsTableRowModelsSortOrder;
    }
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
            strA = String(a.notification.createTimestamp);
            strB = String(b.notification.createTimestamp);
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
          default:
            strA = '';
            strB = '';
        }

        return this.tableComparatorService.compare(
          this.notificationsTableRowModelsSortBy, this.notificationsTableRowModelsSortOrder, strA, strB);
      };
  }
}
