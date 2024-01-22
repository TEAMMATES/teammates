import { LocationStrategy } from '@angular/common';
import { Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MasqueradeModeService } from '../../../services/masquerade-mode.service';

/**
 * Router link that preserves masquerade mode
 */
@Directive({
  selector: 'a[tmRouterLink]',
})
export class TeammatesRouterDirective extends RouterLink {
  private queryParamsInternal: { [k: string]: any } = {};

  @Input()
  set tmRouterLink(commands: any[] | string) {
    this.routerLink = commands;
  }

  @Input()
  // @ts-expect-error query params is redefined in this class
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

  constructor(router: Router, route: ActivatedRoute,
              renderer: Renderer2, el: ElementRef, locationStrategy: LocationStrategy,
              private masqueradeModeService: MasqueradeModeService) {
    super(router, route, null, renderer, el, locationStrategy);
  }
}
