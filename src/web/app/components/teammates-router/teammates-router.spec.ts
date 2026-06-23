import { LocationStrategy } from '@angular/common';
import { ElementRef, Renderer2 } from '@angular/core';
import { inject } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { TeammatesRouterDirective } from './teammates-router.directive';

describe('TeammatesRouterDirective', () => {
  it('should create an instance', () => {
    inject(
      [Router, ActivatedRoute, Renderer2, ElementRef, LocationStrategy],
      (
        router: Router,
        route: ActivatedRoute,
        renderer: Renderer2,
        el: ElementRef,
        locationStrategy: LocationStrategy,
      ) => {
        const directive: TeammatesRouterDirective = new TeammatesRouterDirective(
          router,
          route,
          renderer,
          el,
          locationStrategy,
        );
        expect(directive).toBeTruthy();
      },
    );
  });
});
