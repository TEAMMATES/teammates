import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbAlert } from '@ng-bootstrap/ng-bootstrap/alert';
import { NgxCaptchaModule } from 'ngx-captcha';
import { finalize } from 'rxjs';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { environment } from '../../../../environments/environment';
import { AccountService } from '../../../../services/account.service';
import { CountryService } from '../../../../services/country.service';
import { AccountCreateRequest } from '../../../../types/api-request';
import {
  STUDENT_NAME_MAX_LENGTH,
  INSTITUTION_NAME_MAX_LENGTH,
  EMAIL_MAX_LENGTH,
  NAME_REGEX,
  EMAIL_REGEX,
} from '../../../../types/field-validator';
import {
  ComboboxOption,
  SearchableComboboxComponent,
} from '../../../components/searchable-combobox/searchable-combobox.component';
import { TeammatesRouterDirective } from '../../../components/teammates-router/teammates-router.directive';
import { ErrorMessageOutput } from '../../../error-message-output';

@Component({
  selector: 'tm-instructor-request-form',
  templateUrl: './instructor-request-form.component.html',
  styleUrls: ['./instructor-request-form.component.scss'],
  imports: [FormsModule, ReactiveFormsModule, NgxCaptchaModule, NgbAlert, TeammatesRouterDirective, SearchableComboboxComponent],
})
export class InstructorRequestFormComponent {
  private readonly accountService = inject(AccountService);
  private readonly countryService = inject(CountryService);

  // Create members to be accessed in template
  readonly STUDENT_NAME_MAX_LENGTH!: number;
  readonly INSTITUTION_NAME_MAX_LENGTH!: number;
  readonly EMAIL_MAX_LENGTH!: number;

  readonly countryOptions: ComboboxOption<string>[] = this.countryService.getCountryOptions().map((o) => ({
    value: o.code,
    label: o.name,
  }));

  // Captcha
  captchaSiteKey: string = environment.captchaSiteKey;
  isCaptchaSuccessful = false;
  captchaResponse?: string;
  size: 'compact' | 'normal' = 'normal';
  lang = 'en';

  arf = new FormGroup(
    {
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(STUDENT_NAME_MAX_LENGTH),
        Validators.pattern(NAME_REGEX),
      ]),
      institution: new FormControl('', [
        Validators.required,
        Validators.maxLength(INSTITUTION_NAME_MAX_LENGTH),
        Validators.pattern(NAME_REGEX),
      ]),
      country: new FormControl('', { validators: [Validators.required], updateOn: 'change' }),
      email: new FormControl('', [
        Validators.required,
        Validators.pattern(EMAIL_REGEX),
        Validators.maxLength(EMAIL_MAX_LENGTH),
      ]),
      comments: new FormControl(''),
      recaptcha: new FormControl(''),
    },
    { updateOn: 'submit' },
  );

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

  constructor() {
    this.STUDENT_NAME_MAX_LENGTH = STUDENT_NAME_MAX_LENGTH;
    this.INSTITUTION_NAME_MAX_LENGTH = INSTITUTION_NAME_MAX_LENGTH;
    this.EMAIL_MAX_LENGTH = EMAIL_MAX_LENGTH;
  }

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
    const countryCode = this.country.value!;
    const countryName = this.countryOptions.find((o) => o.value === countryCode)?.label ?? countryCode;
    const institution = this.institution.value!.trim();
    const combinedInstitution = `${institution}, ${countryName}`;

    const requestData: AccountCreateRequest = {
      instructorEmail: email,
      instructorName: name,
      instructorInstitution: combinedInstitution,
      captchaResponse: this.captchaSiteKey ? this.captchaResponse! : '',
    };

    if (comments) {
      requestData.instructorComments = comments;
    }

    this.accountService
      .createAccountRequest(requestData)
      .pipe(
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: () => {
          // Pass form input to parent to display confirmation
          this.requestSubmissionEvent.emit({
            name,
            institution,
            country: countryName,
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
