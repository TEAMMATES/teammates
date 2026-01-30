import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { InstructorRequestFormModel } from './instructor-request-form/instructor-request-form-model';
import { environment } from '../../../environments/environment';
import { NgIf } from '@angular/common';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';
import { InstructorRequestFormComponent } from './instructor-request-form/instructor-request-form.component';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
  imports: [
    NgIf,
    TeammatesRouterDirective,
    InstructorRequestFormComponent,
  ],
})
export class RequestPageComponent {

  accountRequestFormUrl: SafeResourceUrl | null;
  isDeclarationDone: boolean = false;
  submittedFormData: InstructorRequestFormModel | null = null;

  constructor(private sanitizer: DomSanitizer) {
    this.accountRequestFormUrl = environment.accountRequestFormUrl
        ? this.sanitizer.bypassSecurityTrustResourceUrl(environment.accountRequestFormUrl)
        : null;
  }

  onDeclarationButtonClicked(): void {
    this.isDeclarationDone = true;
  }

  onRequestSubmitted(data: InstructorRequestFormModel): void {
    this.submittedFormData = data;
  }
}
