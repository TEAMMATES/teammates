import { Component } from '@angular/core';
import { concat, Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
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

  isAddingInstructors: boolean = false;

  constructor(private accountService: AccountService) {}

  /**
   * Validates and adds the instructor details filled with first form.
   */
  validateAndAddInstructorDetails(): void {
    const invalidLines: string[] = [];
    for (const instructorDetail of this.instructorDetails.split(/\r?\n/)) {
      const instructorDetailSplit: string[] = instructorDetail.split(/[|\t]/).map((item: string) => item.trim());
      if (instructorDetailSplit.length < 3) {
        // TODO handle should add all instructors when promptederror
        invalidLines.push(instructorDetail);
        continue;
      }
      if (!instructorDetailSplit[0] || !instructorDetailSplit[1] || !instructorDetailSplit[2]) {
        // TODO handle error
        invalidLines.push(instructorDetail);
        continue;
      }
      this.instructorsConsolidated.push({
        name: instructorDetailSplit[0],
        email: instructorDetailSplit[1],
        institution: instructorDetailSplit[2],
        status: 'PENDING',
      });
    }
    this.instructorDetails = invalidLines.join('\r\n');
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
    this.asyncAddInstructor(i)
      ?.subscribe({ complete: () => this.isAddingInstructors = false });
  }

  /**
   * Adds the instructor at the i-th index.
   */
  asyncAddInstructor(i: number): Observable<JoinLink> | null {
    const instructor: InstructorData = this.instructorsConsolidated[i];
    if (instructor.status !== 'PENDING' && instructor.status !== 'FAIL') {
      return null;
    }
    this.activeRequests += 1;
    instructor.status = 'ADDING';

    this.isAddingInstructors = true;
    return this.accountService.createAccount({
      instructorEmail: instructor.email,
      instructorName: instructor.name,
      instructorInstitution: instructor.institution,
    }).pipe(
      tap((resp: JoinLink) => {
        instructor.status = 'SUCCESS';
        instructor.joinLink = resp.joinLink;
        this.activeRequests -= 1;
      }, (resp: ErrorMessageOutput) => {
        instructor.status = 'FAIL';
        instructor.message = resp.error.message;
        this.activeRequests -= 1;
      }),
      // Lift error handling to tap operator and catch the final error
      catchError((_err: ErrorMessageOutput, _caught: Observable<JoinLink>) => of({ joinLink: '' })),
    );
  }

  /**
   * Cancels the instructor at the i-th index.
   */
  cancelInstructor(i: number): void {
    this.instructorsConsolidated.splice(i, 1);
  }

  /**
   * Adds all the pending and failed-to-add instructors one by one.
   * Refer to https://github.com/TEAMMATES/teammates/issues/11039
   */
  addAllInstructors(): void {
    concat(
      ...this.instructorsConsolidated
        .map((_instr: InstructorData, index: number): Observable<JoinLink> | null => this.asyncAddInstructor(index))
        .filter((req: Observable<JoinLink> | null): boolean => !!req)
        .map((req: Observable<JoinLink> | null): Observable<JoinLink> => req as Observable<JoinLink>),
    ).subscribe({ complete: () => this.isAddingInstructors = false });
  }

}
