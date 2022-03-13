import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TimezoneService } from '../../../../services/timezone.service';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { NotificationsTableHeaderColorScheme, NotificationsTableRowModel } from './notifications-table-model';

@Component({
  selector: 'tm-notifications-table',
  templateUrl: './notifications-table.component.html',
  styleUrls: ['./notifications-table.component.scss'],
})
export class NotificationsTableComponent {
  SortBy = SortBy;
  SortOrder = SortOrder;
  NotificationsTableHeaderColorScheme = NotificationsTableHeaderColorScheme;

  guessTimezone = 'UTC';

  @Input()
  notificationsTableRowModels: NotificationsTableRowModel[] = [];

  @Input()
  notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;

  @Input()
  notificationsTableRowModelsSortOrder = SortOrder.ASC;

  @Input()
  headerColorScheme = NotificationsTableHeaderColorScheme.BLUE;

  @Output()
  sortNotificationsTableRowModelsEvent: EventEmitter<SortBy> = new EventEmitter();

  constructor(timezoneService: TimezoneService) {
    this.guessTimezone = timezoneService.guessTimezone();
  }

  /**
   * Sorts the list of feedback session row.
   */
   sortNotificationsTableRowModels(by: SortBy): void {
    this.sortNotificationsTableRowModelsEvent.emit(by);
  }
}
