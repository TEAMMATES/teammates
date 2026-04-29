import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { EnrollStatus } from './enroll-status';
import { InstructorCourseEnrollPageComponent } from './instructor-course-enroll-page.component';
import { Student, JoinState } from '../../../types/api-output';
import { StudentEnrollRequest } from '../../../types/api-request';

describe('InstructorCourseEnrollPageComponent', () => {
  let component: InstructorCourseEnrollPageComponent;
  let fixture: ComponentFixture<InstructorCourseEnrollPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NgxPageScrollCoreModule],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEnrollPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should treat enroll result emails case-insensitively', () => {
    const existingStudent: Student = {
      courseId: 'course-id',
      email: 'test@gmail.com',
      name: 'Existing Student',
      sectionName: 'Section A',
      teamName: 'Team A',
      comments: 'old comment',
      joinState: JoinState.JOINED,
    };
    const enrolledStudent: Student = {
      ...existingStudent,
      comments: 'new comment',
    };
    const enrollRequests: Map<number, StudentEnrollRequest> = new Map([
      [
        0,
        {
          section: 'Section A',
          team: 'Team A',
          name: 'Existing Student',
          email: 'Test@gmail.com',
          comments: 'new comment',
        },
      ],
    ]);

    const panels = (component as any).populateEnrollResultPanelList(
      [existingStudent],
      [enrolledStudent],
      enrollRequests,
    );

    expect(panels[EnrollStatus.MODIFIED].studentList).toEqual([enrolledStudent]);
    expect(panels[EnrollStatus.ERROR].studentList).toEqual([]);
    expect(component.modifiedStudentRowsIndex.has(0)).toBe(true);
  });

  it('should detect duplicate enroll emails case-insensitively', () => {
    const enrollRequests: Map<number, StudentEnrollRequest> = new Map([
      [
        0,
        {
          section: 'Section A',
          team: 'Team A',
          name: 'Student One',
          email: 'test@gmail.com',
          comments: '',
        },
      ],
      [
        1,
        {
          section: 'Section A',
          team: 'Team B',
          name: 'Student Two',
          email: 'Test@gmail.com',
          comments: '',
        },
      ],
    ]);

    (component as any).checkEmailNotRepeated(enrollRequests);

    expect(component.invalidRowsIndex).toEqual(new Set([0, 1]));
  });
});
