import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ReCaptcha2Component } from 'ngx-captcha';
import { FormValidator } from 'src/web/types/form-validator';
import { environment } from '../../../environments/environment';
import { AccountService } from '../../../services/account.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  AccountRequestCreateIntent,
  AccountRequestCreateRequest,
  AccountRequestType,
} from '../../../types/api-request';
import { AccountRequestCreateErrorResultsWrapper, ErrorMessageOutput } from '../../error-message-output';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
})
export class RequestPageComponent implements OnInit {

  FormValidator: typeof FormValidator = FormValidator; // enum
  form!: FormGroup;

  backendOtherErrorMessage : string = '';
  readonly supportEmail : string = environment.supportEmail;
  readonly emptyFieldMessage : string = 'This field should not be empty';
  readonly invalidFieldsMessage : string = 'Oops, some information is in incorrect format. Please fix them and submit again.';
  readonly beforeSubmissionMessage : string = `The request is manually processed and you should receive an email from us
  within 24 hours after successfully submitting this form. If you don't get a response within 24 hours 
  (remember to check your spam box too), please contact us at ${this.supportEmail} for follow up.`;
  readonly successMessage : string = 'Your submission is successful and the request will be processed within 24 hours.';
  readonly failureMessage : string = 'Submission fails. See details at the bottom of the form.';

  readonly recaptchaSiteKey: string = environment.captchaSiteKey;

  isFormSaving: boolean = false;

  @ViewChild('recaptchaElem') recaptchaElem!: ReCaptcha2Component;

  constructor(private statusMessageService: StatusMessageService,
              private accountService: AccountService,
              private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.form = new FormGroup({
      name: new FormControl('',
        [this.nonEmptyValidator(), this.maxLengthValidator(FormValidator.PERSON_NAME_MAX_LENGTH)]),
      institute: new FormControl('',
        [this.nonEmptyValidator(), this.maxLengthValidator(FormValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH)]),
      country: new FormControl('',
        [this.nonEmptyValidator(), this.maxLengthValidator(FormValidator.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH)]),
      email: new FormControl('',
        [this.nonEmptyValidator(), this.maxLengthValidator(FormValidator.EMAIL_MAX_LENGTH)]),
      url: new FormControl('',
        [this.maxLengthValidator(FormValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH)]),
      'account-type': new FormControl('instructor',
        [Validators.required, this.fieldExpectedValueValidator('instructor')]),
      comments: new FormControl('',
        [this.maxLengthValidator(FormValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH)]),
      recaptcha: new FormControl(''),
    });
  }

  /**
   * Returns a validator function to check if the form control field has the expected value.
   */
  fieldExpectedValueValidator(expected: string) {
    return function (control: AbstractControl) : ValidationErrors | null {
      if ((control.value as string) !== expected) {
        return { notExpected: true };
      }
      return null;
    }
  }

  /**
   * Returns a validator function to check if the control's value has exceeded the max length
   * after leading/trailing spaces are trimmed.
   */
  maxLengthValidator(maxLength: number) {
    return function (control: AbstractControl) : ValidationErrors | null {
      if ((control.value as string).trim().length > maxLength) {
        return { maxLength: true };
      }
      return null;
    }
  }

  /**
   * Returns a validator function to check if the control's value is empty after leading/trailing spaces are trimmed.
   */
  nonEmptyValidator() {
    return function (control: AbstractControl) : ValidationErrors | null {
      if (!(control.value as string).trim()) {
        return { empty: true };
      }
      return null;
    }
  }

  /**
   * Displays message to user stating that the field exceeds the max length.
   */
  displayExceedMaxLengthMessage(fieldName: string, maxLength: number): string {
    return `${fieldName} should not exceed ${maxLength} characters`;
  }

  /**
   * Submits the account request form.
   */
  onSubmit(): void {
    this.form.markAllAsTouched();

    // set recaptcha validation errors only on submit
    const captchaResponse: string = this.recaptchaElem.getResponse();
    console.log(captchaResponse);
    console.log(this.recaptcha!.value);
    if (this.recaptchaSiteKey !== '' && captchaResponse === '') {
      this.recaptcha!.setErrors({
        unchecked: true,
      });
    }

    if (!this.form.valid) {
      console.log('invalid form');
      this.statusMessageService.showWarningToast('Ensure the form is valid before submission.');
      return;
    }

    this.isFormSaving = true;
    this.backendOtherErrorMessage = '';

    const accReqType: AccountRequestType = this.accountType!.value === 'instructor'
      ? AccountRequestType.INSTRUCTOR_ACCOUNT
      : AccountRequestType.STUDENT_ACCOUNT;
    const reqBody: AccountRequestCreateRequest = {
      instructorName: this.name!.value,
      instructorInstitute: this.institute!.value,
      instructorCountry: this.country!.value,
      instructorEmail: this.email!.value,
      instructorHomePageUrl: this.url!.value,
      otherComments: this.comments!.value,
    };

    this.accountService.createAccountRequest({
      intent: AccountRequestCreateIntent.PUBLIC_CREATE,
      accountRequestType: accReqType,
      captchaResponse: captchaResponse,
      requestBody: reqBody,
    })
      .subscribe(() => {
        this.navigationService.navigateWithSuccessMessage('/web/front/home', this.successMessage);
      }, (resp: ErrorMessageOutput | AccountRequestCreateErrorResultsWrapper) => {
        this.isFormSaving = false;
        this.statusMessageService.showWarningToast(this.failureMessage);

        this.recaptchaElem.resetCaptcha();

        if ('message' in resp.error) {
          this.backendOtherErrorMessage = resp.error.message;
        } else {
          this.backendOtherErrorMessage = this.invalidFieldsMessage;

          if (resp.error.invalidNameMessage) {
            this.name!.setErrors({
              invalidField : resp.error.invalidNameMessage,
            });
          }
          if (resp.error.invalidInstituteMessage) {
            this.institute!.setErrors({
              invalidField : resp.error.invalidInstituteMessage,
            });
          }
          if (resp.error.invalidCountryMessage) {
            this.country!.setErrors({
              invalidField : resp.error.invalidCountryMessage,
            });
          }
          if (resp.error.invalidEmailMessage) {
            this.email!.setErrors({
              invalidField : resp.error.invalidEmailMessage,
            });
          }
          if (resp.error.invalidHomePageUrlMessage) {
            this.url!.setErrors({
              invalidField : resp.error.invalidHomePageUrlMessage,
            });
          }
          if (resp.error.invalidCommentsMessage) {
            this.comments!.setErrors({
              invalidField : resp.error.invalidCommentsMessage,
            });
          }
        }
      });
  }

  get name() {
    return this.form.get('name');
  }

  get institute() {
    return this.form.get('institute');
  }

  get country() {
    return this.form.get('country');
  }

  get email() {
    return this.form.get('email');
  }

  get url() {
    return this.form.get('url');
  }

  get accountType() {
    return this.form.get('account-type');
  }

  get comments() {
    return this.form.get('comments');
  }

  get recaptcha() {
    return this.form.get('recaptcha');
  }
}
