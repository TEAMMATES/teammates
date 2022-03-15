import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '../../services/notification.service';
import { StatusMessageService } from '../../services/status-message.service';
import { TimezoneService } from '../../services/timezone.service';
import { Notification, Notifications, NotificationTargetUser } from '../../types/api-output';
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

  userType: NotificationTargetUser = NotificationTargetUser.GENERAL;
  notificationTabs: NotificationTab[] = [];
  isLoadingNotifications: boolean = false;
  hasLoadingFailed: boolean = false;
  timezone: string = '';

  DATE_FORMAT: string = 'DD MMM YYYY';

  constructor(private router: Router,
              private notificationService: NotificationService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) { }

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
            })
          })
        }, (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      );
  }

  toggleCard(notificationTab: NotificationTab): void {
    notificationTab.hasTabExpanded = !notificationTab.hasTabExpanded;
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
    const param: string = url.split('/')[2]
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
