import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SupportReqStatus, SupportRequest } from 'src/web/types/support-req-types';
import { SimpleModalService } from 'src/web/services/simple-modal.service';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';

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
  @Output() deleteSupportTicketEvent: EventEmitter<string> = new EventEmitter(); 
  @Output() updateSupportTicketStatusEvent: EventEmitter<{oldReq: SupportRequest, status: SupportReqStatus}> = new EventEmitter(); 

  SupportReqStatusTypes = SupportReqStatus
  SUPPORT_REQ_STATUSES = Object.values(SupportReqStatus).slice(0, Object.values(SupportReqStatus).length / 2); 
  collectionSize: number = this.supportRequests.length
  supportRequestRows: SupportRequest[] = this.supportRequests
  supportReqRowsInPage: SupportRequest[] = this.supportRequestRows
  page: number = 1;
  pageSize: number = 10;
  filter = new FormControl('')

  constructor(private simpleModalService: SimpleModalService) {
    this.supportReqRowsInPage = this.supportRequestRows
    this.collectionSize = this.supportRequests.length
  }

  ngOnInit() {
    this.onFilterChange(); 
  }

  onFilterChange() {
    this.filter.valueChanges.pipe(debounceTime(400), distinctUntilChanged()).subscribe(searchFilter => {
      this.paginateAndFilter(searchFilter), 1000
    })
  }

  ngOnChanges(_changes: SimpleChanges) {
    this.collectionSize = this.supportRequests.length;
    this.supportRequestRows = this.supportRequests
    this.supportReqRowsInPage = this.supportRequestRows
  }

  refreshSupportReqPage() {
		this.paginateAndFilter()
	}

  paginateAndFilter(searchFilter = this.filter.value) {
    this.supportReqRowsInPage = this.supportRequests.map((req, i) => ({ id: i + 1, ...req })).slice(
			(this.page - 1) * this.pageSize,
			(this.page - 1) * this.pageSize + this.pageSize,
		)
    this.supportReqRowsInPage = this.matches(searchFilter, this.supportReqRowsInPage)
  }

  matches(text: string, rows: SupportRequest[]): SupportRequest[] {
    return text === '' || text === null ? rows : rows.filter((row) => {
      const term = text.toLowerCase(); 
  
      return (
        row.trackingId.toString().toLowerCase().includes(term) || 
        row.name.toString().toLowerCase().includes(term) || 
        row.email.toString().toLowerCase().includes(term) || 
        row.status.toString().toLowerCase().includes(term) || 
        row.enquiry_type.toString().toLowerCase().includes(term) || 
        row.title.toString().toLowerCase().includes(term)
      )
    })
  }

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  sortSupportTicketList(sortBy: SortBy) {
    this.sortSupportTicketListEvent.emit(sortBy)
  }

  updateSupportRequestStatus(oldReq: SupportRequest, status: SupportReqStatus) {
    this.updateSupportTicketStatusEvent.emit({oldReq, status})
  }

  /**
   * Open the delete student confirmation modal.
   */
    openDeleteModal(supportRequest: SupportRequest): void {
      const modalContent: string = `Are you sure you want to remove support request with ID <strong>${supportRequest.trackingId}</strong>?`;
      const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
          `Delete support request with ID <strong>${supportRequest.trackingId}</strong>?`, SimpleModalType.DANGER, modalContent);
      modalRef.result.then(() => {
        this.deleteSupportTicketEvent.emit(supportRequest.trackingId);
      }, () => {});
    }
}
