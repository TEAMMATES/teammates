import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { JoinState } from '../../../../types/api-output';
import { Gender } from '../../../../types/gender';
import { SearchStudentsTable } from '../../../pages-instructor/instructor-search-page/instructor-search-page.component';
import { StudentAttributes } from '../../../pages-instructor/student-profile/student-attributes';
import { StudentProfile } from '../../../pages-instructor/student-profile/student-profile';
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
    shortName: 'Alice',
    email: 'alice@email.com',
    institute: 'National University of Singapore',
    nationality: 'American',
    gender: Gender.FEMALE,
    moreInfo: 'Hi I am Alice Betsy! I am from Colorado, America. I am a programming and gaming enthusiast. '
      + 'Aspiring to become a Software Architect in a well reputed organization.',
    pictureKey: '',
  };
  readonly exampleStudentAttributes: StudentAttributes = {
    email: 'alice@email.com',
    course: 'test.exa-demo',
    name: 'Alice Betsy',
    lastName: 'Betsy',
    comments: 'Alice is a transfer student.',
    team: 'Team A',
    section: 'Section A',
  };
  readonly exampleSingleStudentResultTables: SearchStudentsTable[] = [{
    courseId: 'Course name appears here',
    sections: [{
      sectionName: 'Section A',
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
      students: [{
        name: 'Alice Betsy',
        email: 'alice@email.com',
        status: JoinState.JOINED,
        team: 'Team A',
      }],
    }],
  }];
  readonly exampleMultipleStudentResultTables: SearchStudentsTable[] = [{
    courseId: 'Course name appears here',
    sections: [
      {
        sectionName: 'Section A',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        students: [
          {
            name: 'Alice Betsy',
            email: 'alice@email.com',
            status: JoinState.JOINED,
            team: 'Team A',
          },
          {
            name: 'Jean Grey',
            email: 'jean@email.com',
            status: JoinState.JOINED,
            team: 'Team A',
          },
        ],
      },
      {
        sectionName: 'Section B',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        students: [
          {
            name: 'Oliver Gates',
            email: 'oliver@email.com',
            status: JoinState.JOINED,
            team: 'Team B',
          },
          {
            name: 'Thora Parker',
            email: 'thora@email.com',
            status: JoinState.JOINED,
            team: 'Team B',
          },
        ],
      },
      {
        sectionName: 'Section C',
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
        students: [
          {
            name: 'Jack Wayne',
            email: 'jack@email.com',
            status: JoinState.JOINED,
            team: 'Team C',
          },
        ],
      },
    ],
  }];

  constructor() {
    super();
  }

  ngOnInit(): void {

  }
}
