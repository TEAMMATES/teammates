import { Component } from '@angular/core';
import { finalize } from 'rxjs';
import { SupportRequestService } from 'src/web/services/supportrequest.service';
import { TableComparatorService } from 'src/web/services/table-comparator.service';
import { SortBy, SortOrder } from 'src/web/types/sort-properties';
import { SupportRequest, SupportRequestStatus } from 'src/web/types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { StatusMessageService } from 'src/web/services/status-message.service';

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

  constructor(private tableComparatorService: TableComparatorService, private supportRequestService: SupportRequestService, private statusMessageService: StatusMessageService) {
  }

  ngOnInit() {
    this.getAllSupportRequests();
  }

  getAllSupportRequests() {
    this.supportRequestService.getAllSupportRequests().pipe(finalize(() => { }))
      .subscribe(  {    next: (resp: {supportRequests: SupportRequest[]}) => {
        if (resp.supportRequests.length > 0) {
          this.supportRequests = resp.supportRequests;
        } else {
          this.statusMessageService.showErrorToast('No support requests!')
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
      complete: () => {

      },
    })
  }

  editSupportRequestStatus(event: { oldReq: SupportRequest, status: SupportRequestStatus }) {
    let newSupportReq = { ...event.oldReq };
    newSupportReq.status = event.status;
    const updatedId = newSupportReq.id;
    this.supportRequestService.updateSupportRequest(newSupportReq).pipe(finalize(() => { })).
    subscribe({
      next: (updatedSupportReq: SupportRequest) => {
        this.statusMessageService.showSuccessToast('Status sucessfully updated!')
        this.supportRequests = this.supportRequests.map(supportRequest => {
          if (supportRequest.id == updatedId) {
            return updatedSupportReq
          } else {
            return supportRequest
          }
        })
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  deleteSupportRequestWithId(id: string) {
    this.supportRequestService.deleteSupportRequest({ id }).pipe(finalize(() => { })).
    subscribe({
      next: () => {
        this.supportRequests = this.supportRequests.filter(supportRequest => {
          return supportRequest.id != id
        })
        this.statusMessageService.showSuccessToast('Support request succesfully deleted');
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
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
          strA = a.id.toString()
          strB = b.id.toString()
          break;
        case SortBy.SUPPORT_REQ_EMAIL:
          strA = a.email.toString();
          strB = b.email.toString();
          break;
        case SortBy.SUPPORT_REQ_NAME:
          strA = a.name.toString();
          strB = b.name.toString();
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
