import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { StudentUpdateRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentAttributes {
  email: string;
  course: string;
  name: string;
  lastName: string;
  comments: string;
  team: string;
  section: string;
}

interface StudentEditDetails {
  student: StudentAttributes;
  isOpenOrPublishedEmailSentForTheCourse: boolean;
}

/**
 * Instructor course student edit page.
 */
@Component({
  selector: 'tm-instructor-course-student-edit-page',
  templateUrl: './instructor-course-student-edit-page.component.html',
  styleUrls: ['./instructor-course-student-edit-page.component.scss'],
})
export class InstructorCourseStudentEditPageComponent implements OnInit, OnDestroy {

  user: string = '';
  @Input() isEnabled: boolean = true;
  courseid: string = '';
  studentemail: string = '';
  student!: StudentAttributes;

  isOpenOrPublishedEmailSentForTheCourse?: boolean;
  isSessionSummarySendEmail: boolean = false;

  isTeamnameFieldChanged: boolean = false;
  isEmailFieldChanged: boolean = false;

  editForm!: FormGroup;
  teamFieldSubscription?: Subscription;
  emailFieldSubscription?: Subscription;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private ngbModal: NgbModal) { }

  ngOnInit(): void {
    if (!this.isEnabled) {
      this.student = {
        email: 'alice@email.com',
        course: '',
        name: 'Alice Betsy',
        lastName: '',
        comments: 'Alice is a transfer student.',
        team: 'Team A',
        section: 'Section A',
      };
      this.studentemail = this.student.email;
      this.initEditForm();
      return;
    }

    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.courseid = queryParams.courseid;
      this.loadStudentEditDetails(queryParams.courseid, queryParams.studentemail, queryParams.user);
    });
  }

  ngOnDestroy(): void {
    if (this.emailFieldSubscription) {
      (this.emailFieldSubscription as Subscription).unsubscribe();
    }
    if (this.teamFieldSubscription) {
      (this.teamFieldSubscription as Subscription).unsubscribe();
    }
  }

  /**
   * Loads student details required for this page.
   */
  loadStudentEditDetails(courseid: string, studentemail: string, user: string): void {
    const paramsMap: { [key: string]: string } = { courseid, studentemail, user };
    this.httpRequestService.get('/students/editDetails', paramsMap)
        .subscribe((resp: StudentEditDetails) => {
          this.student = resp.student;
          if (!this.student) {
            this.statusMessageService.showErrorMessage('Error retrieving student details');
          } else {
            this.studentemail = this.student.email;
            this.isOpenOrPublishedEmailSentForTheCourse =
                resp.isOpenOrPublishedEmailSentForTheCourse;
            this.initEditForm();
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Initializes the student details edit form with the fields fetched from the backend.
   * Subscriptions are set up to listen to changes in the 'teamname' fields and 'newstudentemail' fields.
   */
  private initEditForm(): void {
    this.editForm = new FormGroup({
      studentname: new FormControl(this.student.name),
      sectionname: new FormControl(this.student.section),
      teamname: new FormControl(this.student.team),
      newstudentemail: new FormControl(this.studentemail), // original student email initialized
      comments: new FormControl(this.student.comments),
    });
    this.teamFieldSubscription =
        (this.editForm.get('teamname') as AbstractControl).valueChanges
            .subscribe(() => {
              this.isTeamnameFieldChanged = true;
            });

    this.emailFieldSubscription =
        (this.editForm.get('newstudentemail') as AbstractControl).valueChanges
            .subscribe(() => this.isEmailFieldChanged = true);
  }

  /**
   * Handles logic related to showing the appropriate modal boxes
   * upon submission of the form. Submits the form otherwise.
   */
  onSubmit(confirmDelModal: any, resendPastLinksModal: any): void {
    if (!this.isEnabled) {
      return;
    }

    if (this.isTeamnameFieldChanged) {
      this.ngbModal.open(confirmDelModal);
    } else if (this.isEmailFieldChanged) {
      this.ngbModal.open(resendPastLinksModal);
    } else {
      this.submitEditForm();
    }
  }

  /**
   * Shows the `resendPastSessionLinks` modal if email field has changed.
   * Submits the form  otherwise.
   */
  deleteExistingResponses(resendPastLinksModal: any): void {
    if (this.isEmailFieldChanged) {
      this.ngbModal.open(resendPastLinksModal);
    } else {
      this.submitEditForm();
    }
  }

  /**
   * Sets the boolean value of `isSessionSummarySendEmail` to true if
   * user chooses to resend past session link to the new email.
   */
  resendPastSessionLinks(isResend: boolean): any {
    if (isResend) {
      this.isSessionSummarySendEmail = true;
    }
    this.submitEditForm();
  }

  /**
   * Submits the form data to edit the student details.
   */
  submitEditForm(): void {
    // creates a new object instead of using its reference
    const paramsMap: { [key: string]: string } = {
      user: this.user,
      courseid: this.courseid,
      studentemail: this.studentemail,
    };

    const reqBody: StudentUpdateRequest = {
      name: this.editForm.value.studentname,
      email: this.editForm.value.newstudentemail,
      team: this.editForm.value.teamname,
      section: this.editForm.value.sectionname,
      comments: this.editForm.value.comments,
      isSessionSummarySendEmail: this.isSessionSummarySendEmail,
    };

    this.httpRequestService.put('/student', paramsMap, reqBody)
      .subscribe((resp: MessageOutput) => {
        this.router.navigate(['/web/instructor/courses/details'], {
          queryParams: { courseid: this.courseid },
        }).then(() => {
          this.statusMessageService.showSuccessMessage(resp.message);
        });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
  }
}
