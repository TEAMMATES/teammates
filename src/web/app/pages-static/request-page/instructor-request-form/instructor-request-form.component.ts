import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormControl, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbAlert } from '@ng-bootstrap/ng-bootstrap/alert';
import { finalize } from 'rxjs';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { AccountService } from '../../../../services/account.service';
import { CountryService } from '../../../../services/country.service';
import { AccountCreateRequest } from '../../../../types/api-request';
import {
  STUDENT_NAME_MAX_LENGTH,
  INSTITUTE_NAME_MAX_LENGTH,
  EMAIL_MAX_LENGTH,
  NAME_REGEX,
  EMAIL_REGEX,
} from '../../../../types/field-validator';
import {
  ComboboxOption,
  SearchableComboboxComponent,
} from '../../../components/searchable-combobox/searchable-combobox.component';
import { ErrorMessageOutput } from '../../../error-message-output';

@Component({
  selector: 'tm-instructor-request-form',
  templateUrl: './instructor-request-form.component.html',
  styleUrls: ['./instructor-request-form.component.scss'],
  imports: [FormsModule, ReactiveFormsModule, NgbAlert, SearchableComboboxComponent],
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

  arf = new FormGroup({
    name: new FormControl('', [
      Validators.required,
      Validators.maxLength(STUDENT_NAME_MAX_LENGTH),
      Validators.pattern(NAME_REGEX),
    ]),
    institution: new FormControl('', [
      Validators.required,
      Validators.maxLength(INSTITUTE_NAME_MAX_LENGTH),
      Validators.pattern(NAME_REGEX),
    ]),
    country: new FormControl('', { validators: [Validators.required], updateOn: 'change' }),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern(EMAIL_REGEX),
      Validators.maxLength(EMAIL_MAX_LENGTH),
    ]),
    comments: new FormControl(''),
  });

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
    this.INSTITUTION_NAME_MAX_LENGTH = INSTITUTE_NAME_MAX_LENGTH;
    this.EMAIL_MAX_LENGTH = EMAIL_MAX_LENGTH;
  }

  checkIsFieldRequired(field: FormControl): boolean {
    return field.hasValidator(Validators.required);
  }

  get canSubmit(): boolean {
    return !this.isLoading;
  }

  getFieldValidationClasses(field: FormControl): string {
    if (!field.touched && !this.hasSubmitAttempt) return '';
    if (field.invalid) return 'is-invalid';
    if (field.value !== '') return 'is-valid';
    return '';
  }

  /**
   * Handles form submission.
   */
  onSubmit(): void {
    this.hasSubmitAttempt = true;
    this.isLoading = true;
    this.serverErrorMessage = '';

    if (this.arf.invalid) {
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

    const requestData: AccountCreateRequest = {
      instructorEmail: email,
      instructorName: name,
      instructorInstitution: institution,
      instructorCountry: countryCode,
    };

    if (comments) {
      requestData.instructorComments = comments;
    }

    this.accountService
      .createAccountVerificationRequest(requestData)
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
