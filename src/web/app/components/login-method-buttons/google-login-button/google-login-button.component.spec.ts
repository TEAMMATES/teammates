import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GoogleLoginButtonComponent } from './google-login-button.component';
import { LOGIN_METHOD_BUTTON_CONTEXT, LoginMethodButtonContext } from '../login-method-button-context';

describe('GoogleLoginButtonComponent', () => {
  let component: GoogleLoginButtonComponent;
  let fixture: ComponentFixture<GoogleLoginButtonComponent>;
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

    fixture = TestBed.createComponent(GoogleLoginButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
