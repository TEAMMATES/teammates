import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { InstructorRequestFormData } from './instructor-request-form/InstructorRequestFormData';
import { environment } from '../../../environments/environment';

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

  onDeclarationButtonClicked(): void {
    this.isDeclarationDone = true;
  }

  onRequestSubmitted(data: InstructorRequestFormData): void {
    this.submittedFormData = data;
  }
}
