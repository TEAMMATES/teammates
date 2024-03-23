import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { environment } from '../../../environments/environment';
import { InstructorRequestFormData } from './instructor-request-form/InstructorRequestFormData';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
})
export class RequestPageComponent {

  accountRequestFormUrl: SafeResourceUrl | null;
  isDeclarationDone: boolean = false;
  submittedFormData: InstructorRequestFormData | null = null;

  constructor(private sanitizer: DomSanitizer) {
    this.accountRequestFormUrl = environment.accountRequestFormUrl
        ? this.sanitizer.bypassSecurityTrustResourceUrl(environment.accountRequestFormUrl)
        : null;
  }

  onDeclarationButtonClicked() {
    this.isDeclarationDone = true;
  }

  onRequestSubmitted(data: InstructorRequestFormData) {
    this.submittedFormData = data;
  }
}
