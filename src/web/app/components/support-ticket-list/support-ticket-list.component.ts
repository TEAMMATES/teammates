import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SupportRequest } from 'src/web/types/support-req-types';
import { SimpleModalService } from 'src/web/services/simple-modal.service';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

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
  @Input() useGrayHeading: boolean = false; 
  @Input() isHideTableHead: boolean = false;
  @Input() tableSortOrder: SortOrder = SortOrder.DESC;
  @Input() isActionButtonsEnabled: boolean = true;

  @Output() sortSupportTicketListEvent: EventEmitter<SortBy> = new EventEmitter();

  constructor(private simpleModalService: SimpleModalService) {

  }

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  sortSupportTicketList(sortBy: SortBy) {
    this.sortSupportTicketListEvent.emit(sortBy)
  }

  /**
   * Open the delete student confirmation modal.
   */
    openDeleteModal(supportRequest: SupportRequest): void {
      const modalContent: string = `Are you sure you want to remove support request ID <strong>${supportRequest.trackingId}</strong>?`;
      const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
          `Delete support request ID <strong>${supportRequest.trackingId}</strong>?`, SimpleModalType.DANGER, modalContent);
      modalRef.result.then(() => {
        console.log("Deleting Support Request" + supportRequest.trackingId)
      }, () => {});
    }
}
