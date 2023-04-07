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
  private queryParamsInternal: { [k: string]: any } = {};

  @Input()
  set tmRouterLink(commands: any[] | string) {
    this.routerLink = commands;
  }

  @Input()
  // @ts-ignore
  set queryParams(params: { [k: string]: any }) {
    this.queryParamsInternal = params;
  }

  override get queryParams(): { [k: string]: any } {
    const userParam: string = this.masqueradeModeService.getMasqueradeUser();
    if (userParam !== '') {
      return { ...this.queryParamsInternal, user: userParam };
    }
    return this.queryParamsInternal;
  }

  constructor(router: Router, route: ActivatedRoute, locationStrategy: LocationStrategy,
              private masqueradeModeService: MasqueradeModeService) {
    super(router, route, locationStrategy);
  }
}
