import { Component, OnInit } from '@angular/core';
import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
})
export class RequestPageComponent implements OnInit  {

  /**
   * The URL to account request Google form.
   */
  public googleDocUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {}

  public ngOnInit(): void {
    this.googleDocUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
        'https://docs.google.com/forms/d/e/1FAIpQLSfmiNsVnVANdB1-cOwkfn9l8Ts8eN-CtolLQwi93Nrug0sngw/viewform'
        + '?embedded=true&formkey=dDNsQmU4QXVYTVRhMjA2dEJWYW82Umc6MQ');
  }

}
