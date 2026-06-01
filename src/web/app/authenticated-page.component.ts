import { Component } from '@angular/core';
import { PageComponent } from './page.component';
import { PageAuthType } from './page.authtype';

/**
 * Component for authenticated pages.
 */
@Component({
  selector: 'tm-authenticated-page',
  template: '<tm-page [hideAuthInfo]="false" [pageAuthType]="PageAuthType.AUTHENTICATED"></tm-page>',
  imports: [PageComponent],
})
export class AuthenticatedPageComponent {
  PageAuthType = PageAuthType;
}
