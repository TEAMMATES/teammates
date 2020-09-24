import { Component } from '@angular/core';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true" [hideAuthInfo]="true"></tm-page>',
})
export class PublicPageComponent {}
