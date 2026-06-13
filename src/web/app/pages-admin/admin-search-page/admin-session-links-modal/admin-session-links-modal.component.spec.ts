import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, Subject, throwError } from 'rxjs';
import { StatusMessageService } from '../../../../services/status-message.service';
import { UserService } from '../../../../services/user.service';
import { FeedbackSessionSubmissionStatus, SessionLinks } from '../../../../types/api-output';
import { AdminSessionLinksModalComponent } from './admin-session-links-modal.component';

const DEFAULT_SESSION_LINKS: SessionLinks = {
  courseJoinLink: 'https://teammates.tmt/join?key=abc',
  submissionLinks: [
    {
      feedbackSessionId: '17681c09-f4e5-40c2-be77-eeccf0c221c2',
      name: 'Closed Session Older',
      submissionStartTimestamp: 1710000000000,
      submissionEndTimestamp: 1710086400000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
      url: 'https://teammates.tmt/submission/closed-older',
    },
    {
      feedbackSessionId: '17681c09-f4e5-40c2-be77-eeccf0c221c6',
      name: 'Not Visible Session',
      submissionStartTimestamp: 1710172800000,
      submissionEndTimestamp: 1710259200000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.NOT_VISIBLE,
      url: 'https://teammates.tmt/submission/not-visible',
    },
    {
      feedbackSessionId: '17681c09-f4e5-40c2-be77-eeccf0c221c7',
      name: 'Open Session Later Deadline',
      submissionStartTimestamp: 1710345600000,
      submissionEndTimestamp: 1710691200000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      url: 'https://teammates.tmt/submission/open-later',
    },
    {
      feedbackSessionId: '17681c09-f4e5-40c2-be77-eeccf0c221c8',
      name: 'Visible Not Open Session',
      submissionStartTimestamp: 1710432000000,
      submissionEndTimestamp: 1710518400000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN,
      url: 'https://teammates.tmt/submission/visible-not-open',
    },
    {
      feedbackSessionId: '17681c09-f4e5-40c2-be77-eeccf0c221c9',
      name: 'Grace Period Session',
      submissionStartTimestamp: 1710524800000,
      submissionEndTimestamp: 1710691200000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.GRACE_PERIOD,
      url: 'https://teammates.tmt/submission/grace-period',
    },
    {
      feedbackSessionId: '17681c09-f4e5-40c2-be77-eeccf0c221ca',
      name: 'Open Session Earlier Deadline',
      submissionStartTimestamp: 1710617600000,
      submissionEndTimestamp: 1710604800000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      url: 'https://teammates.tmt/submission/open-earlier',
    },
    {
      feedbackSessionId: 'ee81c09-f4e5-40c2-be77-eeccf0c221c3',
      name: 'Closed Session Newer',
      submissionStartTimestamp: 1710700000000,
      submissionEndTimestamp: 1710786400000,
      timeZone: 'UTC',
      submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
      url: 'https://teammates.tmt/submission/closed-newer',
    },
  ],
  resultsLinks: [
    {
      feedbackSessionId: 'aa81c09-f4e5-40c2-be77-eeccf0c221c4',
      name: 'Results Oldest',
      submissionStartTimestamp: 1710345600000,
      submissionEndTimestamp: 1710432000000,
      timeZone: 'UTC',
      url: 'https://teammates.tmt/results/oldest',
    },
    {
      feedbackSessionId: 'aa81c09-f4e5-40c2-be77-eeccf0c221c5',
      name: 'Results Newest',
      submissionStartTimestamp: 1710524800000,
      submissionEndTimestamp: 1710611200000,
      timeZone: 'UTC',
      url: 'https://teammates.tmt/results/newest',
    },
    {
      feedbackSessionId: 'aa81c09-f4e5-40c2-be77-eeccf0c221c6',
      name: 'Results Middle',
      submissionStartTimestamp: 1710432000000,
      submissionEndTimestamp: 1710518400000,
      timeZone: 'UTC',
      url: 'https://teammates.tmt/results/middle',
    },
  ],
};

