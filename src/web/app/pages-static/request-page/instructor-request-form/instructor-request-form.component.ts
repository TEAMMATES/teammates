import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { InstructorRequestFormModel } from './instructor-request-form-model';

// Use regex to validate URL field as Angular does not have a built-in URL validator
// eslint-disable-next-line
const URL_REGEX = /(https?:\/\/)?(www\.)[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)|(https?:\/\/)?(www\.)?(?!ww)[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/;

@Component({
  selector: 'tm-instructor-request-form',
  templateUrl: './instructor-request-form.component.html',
  styleUrls: ['./instructor-request-form.component.scss'],
})
export class InstructorRequestFormComponent {

  arf = new FormGroup({
    name: new FormControl('', [Validators.required]),
    institution: new FormControl('', [Validators.required]),
    country: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    homePage: new FormControl('', [Validators.pattern(URL_REGEX)]),
    comments: new FormControl(''),
  }, { updateOn: 'submit' });

  // Create members for easier access of arf controls
  name = this.arf.controls.name;
  institution = this.arf.controls.institution;
  country = this.arf.controls.country;
  email = this.arf.controls.email;
  homePage = this.arf.controls.homePage;
  comments = this.arf.controls.comments;

  hasSubmitAttempt = false;

  @Output() requestSubmissionEvent = new EventEmitter<InstructorRequestFormModel>();

  isFieldRequired(field: FormControl): boolean {
    return field.hasValidator(Validators.required);
  }

  isFieldInvalid(field: FormControl): boolean {
    return field.invalid;
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

  onSubmit(): void {
    this.hasSubmitAttempt = true;

    if (this.arf.invalid) {
      // Do not submit form
      return;
    }

    const name = this.name.value!.trim();
    const email = this.email.value!.trim();
    const country = this.country.value!.trim();
    const institution = this.institution.value!.trim();
    const combinedInstitution = `${institution}, ${country}`;
    const homePage = this.homePage.value!;
    const comments = this.comments.value!.trim();

    const submittedData = {
      name,
      email,
      institution: combinedInstitution,
      homePage,
      comments,
    };
    // TODO: connect to API
    // eslint-disable-next-line
    submittedData; // PLACEHOLDER

    // Pass form input to parent to display confirmation
    this.requestSubmissionEvent.emit({
      name,
      institution,
      country,
      email,
      homePage,
      comments,
    });
  }
}
