import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../../services/auth.service';
import { AuthInfo } from '../../../types/api-output';
import { RequestPageComponent } from './request-page.component';

const loggedInAuthInfo: AuthInfo = {
  loginUrl: '/login',
  masquerade: false,
  user: {
    id: 'test@example.com',
    accountId: 'acc1',
    accountEmail: 'user@teammates.tmt',
    isAdmin: false,
    isInstructor: false,
    isStudent: false,
    isMaintainer: false,
  },
};

const loggedOutAuthInfo: AuthInfo = {
  loginUrl: '/login',
  masquerade: false,
};

describe('RequestPageComponent', () => {
  let component: RequestPageComponent;
  let fixture: ComponentFixture<RequestPageComponent>;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    authService = TestBed.inject(AuthService);
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(loggedInAuthInfo));

    fixture = TestBed.createComponent(RequestPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render correctly when not signed in', () => {
    component.authInfo.set(loggedOutAuthInfo);
    component.isLoading.set(false);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should render correctly before submission', () => {
    component.submittedFormData.set(null);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should render correctly after form is submitted', () => {
    component.submittedFormData.set({
      name: 'Jane Smith',
      institution: 'University of Example',
      country: 'Example Republic',
      email: 'js@exampleu.edu',
      comments: '',
    });
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
