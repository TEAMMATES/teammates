import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { FormValidator } from 'src/web/types/form-validator';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { MessageOutput } from '../../../types/api-output';
import { StudentUpdateRequest } from '../../../types/api-request';
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

  isFormSaving: boolean = false;

  form!: FormGroup;

  constructor(private statusMessageService: StatusMessageService,
              private studentService: StudentService,
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
    const reqBody: StudentUpdateRequest = {
      name: this.form.value.name,
      email: this.form.value.newstudentemail,
      team: this.form.value.teamname,
      section: this.form.value.sectionname,
      comments: this.form.value.comments,
      isSessionSummarySendEmail: false,
    };

    this.isFormSaving = true;

    this.studentService.updateStudent({
      courseId: '',
      studentEmail: '',
      requestBody: reqBody,
    })
      .pipe(finalize(() => {
        this.isFormSaving = false;
      }))
      .subscribe((resp: MessageOutput) => {
        this.navigationService.navigateWithSuccessMessage('/web/instructor/courses/details',
          resp.message, { courseid: '' });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
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

  get comments() {
    return this.form.get('comments');
  }
}
