import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { EmailTemplateService } from '../../../services/email-template.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { EmailTemplate, EmailTemplates } from '../../../types/api-output';
import { EmailTemplateUpdateRequest } from '../../../types/api-request';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { ErrorMessageOutput } from '../../error-message-output';

interface EmailTemplateModel {
  subject: string;
  body: string;
  isCustomized: boolean;
}

/**
 * Admin page for viewing and editing configurable email templates.
 */
@Component({
  selector: 'tm-admin-email-templates-page',
  templateUrl: './admin-email-templates-page.component.html',
  styleUrls: ['./admin-email-templates-page.component.scss'],
  imports: [
    FormsModule,
    LoadingSpinnerDirective,
    LoadingRetryComponent,
  ],
})
export class AdminEmailTemplatesPageComponent implements OnInit {

  templateKeys: string[] = [];
  selectedKey: string = '';

  isKeysLoading: boolean = false;
  hasKeysLoadingFailed: boolean = false;
  isTemplateLoading: boolean = false;
  isSaving: boolean = false;

  model: EmailTemplateModel = {
    subject: '',
    body: '',
    isCustomized: false,
  };

  constructor(
    private emailTemplateService: EmailTemplateService,
    private statusMessageService: StatusMessageService,
    private sanitizer: DomSanitizer,
  ) {}

  ngOnInit(): void {
    this.loadTemplateKeys();
  }

  /**
   * Returns a trusted HTML string for the live preview panel.
   * DomSanitizer is used so that inline <style> blocks — common in
   * HTML email templates — are rendered faithfully in the preview.
   */
  get sanitizedPreview(): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(this.model.body);
  }

  /**
   * Loads the list of configurable template keys, then auto-loads the first one.
   */
  loadTemplateKeys(): void {
    this.isKeysLoading = true;
    this.hasKeysLoadingFailed = false;
    this.emailTemplateService.getEmailTemplates()
      .pipe(finalize(() => { this.isKeysLoading = false; }))
      .subscribe({
        next: (data: EmailTemplates) => {
          this.templateKeys = data.templateKeys;
          if (this.templateKeys.length > 0) {
            this.selectedKey = this.templateKeys[0];
            this.loadTemplate(this.selectedKey);
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasKeysLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Called when the admin selects a different template from the dropdown.
   */
  onTemplateKeyChange(): void {
    this.loadTemplate(this.selectedKey);
  }

  /**
   * Fetches the template body and subject for the given key.
   * Falls back to the static default if no custom version exists.
   */
  loadTemplate(templateKey: string): void {
    this.isTemplateLoading = true;
    this.emailTemplateService.getEmailTemplate(templateKey)
      .pipe(finalize(() => { this.isTemplateLoading = false; }))
      .subscribe({
        next: (template: EmailTemplate) => {
          this.model = {
            subject: template.subject,
            body: template.body,
            isCustomized: template.isCustomized,
          };
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Persists the current subject and body to the database.
   */
  saveTemplate(): void {
    this.isSaving = true;
    const request: EmailTemplateUpdateRequest = {
      templateKey: this.selectedKey,
      subject: this.model.subject,
      body: this.model.body,
      resetToDefault: false,
    };
    this.emailTemplateService.updateEmailTemplate(request)
      .pipe(finalize(() => { this.isSaving = false; }))
      .subscribe({
        next: (template: EmailTemplate) => {
          this.model.isCustomized = template.isCustomized;
          this.statusMessageService.showSuccessToast('Email template saved successfully.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Deletes the custom DB record for the current template,
   * then reloads the static default into the editor.
   */
  revertToDefault(): void {
    this.isSaving = true;
    const request: EmailTemplateUpdateRequest = {
      templateKey: this.selectedKey,
      subject: '',
      body: '',
      resetToDefault: true,
    };
    this.emailTemplateService.updateEmailTemplate(request)
      .pipe(finalize(() => { this.isSaving = false; }))
      .subscribe({
        next: (template: EmailTemplate) => {
          this.model = {
            subject: template.subject,
            body: template.body,
            isCustomized: template.isCustomized,
          };
          this.statusMessageService.showSuccessToast('Email template reverted to default.');
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
