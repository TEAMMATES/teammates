import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, Subject, throwError } from 'rxjs';
import { SimpleModalType } from './components/simple-modal/simple-modal-type';
import { UserJoinPageComponent } from './user-join-page.component';
import { AccountService } from '../services/account.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { SimpleModalService } from '../services/simple-modal.service';
import { TimezoneService } from '../services/timezone.service';
import { createMockNgbModalRef } from '../test-helpers/mock-ngb-modal-ref';
import { AuthService } from '../services/auth.service';

describe('UserJoinPageComponent', () => {
  let component: UserJoinPageComponent;
  let fixture: ComponentFixture<UserJoinPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let courseService: CourseService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;
  let queryParamsSubject: Subject<any>;

  const mockAuthInfo = {
    user: {
      id: 'user',
      isAdmin: false,
      isInstructor: false,
      isStudent: true,
      isMaintainer: false,
      accountId: 'account-id',
    },
    loginUrl: '/login',
    masquerade: false,
  };

  beforeEach(async () => {
    queryParamsSubject = new Subject<any>();

    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParamsSubject.asObservable(),
            snapshot: {
              data: {
                authInfo: mockAuthInfo,
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserJoinPageComponent);
    component = fixture.componentInstance;
    ngbModal = TestBed.inject(NgbModal);
    navService = TestBed.inject(NavigationService);
    courseService = TestBed.inject(CourseService);
    simpleModalService = TestBed.inject(SimpleModalService);
    authService = TestBed.inject(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap if user is not logged in and has a valid url', () => {
    component.hasJoined = false;
    component.userId = '';
    component.validUrl = true;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with invalid course join link', () => {
    component.userId = 'user';
    component.validUrl = false;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with valid course join link that has been used', () => {
    component.userId = 'user';
    component.validUrl = true;
    component.hasJoined = true;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with valid course join link that has not been used', () => {
    component.validUrl = true;
    component.userId = 'user';
    component.hasJoined = false;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should show error message if 4xx is returned when joining course', () => {
    const errorMessage = '404 ERROR';
    vi.spyOn(courseService, 'joinCourse').mockReturnValue(
      throwError(() => ({
        error: {
          message: errorMessage,
        },
        status: 404,
      })),
    );

    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    component.joinCourse();

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith('ERROR', SimpleModalType.DANGER, errorMessage);
  });

  it('should show error message if 5xx is returned when joining course', () => {
    const errorMessage = '502 ERROR';
    const requestId = 'requestId';
    vi.spyOn(courseService, 'joinCourse').mockReturnValue(
      throwError(() => ({
        error: {
          message: errorMessage,
          requestId,
        },
        status: 502,
      })),
    );

    const mockModalRef = createMockNgbModalRef();
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.joinCourse();

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(mockModalRef.componentInstance.requestId).toEqual(requestId);
    expect(mockModalRef.componentInstance.errorMessage).toEqual(errorMessage);
  });

  it('should join course when join course button is clicked on', () => {
    const key = 'key';
    const entityType = 'student';
    component.isLoading = false;
    component.hasJoined = false;
    component.userId = 'user';
    component.key = key;
    component.entityType = entityType;
    component.validUrl = true;

    const courseSpy = vi.spyOn(courseService, 'joinCourse').mockReturnValue(of({}));
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(mockAuthInfo));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm');
    btn.click();

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenLastCalledWith({ key });
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith(`/web/${entityType}`);
  });

  it('should redirect user to home page if user is logged in and join URL has been used', () => {
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      of({
        hasJoined: true,
      }),
    );
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    vi.spyOn(authService, 'authInfo').mockReturnValue(mockAuthInfo);

    component.ngOnInit();

    queryParamsSubject.next({
      entitytype: 'student',
      key: 'key',
    });

    expect(component.hasJoined).toBeTruthy();
    expect(component.userId).toEqual('user');
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/home');
  });

  it('should stop loading and show error message if 404 is returned', () => {
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      throwError(() => ({
        status: 404,
      })),
    );

    component.ngOnInit();

    queryParamsSubject.next({
      entitytype: 'student',
      key: 'key',
    });

    expect(component.isLoading).toBeFalsy();
    expect(component.validUrl).toBeFalsy();
  });

  it('should stop loading and redirect if user is not logged in', () => {
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      of({
        hasJoined: true,
      }),
    );

    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    component.ngOnInit();

    queryParamsSubject.next({
      entitytype: 'student',
      key: 'key',
    });

    expect(component.hasJoined).toBeTruthy();
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/home');
  });
});

describe('UserJoinPageComponent creating account', () => {
  let component: UserJoinPageComponent;
  let fixture: ComponentFixture<UserJoinPageComponent>;
  let navService: NavigationService;
  let accountService: AccountService;
  let courseService: CourseService;
  let timezoneService: TimezoneService;
  let queryParamsSubject: Subject<any>;
  let authService: AuthService;

  const mockAuthInfo = {
    user: {
      id: 'user',
      isAdmin: false,
      isInstructor: false,
      isStudent: true,
      isMaintainer: false,
      accountId: 'account-id',
    },
    loginUrl: '/login',
    masquerade: false,
  };

  beforeEach(async () => {
    queryParamsSubject = new Subject<any>();

    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParamsSubject.asObservable(),
            snapshot: {
              data: {
                authInfo: mockAuthInfo,
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserJoinPageComponent);
    component = fixture.componentInstance;
    navService = TestBed.inject(NavigationService);
    accountService = TestBed.inject(AccountService);
    courseService = TestBed.inject(CourseService);
    timezoneService = TestBed.inject(TimezoneService);
    authService = TestBed.inject(AuthService);
  });

  it('should create account and join course when join course button is clicked on', () => {
    component.isLoading = false;
    component.hasJoined = false;
    component.userId = 'user';
    component.isCreatingAccount = true;
    component.key = 'key';
    component.entityType = 'instructor';
    component.validUrl = true;

    const accountSpy = vi.spyOn(accountService, 'createAccount').mockReturnValue(
      of({
        message: 'test message',
      }),
    );
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);
    vi.spyOn(timezoneService, 'guessTimezone').mockReturnValue('UTC');

    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm');
    btn.click();

    expect(accountSpy).toHaveBeenCalledTimes(1);
    expect(accountSpy).toHaveBeenLastCalledWith('key', 'UTC');
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/instructor');
  });

  it('should redirect user to home page if user is logged in and URL has been used', () => {
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      of({
        hasJoined: true,
      }),
    );
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    vi.spyOn(authService, 'authInfo').mockReturnValue(mockAuthInfo);

    component.ngOnInit();

    queryParamsSubject.next({
      entitytype: 'student',
      key: 'key',
      iscreatingaccount: 'true',
    });

    expect(component.hasJoined).toBeTruthy();
    expect(component.userId).toEqual('user');
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/instructor/home');
  });

  it('should stop loading and show error message if 404 is returned when creating new account', () => {
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      throwError(() => ({
        status: 404,
      })),
    );

    component.ngOnInit();

    queryParamsSubject.next({
      entitytype: 'instructor',
      key: 'key',
      iscreatingaccount: 'true',
    });

    expect(component.entityType).toBe('instructor');
    expect(component.isCreatingAccount).toBeTruthy();
    expect(component.isLoading).toBeFalsy();
    expect(component.validUrl).toBeFalsy();
  });
});
