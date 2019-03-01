import { Component } from '@angular/core';
import { AccountService } from '../../../services/account.service';
import { JoinLink } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

interface InstructorData {
  name: string;
  email: string;
  institution: string;
  status: string;
  joinLink?: string;
  message?: string;
}

/**
 * Admin home page.
 */
@Component({
  selector: 'tm-admin-home-page',
  templateUrl: './admin-home-page.component.html',
  styleUrls: ['./admin-home-page.component.scss'],
})
export class AdminHomePageComponent {

  instructorDetails: string = '';
  instructorName: string = '';
  instructorEmail: string = '';
  instructorInstitution: string = '';

  instructorsConsolidated: InstructorData[] = [];
  activeRequests: number = 0;

  constructor(private accountService: AccountService) {}

  /**
   * Validates and adds the instructor details filled with first form.
   */
  validateAndAddInstructorDetails(): void {
    for (const instructorDetail of this.instructorDetails.split(/\r?\n/)) {
      const instructorDetailSplit: string[] = instructorDetail.split(/ ?\| ?/);
      if (instructorDetailSplit.length < 3) {
        // TODO handle error
        continue;
      }
      this.instructorsConsolidated.push({
        name: instructorDetailSplit[0],
        email: instructorDetailSplit[1],
        institution: instructorDetailSplit[2],
        status: 'PENDING',
      });
    }
    this.instructorDetails = '';
  }

  /**
   * Validates and adds the instructor detail filled with second form.
   */
  validateAndAddInstructorDetail(): void {
    if (!this.instructorName || !this.instructorEmail || !this.instructorInstitution) {
      // TODO handle error
      return;
    }
    this.instructorsConsolidated.push({
      name: this.instructorName,
      email: this.instructorEmail,
      institution: this.instructorInstitution,
      status: 'PENDING',
    });
    this.instructorName = '';
    this.instructorEmail = '';
    this.instructorInstitution = '';
  }

  /**
   * Adds the instructor at the i-th index.
   */
  addInstructor(i: number): void {
    this.activeRequests += 1;
    const instructor: InstructorData = this.instructorsConsolidated[i];
    instructor.status = 'ADDING';
    const instructorEmail: string = instructor.email;

    this.accountService.createAccount({
      instructorEmail,
      instructorName: instructor.name,
      instructorInstitution: instructor.institution,
    }).subscribe((resp: JoinLink) => {
      instructor.status = 'SUCCESS';
      instructor.joinLink = resp.joinLink;
      this.activeRequests -= 1;
    }, (resp: ErrorMessageOutput) => {
      instructor.status = 'FAIL';
      instructor.message = resp.error.message;
      this.activeRequests -= 1;
    });
  }

  /**
   * Cancels the instructor at the i-th index.
   */
  cancelInstructor(i: number): void {
    this.instructorsConsolidated.splice(i, 1);
  }

  /**
   * Adds all the pending and failed-to-add instructors.
   */
  addAllInstructors(): void {
    for (let i: number = 0; i < this.instructorsConsolidated.length; i += 1) {
      const instructor: InstructorData = this.instructorsConsolidated[i];
      if (instructor.status === 'PENDING' || instructor.status === 'FAIL') {
        this.addInstructor(i);
      }
    }
  }

}
