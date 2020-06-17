import { Component, Input, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Gender, JoinState, Student, StudentProfile } from '../../../../types/api-output';
import {
  SearchStudentsListRowTable,
} from '../../../pages-instructor/instructor-search-page/student-result-table/student-result-table.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;
  readonly exampleStudentProfile: StudentProfile = {
    name: 'Alice Betsy',
    shortName: 'Alice',
    email: 'alice@email.com',
    institute: 'National University of Singapore',
    nationality: 'American',
    gender: Gender.FEMALE,
    moreInfo: 'Hi I am Alice Betsy! I am from Colorado, America. I am a programming and gaming enthusiast. '
      + 'Aspiring to become a Software Architect in a well reputed organization.',
  };
  readonly exampleStudentAttributes: Student = {
    email: 'alice@email.com',
    courseId: 'test.exa-demo',
    name: 'Alice Betsy',
    lastName: 'Betsy',
    comments: 'Alice is a transfer student.',
    teamName: 'Team A',
    sectionName: 'Section A',
    joinState: JoinState.JOINED,
  };
  readonly exampleSingleStudentResultTables: SearchStudentsListRowTable[] = [{
    courseId: 'Course name appears here',
    students: [{
      sectionName: 'Section A',
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
      name: 'Alice Betsy',
      email: 'alice@email.com',
      status: JoinState.JOINED,
      team: 'Team A',
    }],
  }];
  readonly exampleMultipleStudentResultTables: SearchStudentsListRowTable[] = [{
    courseId: 'Course name appears here',
    students: [
      {
        sectionName: 'Section A',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        name: 'Alice Betsy',
        email: 'alice@email.com',
        status: JoinState.JOINED,
        team: 'Team A',
      },
      {
        sectionName: 'Section A',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        name: 'Jean Grey',
        email: 'jean@email.com',
        status: JoinState.JOINED,
        team: 'Team A',
      },
      {
        sectionName: 'Section B',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        name: 'Oliver Gates',
        email: 'oliver@email.com',
        status: JoinState.JOINED,
        team: 'Team B',
      },
      {
        sectionName: 'Section B',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        name: 'Thora Parker',
        email: 'thora@email.com',
        status: JoinState.JOINED,
        team: 'Team B',
      },
      {
        sectionName: 'Section C',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        name: 'Jack Wayne',
        email: 'jack@email.com',
        status: JoinState.JOINED,
        team: 'Team C',
      },
    ],
  }];

  @Input() isEditDetailsCollapsed: boolean = false;
  isViewProfileCollapsed: boolean = false;
  isViewAllResponsesCollapsed: boolean = false;
  isStudentSearchCollapsed: boolean = false;
  isStudentEmailCollapsed: boolean = false;
  isGoogleAccountCollapsed: boolean = false;
  isChangeGoogleIdCollapsed: boolean = false;

  constructor() {
    super();
  }
}
