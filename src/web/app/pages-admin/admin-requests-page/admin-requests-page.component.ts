import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../../services/account.service';
import { AccountRequest, AccountRequests } from '../../../types/api-output';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';

/**
 * Admin requests page.
 */
@Component({
  selector: 'tm-admin-requests-page',
  templateUrl: './admin-requests-page.component.html',
  styleUrls: ['./admin-requests-page.component.scss'],
  animations: [collapseAnim],
})
export class AdminRequestsPageComponent implements OnInit {

  accountRequestsPendingProcessing: AccountRequest[] = [];

  constructor(
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.accountService.getAccountRequestsPendingProcessing()
      .subscribe((resp: AccountRequests) => {
        this.accountRequestsPendingProcessing = resp.accountRequests;
        console.log(this.accountRequestsPendingProcessing);
      })
  }

}
