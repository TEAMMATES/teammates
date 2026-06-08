import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { InstructorSearchPageComponent } from './instructor-search-page.component';
import { SearchStudentsListRowTable } from './student-result-table/student-result-table.component';
import { HttpRequestService } from '../../../services/http-request.service';
import createSpyFromClass from '../../../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../../../types/api-const';
import { InstructorPermissionSet, InstructorPrivilege, JoinState, Student, Students } from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';

describe('InstructorSearchPageComponent', () => {
  let component: InstructorSearchPageComponent;
  let fixture: ComponentFixture<InstructorSearchPageComponent>;
  let spyHttpRequestService: any;
  let coursesWithStudents: SearchStudentsListRowTable[];

  const mockStudents: Students = {
    students: [
      {
        email: 'alice@example.com',
        courseId: 'CS3281',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-1',
        name: 'Alice',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Section 1',
        sectionId: 'section-1',
      },
      {
        email: 'bob@example.com',
        courseId: 'CS3281',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-2',
        name: 'Bob',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Section 1',
        sectionId: 'section-1',
      },
      {
        email: 'chloe@example.com',
        courseId: 'CS3281',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-3',
        name: 'Chloe',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Section 2',
        sectionId: 'section-2',
      },
      {
        email: 'david@example.com',
        courseId: 'CS3282',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-4',
        name: 'David',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Section 2',
        sectionId: 'section-2',
      },
    ],
  };

  beforeEach(async () => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorSearchPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const { students }: { students: Student[] } = mockStudents;
    coursesWithStudents = component.getCoursesWithStudents(students);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a search key', () => {
    component.searchParams.searchKey = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a student table', () => {
    component.studentsListRowTables = [
      {
        courseId: 'test-exa.demo',
        students: [
          {
            student: {
              name: 'tester',
              teamName: 'Team 1',
              teamId: 'team-1',
              email: 'tester@tester.com',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              sectionId: 'tutorial-group-1',
              courseId: 'test-exa.demo',
              courseName: 'Test Course',
              institute: 'Test Institute',
              userId: 'student-5',
            },
            isAllowedToViewStudentInSection: true,
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
              courseId: 'test-exa.demo',
              courseName: 'Test Course',
              institute: 'Test Institute',
              userId: 'student-6',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            student: {
              name: 'Alice Betsy',
              teamName: 'Team 1',
              teamId: 'team-1',
              email: 'alice.b.tmms@gmail.tmt',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              sectionId: 'section-1',
              courseId: 'test-exa.demo',
              courseName: 'Test Course',
              institute: 'Test Institute',
              userId: 'student-7',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            student: {
              name: 'Danny Engrid',
              teamName: 'Team 1',
              teamId: 'team-1',
              email: 'danny.e.tmms@gmail.tmt',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              sectionId: 'tutorial-group-1',
              courseId: 'test-exa.demo',
              courseName: 'Test Course',
              institute: 'Test Institute',
              userId: 'student-8',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
        ],
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should parse students into courses with sections correctly', () => {
    const { students }: { students: Student[] } = mockStudents;

    // Number of courses should match
    expect(coursesWithStudents.length).toEqual(Array.from(new Set(students.map((s: Student) => s.courseId))).length);

    // Number of sections in a course should match
    expect(
      Array.from(
        new Set(
          coursesWithStudents
            .find((t: SearchStudentsListRowTable) => t.courseId === students[0].courseId)
            ?.students.map((studentModel: StudentListRowModel) => studentModel.student.sectionName),
        ),
      ).length,
    ).toEqual(
      Array.from(
        new Set(
          students.filter((s: Student) => s.courseId === students[0].courseId).map((s: Student) => s.sectionName),
        ),
      ).length,
    );

    // Number of students in a section should match
    expect(
      coursesWithStudents
        .find((t: SearchStudentsListRowTable) => t.courseId === students[0].courseId)
        ?.students.filter((s: StudentListRowModel) => s.student.sectionName === students[0].sectionName).length,
    ).toEqual(students.filter((s: Student) => s.sectionName === students[0].sectionName).length);
  });

  it('should execute GET when fetching privileges', () => {
    spyHttpRequestService.get.mockImplementation((endpoint: string) => {
      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTOR_PRIVILEGE);
      return of<InstructorPrivilege>({
        privileges: {
          courseLevel: {
            canModifyCourse: true,
            canModifySession: true,
            canModifyStudent: true,
            canModifyInstructor: true,
            canViewStudentInSections: true,
            canModifySessionCommentsInSections: true,
            canViewSessionInSections: true,
            canSubmitSessionInSections: true,
          },
          sectionLevel: {},
          sessionLevel: {},
        },
      });
    });
    component.getPrivileges(coursesWithStudents);

    for (const course of coursesWithStudents) {
      expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, {
        courseid: course.courseId,
      });
    }
  });

  it('should combine privileges and course data correctly', () => {
    const basePrivilege: InstructorPermissionSet = {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    };
    const mockPrivilegesArray: InstructorPrivilege[] = [
      {
        privileges: {
          courseLevel: basePrivilege,
          sectionLevel: {},
          sessionLevel: {},
        },
      },
      {
        privileges: {
          courseLevel: {
            ...basePrivilege,
            canViewStudentInSections: false,
            canModifyStudent: true,
          },
          sectionLevel: {},
          sessionLevel: {},
        },
      },
      {
        privileges: {
          courseLevel: {
            ...basePrivilege,
            canViewStudentInSections: true,
            canModifyStudent: false,
          },
          sectionLevel: {},
          sessionLevel: {},
        },
      },
      {
        privileges: {
          courseLevel: {
            ...basePrivilege,
            canViewStudentInSections: false,
            canModifyStudent: false,
          },
          sectionLevel: {},
          sessionLevel: {},
        },
      },
    ];
    component.combinePrivileges([coursesWithStudents, mockPrivilegesArray]);

    const course1Student1: StudentListRowModel = coursesWithStudents[0].students[0];
    expect(course1Student1.isAllowedToViewStudentInSection).toEqual(true);
    expect(course1Student1.isAllowedToModifyStudent).toEqual(true);

    const course1Student2: StudentListRowModel = coursesWithStudents[0].students[1];
    expect(course1Student2.isAllowedToViewStudentInSection).toEqual(false);
    expect(course1Student2.isAllowedToModifyStudent).toEqual(true);

    const course1Student3: StudentListRowModel = coursesWithStudents[0].students[2];
    expect(course1Student3.isAllowedToViewStudentInSection).toEqual(true);
    expect(course1Student3.isAllowedToModifyStudent).toEqual(false);

    const course2Student1: StudentListRowModel = coursesWithStudents[1].students[0];
    expect(course2Student1.isAllowedToViewStudentInSection).toEqual(false);
    expect(course2Student1.isAllowedToModifyStudent).toEqual(false);

    expect(mockPrivilegesArray.length).toEqual(0);
  });
});
