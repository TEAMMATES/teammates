import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { SearchStudentsTable } from '../app/pages-instructor/instructor-search-page/instructor-search-page.component';
import { StudentListSectionData } from '../app/pages-instructor/student-list/student-list-section-data';
import { ResourceEndpoints } from '../types/api-endpoints';
import { InstructorPrivilege, JoinState, Student, Students } from '../types/api-output';
import { HttpRequestService } from './http-request.service';
import { SearchService } from './search.service';

describe('SearchService', () => {
  let spyHttpRequestService: any;
  let service: SearchService;

  const mockStudents: Students = {
    students: [
      {
        email: 'alice@example.com',
        courseId: 'CS3281',
        name: 'Alice',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 1',
      },
      {
        email: 'bob@example.com',
        courseId: 'CS3281',
        name: 'Bob',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 1',
      },
      {
        email: 'chloe@example.com',
        courseId: 'CS3281',
        name: 'Chloe',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 2',
      },
      {
        email: 'david@example.com',
        courseId: 'CS3282',
        name: 'David',
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Section 2',
      },
    ],
  };
  let coursesWithSections: SearchStudentsTable[];

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.get(SearchService);
    coursesWithSections = service.getCoursesWithSections(mockStudents);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when searching for students', () => {
    service.getStudents('Alice');
    const paramMap: { [key: string]: string } = {
      searchkey: 'Alice',
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(
      '/search/students',
      paramMap,
    );
  });

  it('should parse students into courses with sections correctly', () => {
    const { students }: { students: Student[] } = mockStudents;

    // Number of courses should match
    expect(coursesWithSections.length).toEqual(
      Array.from(new Set(students.map((s: Student) => s.courseId))).length,
    );

    // Number of sections in a course should match
    expect(
      coursesWithSections.filter((t: SearchStudentsTable) => t.courseId === students[0].courseId)[0]
        .sections.length,
    ).toEqual(
      Array.from(
        new Set(
          students
            .filter((s: Student) => s.courseId === students[0].courseId)
            .map((s: Student) => s.sectionName),
        ),
      ).length,
    );

    // Number of students in a section should match
    expect(
      coursesWithSections
        .filter((t: SearchStudentsTable) => t.courseId === students[0].courseId)[0]
        .sections.filter((s: StudentListSectionData) => s.sectionName === students[0].sectionName)[0]
        .students.length,
    ).toEqual(
      students.filter((s: Student) => s.sectionName === students[0].sectionName).length,
    );
  });

  it('should execute GET when fetching privileges', () => {
    spyHttpRequestService.get.mockImplementation((endpoint: string) => {
      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTOR_PRIVILEGE);
      return of<InstructorPrivilege>({
        canModifyCourse: true,
        canModifySession: true,
        canModifyStudent: true,
        canModifyInstructor: true,
        canViewStudentInSections: true,
        canModifySessionCommentsInSections: true,
        canViewSessionInSections: true,
        canSubmitSessionInSections: true,
      });
    });
    service.getPrivileges(coursesWithSections);

    for (const course of coursesWithSections) {
      for (const section of course.sections) {
        expect(spyHttpRequestService.get).toHaveBeenCalledWith(
          ResourceEndpoints.INSTRUCTOR_PRIVILEGE,
          {
            courseid: course.courseId,
            sectionname: section.sectionName,
          },
        );
      }
    }
  });

  it('should combine privileges and course data correctly', () => {
    const basePrivilege: InstructorPrivilege = {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    };
    const mockPrivileges: InstructorPrivilege[] = [
      basePrivilege,
      {
        ...basePrivilege,
        canViewStudentInSections: false,
        canModifyStudent: true,
      },
      {
        ...basePrivilege,
        canViewStudentInSections: true,
        canModifyStudent: false,
      },
    ];
    service.combinePrivileges([coursesWithSections, mockPrivileges]);

    const course1Section1: StudentListSectionData = coursesWithSections[0].sections[0];
    expect(course1Section1.isAllowedToViewStudentInSection).toEqual(true);
    expect(course1Section1.isAllowedToModifyStudent).toEqual(true);

    const course1Section2: StudentListSectionData = coursesWithSections[0].sections[1];
    expect(course1Section2.isAllowedToViewStudentInSection).toEqual(false);
    expect(course1Section2.isAllowedToModifyStudent).toEqual(true);

    const course2Section1: StudentListSectionData = coursesWithSections[1].sections[0];
    expect(course2Section1.isAllowedToViewStudentInSection).toEqual(true);
    expect(course2Section1.isAllowedToModifyStudent).toEqual(false);

    expect(mockPrivileges.length).toEqual(0);
  });
});
