import { Component } from '@angular/core';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true"></tm-page>',
})
export class PublicPageComponent {

  constructor() {}

}
