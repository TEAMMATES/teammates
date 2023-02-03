import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SupportRequest } from 'src/web/types/support-req-types';

/**
 * A table displaying a list of support ticket items
 */
@Component({
  selector: 'tm-support-ticket-list',
  templateUrl: './support-ticket-list.component.html',
  styleUrls: ['./support-ticket-list.component.scss'],
})
export class SupportListComponent {
  @Input() supportRequests: SupportRequest[] = []; 
  @Input() tableSortBy: SortBy = SortBy.NONE;
  @Input() tableSortOrder: SortOrder = SortOrder.DESC;

  @Output() sortSupportTicketListEvent: EventEmitter<SortBy> = new EventEmitter();

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  sortSupportTicketList(sortBy: SortBy) {
    this.sortSupportTicketListEvent.emit(sortBy)
  }
}
