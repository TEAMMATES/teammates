import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { SupportRequestService } from 'src/web/services/supportrequest.service';
import { SupportRequest } from 'src/web/types/support-req-types';

import supportRequests from '../../../data/support-requests.dummy.json'

/**
 * Admin support view list page.
 */
@Component({
  selector: 'tm-admin-support-view-page',
  templateUrl: './admin-support-view-page.component.html',
  styleUrls: ['./admin-support-view-page.component.scss']
})
export class AdminSupportViewPageComponent {
  // supportRequests: Observable<SupportRequest[]> 
  supportRequest: Observable<SupportRequest> = supportRequests[0]; 

  constructor(private supportRequestService: SupportRequestService) {
  }
  
  getSupportRequestFromBackend(id: string) {
    return this.supportRequestService.getSupportRequest(id);
  }
}
