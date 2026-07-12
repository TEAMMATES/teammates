import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import { StudentService } from './student.service';
import * as studentCsvListWithSection from './test-data/student-csv-list-with-section';
import * as studentCsvListWithoutSection from './test-data/student-csv-list-without-section';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { Course, CourseView, Students } from '../types/api-output';
import { StudentUpdateRequest } from '../types/api-request';

const defaultStudentUpdateRequest: StudentUpdateRequest = {
  name: 'John Doe',
  email: 'johndoe@gmail.com',
  team: '',
  section: '',
  comments: '',
  isSessionSummarySendEmail: true,
};

const studentCsvListTester: (
  testData: { course: Course; students: Students },
  service: StudentService,
  spyCourseService: CourseService,
  testFn: (str: string) => void,
) => Promise<void> = async (
  testData: { course: Course; students: Students },
  service: StudentService,
  spyCourseService: CourseService,
  testFn: (str: string) => void,
): Promise<void> => {
  const courseView: CourseView = {
    course: testData.course,
  };
  vi.spyOn(spyCourseService, 'getCourseAsInstructor').mockReturnValue(of(courseView));
  vi.spyOn(service, 'getStudents').mockReturnValue(of(testData.students));
  await new Promise<void>((resolve) => {
    service.loadStudentListAsCsv({ courseId: testData.course.courseId }).subscribe((csvResult: string) => {
      testFn(csvResult);
      resolve();
    });
  });
};

describe('StudentService', () => {
  let spyHttpRequestService: HttpRequestService;
  let spyCourseService: CourseService;
  let service: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [HttpRequestService, CourseService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(StudentService);
    spyHttpRequestService = TestBed.inject(HttpRequestService);
    spyCourseService = TestBed.inject(CourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute PUT when updating students in a course', () => {
    const paramMap: Record<string, string> = {
      userid: '12345',
    };
    vi.spyOn(spyHttpRequestService, 'put');

    service.updateStudent(
      {
        userId: paramMap['userid'],
      },
      defaultStudentUpdateRequest,
    );

    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.STUDENT,
      paramMap,
      defaultStudentUpdateRequest,
    );
  });

  it('should execute GET when getting a student by user ID', () => {
    const paramMap: Record<string, string> = {
      userid: '00000000-0000-4000-9000-000000000001',
    };
    vi.spyOn(spyHttpRequestService, 'get');

    service.getStudent({
      userId: paramMap['userid'],
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENT, paramMap);
  });

  it('should execute GET when getting the current student of a course', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.COURSE_ID]: 'CS3281',
    };
    vi.spyOn(spyHttpRequestService, 'get');

    service.getOwnStudent({
      courseId: paramMap[QueryParamKeys.COURSE_ID],
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.OWN_STUDENT, paramMap);
  });

  it('should execute GET when getting all students in a course', () => {
    const paramMap = {
      [QueryParamKeys.COURSE_ID]: ['CS3281'],
    };
    vi.spyOn(spyHttpRequestService, 'get');

    service.getStudents({
      courseIds: paramMap[QueryParamKeys.COURSE_ID],
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENTS, paramMap);
  });

  it('should execute GET when loading students with search parameters', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.SEARCH_KEY]: 'Alice',
      [QueryParamKeys.LIMIT]: '50',
    };
    vi.spyOn(spyHttpRequestService, 'get');

    service.getStudents({
      searchKey: 'Alice',
      limit: 50,
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.STUDENTS, paramMap);
  });

  it('should execute GET when getting own team students', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.COURSE_ID]: 'CS3281',
    };
    vi.spyOn(spyHttpRequestService, 'get');

    service.getOwnTeamStudents({
      courseId: paramMap[QueryParamKeys.COURSE_ID],
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.OWN_TEAM_STUDENTS, paramMap);
  });

  it('should execute DELETE when deleting all students in a course', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.COURSE_ID]: 'CS3281',
    };
    vi.spyOn(spyHttpRequestService, 'delete');

    service.deleteStudentsFromCourse({
      courseId: paramMap[QueryParamKeys.COURSE_ID],
    });

    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.STUDENTS, paramMap);
  });

  it('should generate course student list with section as csv', async () => {
    await studentCsvListTester(studentCsvListWithSection, service, spyCourseService, (csvResult: string) => {
      expect(csvResult).toMatchSnapshot();
    });
  });

  it('should generate course student list without section as csv', async () => {
    await studentCsvListTester(studentCsvListWithoutSection, service, spyCourseService, (csvResult: string) => {
      expect(csvResult).toMatchSnapshot();
    });
  });
});
