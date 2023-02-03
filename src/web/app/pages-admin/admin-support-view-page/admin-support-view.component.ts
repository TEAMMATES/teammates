import { Component } from '@angular/core';
import { SupportRequest } from 'src/web/types/support-req-types';

import supportRequests from '../../../data/support-requests.dummy.json'

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-support-view-page',
  templateUrl: './admin-support-view-page.component.html',
  styleUrls: ['./admin-support-view-page.component.scss']
})
export class AdminSupportViewPageComponent {
  supportRequest: SupportRequest = supportRequests[0];
}
