import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { environment } from '../../../environments/environment';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
})
export class RequestPageComponent {

  accountRequestFormUrl: SafeResourceUrl | null;

  constructor(private sanitizer: DomSanitizer) {
    this.accountRequestFormUrl = environment.accountRequestFormUrl
        ? this.sanitizer.bypassSecurityTrustResourceUrl(environment.accountRequestFormUrl)
        : null;
  }

}
