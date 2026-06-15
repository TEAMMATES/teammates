import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { InstructorWelcomePageComponent } from './instructor-welcome-page.component';
import { AccountService } from '../../services/account.service';
import { CourseService } from '../../services/course.service';
import { NavigationService } from '../../services/navigation.service';
import { TimezoneService } from '../../services/timezone.service';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../types/api-output';

const mockAccountVerificationRequest: AccountVerificationRequest = {
  accountVerificationRequestId: 'test-id-123',
  email: 'test@example.com',
  name: 'Test Instructor',
  institute: 'Test University',
  country: 'Singapore',
  status: AccountVerificationRequestStatus.APPROVED,
  createdAt: 1000000,
};

function createActivatedRoute(accountVerificationRequestId: string | null) {
  return {
    snapshot: {
      queryParamMap: {
        get: (key: string) => (key === 'accountVerificationRequestId' ? accountVerificationRequestId : null),
      },
    },
  };
}

describe('InstructorWelcomePageComponent', () => {
  let component: InstructorWelcomePageComponent;
  let fixture: ComponentFixture<InstructorWelcomePageComponent>;
  let accountService: AccountService;
  let courseService: CourseService;
  let navService: NavigationService;
  let timezoneService: TimezoneService;

  async function setup(accountVerificationRequestId: string | null = 'test-id-123') {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: createActivatedRoute(accountVerificationRequestId),
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorWelcomePageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    courseService = TestBed.inject(CourseService);
    navService = TestBed.inject(NavigationService);
    timezoneService = TestBed.inject(TimezoneService);
  }

  it('should show invalid link when accountVerificationRequestId param is missing', async () => {
    await setup(null);
    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountVerificationRequest()).toBeNull();
  });

  it('should show invalid link when account verification request API call fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Not found' }, status: 404 })),
    );

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountVerificationRequest()).toBeNull();
  });

  it('should load account verification request and show welcome card', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockAccountVerificationRequest));

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(false);
    expect(component.isLoading()).toBe(false);
    expect(component.accountVerificationRequest()).toEqual(mockAccountVerificationRequest);
  });

  it('should show invalid link if account verification request status is pending', async () => {
    await setup();
    const pendingRequest: AccountVerificationRequest = {
      ...mockAccountVerificationRequest,
      status: AccountVerificationRequestStatus.PENDING,
    };
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(pendingRequest));

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountVerificationRequest()).toBeNull();
  });

  it('should show invalid link if account verification request status is rejected', async () => {
    await setup();
    const rejectedRequest: AccountVerificationRequest = {
      ...mockAccountVerificationRequest,
      status: AccountVerificationRequestStatus.REJECTED,
    };
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(rejectedRequest));

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountVerificationRequest()).toBeNull();
  });

  it('should redirect to instructor home if demo course is already created', async () => {
    await setup();
    const request: AccountVerificationRequest = { ...mockAccountVerificationRequest, createdDemoCourseAt: 2000000 };
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(request));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();

    expect(navSpy).toHaveBeenCalledWith('/web/instructor/home');
    expect(component.accountVerificationRequest()).toBeNull();
  });

  it('should navigate to instructor home on successful get started', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockAccountVerificationRequest));
    vi.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
    vi.spyOn(courseService, 'createDemoCourse').mockReturnValue(of({ message: 'Success' }));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();
    component.getStarted();

    expect(courseService.createDemoCourse).toHaveBeenCalledWith({
      accountVerificationRequestId: 'test-id-123',
      timezone: 'Asia/Singapore',
    });
    expect(navSpy).toHaveBeenCalledWith('/web/instructor/home');
  });

  it('should show invalid link if createDemoCourse fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockAccountVerificationRequest));
    vi.spyOn(courseService, 'createDemoCourse').mockReturnValue(
      throwError(() => ({ error: { message: 'Server error' }, status: 500 })),
    );

    fixture.detectChanges();
    component.getStarted();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.accountVerificationRequest()).toBeNull();
    expect(component.isCreatingCourse()).toBe(false);
  });
});
