import { NgClass } from '@angular/common';
import { Component, Input, OnChanges, OnInit, ChangeDetectorRef } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Notification, Notifications, NotificationTargetUser } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { NotificationStyleClassPipe } from '../teammates-common/notification-style-class.pipe';

/**
 * Banner used to display notifications to the user.
 */
@Component({
  selector: 'tm-notification-banner',
  templateUrl: './notification-banner.component.html',
  styleUrls: ['./notification-banner.component.scss'],
  imports: [NgClass, NotificationStyleClassPipe, NgbCollapse],
})
export class NotificationBannerComponent implements OnInit, OnChanges {
  @Input()
  url = '';

  @Input()
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;

  isShown = false;
  notifications: Notification[] = [];

  constructor(
    private notificationService: NotificationService,
    private statusMessageService: StatusMessageService,
    private cdr: ChangeDetectorRef,
  ) {}

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
    this.notificationService
      .getUnreadNotificationsForTargetUser(this.notificationTargetUser)
      .subscribe((response: Notifications) => {
        this.notifications = response.notifications;
        if (this.notifications.length > 0) {
          this.cdr.detectChanges();
          this.isShown = true;
        }
      });
  }

  markNotificationAsRead(notification: Notification): void {
    this.notificationService
      .markNotificationAsRead({
        notificationId: notification.notificationId,
      })
      .subscribe({
        next: () => {
          this.statusMessageService.showSuccessToast('Notification marked as read.');
          if (this.notifications.length > 0) {
            this.advanceToNextNotification();
          }

          if (this.notifications.length === 0) {
            this.closeNotification();
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  advanceToNextNotification(): void {
    this.notifications.shift();
  }

  closeNotification(): void {
    this.isShown = false;
  }

  getButtonClass(notification: Notification): string {
    return `btn btn-${notification.style.toLowerCase()}`;
  }
}
