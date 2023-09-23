import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { JoinState } from '../../../types/api-output';
import { Pipes } from '../../pipes/pipes.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { StudentListComponent } from './student-list.component';
import { StudentListModule } from './student-list.module';

describe('StudentListComponent', () => {
  let component: StudentListComponent;
  let fixture: ComponentFixture<StudentListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
    declarations: [StudentListComponent],
      imports: [
        HttpClientTestingModule,
        TeammatesRouterModule,
        RouterTestingModule,
        NgbModule,
        TeammatesCommonModule,
        Pipes,
        StudentListModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with table head set to hidden', () => {
    component.isHideTableHead = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some student list data', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some student list data when not allowed to modify student for a specific section', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: false,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: false,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },

      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with enable remind button set to true and two students yet to join', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.NOT_JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.NOT_JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];

    component.enableRemindButton = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with enable remind button set to true, one student yet to join when not allowed to modify'
      + ' student', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.NOT_JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: false,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];

    component.enableRemindButton = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some student list data and some students to hide', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];

    component.hiddenStudents = [
      'alice.b.tmms@gmail.tmt',
      'tester@tester.com',
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some student list data with no sections', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'None',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display "Send Invite" button when a student has not joined the course', () => {
    component.enableRemindButton = true;
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          email: 'tester@tester.com',
          joinState: JoinState.NOT_JOINED,
          sectionName: 'Tutorial Group 1',
          courseId: 'text-exa.demo',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ];

    fixture.detectChanges();

    const buttons: any = fixture.debugElement.queryAll(By.css('button'));
    const sendInviteButton = buttons.find((button : any) => button.nativeElement.textContent.includes('Send Invite'));
    expect(sendInviteButton).toBeTruthy();
  });
});
