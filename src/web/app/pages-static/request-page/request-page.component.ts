import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ReCaptcha2Component } from 'ngx-captcha';
import { finalize } from 'rxjs/operators';
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
import { ErrorMessageOutput } from '../../error-message-output';

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

  backendErrorMessage : string = '';
  readonly emptyFieldMessage : string = 'This field should not be empty';

  readonly recaptchaSiteKey: string = environment.captchaSiteKey;

  isFormSaving: boolean = false;

  @ViewChild('recaptchaElem') recaptchaElem!: ReCaptcha2Component;

  constructor(private statusMessageService: StatusMessageService,
              private accountService: AccountService,
              private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.form = new FormGroup({
      name: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.PERSON_NAME_MAX_LENGTH)]),
      institute: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.SECTION_NAME_MAX_LENGTH)]),
      country: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.TEAM_NAME_MAX_LENGTH)]),
      email: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.EMAIL_MAX_LENGTH), Validators.email]),
      url: new FormControl(''),
      'account-type': new FormControl('instructor',
        [Validators.required, this.fieldExpectedValueValidator('instructor')]),
      comments: new FormControl(''),
      recaptcha: new FormControl(''),
    });
  }

  /**
   * Returns a validator function to check if the form control field has the expected value.
   */
  fieldExpectedValueValidator(expected: string) {
    return function (control: AbstractControl) : ValidationErrors | null {
      if (control.value !== expected) {
        return { notExpected: true };
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
      this.statusMessageService.showErrorToast('Ensure the form is valid before submission.');
      return;
    }

    this.isFormSaving = true;
    this.backendErrorMessage = '';

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
      .pipe(finalize(() => {
        this.isFormSaving = false;
      }))
      .subscribe(() => {
        this.navigationService.navigateWithSuccessMessage('/web/front/home',
          `Your submission is successful and the request will be processed within 24 hours. 
          If you don't get a response from us within 24 hours (remember to check your spam box too),
          please contact us at teammates@comp.nus.edu.sg for follow up.`);
        // TODO: change to use environment support email and put this in the form as well, put contact in the form
      }, (resp: ErrorMessageOutput) => {
        this.backendErrorMessage = resp.error.message;

        this.form.setErrors({
          invalidFields : resp.error.message,
        });

        this.recaptchaElem.resetCaptcha();

        // const errors = JSON.parse(resp.error.message);
        // if (errors.name) {
        //   this.name!.setErrors({
        //     invalidField : errors.name,
        //   })
        // }
        // if (errors.institute) {
        //   this.institute!.setErrors({
        //     invalidField : errors.institute,
        //   })
        // }
        // if (errors.country) {
        //   this.country!.setErrors({
        //     invalidField : errors.country,
        //   })
        // }
        // if (errors.email) {
        //   this.email!.setErrors({
        //     invalidField : errors.email,
        //   })
        // }
      });

    // this.studentService.updateStudent({
    //   courseId: '',
    //   studentEmail: '',
    //   requestBody: reqBody,
    // })
    //   .pipe(finalize(() => {
    //     this.isFormSaving = false;
    //   }))
    //   .subscribe((resp: MessageOutput) => {
    //     this.navigationService.navigateWithSuccessMessage('/web/instructor/courses/details',
    //       resp.message, { courseid: '' });
    //   }, (resp: ErrorMessageOutput) => {
    //     this.statusMessageService.showErrorToast(resp.error.message);
    //   });
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
