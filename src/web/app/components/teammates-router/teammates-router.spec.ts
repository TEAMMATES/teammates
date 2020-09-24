import { LocationStrategy } from '@angular/common';
import { inject } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MasqueradeModeService } from '../../../services/masquerade-mode.service';
import { TeammatesRouterDirective } from './teammates-router.directive';

describe('TeammatesRouterDirective', () => {
  it('should create an instance', () => {
    inject([Router, ActivatedRoute, LocationStrategy, MasqueradeModeService],
        (router: Router, route: ActivatedRoute, locationStrategy: LocationStrategy,
         masqueradeModeService: MasqueradeModeService) => {
          const directive: TeammatesRouterDirective = new TeammatesRouterDirective(
              router, route, locationStrategy, masqueradeModeService);
          expect(directive).toBeTruthy();
        });

  });
});
