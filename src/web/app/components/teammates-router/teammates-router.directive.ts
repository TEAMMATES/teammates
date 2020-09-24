import { LocationStrategy } from '@angular/common';
import { Directive, Input } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';
import { MasqueradeModeService } from '../../../services/masquerade-mode.service';

/**
 * Router link that preserves masquerade mode
 */
@Directive({
  selector: 'a[tmRouterLink]',
})
export class TeammatesRouterDirective extends RouterLinkWithHref {
  private _queryParams: { [k: string]: any } = {};

  @Input()
  set tmRouterLink(commands: any[] | string) {
    this.routerLink = commands;
  }

  @Input()
  set queryParams(params: { [k: string]: any }) {
    this._queryParams = params;
  }

  get queryParams(): { [k: string]: any } {
    const userParam: string = this.masqueradeModeService.getMasqueradeUser();
    if (userParam !== '') {
      return { ...this._queryParams, user: userParam };
    }
    return this._queryParams;
  }

  constructor(router: Router, route: ActivatedRoute, locationStrategy: LocationStrategy,
              private masqueradeModeService: MasqueradeModeService) {
    super(router, route, locationStrategy);
  }
}
