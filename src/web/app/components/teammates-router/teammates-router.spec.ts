import { LocationStrategy } from '@angular/common';
import { inject } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { TeammatesRouterDirective } from './teammates-router.directive';

describe('TeammatesRouterDirective', () => {
  it('should create an instance', () => {
    inject([Router, ActivatedRoute, LocationStrategy],
        (router: Router, route: ActivatedRoute, locationStrategy: LocationStrategy) => {
          const directive: TeammatesRouterDirective = new TeammatesRouterDirective(router, route, locationStrategy);
          expect(directive).toBeTruthy();
        });

  });
});
