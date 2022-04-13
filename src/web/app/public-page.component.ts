import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true" [hideAuthInfo]="true"></tm-page>',
})
export class PublicPageComponent {
  constructor(private route: ActivatedRoute,
              private authService: AuthService) {
    if (environment.maintenance) {
      return;
    }
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe(() => {
        // No need to do anything with result; this is necessary to get CSRF token
      });
    });
  }

}
