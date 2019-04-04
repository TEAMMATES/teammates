import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentHomePageComponent } from './student-home-page.component';

import { MatSnackBarModule } from '@angular/material';
import { ResponseStatusPipe } from '../../pipes/session-response-status.pipe';
import { SubmissionStatusPipe } from '../../pipes/session-submission-status.pipe';

describe('StudentHomePageComponent', () => {
  let component: StudentHomePageComponent;
  let fixture: ComponentFixture<StudentHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        StudentHomePageComponent,
        ResponseStatusPipe,
        SubmissionStatusPipe,
      ],
      imports: [
        HttpClientTestingModule,
        NgbModule,
        RouterTestingModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback sessions', () => {
    const studentName: string = '';
    const studentCourse: any = {
      course: {
        id: 'CS3281',
        name: 'Thematic Systems',
      },
      feedbackSessions: [],
    };
    component.user = studentName;
    component.courses = [studentCourse];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with all feedback sessions over 2 courses', () => {
    const studentName: string = 'John Doe';
    const studentCourseA: any = {
      course: {
        id: 'CS2103',
        name: 'Software Engineering',
      },
      feedbackSessions: [
        {
          feedbackSession: {
            feedbackSessionName: 'First Session',
            courseId: 'CS2103',
          },
        },
        {
          feedbackSession: {
            feedbackSessionName: 'Second Session',
            courseId: 'CS2103',
          },
        },
      ],
    };

    const studentCourseB: any = {
      course: {
        id: 'CS2102',
        name: 'Databases',
      },
      feedbackSessions: [
        {
          feedbackSession: {
            feedbackSessionName: 'Third Session',
            courseId: 'CS2102',
          },
        },
        {
          feedbackSession: {
            feedbackSessionName: 'Fourth Session',
            courseId: 'CS2102',
          },
        },
      ],
    };

    const publishedSessionInfoMap: any = {
      endTime: '1200',
      isOpened: true,
      isWaitingToOpen: true,
      isPublished: true,
      isSubmitted: true,
    };

    const unpublishedSessionInfoMap: any = {
      endTime: '1200',
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: false,
    };

    const submittedSessionInfoMap: any = {
      endTime: '1200',
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    };

    const concludedSessionInfoMap: any = {
      endTime: '1200',
      isOpened: false,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    };

    component.user = studentName;
    component.courses = [studentCourseA, studentCourseB];
    component.sessionsInfoMap = {
      'CS2103%First Session': publishedSessionInfoMap,
      'CS2103%Second Session': unpublishedSessionInfoMap,
      'CS2102%Third Session': submittedSessionInfoMap,
      'CS2102%Fourth Session': concludedSessionInfoMap,
    };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
