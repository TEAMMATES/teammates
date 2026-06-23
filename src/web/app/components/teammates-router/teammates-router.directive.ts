/* eslint-disable @angular-eslint/prefer-inject */
import { LocationStrategy } from '@angular/common';
import { Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

/**
 * Router link that preserves masquerade mode
 */
@Directive({ selector: 'a[tmRouterLink]' })
export class TeammatesRouterDirective extends RouterLink {
  // TODO: Do not extend RouterLink
  @Input()
  set tmRouterLink(commands: unknown[] | string | null | undefined) {
    this.routerLink = commands;
  }

  constructor(
    router: Router,
    route: ActivatedRoute,
    renderer: Renderer2,
    el: ElementRef,
    locationStrategy: LocationStrategy,
  ) {
    super(router, route, null, renderer, el, locationStrategy);
  }
}
