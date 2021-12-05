import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Student help page.
 */
@Component({
  selector: 'tm-student-help-page',
  templateUrl: './student-help-page.component.html',
  styleUrls: ['./student-help-page.component.scss'],
})
export class StudentHelpPageComponent {

  readonly supportEmail: string = environment.supportEmail;

}
