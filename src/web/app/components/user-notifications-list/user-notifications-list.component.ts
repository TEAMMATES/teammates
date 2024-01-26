import { Component, Input, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Notification,
  Notifications,
  NotificationTargetUser,
  ReadNotifications,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';
import { collapseAnim } from '../teammates-common/collapse-anim';

export interface NotificationTab {
  notification: Notification;
  hasTabExpanded: boolean;
  isRead: boolean;
  startDate: string;
  endDate: string;
}

/**
 * Component for user notifications list.
 */
@Component({
  selector: 'tm-user-notifications-list',
  templateUrl: './user-notifications-list.component.html',
  styleUrls: ['./user-notifications-list.component.scss'],
  animations: [collapseAnim],
})
export class UserNotificationsListComponent implements OnInit {

  // enum
  NotificationTargetUser: typeof NotificationTargetUser = NotificationTargetUser;
  SortBy: typeof SortBy = SortBy;

  @Input()
  userType: NotificationTargetUser = NotificationTargetUser.GENERAL;

  @Input()
  timezone = '';

  notificationTabs: NotificationTab[] = [];
  notificationsSortBy: SortBy = SortBy.NONE;
  readNotifications: Set<string> = new Set();

  isLoadingNotifications: boolean = false;
  hasLoadingFailed: boolean = false;

  DATE_FORMAT: string = 'DD MMM YYYY';

  constructor(private notificationService: NotificationService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService,
              private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.hasLoadingFailed = false;
    this.isLoadingNotifications = true;

    this.notificationService.getReadNotifications()
      .subscribe({
        next: (readNotifications: ReadNotifications) => {
          this.readNotifications = new Set(readNotifications.readNotifications);
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });

    this.notificationService.getAllNotificationsForTargetUser(this.userType)
      .pipe(finalize(() => { this.isLoadingNotifications = false; }))
      .subscribe({
        next: (notifications: Notifications) => {
          notifications.notifications.forEach((notification: Notification) => {
            this.notificationTabs.push({
              notification,
              hasTabExpanded: !this.readNotifications.has(notification.notificationId),
              isRead: this.readNotifications.has(notification.notificationId),
              startDate: this.timezoneService.formatToString(
                  notification.startTimestamp, this.timezone, this.DATE_FORMAT,
              ),
              endDate: this.timezoneService.formatToString(
                  notification.endTimestamp, this.timezone, this.DATE_FORMAT,
              ),
            });
          });
          this.sortNotificationsBy(this.notificationsSortBy);
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  toggleCard(notificationTab: NotificationTab): void {
    notificationTab.hasTabExpanded = !notificationTab.hasTabExpanded;
  }

  markNotificationAsRead(notificationTab: NotificationTab): void {
    const notification: Notification = notificationTab.notification;
    this.notificationService.markNotificationAsRead({
      notificationId: notification.notificationId,
      endTimestamp: notification.endTimestamp,
    })
      .subscribe({
        next: (readNotifications: ReadNotifications) => {
          this.readNotifications = new Set(readNotifications.readNotifications);
          notificationTab.isRead = true;
          this.statusMessageService.showSuccessToast('Notification marked as read.');
          notificationTab.hasTabExpanded = false;
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  getBodyTextClass(notificationTab: NotificationTab): string {
    return notificationTab.isRead ? 'card-body' : 'card-body pb-0';
  }

  getButtonClass(notificationTab: NotificationTab): string {
    return `btn btn-${notificationTab.notification.style.toLowerCase()}`;
  }

  /**
   * Checks the option selected to sort notifications.
   */
  isSelectedForSorting(by: SortBy): boolean {
    return this.notificationsSortBy === by;
  }

  /**
   * Sorts the notifications according to the selected option.
   */
  sortNotificationsBy(by: SortBy): void {
    this.notificationsSortBy = by;
    this.notificationTabs.sort(this.sortTabsBy(by));
  }

  /**
   * Sorts the notification tabs in order.
   */
  sortTabsBy(by: SortBy): ((a: NotificationTab, b: NotificationTab) => number) {
    return ((a: NotificationTab, b: NotificationTab): number => {
      let strA: string;
      let strB: string;
      let order: SortOrder;
      switch (by) {
        case SortBy.NOTIFICATION_START_TIME:
          strA = a.notification.startTimestamp.toString();
          strB = b.notification.startTimestamp.toString();
          order = SortOrder.DESC;
          break;
        case SortBy.NOTIFICATION_END_TIME:
          strA = a.notification.endTimestamp.toString();
          strB = b.notification.endTimestamp.toString();
          order = SortOrder.ASC;
          break;
        default:
          strA = '';
          strB = '';
          order = SortOrder.ASC;
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }
}
