import { Component } from '@angular/core';
import { finalize } from 'rxjs';
import { SupportRequestService } from 'src/web/services/supportrequest.service';
import { TableComparatorService } from 'src/web/services/table-comparator.service';
import { SortBy, SortOrder } from 'src/web/types/sort-properties';
import { SupportReqStatus, SupportRequest } from 'src/web/types/support-req-types';

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-support-page',
  templateUrl: './admin-support-page.component.html',
  styleUrls: ['./admin-support-page.component.scss']
})
export class AdminSupportPageComponent {
  supportRequests: SupportRequest[] = []
  supportReqSortBy: SortBy = SortBy.NONE;
  supportReqSortOrder: SortOrder = SortOrder.DESC;

  constructor(private tableComparatorService: TableComparatorService, private supportRequestService: SupportRequestService) {
    this.getAllSupportRequests()
  }

  getAllSupportRequests() {
    this.supportRequestService.getAllSupportRequests().pipe(finalize(() => { }))
      .subscribe((reqs: SupportRequest[]) => {
        this.supportRequests = reqs;
      })
  }

  editSupportRequestStatus(event: { oldReq: SupportRequest, status: SupportReqStatus }) {
    let newSupportReq = { ...event.oldReq };
    newSupportReq.status = event.status;
    this.supportRequestService.updateSupportRequest(newSupportReq);
  }

  deleteSupportRequestWithId(id: string) {
    this.supportRequestService.deleteSupportRequest({ id });
  }

  /**
   * Sorts the support requests list.
   */
  sortSupportRequestsList(sortBy: SortBy): void {
    this.supportReqSortOrder = this.supportReqSortBy !== sortBy ? SortOrder.ASC :
      (this.supportReqSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC);
    this.supportReqSortBy = sortBy;
    if (sortBy !== SortBy.NONE) {
      this.supportRequests.sort(this.sortSupportRequestsBy(sortBy, this.supportReqSortOrder));
    }
  }

  /**
   * Returns a function to determine the order of sort for students.
   */
  sortSupportRequestsBy(by: SortBy, order: SortOrder):
    ((a: SupportRequest, b: SupportRequest) => number) {
    return (a: SupportRequest, b: SupportRequest): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SUPPORT_REQ_TRACKING_ID:
          strA = a.trackingId.toString()
          strB = b.trackingId.toString()
          break;
        case SortBy.SUPPORT_REQ_EMAIL:
          strA = a.email.toString();
          strB = b.email.toString();
          break;
        case SortBy.SUPPORT_REQ_NAME:
          strA = a.name.toString();
          strB = b.name.toString();
          break;
        case SortBy.SUPPORT_REQ_TITLE:
          strA = a.title.toString();
          strB = b.title.toString();
          break;
        case SortBy.SUPPORT_REQ_ENQUIRY_TYPE:
          strA = a.type.toString();
          strB = b.type.toString();
          break;
        case SortBy.SUPPORT_REQ_STATUS:
          strA = a.status.toString();
          strB = b.status.toString();
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(by, order, strA, strB);
    };
  }
}
