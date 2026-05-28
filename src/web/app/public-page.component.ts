import { Component } from '@angular/core';
import { PageComponent } from './page.component';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [hideAuthInfo]="true"></tm-page>',
  imports: [PageComponent],
})
export class PublicPageComponent {}
