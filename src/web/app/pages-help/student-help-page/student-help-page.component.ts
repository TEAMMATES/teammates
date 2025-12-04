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

  // Alexa: Smoothly scroll to an anchor on the same page
  scroll(anchor: string): void {
    if (anchor === 'top') {
      window.scrollTo({ top: 0, behavior: 'smooth' });
      return;
    }

    const el = document.getElementById(anchor);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}

