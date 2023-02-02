import { Component } from '@angular/core';

import support_requests from '../../../data/support-requests.dummy.json'

/**
 * Admin search page.
 */
@Component({
  selector: 'tm-admin-support-page',
  templateUrl: './admin-support-page.component.html',
  styleUrls: ['./admin-support-page.component.scss']
})
export class AdminSupportPageComponent {
  support_requests = support_requests

  constructor(
  ) {}
}
