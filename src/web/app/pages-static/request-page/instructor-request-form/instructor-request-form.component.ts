import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { InstructorRequestFormData } from './InstructorRequestFormData';

const URL_REGEX = '(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)';

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
    comments: new FormControl('')
  }, {updateOn: 'submit'});

  // Create members for easier access of arf controls
  name = this.arf.controls.name;
  institution = this.arf.controls.institution;
  country = this.arf.controls.country;
  email = this.arf.controls.email;
  homePage = this.arf.controls.homePage;
  comments = this.arf.controls.comments;

  hasSubmitAttempt = false;

  @Output() requestSubmitted = new EventEmitter<InstructorRequestFormData>();

  isFieldRequired(field: FormControl): boolean {
    return field.hasValidator(Validators.required);
  }

  isFieldInvalid(field: FormControl): boolean {
    return field.invalid;
  }

  getFieldValidationClasses(field: FormControl): string {
    let str = "";
    if (this.hasSubmitAttempt) {
      if (field.invalid) {
        str = "is-invalid";
      } else if (field.value !== "") {
        str = "is-valid";
      }
    }
    return str;
  }

  onSubmit() {
    this.hasSubmitAttempt = true;

    if (this.arf.invalid) {
      // Do not submit form
      return;
    }

    let name = this.name.value!.trim();
    let email = this.email.value!.trim();
    let country = this.country.value!.trim();
    let institution = this.institution.value!.trim();
    let combinedInstitution = country + " " + institution;
    let homePage = this.homePage.value!;
    let comments = this.comments.value!.trim();

    let submittedData = {
      name: name,
      email: email,
      institution: combinedInstitution,
      homePage: homePage,
      comments: comments
    }
    // TODO: connect to API
    console.log(submittedData);

    // Pass form input to parent to display confirmation
    this.requestSubmitted.emit({
      name: name,
      institution: institution,
      country: country,
      email: email,
      homePage: homePage,
      comments: comments
    });
  }
}
