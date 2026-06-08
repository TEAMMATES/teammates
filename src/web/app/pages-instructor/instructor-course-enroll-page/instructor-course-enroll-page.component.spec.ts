import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { EnrollStatus } from './enroll-status';
import { InstructorCourseEnrollPageComponent } from './instructor-course-enroll-page.component';
import { Student, JoinState } from '../../../types/api-output';
import { StudentEnrollRequest } from '../../../types/api-request';

describe('InstructorCourseEnrollPageComponent', () => {
  let component: InstructorCourseEnrollPageComponent;
  let fixture: ComponentFixture<InstructorCourseEnrollPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

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
      courseName: 'Test Course',
      institute: 'Test Institute',
      userId: 'test-user-id',
      email: 'test@gmail.com',
      name: 'Existing Student',
      sectionName: 'Section A',
      sectionId: 'section-a',
      teamName: 'Team A',
      teamId: 'team-a',
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

    const panels = component.populateEnrollResultPanelList([existingStudent], [enrolledStudent], enrollRequests);

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

    component.checkEmailNotRepeated(enrollRequests);

    expect(component.invalidRowsIndex).toEqual(new Set([0, 1]));
  });
});
