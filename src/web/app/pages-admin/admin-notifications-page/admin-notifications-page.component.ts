import { Component, OnInit, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import moment from 'moment-timezone';
import { finalize } from 'rxjs/operators';
import {
  NotificationEditFormMode,
  NotificationEditFormModel,
} from './notification-edit-form/notification-edit-form-model';
import { NotificationsTableRowModel } from './notifications-table/notifications-table-model';
import { NotificationsTableComponent } from './notifications-table/notifications-table.component';
import { NotificationService } from '../../../services/notification.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import { MessageOutput, Notification, Notifications } from '../../../types/api-output';
import { NotificationStyle, NotificationTargetUser } from '../../../types/api-request';
import { getDefaultDateFormat, getDefaultTimeFormat, getLatestTimeFormat } from '../../../types/datetime-const';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';
import { NotificationEditFormComponent } from './notification-edit-form/notification-edit-form.component';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { SortableEvent } from '../../components/sortable-table/sortable-table.component';

@Component({
  selector: 'tm-admin-notifications-page',
  templateUrl: './admin-notifications-page.component.html',
  styleUrls: ['./admin-notifications-page.component.scss'],
  imports: [
    NotificationEditFormComponent,
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    NotificationsTableComponent,
    NgbCollapse,
  ],
})
export class AdminNotificationsPageComponent implements OnInit {
  private notificationService = inject(NotificationService);
  private simpleModalService = inject(SimpleModalService);
  private statusMessageService = inject(StatusMessageService);
  private tableComparatorService = inject(TableComparatorService);
  private timezoneService = inject(TimezoneService);

  NotificationEditFormMode!: typeof NotificationEditFormMode;

  currentNotificationEditFormMode = NotificationEditFormMode.ADD;
  isNotificationLoading = false;
  hasNotificationLoadingFailed = false;

  isNotificationEditFormExpanded = false;

  notificationEditFormModel: NotificationEditFormModel = {
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

  // Keep row models immutable so NotificationsTableComponent can rebuild its derived row data via ngOnChanges.
  notificationsTableRowModels: NotificationsTableRowModel[] = [];
  notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
  notificationsTableRowModelsSortOrder = SortOrder.DESC;

  guessTimezone = 'UTC';

  constructor() {
    this.NotificationEditFormMode = NotificationEditFormMode;
  }

  ngOnInit(): void {
    this.initNotificationEditFormModel();
    this.loadNotifications();
    this.guessTimezone = this.timezoneService.guessTimezone();
  }

  /**
   * Closes form and initializes values for the edit form model.
   */
  initNotificationEditFormModel(): void {
    this.isNotificationEditFormExpanded = false;

    const nearFuture: moment.Moment = moment().add(1, 'hours');
    const tomorrow: moment.Moment = moment().add(1, 'days');
    this.notificationEditFormModel = {
      notificationId: '',

      startTime: {
        minute: nearFuture.hour() === 0 ? 59 : 0, // for 00:00 midnight, we use 23:59
        hour: nearFuture.hour() === 0 ? 23 : nearFuture.hour(),
      },
      startDate: {
        year: nearFuture.year(),
        month: nearFuture.month() + 1, // moment return 0-11 for month
        day: nearFuture.date(),
      },
      endTime: getLatestTimeFormat(),
      endDate: {
        year: tomorrow.year(),
        month: tomorrow.month() + 1, // moment return 0-11 for month
        day: tomorrow.date(),
      },

      style: NotificationStyle.SUCCESS,
      targetUser: NotificationTargetUser.GENERAL,

      title: '',
      message: '',

      isSaving: false,
      isDeleting: false,
    };

    this.currentNotificationEditFormMode = NotificationEditFormMode.ADD;
  }

  /**
   * Loads all notifications from backend.
   */
  loadNotifications(): void {
    this.isNotificationLoading = true;
    this.notificationService
      .getNotifications({
        targetUsers: [
          NotificationTargetUser.STUDENT,
          NotificationTargetUser.INSTRUCTOR,
          NotificationTargetUser.GENERAL,
        ],
        isFetchingActive: false,
      })
      .pipe(
        finalize(() => {
          this.isNotificationLoading = false;
        }),
      )
      .subscribe({
        next: (notifications: Notifications) => {
          this.notificationsTableRowModels = notifications.notifications.map((notification: Notification) => ({
            isHighlighted: false,
            notification,
          }));
          this.sortNotificationsTableRowModelsHandler({
            sortBy: SortBy.NOTIFICATION_CREATE_TIME,
            sortOrder: SortOrder.DESC,
          });
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasNotificationLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
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
   * Loads notification data into the edit form for updating actions.
   * Will do checks to skip or show warnings if necessary.
   */
  loadNotificationEditFormHandler(notification: Notification): void {
    if (this.isNotificationEditFormExpanded) {
      if (this.notificationEditFormModel.notificationId !== notification.notificationId) {
        // Warns user that the existing edits will be cleared
        this.simpleModalService
          .openConfirmationModal(
            'Discard unsaved edit?',
            SimpleModalType.WARNING,
            'Warning: If you choose to edit another notification, any unsaved changes will be lost.',
          )
          .result.then(() => {
            this.loadNotificationEditForm(notification);
          });
      }
      // Not to do anything if the notification is already loaded
    } else {
      // Loads notification data into the edit form without doing any check if the form was closed originally
      this.loadNotificationEditForm(notification);
    }
  }

  /**
   * The actual function to load data into edit form.
   */
  loadNotificationEditForm(notification: Notification): void {
    const startTime = moment(notification.startTimestamp);
    const endTime = moment(notification.endTimestamp);
    this.notificationEditFormModel = {
      notificationId: notification.notificationId,

      startTime: {
        minute: startTime.hour() === 0 ? 59 : 0, // for 00:00 midnight, we use 23:59
        hour: startTime.hour() === 0 ? 23 : startTime.hour(),
      },
      startDate: {
        year: startTime.year(),
        month: startTime.month() + 1, // moment return 0-11 for month
        day: startTime.date(),
      },
      endTime: {
        minute: endTime.hour() === 0 ? 59 : 0, // for 00:00 midnight, we use 23:59
        hour: endTime.hour() === 0 ? 23 : endTime.hour(),
      },
      endDate: {
        year: endTime.year(),
        month: endTime.month() + 1, // moment return 0-11 for month
        day: endTime.date(),
      },

      style: notification.style,
      targetUser: notification.targetUser,

      title: notification.title,
      message: notification.message,

      isSaving: false,
      isDeleting: false,
    };

    this.currentNotificationEditFormMode = NotificationEditFormMode.EDIT;
    this.isNotificationEditFormExpanded = true;
  }

  /**
   * Adds a new notification.
   */
  addNewNotificationHandler(): void {
    this.notificationEditFormModel.isSaving = true;

    // Timezone is not specified here so it will be guessed from browser's request.
    const startTime = this.timezoneService.resolveLocalDateTime(
      this.notificationEditFormModel.startDate,
      this.notificationEditFormModel.startTime,
    );
    const endTime = this.timezoneService.resolveLocalDateTime(
      this.notificationEditFormModel.endDate,
      this.notificationEditFormModel.endTime,
    );

    this.notificationService
      .createNotification({
        title: this.notificationEditFormModel.title,
        message: this.notificationEditFormModel.message,
        style: this.notificationEditFormModel.style,
        targetUser: this.notificationEditFormModel.targetUser,
        startTimestamp: startTime,
        endTimestamp: endTime,
      })
      .pipe(
        finalize(() => {
          this.notificationEditFormModel.isSaving = false;
        }),
      )
      .subscribe({
        next: (notification: Notification) => {
          this.notificationsTableRowModels = [
            {
              isHighlighted: true,
              notification,
            },
            ...this.notificationsTableRowModels,
          ];
          this.initNotificationEditFormModel();
          this.statusMessageService.showSuccessToast('Notification created successfully.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Updates an existing notification.
   */
  editExistingNotificationHandler(): void {
    this.notificationEditFormModel.isSaving = true;

    // Timezone is not specified here so it will be guessed from browser's request.
    const startTime = this.timezoneService.resolveLocalDateTime(
      this.notificationEditFormModel.startDate,
      this.notificationEditFormModel.startTime,
    );
    const endTime = this.timezoneService.resolveLocalDateTime(
      this.notificationEditFormModel.endDate,
      this.notificationEditFormModel.endTime,
    );

    this.notificationService
      .updateNotification(
        {
          title: this.notificationEditFormModel.title,
          message: this.notificationEditFormModel.message,
          style: this.notificationEditFormModel.style,
          targetUser: this.notificationEditFormModel.targetUser,
          startTimestamp: startTime,
          endTimestamp: endTime,
        },
        this.notificationEditFormModel.notificationId,
      )
      .pipe(
        finalize(() => {
          this.notificationEditFormModel.isSaving = false;
        }),
      )
      .subscribe({
        next: (notification: Notification) => {
          this.statusMessageService.showSuccessToast('Notification updated successfully.');

          this.notificationsTableRowModels = this.notificationsTableRowModels.map(
            (rowModel: NotificationsTableRowModel) => {
              if (rowModel.notification.notificationId === notification.notificationId) {
                return {
                  ...rowModel,
                  isHighlighted: true,
                  notification,
                };
              }
              return rowModel;
            },
          );

          this.initNotificationEditFormModel();
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Deletes a notification.
   */
  deleteNotificationHandler(notificationId: string): void {
    this.notificationService.deleteNotification(notificationId).subscribe({
      next: (msg: MessageOutput) => {
        this.statusMessageService.showSuccessToast(msg.message);
        this.notificationsTableRowModels = this.notificationsTableRowModels.filter(
          (notificationsTableRowModel: NotificationsTableRowModel) => {
            return notificationsTableRowModel.notification.notificationId !== notificationId;
          },
        );
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Handles sorting event from the table sub-component.
   */
  sortNotificationsTableRowModelsHandler(event: SortableEvent): void {
    this.notificationsTableRowModelsSortBy = event.sortBy;
    this.notificationsTableRowModelsSortOrder = event.sortOrder;
    // before sorting, remove highlights from all rows
    this.notificationsTableRowModels = this.notificationsTableRowModels
      .map((notificationsTableRowModel: NotificationsTableRowModel) => ({
        ...notificationsTableRowModel,
        isHighlighted: false,
      }))
      .sort(this.getNotificationsTableRowModelsComparator());
  }

  /**
   * Gets a comparator to sort the row models based on sortBy and sortOrder.
   */
  getNotificationsTableRowModelsComparator(): (a: NotificationsTableRowModel, b: NotificationsTableRowModel) => number {
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
        case SortBy.NOTIFICATION_STYLE:
          strA = String(a.notification.style);
          strB = String(b.notification.style);
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(
        this.notificationsTableRowModelsSortBy,
        this.notificationsTableRowModelsSortOrder,
        strA,
        strB,
      );
    };
  }
}
