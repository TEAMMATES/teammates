import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
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

  redirectUrl: SafeResourceUrl | null;
  supportEmail: string;

  constructor(private sanitizer: DomSanitizer) {
    this.redirectUrl = environment.redirectUrl
        ? this.sanitizer.bypassSecurityTrustResourceUrl(environment.redirectUrl)
        : null;
    this.supportEmail = environment.supportEmail;
  }
}
