import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { SupportRequestService } from 'src/web/services/supportrequest.service';
import { SupportRequest } from 'src/web/types/support-req-types';

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
  supportRequest: Observable<SupportRequest> | null = null;
  currId: string = ''; 

  constructor(private supportRequestService: SupportRequestService, 
    private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.currId = params['id']
      this.supportRequest = this.getSupportRequestFromBackend(this.currId)
    })
  }
  
  getSupportRequestFromBackend(id: string) {
    return this.supportRequestService.getOneSupportRequest({id});
  }
}
