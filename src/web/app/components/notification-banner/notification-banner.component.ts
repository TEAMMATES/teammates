import { NgClass } from '@angular/common';
import { Component, Input, OnChanges, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { forkJoin } from 'rxjs';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Notification, Notifications, NotificationTargetUser, ReadNotifications } from '../../../types/api-output';
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
  private notificationService = inject(NotificationService);
  private statusMessageService = inject(StatusMessageService);
  private cdr = inject(ChangeDetectorRef);

  @Input()
  url = '';

  @Input()
  notificationTargetUser: NotificationTargetUser = NotificationTargetUser.GENERAL;

  isShown = false;
  notifications: Notification[] = [];

  ngOnInit(): void {
    if (this.notificationTargetUser !== NotificationTargetUser.GENERAL && !this.isNotificationsPage()) {
      this.fetchNotifications();
    }
  }

  ngOnChanges(): void {
    if (this.isNotificationsPage()) {
      // Clear stale unread cache and hide banner while on notifications page
      this.notifications = [];
      this.closeNotification();
      return;
    }

  // When leaving notifications page, refresh from server
  if (this.notificationTargetUser !== NotificationTargetUser.GENERAL) {
    this.fetchNotifications();
  }
}

  fetchNotifications(): void {
    if (this.isNotificationsPage()) {
      this.notifications = [];
      this.closeNotification();
      return;
    }
    forkJoin({
      readNotifications: this.notificationService.getReadNotifications(),
      notifications: this.notificationService.getNotifications({
        targetUsers: this.getTargetUsers(),
        isFetchingActive: true,
      }),
    }).subscribe(
      ({
        readNotifications,
        notifications,
      }: {
        readNotifications: ReadNotifications;
        notifications: Notifications;
      }) => {
        const readNotificationsSet = new Set(readNotifications.readNotifications);
        this.notifications = notifications.notifications.filter(
          (notification: Notification) => !readNotificationsSet.has(notification.notificationId),
        );
        if (this.notifications.length > 0) {
          this.cdr.detectChanges();
          this.isShown = true;
        }
      },
    );
  }
  private isNotificationsPage(): boolean {
    return this.url.includes('notifications');
  }
  private getTargetUsers(): NotificationTargetUser[] {
    return Array.from(new Set([this.notificationTargetUser, NotificationTargetUser.GENERAL]));
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
