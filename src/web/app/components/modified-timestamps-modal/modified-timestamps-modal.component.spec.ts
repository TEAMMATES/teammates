import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TweakedTimestampData } from '../../pages-instructor/instructor-session-base-page.component';
import { ModifiedTimestampModalComponent } from './modified-timestamps-modal.component';

describe('ModifiedTimestampModalComponent', () => {
  let component: ModifiedTimestampModalComponent;
  let fixture: ComponentFixture<ModifiedTimestampModalComponent>;

  const coursesOfModifiedSession: string[] = [
    'CS315-Semester12023',
  ];

  const modifiedSessions: Record<string, TweakedTimestampData> = {
    'Instructor Feedback Session': {
      newTimestamp: {
        submissionStartTimestamp: '23 Apr 2023 9:00 PM',
        submissionEndTimestamp: '27 Apr 2023 11:59 PM',
        sessionVisibleTimestamp: 'On submission opening time',
        responseVisibleTimestamp: 'Not now (publish manually)',
      },
      oldTimestamp: {
        submissionStartTimestamp: '22 Mar 2023 9:00 PM',
        submissionEndTimestamp: '31 Mar 2023 11:59 PM',
        sessionVisibleTimestamp: 'On submission opening time',
        responseVisibleTimestamp: 'Not now (publish manually)',
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModifiedTimestampModalComponent],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifiedTimestampModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with coursesOfModifiedSession and modifiedSessions', () => {
    component.coursesOfModifiedSession = coursesOfModifiedSession;
    component.modifiedSessions = modifiedSessions;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with modifiedSessions', () => {
    component.modifiedSessions = modifiedSessions;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

});
