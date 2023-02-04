import { Component, Input } from '@angular/core';
import { SupportReqEnquiryType, SupportReqStatus, SupportRequest } from 'src/web/types/support-req-types';

/**
 * A detailed view of the support request
 */
@Component({
  selector: 'tm-support-ticket-view',
  templateUrl: './support-ticket-view.component.html',
  styleUrls: ['./support-ticket-view.component.scss'],
})
export class SupportViewComponent {
  @Input() supportRequest: SupportRequest = {
    trackingId: "-1",
    email: '',
    name: '',
    type: SupportReqEnquiryType.GENERAL_HELP,
    title: '',
    initial_msg: '',
    status: SupportReqStatus.NEW,
    createdAt: 0,
    updatedAt: 0,
  };
}
