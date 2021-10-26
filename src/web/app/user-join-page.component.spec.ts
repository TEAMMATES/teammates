import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { NavigationService } from '../services/navigation.service';
import { UserJoinPageComponent } from './user-join-page.component';
import Spy = jasmine.Spy;

describe('UserJoinPageComponent', () => {
  let component: UserJoinPageComponent;
  let fixture: ComponentFixture<UserJoinPageComponent>;
  let navService: NavigationService;
  let courseService: CourseService;
  let authService: AuthService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserJoinPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
      ],
      providers: [
        NavigationService,
        CourseService,
        AuthService,
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              entitytype: 'student',
              key: 'key',
            }),
          },
        },
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserJoinPageComponent);
    component = fixture.componentInstance;
    navService = TestBed.inject(NavigationService);
    courseService = TestBed.inject(CourseService);
    authService = TestBed.inject(AuthService);
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

  it('should join course when join course button is clicked on', () => {
    const params: string[] = ['key', 'student'];
    component.isLoading = false;
    component.hasJoined = false;
    component.userId = 'user';
    component.key = params[0];
    component.entityType = params[1];
    component.validUrl = true;

    const courseSpy: Spy = spyOn(courseService, 'joinCourse').and.returnValue(of({}));
    const navSpy: Spy = spyOn(navService, 'navigateByURL');

    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement.querySelector('#btn-confirm');
    btn.click();

    expect(courseSpy.calls.count()).toEqual(1);
    expect(courseSpy.calls.mostRecent().args).toEqual(params);
    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual(`/web/${params[1]}`);
  });

  it('should redirect user to home page if user is logged in and join URL has been used', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(of({
      user: {
        id: 'user',
        isAdmin: false,
        isInstructor: false,
        isStudent: false,
        isMaintainer: false,
      },
      masquerade: false,
    }));
    spyOn(courseService, 'getJoinCourseStatus').and.returnValue(of({
      hasJoined: true,
    }));
    const navSpy: Spy = spyOn(navService, 'navigateByURL');

    component.ngOnInit();

    expect(component.hasJoined).toBeTruthy();
    expect(component.userId).toEqual('user');
    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/student/home');
  });

  it('should stop loading and show error message if 404 is returned', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(of({
      user: {
        id: 'user',
        isAdmin: false,
        isInstructor: false,
        isStudent: false,
        isMaintainer: false,
      },
      masquerade: false,
    }));
    spyOn(courseService, 'getJoinCourseStatus').and.returnValue(throwError({
      status: 404,
    }));

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
    expect(component.validUrl).toBeFalsy();
  });

  it('should stop loading and redirect if user is not logged in', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(of({
      masquerade: false,
    }));
    spyOn(courseService, 'getJoinCourseStatus').and.returnValue(of({
      hasJoined: true,
    }));

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
  });
});
