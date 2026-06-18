import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginPageComponent } from './login-page.component';
import { of } from 'rxjs';
import { Config } from '../../types/api-output';
import { ConfigService } from '../../services/config.service';

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let fixture: ComponentFixture<LoginPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();

    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    component.nextUrl = '/web/front';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should use the route-bound nextUrl input', () => {
    component.nextUrl = '/web/instructor/home';

    expect(component.nextUrl).toBe('/web/instructor/home');
  });
});

describe('LoginPageComponent snapshot', () => {
  let fixture: ComponentFixture<LoginPageComponent>;
  let configService: ConfigService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();
    fixture = TestBed.createComponent(LoginPageComponent);
    configService = TestBed.inject(ConfigService);
  });

  it('should match snapshot with login methods loaded', () => {
    vi.spyOn(configService, 'getConfig').mockReturnValue(of({ loginMethods: ['google'] } as Config));
    fixture.componentInstance.nextUrl = '/web/front';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
