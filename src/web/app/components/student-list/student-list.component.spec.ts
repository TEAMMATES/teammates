import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import { StudentListComponent, StudentListRowModel } from './student-list.component';
import { StudentListModule } from './student-list.module';
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder, studentBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { JoinState } from '../../../types/api-output';
import { Pipes } from '../../pipes/pipes.module';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

describe('StudentListComponent', () => {
  let component: StudentListComponent;
  let fixture: ComponentFixture<StudentListComponent>;
  let simpleModalService: SimpleModalService;
  let courseService: CourseService;
  let statusMessageService: StatusMessageService;

  const studentListRowModelBuilder = createBuilder<StudentListRowModel>({
    student: studentBuilder.build(),
    isAllowedToModifyStudent: true,
    isAllowedToViewStudentInSection: true,
  });

  const getButtonGroupByStudentEmail = (email: string): DebugElement | null => {
    const studentListDebugElement = fixture.debugElement;
    if (studentListDebugElement) {
      const studentRows = studentListDebugElement.queryAll(By.css('tbody tr'));
      for (const row of studentRows) {
        const emailSpan = row.query(By.css('td:nth-child(5) span'));
        if (emailSpan && emailSpan.nativeElement.textContent.trim() === email) {
          return row.query(By.css('tm-group-buttons'));
        }
      }
    }
    return null;
  };

  const getButtonByText = (buttonGroup: DebugElement | null, text: string): DebugElement | null => {
    if (buttonGroup) {
      const buttons = buttonGroup.queryAll(By.css('.btn'));
      return buttons.find((button) => button.nativeElement.textContent.includes(text)) ?? null;
    }

    return null;
  };

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
    simpleModalService = TestBed.inject(SimpleModalService);
    courseService = TestBed.inject(CourseService);
    statusMessageService = TestBed.inject(StatusMessageService);
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

  it('hasSection: should return true when there are sections in the course', () => {
    const studentOne = studentBuilder.sectionName('None').build();
    const studentTwo = studentBuilder.sectionName('section-one').build();
    component.studentModels = [
      studentListRowModelBuilder.student(studentOne).build(),
      studentListRowModelBuilder.student(studentTwo).build(),
    ];

    expect(component.hasSection()).toBe(true);
  });

  it('hasSection: should return false when there are no sections in the course', () => {
    const studentOne = studentBuilder.sectionName('None').build();
    const studentTwo = studentBuilder.sectionName('None').build();
    component.studentModels = [
      studentListRowModelBuilder.student(studentOne).build(),
      studentListRowModelBuilder.student(studentTwo).build(),
    ];

    expect(component.hasSection()).toBe(false);
  });

  it('openReminderModal: should display warning when reminding student to join course', async () => {
    const promise: Promise<void> = Promise.resolve();
    const mockModalRef = createMockNgbModalRef({}, promise);
    const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);

    const reminderStudentFromCourseSpy = jest.spyOn(component, 'remindStudentFromCourse');

    const student = studentBuilder.build();
    student.joinState = JoinState.NOT_JOINED;
    const studentModel = studentListRowModelBuilder.student(student).build();
    component.enableRemindButton = true;
    component.studentModels = [studentModel];

    fixture.detectChanges();

    const buttonGroup = getButtonGroupByStudentEmail(studentModel.student.email);
    const sendInviteButton = getButtonByText(buttonGroup, 'Send Invite');

    sendInviteButton?.nativeElement.click();

    await promise;

    const expectedModalContent: string = `Usually, there is no need to use this feature because
          TEAMMATES sends an automatic invite to students at the opening time of each session.
          Send a join request to <strong>${studentModel.student.email}</strong> anyway?`;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Send join request?',
        SimpleModalType.INFO, expectedModalContent);

    expect(reminderStudentFromCourseSpy).toHaveBeenCalledWith(studentModel.student.email);
  });

  it('openDeleteModal: should display warning when deleting student from course', async () => {
    const promise: Promise<void> = Promise.resolve();
    const mockModalRef = createMockNgbModalRef({}, promise);
    const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);

    const removeStudentFromCourseSpy = jest.spyOn(component, 'removeStudentFromCourse');

    const studentModel = studentListRowModelBuilder.build();
    component.studentModels = [studentModel];

    fixture.detectChanges();

    const buttonGroup = getButtonGroupByStudentEmail(studentModel.student.email);
    const deleteButton = getButtonByText(buttonGroup, 'Delete');

    deleteButton?.nativeElement.click();

    await promise;

    const expectedModalHeader = `Delete student <strong>${studentModel.student.name}</strong>?`;
    const expectedModalContent: string = 'Are you sure you want to remove '
        + `<strong>${studentModel.student.name}</strong> `
        + `from the course <strong>${component.courseId}?</strong>`;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(expectedModalHeader,
        SimpleModalType.DANGER, expectedModalContent);

    expect(removeStudentFromCourseSpy).toHaveBeenCalledWith(studentModel.student.email);
    expect(component.students).not.toContain(studentModel.student.email);
  });

  it('remindStudentFromCourse: should call statusMessageService.showSuccessToast with'
    + 'correct message upon success', () => {
    const successMessage = 'success';
    jest.spyOn(courseService, 'remindStudentForJoin')
        .mockReturnValue(of({ message: successMessage }));
    const studentEmail = 'testemail@gmail.com';

    const statusMessageServiceSpy = jest.spyOn(statusMessageService, 'showSuccessToast');

    component.remindStudentFromCourse(studentEmail);

    expect(statusMessageServiceSpy).toHaveBeenLastCalledWith(successMessage);
  });

  it('remindStudentFromCourse: should call statusMessageService.showErrorToast with correct message upon error', () => {
    const errorMessage = 'error';
    jest.spyOn(courseService, 'remindStudentForJoin')
        .mockReturnValue(throwError(() => ({
          error: { message: errorMessage },
        })));
    const studentEmail = 'testemail@gmail.com';

    const statusMessageServiceSpy = jest.spyOn(statusMessageService, 'showErrorToast');

    component.remindStudentFromCourse(studentEmail);

    expect(statusMessageServiceSpy).toHaveBeenLastCalledWith(errorMessage);
  });
});
