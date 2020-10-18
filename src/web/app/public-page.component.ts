import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { MasqueradeModeService } from '../services/masquerade-mode.service';
import { AuthInfo } from '../types/api-output';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true" [hideAuthInfo]="true"></tm-page>',
})
export class PublicPageComponent {

  constructor(private route: ActivatedRoute,
              private authService: AuthService,
              private masqueradeModeService: MasqueradeModeService) {
    if (environment.maintenance) {
      return;
    }
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe((res: AuthInfo) => {
        if (res.user && res.masquerade) {
          this.masqueradeModeService.setMasqueradeUser(res.user.id);
        }
      });
    });
  }

}
