import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import { StudentService } from './student.service';
import { ResourceEndpoints } from '../types/api-const';
import { Course, Students } from '../types/api-output';
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
  courseId: string,
  service: StudentService,
  spyCourseService: any,
  testFn: (str: string) => void,
) => Promise<void> = async (
  courseId: string,
  service: StudentService,
  spyCourseService: any,
  testFn: (str: string) => void,
): Promise<void> => {
  const testDataModule = await import(`./test-data/${courseId}`);
  const testData = testDataModule.default ?? testDataModule;
  const course: Course = testData.course;
  const students: Students = testData.students;
  vi.spyOn(spyCourseService, 'getCourseAsInstructor').mockReturnValue(of(course));
  vi.spyOn(service, 'getStudentsFromCourse').mockReturnValue(of(students));
  await new Promise<void>((resolve) => {
    service.loadStudentListAsCsv({ courseId }).subscribe((csvResult: string) => {
      testFn(csvResult);
      resolve();
    });
  });
};

describe('StudentService', () => {
  let spyHttpRequestService: any;
  let spyCourseService: any;
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

  it('should execute DELETE when deleting all students in a course', () => {
    const paramMap: Record<string, string> = {
      courseid: 'CS3281',
      limit: '100',
    };
    vi.spyOn(spyHttpRequestService, 'delete');

    service.batchDeleteStudentsFromCourse({
      courseId: paramMap['courseid'],
      limit: Number.parseInt(paramMap['limit'], 10),
    });

    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.STUDENTS, paramMap);
  });

  it('should generate course student list with section as csv', async () => {
    await studentCsvListTester('studentCsvListWithSection.json', service, spyCourseService, (csvResult: string) => {
      expect(csvResult).toMatchSnapshot();
    });
  });

  it('should generate course student list without section as csv', async () => {
    await studentCsvListTester('studentCsvListWithoutSection.json', service, spyCourseService, (csvResult: string) => {
      expect(csvResult).toMatchSnapshot();
    });
  });
});
