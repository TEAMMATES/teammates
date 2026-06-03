import { Component, EventEmitter, Input, OnChanges, Output, inject } from '@angular/core';
import { NotificationsTableHeaderColorScheme, NotificationsTableRowModel } from './notifications-table-model';
import { NotificationActionsCellComponent } from './notification-actions-cell.component';
import { NotificationDateCellComponent } from './notification-date-cell.component';
import { NotificationStyleCellComponent } from './notification-style-cell.component';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { Notification } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableEvent,
  SortableTableCellData,
  SortableTableComponent,
  SortableTableHeaderColorScheme,
} from '../../../components/sortable-table/sortable-table.component';

@Component({
  selector: 'tm-notifications-table',
  templateUrl: './notifications-table.component.html',
  styleUrls: ['./notifications-table.component.scss'],
  imports: [SortableTableComponent],
})
export class NotificationsTableComponent implements OnChanges {
  private simpleModalService = inject(SimpleModalService);
  private rowDataToModelMap = new WeakMap<SortableTableCellData[], NotificationsTableRowModel>();

  @Input()
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
  deleteNotificationEvent: EventEmitter<string> = new EventEmitter();

  @Output()
  loadNotificationEditFormEvent: EventEmitter<Notification> = new EventEmitter();

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];
  rowIdGetter = (rowData: SortableTableCellData[]): string | undefined => this.getRowId(rowData);
  rowClassGetter = (rowData: SortableTableCellData[]): string | undefined => this.getRowClass(rowData);

  constructor() {
    this.setColumnsData();
  }

  ngOnChanges(): void {
    this.setRowsData();
  }

  /**
   * Sorts the list of notifications row.
   */
  sortNotificationsTableRowModels(by: SortBy): void {
    this.sortNotificationsTableRowModelsEvent.emit(by);
  }

  sortNotificationsTableRowsEventHandler(event: SortableEvent): void {
    this.sortNotificationsTableRowModels(event.sortBy);
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

  getHeaderColorScheme(): SortableTableHeaderColorScheme {
    return this.headerColorScheme === NotificationsTableHeaderColorScheme.WHITE
      ? SortableTableHeaderColorScheme.WHITE
      : SortableTableHeaderColorScheme.BLUE;
  }

  private getRowId(rowData: SortableTableCellData[]): string | undefined {
    return this.rowDataToModelMap.get(rowData)?.notification.notificationId;
  }

  private getRowClass(rowData: SortableTableCellData[]): string | undefined {
    return this.rowDataToModelMap.get(rowData)?.isHighlighted ? 'table-success' : undefined;
  }

  private setColumnsData(): void {
    this.columnsData = [
      { header: 'Title', sortBy: SortBy.NOTIFICATION_TITLE },
      { header: 'Start Time', sortBy: SortBy.NOTIFICATION_START_TIME },
      { header: 'End Time', sortBy: SortBy.NOTIFICATION_END_TIME },
      { header: 'Target User', sortBy: SortBy.NOTIFICATION_TARGET_USER },
      { header: 'Style', sortBy: SortBy.NOTIFICATION_STYLE },
      { header: 'Creation Time', sortBy: SortBy.NOTIFICATION_CREATE_TIME },
      { header: 'Actions' },
    ];
  }

  private setRowsData(): void {
    this.rowDataToModelMap = new WeakMap<SortableTableCellData[], NotificationsTableRowModel>();
    this.rowsData = this.notificationsTableRowModels.map((notificationsTableRowModel: NotificationsTableRowModel) => {
      const notification: Notification = notificationsTableRowModel.notification;
      const rowData: SortableTableCellData[] = [
        { value: notification.title },
        {
          value: notification.startTimestamp,
          customComponent: {
            component: NotificationDateCellComponent,
            componentData: () => ({
              timestamp: notification.startTimestamp,
              guessTimezone: this.guessTimezone,
            }),
          },
        },
        {
          value: notification.endTimestamp,
          customComponent: {
            component: NotificationDateCellComponent,
            componentData: () => ({
              timestamp: notification.endTimestamp,
              guessTimezone: this.guessTimezone,
            }),
          },
        },
        { value: notification.targetUser },
        {
          value: notification.style,
          customComponent: {
            component: NotificationStyleCellComponent,
            componentData: () => ({
              style: notification.style,
            }),
          },
        },
        {
          value: notification.createdAt,
          customComponent: {
            component: NotificationDateCellComponent,
            componentData: () => ({
              timestamp: notification.createdAt,
              guessTimezone: this.guessTimezone,
            }),
          },
        },
        {
          customComponent: {
            component: NotificationActionsCellComponent,
            componentData: () => ({
              loadNotificationEditForm: () => this.loadNotificationEditForm(notification),
              deleteNotification: () => this.deleteNotification(notification.notificationId, notification.title),
            }),
          },
        },
      ];
      this.rowDataToModelMap.set(rowData, notificationsTableRowModel);
      return rowData;
    });
  }
}
