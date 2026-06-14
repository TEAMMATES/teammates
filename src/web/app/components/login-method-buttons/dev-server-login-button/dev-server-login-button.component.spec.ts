import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DevServerLoginButtonComponent } from './dev-server-login-button.component';
import { LOGIN_METHOD_BUTTON_CONTEXT, LoginMethodButtonContext } from '../login-method-button-context';

describe('DevServerLoginButtonComponent', () => {
  let component: DevServerLoginButtonComponent;
  let fixture: ComponentFixture<DevServerLoginButtonComponent>;
  let mockLoginMethodButtonContext: LoginMethodButtonContext;

  beforeEach(async () => {
    mockLoginMethodButtonContext = { nextUrl: '/test' };

    await TestBed.configureTestingModule({
      providers: [
        {
          provide: LOGIN_METHOD_BUTTON_CONTEXT,
          useValue: mockLoginMethodButtonContext,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DevServerLoginButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
