import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Gender, JoinState, Student, StudentProfile } from '../../../../types/api-output';
import {
  SearchStudentsListRowTable,
} from '../../../pages-instructor/instructor-search-page/student-result-table/student-result-table.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { StudentsSectionQuestions } from './students-section-questions';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enum
  StudentsSectionQuestions: typeof StudentsSectionQuestions = StudentsSectionQuestions;

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
      student: this.exampleStudentAttributes,
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    }],
  }];
  readonly exampleMultipleStudentResultTables: SearchStudentsListRowTable[] = [{
    courseId: 'Course name appears here',
    students: [
      {
        student: {
          sectionName: 'Section A',
          name: 'Alice Betsy',
          email: 'alice@email.com',
          joinState: JoinState.JOINED,
          teamName: 'Team A',
          courseId: 'Course name appears here',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,

      },
      {
        student: {
          name: 'Jean Grey',
          email: 'jean@email.com',
          joinState: JoinState.JOINED,
          teamName: 'Team A',
          sectionName: 'Section A',
          courseId: 'Course name appears here',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,

      },
      {
        student: {
          name: 'Oliver Gates',
          email: 'oliver@email.com',
          joinState: JoinState.JOINED,
          teamName: 'Team B',
          sectionName: 'Section B',
          courseId: 'Course name appears here',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Thora Parker',
          email: 'thora@email.com',
          joinState: JoinState.JOINED,
          teamName: 'Team B',
          sectionName: 'Section B',
          courseId: 'Course name appears here',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Jack Wayne',
          email: 'jack@email.com',
          joinState: JoinState.JOINED,
          teamName: 'Team C',
          sectionName: 'Section C',
          courseId: 'Course name appears here',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ],
  }];

  questionsToCollapsed: Record<string, boolean> = {
    [StudentsSectionQuestions.STUDENT_EDIT_DETAILS]: false,
    [StudentsSectionQuestions.STUDENT_EMAIL]: false,
    [StudentsSectionQuestions.STUDENT_GOOGLE_ACCOUNT]: false,
    [StudentsSectionQuestions.STUDENT_SEARCH]: false,
    [StudentsSectionQuestions.STUDENT_VIEW_PROFILE]: false,
    [StudentsSectionQuestions.STUDENT_VIEW_RESPONSES]: false,
    [StudentsSectionQuestions.STUDENT_CHANGE_ID]: false,
    [StudentsSectionQuestions.STUDENT_EDIT_DETAILS]: false,
  };

  constructor() {
    super();
  }

  expand(questionId: string): void {
    this.questionsToCollapsed[questionId] = true;
  }
}
