import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Notification, Notifications, NotificationTargetUser } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { collapseAnim } from '../teammates-common/collapse-anim';

/**
 * Banner used to display notifications to the user.
 */
@Component({
  selector: 'tm-notification-banner',
  templateUrl: './notification-banner.component.html',
  styleUrls: ['./notification-banner.component.scss'],
  animations: [collapseAnim],
})
export class NotificationBannerComponent implements OnInit, OnChanges {

  @Input()
  url: string = '';

  @Input()
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;

  isShown: boolean = true;
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    if (this.notificationTargetUser !== NotificationTargetUser.GENERAL) {
      this.fetchNotifications();
    }
  }

  ngOnChanges(): void {
    // Hide the notification banner if the user is on user notifications page
    if (this.url.includes('notifications')) {
      this.closeNotification();
    }
  }

  fetchNotifications(): void {
    this.notificationService.getUnreadNotificationsForTargetUser(this.notificationTargetUser)
      .subscribe((response: Notifications) => {
        this.notifications = response.notifications;
      });
  }

  markNotificationAsRead(notification: Notification): void {
    this.notificationService.markNotificationAsRead({
      notificationId: notification.notificationId,
      endTimestamp: notification.endTimestamp,
    })
      .subscribe({
        next: () => {
          this.statusMessageService.showSuccessToast('Notification marked as read.');
          this.closeNotification();
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  closeNotification(): void {
    this.isShown = false;
  }

  getButtonClass(notification: Notification): string {
    return `btn btn-${notification.style.toLowerCase()}`;
  }
}
