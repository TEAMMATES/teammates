import { LocationStrategy } from '@angular/common';
import { Directive, Input } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

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
    const urlParams: URLSearchParams = new URLSearchParams(window.location.search);
    const userParam: string | null = urlParams.get('user');
    if (userParam) {
      return { ...this._queryParams, user: userParam };
    }
    return this._queryParams;
  }

  constructor(router: Router, route: ActivatedRoute, locationStrategy: LocationStrategy) {
    super(router, route, locationStrategy);
  }
}
