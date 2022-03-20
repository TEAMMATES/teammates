import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Notification } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { NotificationsTableHeaderColorScheme, NotificationsTableRowModel } from './notifications-table-model';

@Component({
  selector: 'tm-notifications-table',
  templateUrl: './notifications-table.component.html',
  styleUrls: ['./notifications-table.component.scss'],
})
export class NotificationsTableComponent implements OnInit {
  SortBy = SortBy;
  SortOrder = SortOrder;
  NotificationsTableHeaderColorScheme = NotificationsTableHeaderColorScheme;

  guessTimezone = 'UTC';

  @Input()
  notificationsTableRowModels: NotificationsTableRowModel[] = [];

  @Input()
  notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;

  @Input()
  notificationsTableRowModelsSortOrder = SortOrder.DESC;

  @Input()
  headerColorScheme = NotificationsTableHeaderColorScheme.BLUE;

  @Output()
  sortNotificationsTableRowModelsEvent: EventEmitter<SortBy> = new EventEmitter();

  @Output()
  deleteNotificationEvent: EventEmitter<String> = new EventEmitter();

  loadNotificationEditFormEvent: EventEmitter<Notification> = new EventEmitter();

  constructor(private simpleModalService: SimpleModalService, private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    this.guessTimezone = this.timezoneService.guessTimezone();
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortNotificationsTableRowModels(by: SortBy): void {
    this.sortNotificationsTableRowModelsEvent.emit(by);
  }

  /**
   * Deletes a notification based on its ID.
   */
  deleteNotification(notificationId: string, title: string): void {
    const modalRef = this.simpleModalService.openConfirmationModal(
      'Confirm your action',
      SimpleModalType.DANGER,
      `Do you want to delete this notification (titled "${title}") permanently? This action will not be reversible.`,
    );
    modalRef.result.then(() => this.deleteNotificationEvent.emit(notificationId));
  }

  /**
   * Loads the notification edit form.
   */
  loadNotificationEditForm(notification: Notification): void {
    this.loadNotificationEditFormEvent.emit(notification);
  }
}
