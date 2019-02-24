import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentHomePageComponent } from './student-home-page.component';

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

  it('should snap with null fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with blank fields', () => {
    const studentName: string = '';
    const studentCourse: any = {
      course: {},
      feedbackSessions: [],
    };
    component.user = studentName;
    component.courses = [studentCourse];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    const studentName: string = 'John Doe';
    const studentCourse: any = {
      course: {
        id: 'CS2103',
        name: 'Software Engineering',
      },
      feedbackSessions: [],
    };
    const sessionInfoMap: any = {
      endTime: '1200',
      isOpened: true,
      isWaitingToOpen: true,
      isPublished: true,
      isSubmitted: true,
    };

    component.user = studentName;
    component.courses = [studentCourse];
    component.sessionsInfoMap.set('Session', sessionInfoMap);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
