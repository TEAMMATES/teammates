import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginPageComponent } from './login-page.component';
import { ActivatedRoute, Params } from '@angular/router';
import { of, Subject } from 'rxjs';
import { Config, LoginMethod } from '../../types/api-output';
import { ConfigService } from '../../services/config.service';

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let fixture: ComponentFixture<LoginPageComponent>;
  let queryParams$: Subject<Params>;

  beforeEach(async () => {
    queryParams$ = new Subject<Params>();
    await TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParams$.asObservable(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should extract query params', () => {
    const mockQueryParams = { redirect: 'http://url/login?nextUrl=something' };
    component.ngOnInit();
    queryParams$.next(mockQueryParams);
    expect(component.backendLoginUrl).toBe(mockQueryParams.redirect);
  });

  it('should return true for supported login method', () => {
    component.loginMethods.add(LoginMethod.GOOGLE);
    expect(component.isSupported(LoginMethod.GOOGLE)).toBeTruthy();
  });

  it('should return false for unsupported login method', () => {
    component.loginMethods.delete(LoginMethod.GOOGLE);
    expect(component.isSupported(LoginMethod.GOOGLE)).toBeFalsy();
  });
});

describe('LoginPageComponent snapshot', () => {
  let fixture: ComponentFixture<LoginPageComponent>;
  let queryParams$: Subject<Params>;
  let configService: ConfigService;

  beforeEach(async () => {
    queryParams$ = new Subject<Params>();
    await TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParams$.asObservable(),
          },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginPageComponent);
    configService = TestBed.inject(ConfigService);
  });

  it('should match snapshot with login methods loaded', () => {
    vi.spyOn(configService, 'getConfig').mockReturnValue(of({ loginMethods: ['google'] } as Config));
    fixture.detectChanges();
    console.log('Snapshot of LoginPageComponent with login methods loaded:' + fixture.nativeElement.innerHTML);
    expect(fixture).toMatchSnapshot();
  });
});
