import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MasqueradeModeService } from '../services/masquerade-mode.service';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true" [hideAuthInfo]="true"></tm-page>',
})
export class PublicPageComponent {

  constructor(private route: ActivatedRoute,
              private masqueradeModeService: MasqueradeModeService) {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.user) {
        this.masqueradeModeService.setMasqueradeUser(queryParams.user);
      }
    });
  }

}
