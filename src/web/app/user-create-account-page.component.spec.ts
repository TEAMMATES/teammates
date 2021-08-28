import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AccountService } from '../services/account.service';
import { AuthService } from '../services/auth.service';
import { NavigationService } from '../services/navigation.service';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { UserCreateAccountPageComponent } from './user-create-account-page.component';
import Spy = jasmine.Spy;

describe('UserCreateAccountPageComponent', () => {
  let component: UserCreateAccountPageComponent;
  let fixture: ComponentFixture<UserCreateAccountPageComponent>;
  let navService: NavigationService;
  let accountService: AccountService;
  let authService: AuthService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserCreateAccountPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        LoadingSpinnerModule,
      ],
      providers: [
        NavigationService,
        AccountService,
        AuthService,
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              key: 'key',
            }),
          },
        },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserCreateAccountPageComponent);
    component = fixture.componentInstance;
    navService = TestBed.inject(NavigationService);
    accountService = TestBed.inject(AccountService);
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap if user has not created an account and has a valid url', () => {
    component.hasJoined = false;
    component.userId = '';
    component.validUrl = true;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with invalid url', () => {
    component.validUrl = false;
    component.isLoading = false;

    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should create account when create account button is clicked on', () => {
    const params: string[] = ['key'];
    component.isLoading = false;
    component.userId = 'user';
    component.key = params[0];
    component.validUrl = true;

    const accountSpy: Spy = spyOn(
      accountService,
      'createAccount',
    ).and.returnValue(of({}));
    const navSpy: Spy = spyOn(navService, 'navigateByURL');

    fixture.detectChanges();

    const btn: any =
      fixture.debugElement.nativeElement.querySelector('#btn-confirm');
    btn.click();

    expect(accountSpy.calls.count()).toEqual(1);
    expect(accountSpy.calls.mostRecent().args).toEqual(params);
    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/instructor');
  });

  it('should redirect user to home page if user already has account', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(
      of({
        user: {
          id: 'user',
          isInstructor: true,
        },
      }),
    );
    const navSpy: Spy = spyOn(navService, 'navigateByURL');

    component.ngOnInit();

    expect(component.userId).toEqual('user');
    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/instructor');
  });
});
