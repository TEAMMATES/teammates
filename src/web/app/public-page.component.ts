import { Component } from '@angular/core';
import { PageComponent } from './page.component';
import { PageAuthType } from './page.authtype';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [hideAuthInfo]="true" [pageAuthType]="pageAuthType"></tm-page>',
  imports: [PageComponent],
})
export class PublicPageComponent {
  pageAuthType = PageAuthType.PUBLIC;
}
