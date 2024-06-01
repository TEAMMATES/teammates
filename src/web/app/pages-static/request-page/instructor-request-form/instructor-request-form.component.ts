import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { environment } from '../../../../environments/environment';
import { AccountService } from '../../../../services/account.service';
import { AccountCreateRequest } from '../../../../types/api-request';
import { FormValidator } from '../../../../types/form-validator';
import { ErrorMessageOutput } from '../../../error-message-output';

@Component({
  selector: 'tm-instructor-request-form',
  templateUrl: './instructor-request-form.component.html',
  styleUrls: ['./instructor-request-form.component.scss'],
})
export class InstructorRequestFormComponent {

  constructor(private accountService: AccountService) {}

  // Create members to be accessed in template
  readonly STUDENT_NAME_MAX_LENGTH = FormValidator.STUDENT_NAME_MAX_LENGTH;
  readonly INSTITUTION_NAME_MAX_LENGTH = FormValidator.INSTITUTION_NAME_MAX_LENGTH;
  readonly COUNTRY_NAME_MAX_LENGTH = FormValidator.COUNTRY_NAME_MAX_LENGTH;
  readonly EMAIL_MAX_LENGTH = FormValidator.EMAIL_MAX_LENGTH;

  // Captcha
  captchaSiteKey: string = environment.captchaSiteKey;
  isCaptchaSuccessful: boolean = false;
  captchaResponse?: string;
  size: 'compact' | 'normal' = 'normal';
  lang: string = 'en';

  arf = new FormGroup({
    name: new FormControl('', [
      Validators.required,
      Validators.maxLength(FormValidator.STUDENT_NAME_MAX_LENGTH),
      Validators.pattern(FormValidator.NAME_REGEX),
    ]),
    institution: new FormControl('', [
      Validators.required,
      Validators.maxLength(FormValidator.INSTITUTION_NAME_MAX_LENGTH),
      Validators.pattern(FormValidator.NAME_REGEX),
    ]),
    country: new FormControl('', [
      Validators.required,
      Validators.maxLength(FormValidator.COUNTRY_NAME_MAX_LENGTH),
      Validators.pattern(FormValidator.NAME_REGEX),
    ]),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern(FormValidator.EMAIL_REGEX),
      Validators.maxLength(FormValidator.EMAIL_MAX_LENGTH),
    ]),
    comments: new FormControl(''),
    recaptcha: new FormControl(''),
  }, { updateOn: 'submit' });

  // Create members for easier access of arf controls
  name = this.arf.controls.name;
  institution = this.arf.controls.institution;
  country = this.arf.controls.country;
  email = this.arf.controls.email;
  comments = this.arf.controls.comments;

  hasSubmitAttempt = false;
  isLoading = false;
  @Output() requestSubmissionEvent = new EventEmitter<InstructorRequestFormModel>();

  serverErrorMessage = '';

  checkIsFieldRequired(field: FormControl): boolean {
    return field.hasValidator(Validators.required);
  }

  get canSubmit(): boolean {
    return !this.isLoading;
  }

  getFieldValidationClasses(field: FormControl): string {
    let str = '';
    if (this.hasSubmitAttempt) {
      if (field.invalid) {
        str = 'is-invalid';
      } else if (field.value !== '') {
        str = 'is-valid';
      }
    }
    return str;
  }

  /**
   * Handles successful completion of reCAPTCHA challenge.
   *
   * @param captchaResponse user's reCAPTCHA response token.
   */
  handleCaptchaSuccess(captchaResponse: string): void {
    this.isCaptchaSuccessful = true;
    this.captchaResponse = captchaResponse;
  }

  /**
   * Handles form submission.
   */
  onSubmit(): void {
    this.hasSubmitAttempt = true;
    this.isLoading = true;
    this.serverErrorMessage = '';

    if (this.arf.invalid || (this.captchaSiteKey && !this.captchaResponse)) {
      this.isLoading = false;
      // Do not submit form
      return;
    }

    const name = this.name.value!.trim();
    const email = this.email.value!.trim();
    const comments = this.comments.value!.trim();
    // Country Mapping
    const countryMapping: { [key: string]: string } = {
      'united states': 'USA',
      us: 'USA',
      america: 'USA',
      uk: 'United Kingdom',
      deutschland: 'Germany',
      'united arab emirates': 'UAE',
      españa: 'Spain',
      méxico: 'Mexico',
      belgië: 'Belgium',
      holland: 'Netherlands',
    };
    // Combine country and institution
    const country = this.country.value!.trim();
    const mappedCountry = countryMapping[country.toLowerCase()] || country;
    const institution = this.institution.value!.trim();
    const combinedInstitution = `${institution}, ${mappedCountry}`;

    const requestData: AccountCreateRequest = {
      instructorEmail: email,
      instructorName: name,
      instructorInstitution: combinedInstitution,
      captchaResponse: this.captchaSiteKey ? this.captchaResponse! : '',
    };

    if (comments) {
      requestData.instructorComments = comments;
    }

    this.accountService.createAccountRequest(requestData)
      .pipe(finalize(() => { this.isLoading = false; }))
      .subscribe({
        next: () => {
          // Pass form input to parent to display confirmation
          this.requestSubmissionEvent.emit({
            name,
            institution,
            country,
            email,
            comments,
          });
        },
        error: (resp: ErrorMessageOutput) => {
          this.serverErrorMessage = resp.error.message;
        },
      });
  }
}
