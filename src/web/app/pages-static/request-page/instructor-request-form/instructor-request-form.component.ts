import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

const URL_REGEX = '(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)';

@Component({
  selector: 'tm-instructor-request-form',
  templateUrl: './instructor-request-form.component.html',
  styleUrls: ['./instructor-request-form.component.scss']
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

  isFieldRequired(field: FormControl): boolean {
    return field.hasValidator(Validators.required);
  }

  isFieldInvalid(field: FormControl): boolean {
    return field.touched && field.invalid;
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
  }
}
