import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SessionLinksRecoveryPageComponent } from './session-links-recovery-page.component';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';

const mockStatusMessageService: jest.Mocked<Partial<StatusMessageService>> = {
  showErrorToast: jest.fn(),
};

const mockFeedbackSessionsService = {
  sendFeedbackSessionLinkToRecoveryEmail: jest.fn(),
};

/**
 * Sets a valid email in the form.
 */
function setValidEmail(component: SessionLinksRecoveryPageComponent): void {
  component.formSessionLinksRecovery.controls['email'].setValue('test@example.com');
}

/**
 * Sets the CAPTCHA state for testing purposes.
 */
function setCaptchaState(
  component: SessionLinksRecoveryPageComponent,
  state: { loaded: boolean; error: boolean },
): void {
  component.captchaLoaded = state.loaded;
  component.captchaError = state.error;
  (component as any).captchaSiteKey = 'fake-key';
}

describe('SessionLinksRecoveryPageComponent', () => {
  let component: SessionLinksRecoveryPageComponent;
  let fixture: ComponentFixture<SessionLinksRecoveryPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [SessionLinksRecoveryPageComponent],
      providers: [
        { provide: StatusMessageService, useValue: mockStatusMessageService },
        { provide: FeedbackSessionsService, useValue: mockFeedbackSessionsService },
      ],
    })
      .overrideComponent(SessionLinksRecoveryPageComponent, {
        set: {
          imports: [FormsModule, ReactiveFormsModule],
        },
      })
      .compileComponents();
  }));

  beforeEach(() => {
    jest.clearAllMocks();

    fixture = TestBed.createComponent(SessionLinksRecoveryPageComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    component.captchaElem = { reloadCaptcha: jest.fn() } as any;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error when email is invalid or empty', () => {
    component.onSubmitFormSessionLinksRecovery(component.formSessionLinksRecovery);

    expect(mockStatusMessageService.showErrorToast).toHaveBeenCalledWith('Please enter a valid email address.');
  });

  it('should show error when captcha is not completed', () => {
    setValidEmail(component);
    setCaptchaState(component, { loaded: true, error: false });

    component.onSubmitFormSessionLinksRecovery(component.formSessionLinksRecovery);

    expect(mockStatusMessageService.showErrorToast).toHaveBeenCalledWith(
      'Please complete the "I\'m not a robot" checkbox before submitting.',
    );
  });
});
