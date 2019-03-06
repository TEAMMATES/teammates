import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReCaptcha2Component } from 'ngx-captcha';
import { environment } from '../../../environments/environment';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Student recover session links page.
 */
@Component({
  selector: 'tm-session-links-recovery-page',
  templateUrl: './session-links-recovery-page.component.html',
  styleUrls: ['./session-links-recovery-page.component.scss'],
})
export class SessionLinksRecoveryPageComponent implements OnInit {

  // ngx-recaptcha2 element properties
  captchaSuccess: boolean = false;
  captchaResponse?: string;
  size: 'compact' | 'normal' = 'normal';
  lang: string = 'en';

  formSessionLinksRecovery!: FormGroup;
  readonly captchaSiteKey: string = environment.captchaSiteKey;

  @ViewChild('captchaElem') captchaElem!: ReCaptcha2Component;

  constructor(private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.formSessionLinksRecovery = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: ['', Validators.required],
    });
  }

  /**
   * Sends the feedback session links to the recovery email address.
   */
  onSubmitFormSessionLinksRecovery(sessionLinksRecoveryForm: FormGroup): void {
    if (!this.formSessionLinksRecovery.valid || this.captchaResponse === undefined) {
      this.statusMessageService.showErrorMessage(
          'Please enter a valid email address and click the reCAPTCHA before submitting.');
      return;
    }

    const paramsMap: { [key: string]: string } = {
      sessionlinksrecoveryemail: sessionLinksRecoveryForm.controls.email.value,
      captcharesponse: this.captchaResponse,
    };

    this.httpRequestService.get('/sessionlinksrecovery', paramsMap)
      .subscribe((resp: MessageOutput) => {
        this.statusMessageService.showSuccessMessage(resp.message);

        this.resetFormGroups();
      }, (response: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(response.error.message);

        this.resetRecaptchaFormGroup();
      });
  }

  /**
   * Resets the email and reCAPTCHA input fields in the form.
   */
  resetFormGroups(): void {
    (this.formSessionLinksRecovery = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: ['', Validators.required],
    }));
    this.captchaElem.reloadCaptcha();
  }

  /**
   * Resets reCAPTCHA in the form.
   */
  resetRecaptchaFormGroup(): void {
    (this.formSessionLinksRecovery = this.formBuilder.group({
      recaptcha: ['', Validators.required],
    }));
    this.captchaElem.reloadCaptcha();
  }

  /**
   * Handles successful completion recaptcha challenge.
   * @param captchaResponse User's captcha response token
   */
  handleSuccess(captchaResponse: string): void {
    this.captchaSuccess = true;
    this.captchaResponse = captchaResponse;
  }
}
