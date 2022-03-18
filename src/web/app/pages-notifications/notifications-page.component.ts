import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '../../services/notification.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TableComparatorService } from '../../services/table-comparator.service';
import { TimezoneService } from '../../services/timezone.service';
import { Notification, Notifications, NotificationTargetUser } from '../../types/api-output';
import { SortBy, SortOrder } from '../../types/sort-properties';
import { collapseAnim } from '../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../error-message-output';

interface NotificationTab {
  notification: Notification;
  hasTabExpanded: boolean;
  startDate: string;
  endDate: string;
}

/**
 * Component for notifications page.
 */
@Component({
  selector: 'tm-notifications-page',
  templateUrl: './notifications-page.component.html',
  styleUrls: ['./notifications-page.component.scss'],
  animations: [collapseAnim],
})
export class NotificationsPageComponent implements OnInit {

  // enum
  NotificationTargetUser: typeof NotificationTargetUser = NotificationTargetUser;
  SortBy: typeof SortBy = SortBy;

  userType: NotificationTargetUser = NotificationTargetUser.GENERAL;
  notificationTabs: NotificationTab[] = [];
  notificationsSortBy: SortBy = SortBy.NOTIFICATION_END_TIME;

  isLoadingNotifications: boolean = false;
  hasLoadingFailed: boolean = false;
  timezone: string = '';

  DATE_FORMAT: string = 'DD MMM YYYY';

  constructor(private router: Router,
              private notificationService: NotificationService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService,
              private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone();
    this.userType = this.parseUserType(this.router.url);
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.hasLoadingFailed = false;
    this.isLoadingNotifications = true;
    this.notificationService.getNotificationsByTargetUser(this.userType)
      .pipe(finalize(() => { this.isLoadingNotifications = false; }))
      .subscribe((notifications: Notifications) => {
          notifications.notifications.forEach((notification: Notification) => {
            this.notificationTabs.push({
              notification,
              hasTabExpanded: true,
              startDate: this.timezoneService.formatToString(
                notification.startTimestamp, this.timezone, this.DATE_FORMAT,
              ),
              endDate: this.timezoneService.formatToString(
                notification.endTimestamp, this.timezone, this.DATE_FORMAT,
              ),
            });
          });
          this.sortNotificationsBy(this.notificationsSortBy);
        }, (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      );
  }

  toggleCard(notificationTab: NotificationTab): void {
    notificationTab.hasTabExpanded = !notificationTab.hasTabExpanded;
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

  getUserTypeString(): string {
    switch (this.userType) {
      case NotificationTargetUser.STUDENT:
        return 'students';
      case NotificationTargetUser.INSTRUCTOR:
        return 'instructors';
      default:
        return 'all users';
    }
  }

  parseUserType(url: string): NotificationTargetUser {
    // From the url of the form /web/<user>/notifications, retrieves the <user> parameter
    const param: string = url.split('/')[2];
    switch (param) {
      case 'student':
        return NotificationTargetUser.STUDENT;
      case 'instructor':
        return NotificationTargetUser.INSTRUCTOR;
      default:
        return NotificationTargetUser.GENERAL;
    }
  }
}
