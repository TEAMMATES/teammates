import { Component } from '@angular/core';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';

/**
 * Features page.
 */
@Component({
  selector: 'tm-features-page',
  templateUrl: './features-page.component.html',
  styleUrls: ['./features-page.component.scss'],
  imports: [TeammatesRouterDirective],
})
export class FeaturesPageComponent {}
