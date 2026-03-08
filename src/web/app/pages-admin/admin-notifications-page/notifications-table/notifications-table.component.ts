import { NgClass, NgIf, NgFor } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { NotificationsTableHeaderColorScheme, NotificationsTableRowModel } from './notifications-table-model';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { Notification } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { FormatDateBriefPipe } from '../../../components/teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../../../components/teammates-common/format-date-detail.pipe';
import { NotificationStyleClassPipe } from '../../../components/teammates-common/notification-style-class.pipe';
import {
    NotificationStyleDescriptionPipe,
} from '../../../components/teammates-common/notification-style-description.pipe';

// Import types from sortable-table
import { SortableTableComponent, ColumnData, SortableTableCellData, SortableTableHeaderColorScheme } from '../../../components/sortable-table/sortable-table.component';

@Component({
    selector: 'tm-notifications-table',
    templateUrl: './notifications-table.component.html',
    styleUrls: ['./notifications-table.component.scss'],
    standalone: true,
    imports: [
        NgClass,
        NgIf,
        NgFor,
        NgbTooltip,
        FormatDateDetailPipe,
        FormatDateBriefPipe,
        NotificationStyleDescriptionPipe,
        NotificationStyleClassPipe,
        SortableTableComponent,
    ],
})
export class NotificationsTableComponent implements OnChanges {
    SortBy = SortBy;
    SortOrder = SortOrder;
    NotificationsTableHeaderColorScheme = NotificationsTableHeaderColorScheme;

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
    sortNotificationsTableRowModelsEvent: EventEmitter<{ sortBy: SortBy, sortOrder: SortOrder }> = new EventEmitter();

    @Output()
    deleteNotificationEvent: EventEmitter<string> = new EventEmitter();

    @Output()
    loadNotificationEditFormEvent: EventEmitter<Notification> = new EventEmitter();

    get tableSortBy(): SortBy {
        return this.notificationsTableRowModelsSortBy;
    }
    get tableSortOrder(): SortOrder {
        return this.notificationsTableRowModelsSortOrder;
    }
    get tableHeaderColorScheme(): SortableTableHeaderColorScheme {
        return this.headerColorScheme as unknown as SortableTableHeaderColorScheme;
    }

    columnsData: ColumnData[] = [
        {
            header: 'Title',
            sortBy: SortBy.NOTIFICATION_TITLE,
            alignment: 'start',
        },
        {
            header: 'Message',
            alignment: 'start',
        },
        {
            header: 'Start Time',
            sortBy: SortBy.NOTIFICATION_START_TIME,
            alignment: 'center',
        },
        {
            header: 'Created At',
            sortBy: SortBy.NOTIFICATION_CREATE_TIME,
            alignment: 'center',
        },
        {
            header: 'Actions',
            alignment: 'center',
        },
    ];

    rowsData: SortableTableCellData[][] = [];

    constructor(private simpleModalService: SimpleModalService) { }

    ngOnChanges(_changes: SimpleChanges): void {
        this.rowsData = this.notificationsTableRowModels.map((rowModel) => [
            {
                value: rowModel.notification.title,
                displayValue: rowModel.notification.title,
            },
            {
                value: rowModel.notification.message,
                displayValue: rowModel.notification.message,
            },
            {
                value: rowModel.notification.startTimestamp,
                displayValue: this.formatDate(rowModel.notification.startTimestamp),
            },
            {
                value: rowModel.notification.createdAt,
                displayValue: this.formatDate(rowModel.notification.createdAt),
            },
            {
                value: rowModel.notification.notificationId,
                displayValue: 'Actions',
            },
        ]);
    }

    formatDate(timestamp: number): string {
        return new Date(timestamp).toLocaleString('en-US', { timeZone: this.guessTimezone || 'UTC' });
    }

    /**
     * Handles sort event from sortable-table.
     */
    sortNotificationsTableRowModels(event: { sortBy: SortBy, sortOrder: SortOrder }): void {
        this.notificationsTableRowModelsSortBy = event.sortBy;
        this.notificationsTableRowModelsSortOrder = event.sortOrder;
        this.ngOnChanges({});
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