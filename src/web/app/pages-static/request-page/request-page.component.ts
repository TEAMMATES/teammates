import { Component } from '@angular/core';
import { InstructorRequestFormModel } from './instructor-request-form/instructor-request-form-model';
import { InstructorRequestFormComponent } from './instructor-request-form/instructor-request-form.component';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
  imports: [TeammatesRouterDirective, InstructorRequestFormComponent],
})
export class RequestPageComponent {
  isDeclarationDone = false;
  submittedFormData: InstructorRequestFormModel | null = null;

  onDeclarationButtonClicked(): void {
    this.isDeclarationDone = true;
  }

  onRequestSubmitted(data: InstructorRequestFormModel): void {
    this.submittedFormData = data;
  }
}
