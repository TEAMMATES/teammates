/* eslint-disable @angular-eslint/prefer-inject */
import { LocationStrategy } from '@angular/common';
import { Directive, ElementRef, Input, Renderer2 } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MasqueradeModeService } from '../../../services/masquerade-mode.service';

/**
 * Router link that preserves masquerade mode
 */
@Directive({ selector: 'a[tmRouterLink]' })
export class TeammatesRouterDirective extends RouterLink {
  // TODO: Do not extend RouterLink
  private queryParamsInternal: Record<string, unknown> = {};

  @Input()
  set tmRouterLink(commands: unknown[] | string | null | undefined) {
    this.routerLink = commands;
  }

  @Input()
  // @ts-expect-error query params is redefined in this class
  set queryParams(params: Record<string, unknown>) {
    this.queryParamsInternal = params;
    super.queryParams = this.queryParams;
  }

  override get queryParams(): Record<string, unknown> {
    const accountIdParam: string = this.masqueradeModeService.getMasqueradeAccountId();
    if (accountIdParam !== '') {
      return { ...this.queryParamsInternal, masqueradeaccountid: accountIdParam };
    }
    return this.queryParamsInternal;
  }

  constructor(
    router: Router,
    route: ActivatedRoute,
    renderer: Renderer2,
    el: ElementRef,
    locationStrategy: LocationStrategy,
    private masqueradeModeService: MasqueradeModeService,
  ) {
    super(router, route, null, renderer, el, locationStrategy);
  }
}
