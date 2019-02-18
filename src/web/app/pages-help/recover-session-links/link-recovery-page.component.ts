import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Student recover session links page.
 */
@Component({
  selector: 'tm-student-recover-session-links-page',
  templateUrl: './link-recovery-page.component.html',
  styleUrls: ['./link-recovery-page.component.scss'],
})
export class LinkRecoveryPageComponent implements OnInit {

  // ngx-recaptcha2 element properties
  captchaIsLoaded: boolean = false;
  captchaSuccess: boolean = false;
  captchaIsExpired: boolean = false;
  captchaResponse?: string;
  size: 'compact' | 'normal' = 'normal';
  lang: string = 'en';

  formLinkRecovery!: FormGroup;
  readonly siteKey: string = '6LeZSZEUAAAAAO-xCzi314NoCgJILEH9qzFuer3P';

  constructor(private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.formLinkRecovery = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: ['', Validators.required],
    });
  }

  /**
   * Sends the feedback session links to the registered email address.
   */
  onSubmitLinkRecovery(linkRecoveryForm: FormGroup): void {
    const recoveryEmail: string = linkRecoveryForm.controls.email.value;

    if (!recoveryEmail || this.captchaResponse === undefined) {
      this.statusMessageService.showErrorMessage(
          'Please enter an email address and click the reCAPTCHA before submitting.');
      return;
    }

    const paramsMap: { [key: string]: string } = {
      recoveryemail: recoveryEmail,
      captcharesponse: this.captchaResponse,
    };

    this.httpRequestService.get('/recovery', paramsMap)
      .subscribe((resp: MessageOutput) => {
        this.statusMessageService.showSuccessMessage(resp.message);

        // Reset input field and reCAPTCHA
        this.handleReset();
      }, (response: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(response.error.message);
      });
  }

  /**
   * Handles reset of the reCAPTCHA.
   */
  handleReset(): void {
    this.captchaSuccess = false;
    this.captchaResponse = undefined;
  }

  /**
   * Handles successful completion recaptcha challenge.
   * @param captchaResponse User's captcha response token
   */
  handleSuccess(captchaResponse: string): void {
    this.captchaSuccess = true;
    this.captchaResponse = captchaResponse;
  }

  /**
   * Handles loading of the reCAPTCHA.
   */
  handleLoad(): void {
    this.captchaIsLoaded = true;
    this.captchaIsExpired = false;
  }

  /**
   * Handles expiry of the reCAPTCHA.
   */
  handleExpire(): void {
    this.captchaSuccess = false;
    this.captchaIsExpired = true;
  }
}
