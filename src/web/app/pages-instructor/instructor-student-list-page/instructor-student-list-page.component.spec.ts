import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { CourseTab, InstructorStudentListPageComponent } from './instructor-student-list-page.component';
import { Course } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';

describe('InstructorStudentListPageComponent', () => {
  let component: InstructorStudentListPageComponent;
  let fixture: ComponentFixture<InstructorStudentListPageComponent>;

  const course1: Course = {
    courseId: 'course1Id',
    courseName: 'Course 1',
    timeZone: 'UTC',
    institute: 'Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
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
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-1',
          name: 'Student 1',
          teamName: 'Team 1',
          teamId: 'team-1',
          sectionName: 'Section 1',
          sectionId: 'section-1',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          email: 'student2@example.com',
          courseId: 'course1Id',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-2',
          name: 'Student 2',
          teamName: 'Team 1',
          teamId: 'team-1',
          sectionName: 'Section 1',
          sectionId: 'section-1',
        },
        isAllowedToModifyStudent: true,
      },
      {
        student: {
          email: 'student3@example.com',
          courseId: 'course1Id',
          courseName: 'Test Course',
          institute: 'Test Institute',
          userId: 'student-3',
          name: 'Student 3',
          teamName: 'Team 4',
          teamId: 'team-4',
          sectionName: 'Section 5',
          sectionId: 'section-5',
        },
        isAllowedToModifyStudent: true,
      },
    ],
    studentSortBy: SortBy.NONE,
    studentSortOrder: SortOrder.ASC,
    hasTabExpanded: false,
    hasStudentLoaded: false,
    hasLoadingFailed: false,
    stats: {
      numOfSections: 0,
      numOfStudents: 0,
      numOfTeams: 0,
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorStudentListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
