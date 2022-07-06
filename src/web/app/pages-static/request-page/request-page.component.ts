import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { FormValidator } from 'src/web/types/form-validator';
import { AccountService } from '../../../services/account.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { JoinLink } from '../../../types/api-output';
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

  readonly emptyFieldMessage : string = 'This field should not be empty';

  backendErrorMessage : string = '';

  isFormSaving: boolean = false;

  form!: FormGroup;

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
      comments: new FormControl(''),
    });
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
    this.isFormSaving = true;
    this.backendErrorMessage = '';

    this.accountService.createAccountRequest({
      instructorName: this.name!.value,
      instructorInstitute: this.institute!.value,
      instructorCountry: this.country!.value,
      instructorEmail: this.email!.value,
      instructorHomePageUrl: this.url!.value,
      otherComments: this.comments!.value,
    })
      .pipe(finalize(() => {
        this.isFormSaving = false;
      }))
      .subscribe((resp: JoinLink) => { // TODO: change to MessageOutput and resp.message
        this.navigationService.navigateWithSuccessMessage('/web/front/home', resp.joinLink);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);

        this.backendErrorMessage = resp.error.message;

        // this.form.setErrors({
        //   invalidFields : resp.error.message,
        // });
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

  get comments() {
    return this.form.get('comments');
  }
}
