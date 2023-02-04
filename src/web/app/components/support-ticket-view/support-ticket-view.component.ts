import { Component, Input } from '@angular/core';
import { SupportReqStatus, SupportRequest } from 'src/web/types/support-req-types';
import { SupportRequestType } from 'src/web/types/api-output';

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
    type: SupportRequestType.GENERAL_ENQUIRY,
    title: '',
    message: '',
    status: SupportReqStatus.NEW,
    createdAt: 0,
    updatedAt: 0,
  };
}
