import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Version redirect banner.
 */
@Component({
  selector: 'tm-redirect-banner',
  templateUrl: './redirect-banner.component.html',
  styleUrls: ['./redirect-banner.component.scss'],
})
export class RedirectBannerComponent {

  redirectUrl: string;
  supportEmail: string;
  isDismissed: boolean;

  constructor() {
    this.isDismissed = false;
    this.redirectUrl = environment.redirectUrl;
    this.supportEmail = environment.supportEmail;
  }
}
