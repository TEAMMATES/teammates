import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';

/**
 * Student help page.
 */
@Component({
    selector: 'tm-student-help-page',
    templateUrl: './student-help-page.component.html',
    styleUrls: ['./student-help-page.component.scss'],
    imports: [TeammatesRouterDirective],
})
export class StudentHelpPageComponent {

  readonly supportEmail: string = environment.supportEmail;

}
