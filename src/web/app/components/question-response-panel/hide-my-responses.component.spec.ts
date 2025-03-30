import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { QuestionResponsePanelComponent } from './question-response-panel.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';

describe('HideMyResponses Feature', () => {
  let component: QuestionResponsePanelComponent;
  let fixture: ComponentFixture<QuestionResponsePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuestionResponsePanelComponent ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
      ],
      providers: [
        FeedbackSessionsService,
        StatusMessageService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(QuestionResponsePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be able to toggle hide/show responses', fakeAsync(() => {
    expect(component.hideSelfResponses).toBeFalsy();

    component.hideSelfResponses = true;
    tick();
    fixture.detectChanges();
    expect(component.hideSelfResponses).toBeTruthy();

    component.hideSelfResponses = false;
    tick();
    fixture.detectChanges();
    expect(component.hideSelfResponses).toBeFalsy();
  }));

  it('should initialize with default values', () => {
    expect(component.session).toBeDefined();
    expect(component.session.courseId).toBe('');
    expect(component.questions).toEqual([]);
    expect(component.regKey).toBe('');
    expect(component.previewAsPerson).toBe('');
  });

  it('should have a defined FeedbackSessionsService', () => {
    const feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    expect(feedbackSessionsService).toBeDefined();
  });

  it('should have a defined StatusMessageService', () => {
    const statusMessageService = TestBed.inject(StatusMessageService);
    expect(statusMessageService).toBeDefined();
  });

  it('should render without errors', () => {
    expect(fixture.nativeElement).toBeTruthy();
  });

  it('should have RESPONSE_HIDDEN_QUESTIONS array with "CONTRIB" initially', () => {
    expect(component.RESPONSE_HIDDEN_QUESTIONS).toEqual(['CONTRIB']);
  });

  it('should have "STUDENT_RESULT" as initial intent', () => {
    expect(component.intent).toBe('STUDENT_RESULT');
  });

  it('should update hideSelfResponses when toggled multiple times', fakeAsync(() => {
    component.hideSelfResponses = true;
    tick();
    fixture.detectChanges();
    expect(component.hideSelfResponses).toBeTruthy();

    component.hideSelfResponses = false;
    tick();
    fixture.detectChanges();
    expect(component.hideSelfResponses).toBeFalsy();

    component.hideSelfResponses = true;
    tick();
    fixture.detectChanges();
    expect(component.hideSelfResponses).toBeTruthy();
  }));
});
