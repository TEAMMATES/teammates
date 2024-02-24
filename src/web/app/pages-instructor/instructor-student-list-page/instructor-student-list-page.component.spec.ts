import { HttpStatusCode } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { throwError } from 'rxjs';
import { CourseTab, InstructorStudentListPageComponent } from './instructor-student-list-page.component';
import { InstructorStudentListPageModule } from './instructor-student-list-page.module';
import { StudentService } from '../../../services/student.service';
import { Course } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

describe('InstructorStudentListPageComponent', () => {
  let component: InstructorStudentListPageComponent;
  let fixture: ComponentFixture<InstructorStudentListPageComponent>;
  let studentService: StudentService;

  const course1: Course = {
    courseId: 'course1Id',
    courseName: 'Course 1',
    timeZone: 'UTC',
    institute: 'Institute',
    creationTimestamp: 1649791778732,
    deletionTimestamp: 0,
  };

  const course1Tab: CourseTab = {
    course: course1,
    studentList: [
      {
        student: {
          email: 'student1@example.com',
          courseId: 'course1Id',
          name: 'Student 1',
          teamName: 'Team 1',
          sectionName: 'Section 1',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          email: 'student2@example.com',
          courseId: 'course1Id',
          name: 'Student 2',
          teamName: 'Team 1',
          sectionName: 'Section 1',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          email: 'student3@example.com',
          courseId: 'course1Id',
          name: 'Student 3',
          teamName: 'Team 4',
          sectionName: 'Section 5',
        },
        isAllowedToViewStudentInSection: true,
        isAllowedToModifyStudent: true,
      },
    ],
    studentSortBy: SortBy.NONE,
    studentSortOrder: SortOrder.ASC,
    hasTabExpanded: false,
    hasStudentLoaded: false,
    hasLoadingFailed: false,
    isAbleToViewStudents: true,
    stats: {
      numOfSections: 0,
      numOfStudents: 0,
      numOfTeams: 0,
    },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TeammatesRouterModule,
        RouterTestingModule,
        FormsModule,
        InstructorStudentListPageModule,
        PanelChevronModule,
        BrowserAnimationsModule,
      ],
      providers: [
        StudentService,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentListPageComponent);
    studentService = TestBed.inject(StudentService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should block instructors from viewing student details if they do not have the permission', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(throwError(() => ({
      status: HttpStatusCode.Forbidden,
      error: {
        message: 'You are not authorized to access this resource.',
      },
    })));
    component.loadStudents(course1Tab);
    expect(course1Tab.isAbleToViewStudents).toBeFalsy();
    expect(course1Tab.hasStudentLoaded).toBeTruthy();
    expect(course1Tab.studentList.length).toEqual(0);
  });

  it('should snap with a course with students the instructor has no permission to view', () => {
    component.courseTabList.push(course1Tab);
    component.isLoadingCourses = false;
    component.courseTabList[0].hasTabExpanded = true;
    component.courseTabList[0].hasStudentLoaded = true;
    component.courseTabList[0].isAbleToViewStudents = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
