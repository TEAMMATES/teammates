import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { StudentListComponent, StudentListRowModel } from './student-list.component';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { createBuilder, studentBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { JoinState } from '../../../types/api-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

describe('StudentListComponent', () => {
  let component: StudentListComponent;
  let fixture: ComponentFixture<StudentListComponent>;
  let simpleModalService: SimpleModalService;

  const studentListRowModelBuilder = createBuilder<StudentListRowModel>({
    student: studentBuilder.build(),
    isAllowedToModifyStudent: true,
  });

  const getButtonGroupByStudentEmail = (email: string): DebugElement | null => {
    const studentListDebugElement = fixture.debugElement;
    if (studentListDebugElement) {
      const studentRows = studentListDebugElement.queryAll(By.css('tbody tr'));
      for (const row of studentRows) {
        const emailSpan = row.query(By.css('td:nth-child(5) span'));
        if (emailSpan?.nativeElement.textContent.trim() === email) {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentListComponent);
    simpleModalService = TestBed.inject(SimpleModalService);
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
          teamId: 'team-1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-001',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-002',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-003',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-004',
        },
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
          teamId: 'team-1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-005',
        },
        isAllowedToModifyStudent: false,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-006',
        },
        isAllowedToModifyStudent: false,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-007',
        },
        isAllowedToModifyStudent: true,
      },

      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-008',
        },
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
          teamId: 'team-1',
          email: 'tester@tester.com',
          joinState: JoinState.NOT_JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-009',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.NOT_JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-010',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-011',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-012',
        },
        isAllowedToModifyStudent: true,
      },
    ];

    component.enableRemindButton = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it(
    'should snap with enable remind button set to true, one student yet to join when not allowed to modify' +
    ' student',
    () => {
      component.studentModels = [
        {
          student: {
            name: 'tester',
            teamName: 'Team 1',
            teamId: 'team-1',
            email: 'tester@tester.com',
            joinState: JoinState.NOT_JOINED,
            sectionName: 'Tutorial Group 1',
            sectionId: 'tutorial-group-1',
            courseId: 'text-exa.demo',
            courseName: 'Test Course',
            institute: 'Test Institute',
            userId: 'student-013',
          },
          isAllowedToModifyStudent: false,
        },
        {
          student: {
            name: 'Benny Charles',
            teamName: 'Team 1',
            teamId: 'team-1',
            email: 'benny.c.tmms@gmail.tmt',
            joinState: JoinState.JOINED,
            sectionName: 'Tutorial Group 1',
            sectionId: 'tutorial-group-1',
            courseId: 'text-exa.demo',
            courseName: 'Test Course',
            institute: 'Test Institute',
            userId: 'student-014',
          },
          isAllowedToModifyStudent: true,
        },
        {
          student: {
            name: 'Alice Betsy',
            teamName: 'Team 1',
            teamId: 'team-1',
            email: 'alice.b.tmms@gmail.tmt',
            joinState: JoinState.JOINED,
            sectionName: 'Tutorial Group 2',
            sectionId: 'tutorial-group-2',
            courseId: 'text-exa.demo',
            courseName: 'Test Course',
            institute: 'Test Institute',
            userId: 'student-015',
          },
          isAllowedToModifyStudent: true,
        },
        {
          student: {
            name: 'Danny Engrid',
            teamName: 'Team 1',
            teamId: 'team-1',
            email: 'danny.e.tmms@gmail.tmt',
            joinState: JoinState.JOINED,
            sectionName: 'Tutorial Group 2',
            sectionId: 'tutorial-group-2',
            courseId: 'text-exa.demo',
            courseName: 'Test Course',
            institute: 'Test Institute',
            userId: 'student-016',
          },
          isAllowedToModifyStudent: true,
        },
      ];

      component.enableRemindButton = true;

      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    },
  );

  it('should snap with some student list data and some students to hide', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-017',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Benny Charles',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'benny.c.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 1',
          sectionId: 'tutorial-group-1',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-018',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Alice Betsy',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'alice.b.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'tutorial-group-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-019',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          name: 'Danny Engrid',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'danny.e.tmms@gmail.tmt',
          joinState: JoinState.JOINED,
          sectionName: 'Tutorial Group 2',
          sectionId: 'section-2',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-020',
        },
        isAllowedToModifyStudent: true,
      },
    ];

    component.hiddenStudents = ['alice.b.tmms@gmail.tmt', 'tester@tester.com'];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some student list data with no sections', () => {
    component.studentModels = [
      {
        student: {
          name: 'tester',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'tester@tester.com',
          joinState: JoinState.JOINED,
          sectionName: 'None',
          sectionId: 'None',
          courseId: 'text-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-021',
        },
        isAllowedToModifyStudent: true,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
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



  it('openDeleteModal: should display warning when deleting student from course', async () => {
    const promise: Promise<void> = Promise.resolve();
    const mockModalRef = createMockNgbModalRef({}, promise);
    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);

    const removeStudentFromCourseSpy = vi.spyOn(component, 'removeStudentFromCourse');

    const studentModel = studentListRowModelBuilder.build();
    component.studentModels = [studentModel];

    fixture.detectChanges();

    const buttonGroup = getButtonGroupByStudentEmail(studentModel.student.email);
    const deleteButton = getButtonByText(buttonGroup, 'Delete');

    deleteButton?.nativeElement.click();

    await promise;

    const expectedModalHeader = `Delete student <strong>${studentModel.student.name}</strong>?`;
    const expectedModalContent: string =
      'Are you sure you want to remove ' +
      `<strong>${studentModel.student.name}</strong> ` +
      `from the course <strong>${component.courseId}?</strong>`;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(expectedModalHeader, SimpleModalType.DANGER, expectedModalContent);

    expect(removeStudentFromCourseSpy).toHaveBeenCalledWith(studentModel);
    expect(component.students).not.toContain(studentModel);
  });





  it('setRowData: should highlight partial matches when enabled', () => {
    component.isPartialMatchHighlightingEnabled = true;
    component.searchString = 'te';
    component.studentModels = [
      {
        student: {
          name: 'Tester',
          teamName: 'Team 1',
          teamId: 'team-1',
          email: 'tester@example.com',
          joinState: JoinState.JOINED,
          sectionName: 'Section 1',
          sectionId: 'section-1',
          courseId: 'test-exa.demo',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-023',
        },
        isAllowedToModifyStudent: true,
      },
    ];

    expect(component.rowsData[0][2].displayValue).toBe(
      '<span class="highlighted-text">Te</span>s<span class="highlighted-text">te</span>r',
    );
  });
});
