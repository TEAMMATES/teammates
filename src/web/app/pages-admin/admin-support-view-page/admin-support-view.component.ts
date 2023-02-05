import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { StatusMessageService } from 'src/web/services/status-message.service';
import { SupportRequestService } from 'src/web/services/supportrequest.service';
import { SupportRequest } from 'src/web/types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin support view list page.
 */
@Component({
  selector: 'tm-admin-support-view-page',
  templateUrl: './admin-support-view-page.component.html',
  styleUrls: ['./admin-support-view-page.component.scss']
})
export class AdminSupportViewPageComponent {
  supportRequest: SupportRequest | null = null;
  currId: string = ''; 

  constructor(private supportRequestService: SupportRequestService, 
    private route: ActivatedRoute, private statusMessageService: StatusMessageService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.currId = params['id']
      this.getSupportRequestFromBackend(this.currId)
    })
  }
  
  getSupportRequestFromBackend(id: string) {
    return this.supportRequestService.getOneSupportRequest({id}).pipe(finalize(() => { }))
    .subscribe(  {    next: (resp: SupportRequest) => {
      this.supportRequest = resp;
    },
    error: (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    },
    complete: () => {

    },
  });
  }
}
