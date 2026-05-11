import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReCaptcha2Component, NgxCaptchaModule } from 'ngx-captcha';
import { finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { SessionLinksRecoveryResponse } from '../../../types/api-output';
import { AjaxLoadingComponent } from '../../components/ajax-loading/ajax-loading.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Student recover session links page.
 */
@Component({
  selector: 'tm-session-links-recovery-page',
  templateUrl: './session-links-recovery-page.component.html',
  imports: [FormsModule, ReactiveFormsModule, NgxCaptchaModule, AjaxLoadingComponent],
})
export class SessionLinksRecoveryPageComponent implements OnInit {
  private feedbackSessionsService = inject(FeedbackSessionsService);
  private statusMessageService = inject(StatusMessageService);
  private formBuilder = inject(UntypedFormBuilder);

  // ngx-recaptcha2 element properties
  captchaSuccess = false;
  captchaResponse?: string;
  size: 'compact' | 'normal' = 'normal';
  lang = 'en';
  captchaLoaded = false;
  captchaError = false;

  formSessionLinksRecovery!: UntypedFormGroup;
  isFormSubmitting = false;
  readonly captchaSiteKey: string = environment.captchaSiteKey;

  @ViewChild('captchaElem') captchaElem!: ReCaptcha2Component;

  ngOnInit(): void {
    this.formSessionLinksRecovery = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: [''],
    });
  }

  /**
   * Sends the feedback session links to the recovery email address.
   */
  onSubmitFormSessionLinksRecovery(sessionLinksRecoveryForm: UntypedFormGroup): void {
    if (!this.captchaSiteKey) {
      this.captchaResponse = '';
    }

    if (!this.formSessionLinksRecovery.valid) {
      this.statusMessageService.showErrorToast('Please enter a valid email address.');
      return;
    }

    if (this.captchaResponse === undefined) {
      this.statusMessageService.showErrorToast('Please complete the "I\'m not a robot" checkbox before submitting.');
      return;
    }

    this.isFormSubmitting = true;

    this.feedbackSessionsService
      .sendFeedbackSessionLinkToRecoveryEmail({
        sessionLinksRecoveryEmail: sessionLinksRecoveryForm.controls['email'].value,
        captchaResponse: this.captchaResponse,
      })
      .pipe(
        finalize(() => {
          this.isFormSubmitting = false;
        }),
      )
      .subscribe({
        next: (resp: SessionLinksRecoveryResponse) => {
          if (resp.isEmailSent) {
            this.statusMessageService.showSuccessToast(resp.message);
          } else {
            this.statusMessageService.showErrorToast(resp.message);
          }
        },
        error: (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(response.error.message);
        },
      });
    this.resetFormGroups();
  }

  /**
   * Resets the email and reCAPTCHA input fields in the form.
   */
  resetFormGroups(): void {
    this.formSessionLinksRecovery = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: [''],
    });

    this.reloadCaptcha();
  }

  /**
   * Reloads the reCAPTCHA widget if a non-empty site key is present.
   */
  reloadCaptcha(): void {
    if (this.captchaSiteKey) {
      this.captchaElem.reloadCaptcha();
    }
  }

  /**
   * Handles successful completion recaptcha challenge.
   *
   * @param captchaResponse User's captcha response token
   */
  handleSuccess(captchaResponse: string): void {
    this.captchaSuccess = true;
    this.captchaResponse = captchaResponse;
  }

  handleLoad(): void {
    this.captchaLoaded = true;
    this.captchaError = false;
  }

  handleError(): void {
    this.captchaError = true;
  }

  isCaptchaReady(): boolean {
    if (!this.captchaSiteKey) return true; // dev mode
    return this.captchaLoaded && !this.captchaError;
  }
}
