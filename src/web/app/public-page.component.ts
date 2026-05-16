import { Component, inject } from '@angular/core';
import { PageComponent } from './page.component';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true" [hideAuthInfo]="true"></tm-page>',
  imports: [PageComponent],
})
export class PublicPageComponent {
  private authService = inject(AuthService);

  constructor() {
    if (environment.maintenance) {
      return;
    }

    this.authService.getAuthUser().subscribe(() => {
      // No need to do anything with result; this is necessary to get CSRF token
    });
  }
}
