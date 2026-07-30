import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { InstructorSearchPageComponent } from './instructor-search-page.component';
import { SearchStudentsListRowTable } from './student-result-table/student-result-table.component';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../../../test-helpers/mock-http-request';
import {
  InstructorCourses,
  InstructorPermissionSet,
  InstructorPrivilege,
  JoinState,
  Student,
  Students,
} from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';

describe('InstructorSearchPageComponent', () => {
  let component: InstructorSearchPageComponent;
  let fixture: ComponentFixture<InstructorSearchPageComponent>;
  let spyHttpRequestService: MockHttpRequestService;
  let coursesWithStudents: SearchStudentsListRowTable[];
  const mockCourseService = {
    getAllCoursesAsInstructor: vi.fn(),
  };
  const mockInstructorService = {
    loadInstructorPrivilege: vi.fn(),
  };

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
    spyHttpRequestService = createMockHttpRequestService();
    mockCourseService.getAllCoursesAsInstructor.mockReturnValue(
      of<InstructorCourses>({
        courses: [
          {
            courseId: 'CS3281',
            courseName: 'Course 1',
            timeZone: 'UTC',
            institute: 'Test Institute',
            country: 'SG',
            instituteId: 'test-institute',
            creationTimestamp: 0,
            deletionTimestamp: 0,
          },
          {
            courseId: 'CS3282',
            courseName: 'Course 2',
            timeZone: 'UTC',
            institute: 'Test Institute',
            country: 'SG',
            instituteId: 'test-institute',
            creationTimestamp: 0,
            deletionTimestamp: 0,
          },
        ],
        instructorPermissions: {},
      }),
    );
    mockInstructorService.loadInstructorPrivilege.mockReturnValue(
      of<InstructorPrivilege>({
        privileges: {
          courseLevel: {
            canModifyCourse: false,
            canModifySession: false,
            canModifyStudent: false,
            canModifyInstructor: false,
            canViewSession: false,
            canSubmitSession: false,
          },
          sectionLevel: {},
          sessionLevel: {},
        },
      }),
    );
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        { provide: CourseService, useValue: mockCourseService },
        { provide: InstructorService, useValue: mockInstructorService },
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

  it('should call loadInstructorPrivilege once per unique courseId', () => {
    component.getPrivileges(coursesWithStudents).subscribe(() => {
      // 2 unique courses: CS3281 and CS3282
      expect(mockInstructorService.loadInstructorPrivilege).toHaveBeenCalledTimes(2);
      expect(mockInstructorService.loadInstructorPrivilege).toHaveBeenCalledWith({ courseId: 'CS3281' });
      expect(mockInstructorService.loadInstructorPrivilege).toHaveBeenCalledWith({ courseId: 'CS3282' });
    });
  });

  it('should use cached privileges on subsequent calls', () => {
    component.getPrivileges(coursesWithStudents).subscribe(() => {
      mockInstructorService.loadInstructorPrivilege.mockClear();

      component.getPrivileges(coursesWithStudents).subscribe(() => {
        expect(mockInstructorService.loadInstructorPrivilege).not.toHaveBeenCalled();
      });
    });
  });

  it('should combine privileges and course data correctly', () => {
    const basePrivilege: InstructorPermissionSet = {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewSession: true,
      canSubmitSession: true,
    };
    const privilegeMap: Map<string, InstructorPrivilege> = new Map([
      [
        'CS3281',
        {
          privileges: {
            courseLevel: basePrivilege,
            sectionLevel: {},
            sessionLevel: {},
          },
        },
      ],
      [
        'CS3282',
        {
          privileges: {
            courseLevel: {
              ...basePrivilege,
              canModifyStudent: false,
            },
            sectionLevel: {},
            sessionLevel: {},
          },
        },
      ],
    ]);
    component.combinePrivileges(coursesWithStudents, privilegeMap);

    // CS3281 has courseLevel canModifyStudent: true
    expect(coursesWithStudents[0].students[0].isAllowedToModifyStudent).toEqual(true);
    expect(coursesWithStudents[0].students[1].isAllowedToModifyStudent).toEqual(true);
    expect(coursesWithStudents[0].students[2].isAllowedToModifyStudent).toEqual(true);

    // CS3282 has courseLevel canModifyStudent: false
    expect(coursesWithStudents[1].students[0].isAllowedToModifyStudent).toEqual(false);
  });
});
