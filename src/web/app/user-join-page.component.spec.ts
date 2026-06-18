import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, throwError } from 'rxjs';
import { SimpleModalType } from './components/simple-modal/simple-modal-type';
import { UserJoinPageComponent } from './user-join-page.component';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { SimpleModalService } from '../services/simple-modal.service';
import { createMockNgbModalRef } from '../test-helpers/mock-ngb-modal-ref';

describe('UserJoinPageComponent', () => {
  let component: UserJoinPageComponent;
  let fixture: ComponentFixture<UserJoinPageComponent>;
  let navService: NavigationService;
  let courseService: CourseService;
  let authService: AuthService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              entityType: 'student',
              key: 'key',
            }),
          },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserJoinPageComponent);
    component = fixture.componentInstance;
    ngbModal = TestBed.inject(NgbModal);
    navService = TestBed.inject(NavigationService);
    courseService = TestBed.inject(CourseService);
    authService = TestBed.inject(AuthService);
    simpleModalService = TestBed.inject(SimpleModalService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap if user is not logged in and has a valid url', () => {
    component.hasJoined = false;
    component.accountEmail = '';
    component.validUrl = true;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with invalid course join link', () => {
    component.accountEmail = 'user@teammates.tmt';
    component.validUrl = false;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with valid course join link that has been used', () => {
    component.accountEmail = 'user@teammates.tmt';
    component.validUrl = true;
    component.hasJoined = true;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with valid course join link that has not been used', () => {
    component.validUrl = true;
    component.accountEmail = 'user@teammates.tmt';
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
        },
        status: 502,
        headers: {
          get: (headerName: string) => {
            if (headerName === 'X-Request-Id') {
              return requestId;
            }
            return null;
          },
        },
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
    component.accountEmail = 'user@teammates.tmt';
    component.key = key;
    component.entityType = entityType;
    component.validUrl = true;

    const courseSpy = vi.spyOn(courseService, 'joinCourse').mockReturnValue(of({ message: 'Joined course' }));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();

    const btn = fixture.debugElement.nativeElement.querySelector('#btn-confirm');
    btn.click();

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenLastCalledWith({ key });
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith(`/web/${entityType}`);
  });

  it('should redirect user to home page if user is logged in and join URL has been used', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(
      of({
        loginUrl: '/login',
        user: {
          id: 'user',
          accountEmail: 'user@teammates.tmt',
          isAdmin: false,
          isInstructor: false,
          isStudent: false,
          isMaintainer: false,
          accountId: 'account-id',
        },
        masquerade: false,
      }),
    );
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      of({
        hasJoined: true,
      }),
    );
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    component.ngOnInit();

    expect(component.hasJoined).toBeTruthy();
    expect(component.accountEmail).toEqual('user@teammates.tmt');
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/home');
  });

  it('should stop loading and show error message if 404 is returned', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(
      of({
        loginUrl: '/login',
        user: {
          id: 'user',
          accountEmail: 'user@teammates.tmt',
          isAdmin: false,
          isInstructor: false,
          isStudent: false,
          isMaintainer: false,
          accountId: '',
        },
        masquerade: false,
      }),
    );
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      throwError(() => ({
        status: 404,
      })),
    );

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
    expect(component.validUrl).toBeFalsy();
  });

  it('should stop loading and redirect if user is not logged in', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(
      of({
        loginUrl: '/login',
        masquerade: false,
      }),
    );
    vi.spyOn(courseService, 'getJoinCourseStatus').mockReturnValue(
      of({
        hasJoined: true,
      }),
    );

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
  });
});
