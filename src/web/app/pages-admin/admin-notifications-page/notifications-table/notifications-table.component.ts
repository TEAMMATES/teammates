import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CellWithActionsComponent } from './cell-with-actions.component';
import { NotificationsTableHeaderColorScheme, NotificationsTableRowModel } from './notifications-table-model';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Notification } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import {
  ColumnData,
  SortableEvent,
  SortableTableCellData,
  SortableTableHeaderColorScheme,
} from '../../../components/sortable-table/sortable-table.component';
import { FormatDateBriefPipe } from '../../../components/teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../../../components/teammates-common/format-date-detail.pipe';
import { NotificationStyleDescriptionPipe } from
'../../../components/teammates-common/notification-style-description.pipe';

@Component({
  selector: 'tm-notifications-table',
  templateUrl: './notifications-table.component.html',
  styleUrls: ['./notifications-table.component.scss'],
})

export class NotificationsTableComponent implements OnInit {
  SortBy = SortBy;
  SortOrder = SortOrder;
  NotificationsTableHeaderColorScheme = NotificationsTableHeaderColorScheme;

  @Input()
  guessTimezone = 'UTC';

  @Input()
  notifications: NotificationsTableRowModel[] = [];

  @Input()
  notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;

  @Input()
  notificationsTableRowModelsSortOrder = SortOrder.DESC;

  @Input()
  tableSortBy: SortBy = SortBy.NONE;

  @Input()
  tableSortOrder: SortOrder = SortOrder.ASC;

  @Input() set notificationsTableRowModels(notificationRowModels: NotificationsTableRowModel[]) {
    this.notifications = notificationRowModels;
    this.setRowData();
  }

  @Input()
  headerColorScheme = SortableTableHeaderColorScheme.BLUE;

  @Output()
  sortNotificationsTableRowModelsEvent: EventEmitter<SortableEvent> = new EventEmitter();

  @Output()
  addNotificationEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  deleteNotificationEvent: EventEmitter<string> = new EventEmitter();

  @Output()
  loadNotificationEditFormEvent: EventEmitter<Notification> = new EventEmitter();

  notificationStyleDescriptionPipe = new NotificationStyleDescriptionPipe();
  dateDetailPipe = new FormatDateDetailPipe(this.timeZoneService);
  dateBriefPipe = new FormatDateBriefPipe(this.timeZoneService);
  rowsData : SortableTableCellData[][] = [];
  columnsData : ColumnData[] = [];

  backgroundColor: string = 'background-color: #198754;';

  constructor(private simpleModalService: SimpleModalService, private timeZoneService: TimezoneService) { }

  ngOnInit(): void {
    this.setRowData();
    this.setColumnData();
  }

  setColumnData(): void {
    this.columnsData = [
        {
            header: 'Title',
            sortBy: SortBy.NOTIFICATION_TITLE,
            headerClass: 'sort-by-title',
        },
        {
            header: 'Start Time',
            sortBy: SortBy.NOTIFICATION_START_TIME,
            headerClass: 'sort-by-start-time',
        },
        {
            header: 'End Time',
            sortBy: SortBy.NOTIFICATION_END_TIME,
            headerClass: 'sort-by-end-time',
        },
        {
            header: 'Target User',
            sortBy: SortBy.NOTIFICATION_TARGET_USER,
            headerClass: 'sort-by-target-user',
        },
        {
            header: 'Style',
            sortBy: SortBy.NOTIFICATION_STYLE,
            headerClass: 'sort-by-style',

        },
        {
          header: 'Creation Time',
          sortBy: SortBy.NOTIFICATION_CREATE_TIME,
          headerClass: 'sort-by-create-time',
        },
        {
            header: 'Action(s)',
        },
    ];
  }

  setRowData(): void {
    this.rowsData = this.notifications.map((rowModel) => {
      const rowData: SortableTableCellData[] = [
        {
          value: rowModel.notification.title,
          style: rowModel.isHighlighted ? this.backgroundColor : '',
        },
        {
          value: rowModel.notification.startTimestamp,
          displayValue: this.dateBriefPipe.transform(rowModel.notification.startTimestamp, this.guessTimezone),
          tooltip: this.dateDetailPipe.transform(rowModel.notification.startTimestamp, this.guessTimezone),
          style: rowModel.isHighlighted ? this.backgroundColor : '',
          class: 'ngb-tooltip-class',
        },
        {
          value: rowModel.notification.endTimestamp,
          displayValue: this.dateBriefPipe.transform(rowModel.notification.endTimestamp, this.guessTimezone),
          tooltip: this.dateDetailPipe.transform(rowModel.notification.endTimestamp, this.guessTimezone),
          style: rowModel.isHighlighted ? this.backgroundColor : '',
          class: 'ngb-tooltip-class',
        },
        {
          value: rowModel.notification.targetUser,
          displayValue: rowModel.notification.targetUser,
          style: rowModel.isHighlighted ? this.backgroundColor : '',

        },
        {
          value: rowModel.notification.style,
          displayValue: this.notificationStyleDescriptionPipe.transform(rowModel.notification.style),
          style: rowModel.isHighlighted ? this.backgroundColor : '',

        },
        {
          value: rowModel.notification.createdAt,
          displayValue: this.dateBriefPipe.transform(rowModel.notification.createdAt, this.guessTimezone),
          tooltip: this.dateDetailPipe.transform(rowModel.notification.createdAt, this.guessTimezone),
          style: rowModel.isHighlighted ? this.backgroundColor : '',
          class: 'ngb-tooltip-class',
        },
          this.createActionsCell(rowModel),
      ];

      return rowData;
    });
  }

  createActionsCell(rowModel: NotificationsTableRowModel): SortableTableCellData {
    const actionsCell: SortableTableCellData = {
      style: rowModel.isHighlighted ? this.backgroundColor : '',
      customComponent: {
      component: CellWithActionsComponent,
      componentData: (idx: number) => ({
        idx,
        notificationId: rowModel.notification.notificationId,
        deleteNotification: () => this.deleteNotification(rowModel.notification.notificationId,
          rowModel.notification.title),
        editNotification: () => this.loadNotificationEditForm(rowModel.notification),
      }),
      },
    };

    return actionsCell;
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortNotificationsTableRowModels(event: { sortBy: SortBy, sortOrder: SortOrder }): void {
    this.sortNotificationsTableRowModelsEvent.emit(event);
  }

  getAriaSort(by: SortBy): string {
    if (by !== this.notificationsTableRowModelsSortBy) {
      return 'none';
    }
    return this.notificationsTableRowModelsSortOrder === SortOrder.ASC ? 'ascending' : 'descending';
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
