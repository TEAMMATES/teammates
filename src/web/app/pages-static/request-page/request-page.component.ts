import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
})
export class RequestPageComponent {

  googleDocUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    this.googleDocUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
        'https://docs.google.com/forms/d/e/1FAIpQLSfmiNsVnVANdB1-cOwkfn9l8Ts8eN-CtolLQwi93Nrug0sngw/viewform'
        + '?embedded=true&formkey=dDNsQmU4QXVYTVRhMjA2dEJWYW82Umc6MQ');
  }

}
