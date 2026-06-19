import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { InstructorSearchPageComponent } from './instructor-search-page.component';
import { SearchStudentsListRowTable } from './student-result-table/student-result-table.component';
import { HttpRequestService } from '../../../services/http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../../../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../../../types/api-const';
import { InstructorPermissionSet, InstructorPrivilege, JoinState, Student, Students } from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';

describe('InstructorSearchPageComponent', () => {
  let component: InstructorSearchPageComponent;
  let fixture: ComponentFixture<InstructorSearchPageComponent>;
  let spyHttpRequestService: MockHttpRequestService;
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

  const basePrivilege: InstructorPermissionSet = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudent: true,
    canModifySessionComments: true,
    canViewSession: true,
    canSubmitSession: true,
  };

  beforeEach(async () => {
    spyHttpRequestService = createMockHttpRequestService();
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

  it('should call loadInstructorPrivilege once per course, not per section', () => {
    spyHttpRequestService.get.mockImplementation((endpoint: string) => {
      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTOR_PRIVILEGE);
      return of<InstructorPrivilege>({
        privileges: {
          courseLevel: basePrivilege,
          sectionLevel: {},
          sessionLevel: {},
        },
      });
    });
    component.getPrivileges(coursesWithStudents);

    // coursesWithStudents has 2 courses (CS3281 and CS3282), so we expect exactly 2 API calls
    // Previously, the bug caused one call per section (CS3281 has 2 sections => 3 calls total)
    const distinctCourseIds = Array.from(new Set(coursesWithStudents.map((c) => c.courseId)));
    expect(spyHttpRequestService.get).toHaveBeenCalledTimes(distinctCourseIds.length);
    for (const courseId of distinctCourseIds) {
      expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, {
        courseid: courseId,
      });
    }
  });

  it('should use cached privileges on repeated calls for the same course', () => {
    spyHttpRequestService.get.mockReturnValue(
      of<InstructorPrivilege>({
        privileges: { courseLevel: basePrivilege, sectionLevel: {}, sessionLevel: {} },
      }),
    );

    // First call populates the cache
    component.getPrivileges(coursesWithStudents);
    const firstCallCount = spyHttpRequestService.get.mock.calls.length;

    // Second call with the same courses should use the cache and make no new API calls
    component.getPrivileges(coursesWithStudents);
    expect(spyHttpRequestService.get).toHaveBeenCalledTimes(firstCallCount);
  });

  it('should combine privileges using course-keyed map correctly', () => {
    const privilegeMap = new Map<string, InstructorPrivilege>([
      [
        'CS3281',
        {
          privileges: {
            courseLevel: basePrivilege,
            sectionLevel: {
              'section-2': { ...basePrivilege, canViewStudent: false, canModifyStudent: false },
            },
            sessionLevel: {},
          },
        },
      ],
      [
        'CS3282',
        {
          privileges: {
            courseLevel: { ...basePrivilege, canViewStudent: false, canModifyStudent: false },
            sectionLevel: {},
            sessionLevel: {},
          },
        },
      ],
    ]);
    component.combinePrivileges([coursesWithStudents, privilegeMap]);

    // CS3281, Section 1 students — use courseLevel (canView: true, canModify: true)
    const cs3281Section1Students = coursesWithStudents[0].students.filter(
      (s: StudentListRowModel) => s.student.sectionId === 'section-1',
    );
    for (const studentModel of cs3281Section1Students) {
      expect(studentModel.isAllowedToViewStudentInSection).toEqual(true);
      expect(studentModel.isAllowedToModifyStudent).toEqual(true);
    }

    // CS3281, Section 2 students — use sectionLevel override (canView: false, canModify: false)
    const cs3281Section2Students = coursesWithStudents[0].students.filter(
      (s: StudentListRowModel) => s.student.sectionId === 'section-2',
    );
    for (const studentModel of cs3281Section2Students) {
      expect(studentModel.isAllowedToViewStudentInSection).toEqual(false);
      expect(studentModel.isAllowedToModifyStudent).toEqual(false);
    }

    // CS3282 students — courseLevel (canView: false, canModify: false)
    const cs3282Students = coursesWithStudents[1].students;
    for (const studentModel of cs3282Students) {
      expect(studentModel.isAllowedToViewStudentInSection).toEqual(false);
      expect(studentModel.isAllowedToModifyStudent).toEqual(false);
    }
  });
});
