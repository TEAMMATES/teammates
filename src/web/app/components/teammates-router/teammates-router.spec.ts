import { LocationStrategy } from '@angular/common';
import { ElementRef, Renderer2 } from '@angular/core';
import { inject } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { TeammatesRouterDirective } from './teammates-router.directive';
import { MasqueradeModeService } from '../../../services/masquerade-mode.service';

describe('TeammatesRouterDirective', () => {
  it('should create an instance', () => {
    inject([Router, ActivatedRoute, Renderer2, ElementRef, LocationStrategy, MasqueradeModeService],
        (router: Router, route: ActivatedRoute, renderer: Renderer2, el: ElementRef,
         locationStrategy: LocationStrategy, masqueradeModeService: MasqueradeModeService) => {
          const directive: TeammatesRouterDirective = new TeammatesRouterDirective(
              router, route, renderer, el, locationStrategy, masqueradeModeService);
          expect(directive).toBeTruthy();
        });

  });
});
