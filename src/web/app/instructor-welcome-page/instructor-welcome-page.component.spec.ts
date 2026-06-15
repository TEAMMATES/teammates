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
import { AccountRequest, AccountRequestStatus } from '../../types/api-output';

const mockAccountRequest: AccountRequest = {
  accountRequestId: 'test-id-123',
  email: 'test@example.com',
  name: 'Test Instructor',
  institute: 'Test University',
  country: 'Singapore',
  status: AccountRequestStatus.APPROVED,
  createdAt: 1000000,
};

function createActivatedRoute(accountRequestId: string | null) {
  return {
    snapshot: {
      queryParamMap: {
        get: (key: string) => (key === 'accountRequestId' ? accountRequestId : null),
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

  async function setup(accountRequestId: string | null = 'test-id-123') {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: createActivatedRoute(accountRequestId),
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

  it('should show invalid link when accountRequestId param is missing', async () => {
    await setup(null);
    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountRequest()).toBeNull();
  });

  it('should show invalid link when account request API call fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Not found' }, status: 404 })),
    );

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountRequest()).toBeNull();
  });

  it('should load account request and show welcome card', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(of(mockAccountRequest));

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(false);
    expect(component.isLoading()).toBe(false);
    expect(component.accountRequest()).toEqual(mockAccountRequest);
  });

  it('should show invalid link if account request status is pending', async () => {
    await setup();
    const pendingRequest: AccountRequest = { ...mockAccountRequest, status: AccountRequestStatus.PENDING };
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(of(pendingRequest));

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountRequest()).toBeNull();
  });

  it('should show invalid link if account request status is rejected', async () => {
    await setup();
    const rejectedRequest: AccountRequest = { ...mockAccountRequest, status: AccountRequestStatus.REJECTED };
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(of(rejectedRequest));

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountRequest()).toBeNull();
  });

  it('should redirect to instructor home if demo course is already created', async () => {
    await setup();
    const request: AccountRequest = { ...mockAccountRequest, createdDemoCourseAt: 2000000 };
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(of(request));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();

    expect(navSpy).toHaveBeenCalledWith('/web/instructor/home');
    expect(component.accountRequest()).toBeNull();
  });

  it('should navigate to instructor home on successful get started', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(of(mockAccountRequest));
    vi.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
    vi.spyOn(courseService, 'createDemoCourse').mockReturnValue(of({ message: 'Success' }));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();
    component.getStarted();

    expect(courseService.createDemoCourse).toHaveBeenCalledWith({
      accountRequestId: 'test-id-123',
      timezone: 'Asia/Singapore',
    });
    expect(navSpy).toHaveBeenCalledWith('/web/instructor/home');
  });

  it('should show invalid link if createDemoCourse fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountRequest').mockReturnValue(of(mockAccountRequest));
    vi.spyOn(courseService, 'createDemoCourse').mockReturnValue(
      throwError(() => ({ error: { message: 'Server error' }, status: 500 })),
    );

    fixture.detectChanges();
    component.getStarted();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.accountRequest()).toBeNull();
    expect(component.isCreatingCourse()).toBe(false);
  });
});
