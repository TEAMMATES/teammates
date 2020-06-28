import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Observable, of } from 'rxjs';
import { ResourceEndpoints } from '../types/api-endpoints';
import { JoinState, Student } from '../types/api-output';
import { StudentUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';
import { StudentService } from './student.service';
import Spy = jasmine.Spy;

const defaultStudentUpdateRequest: StudentUpdateRequest = {
  name: 'John Doe',
  email: 'johndoe@gmail.com',
  team: '',
  section: '',
  comments: '',
  isSessionSummarySendEmail: true,
};

describe('StudentService', () => {
  let spyHttpRequestService: any;
  let service: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [HttpRequestService],
    });
    service = TestBed.get(StudentService);
    spyHttpRequestService = TestBed.get(HttpRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute PUT when updating students in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      studentemail: 'johndoe@gmail.com',
    };
    spyOn(spyHttpRequestService, 'put').and.stub();

    service.updateStudent({
      courseId: paramMap.courseid,
      studentEmail: paramMap.studentemail,
      requestBody: defaultStudentUpdateRequest,
    });

    expect(spyHttpRequestService.put)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENT, paramMap, defaultStudentUpdateRequest);
  });

  it('should execute DELETE when deleting all students in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
    };
    spyOn(spyHttpRequestService, 'delete').and.stub();

    service.deleteAllStudentsFromCourse({
      courseId: paramMap.courseid,
    });

    expect(spyHttpRequestService.delete)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENTS, paramMap);
  });

  it('should execute POST when regenerating key of a student in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      studentemail: 'johndoe@gmail.com',
    };
    spyOn(spyHttpRequestService, 'post').and.stub();

    service.regenerateStudentCourseLinks(paramMap.courseid, paramMap.studentemail);

    expect(spyHttpRequestService.post)
        .toHaveBeenCalledWith(ResourceEndpoints.STUDENT_COURSE_LINKS_REGENERATION, paramMap);
  });

  it('should execute GET on course & students endpoints when generating student csv list', () => {
    const courseId: string = 'CS3281';
    const httpSpy: Spy = spyOn(spyHttpRequestService, 'get').and.returnValue(of(''));
    const studentList: Observable<string> = service.loadStudentListAsCsv({ courseId });
    expect(httpSpy).toBeCalledWith(ResourceEndpoints.COURSE, {
      courseid: courseId,
      entitytype: 'instructor',
    });
    studentList.subscribe(() => expect(httpSpy).toBeCalledWith(ResourceEndpoints.STUDENTS, {
      courseid: courseId,
    }));
  });

  it('should show course student list with section', () => {
    const courseId: string = 'listWithSection';
    const courseName: string = 'List With Section';
    const students: Student[] =
      [
        {
          courseId,
          email: 'student1OfTypicalCourse@typicalCourse.com',
          name: 'student1OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: 'Team 1',
          sectionName: 'Section A',
        },
        {
          courseId,
          email: 'student2OfTypicalCourse@typicalCourse.com',
          name: 'student2OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: 'Team 2',
          sectionName: 'Section A',
        },
      ];
    expect(service.processStudentsToCsv(courseId, courseName, students)).toMatchSnapshot();
  });

  it('should show course student list without section', () => {
    const courseId: string = 'listWithoutSection';
    const courseName: string = 'List Without Section';
    const students: Student[] =
      [
        {
          courseId,
          email: 'student1OfTypicalCourse@typicalCourse.com',
          name: 'student1OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: 'Team 1',
          sectionName: 'None',
        },
        {
          courseId,
          email: 'student2OfTypicalCourse@typicalCourse.com',
          name: 'student2OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: 'Team 2',
          sectionName: 'None',
        },
      ];
    expect(service.processStudentsToCsv(courseId, courseName, students)).toMatchSnapshot();
  });

  it('should show course student list with special team name', () => {
    const courseId: string = 'listWithSpecialTeamName';
    const courseName: string = 'List With Special Team Name';
    const students: Student[] =
      [
        {
          courseId,
          email: 'student1OfTypicalCourse@typicalCourse.com',
          name: 'student1OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: 'N/A',
          sectionName: 'Section A',
        },
        {
          courseId,
          email: 'student2OfTypicalCourse@typicalCourse.com',
          name: 'student2OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: '-Nil-',
          sectionName: 'Section A',
        },
      ];
    expect(service.processStudentsToCsv(courseId, courseName, students)).toMatchSnapshot();
  });

  it('should show course student list with student last name', () => {
    const courseId: string = 'listWithLastName';
    const courseName: string = 'List With Last Name';
    const students: Student[] =
      [
        {
          courseId,
          email: 'student1OfTypicalCourse@typicalCourse.com',
          name: 'student1OfTypicalCourse',
          lastName: ' of The Last Name',
          joinState: JoinState.JOINED,
          teamName: 'Team 1',
          sectionName: 'Section A',
        },
        {
          courseId,
          email: 'student2OfTypicalCourse@typicalCourse.com',
          name: 'student2OfTypicalCourse',
          lastName: "with apostrophe' here",
          joinState: JoinState.JOINED,
          teamName: 'Team 2',
          sectionName: 'Section A',
        },
      ];
    expect(service.processStudentsToCsv(courseId, courseName, students)).toMatchSnapshot();
  });

  it('should show course student list with unregistered student', () => {
    const courseId: string = 'listWithUnregistered';
    const courseName: string = 'List With Unregistered Student';
    const students: Student[] =
      [
        {
          courseId,
          email: 'student1OfTypicalCourse@typicalCourse.com',
          name: 'student1OfTypicalCourse',
          joinState: JoinState.JOINED,
          teamName: 'Team 1',
          sectionName: 'Section A',
        },
        {
          courseId,
          email: 'student2OfTypicalCourse@typicalCourse.com',
          name: 'student2OfTypicalCourse',
          joinState: JoinState.NOT_JOINED,
          teamName: 'Team 2',
          sectionName: 'Section A',
        },
      ];
    expect(service.processStudentsToCsv(courseId, courseName, students)).toMatchSnapshot();
  });
});
