import { Component } from '@angular/core';
import { TeammatesRouterDirective } from '../components/teammates-router/teammates-router.directive';

/**
 * "Page not found" page.
 *
 * Users will be redirected to this page if they attempt to navigate to an unknown page of the application.
 */
@Component({
    selector: 'tm-page-not-found',
    templateUrl: './page-not-found.component.html',
    styleUrls: ['./page-not-found.component.scss'],
    imports: [TeammatesRouterDirective],
})
export class PageNotFoundComponent {}