describe('AdminSessionLinksModalComponent', () => {
  let component: AdminSessionLinksModalComponent;
  let fixture: ComponentFixture<AdminSessionLinksModalComponent>;
  let userService: UserService;
  let statusMessageService: StatusMessageService;
  let activeModal: NgbActiveModal;

  beforeEach(async () => {
    Object.defineProperty(navigator, 'clipboard', {
      configurable: true,
      value: {
        writeText: vi.fn().mockResolvedValue(undefined),
      },
    });

    await TestBed.configureTestingModule({
      providers: [NgbActiveModal, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminSessionLinksModalComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    statusMessageService = TestBed.inject(StatusMessageService);
    activeModal = TestBed.inject(NgbActiveModal);
    component.userId = '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4';
    component.userName = 'Student';
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show loading state while waiting for links', () => {
    const sessionLinks$ = new Subject<SessionLinks>();
    vi.spyOn(userService, 'getSessionLinks').mockReturnValue(sessionLinks$.asObservable());

    fixture.detectChanges();

    expect(fixture.debugElement.nativeElement.querySelector('tm-ajax-loading')).not.toBeNull();
  });

  it('should render submission and result links after loading', () => {
    vi.spyOn(userService, 'getSessionLinks').mockReturnValue(of(DEFAULT_SESSION_LINKS));

    fixture.detectChanges();

    expect(fixture.debugElement.nativeElement.querySelectorAll('[data-testid="submission-link-row"]').length).toBe(7);
    expect(fixture.debugElement.nativeElement.querySelectorAll('[data-testid="results-link-row"]').length).toBe(3);

    const statusBadge: HTMLElement = fixture.debugElement.nativeElement.querySelector('.badge');
    expect(statusBadge.textContent).toContain('Open');
    expect(statusBadge.className).toContain('bg-success');
  });

  it('should sort submission and result links for display', () => {
    vi.spyOn(userService, 'getSessionLinks').mockReturnValue(of(DEFAULT_SESSION_LINKS));

    fixture.detectChanges();

    const submissionLinkNames = Array.from<Element>(
      fixture.debugElement.nativeElement.querySelectorAll('[data-testid="submission-link-row"] .fw-semibold'),
    ).map((element: Element) => element.textContent?.trim());
    const resultLinkNames = Array.from<Element>(
      fixture.debugElement.nativeElement.querySelectorAll('[data-testid="results-link-row"] .fw-semibold'),
    ).map((element: Element) => element.textContent?.trim());

    expect(submissionLinkNames).toEqual([
      'Grace Period Session',
      'Open Session Earlier Deadline',
      'Open Session Later Deadline',
      'Visible Not Open Session',
      'Not Visible Session',
      'Closed Session Newer',
      'Closed Session Older',
    ]);
    expect(resultLinkNames).toEqual(['Results Newest', 'Results Middle', 'Results Oldest']);
  });

  it('should copy a link and show success toast', async () => {
    vi.spyOn(userService, 'getSessionLinks').mockReturnValue(of(DEFAULT_SESSION_LINKS));
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast');

    fixture.detectChanges();

    const copyButton: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector(
      '[data-testid="copy-course-join-link-button"]',
    );
    copyButton.click();
    await fixture.whenStable();

    expect(navigator.clipboard.writeText).toHaveBeenCalledWith(DEFAULT_SESSION_LINKS.courseJoinLink);
    expect(successSpy).toHaveBeenCalledWith('Link copied.');
  });

  it('should show error and dismiss on load failure', () => {
    vi.spyOn(userService, 'getSessionLinks').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'Unable to load links.',
        },
      })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');
    const dismissSpy = vi.spyOn(activeModal, 'dismiss');

    fixture.detectChanges();

    expect(errorSpy).toHaveBeenCalledWith('Unable to load links.');
    expect(dismissSpy).toHaveBeenCalledWith('Unable to load links.');
  });
});
