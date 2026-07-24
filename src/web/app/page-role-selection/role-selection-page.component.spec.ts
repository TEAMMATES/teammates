import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { AuthInfo } from '../../types/api-output';
import { RoleSelectionPageComponent } from './role-selection-page.component';

const authInfoFor = (roles: {
  isStudent?: boolean;
  isInstructor?: boolean;
  isAdmin?: boolean;
  isMaintainer?: boolean;
}): AuthInfo => ({
  masquerade: false,
  user: {
    accountId: 'account-id',
    accountEmail: 'user@example.com',
    isStudent: !!roles.isStudent,
    isInstructor: !!roles.isInstructor,
    isAdmin: !!roles.isAdmin,
    isMaintainer: !!roles.isMaintainer,
  },
});

describe('RoleSelectionPageComponent', () => {
  let fixture: ComponentFixture<RoleSelectionPageComponent>;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(RoleSelectionPageComponent);
    authService = TestBed.inject(AuthService);
  });

  it('should show available role pages', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(authInfoFor({ isStudent: true, isInstructor: true })));

    fixture.detectChanges();

    const pageText = fixture.nativeElement.textContent;
    expect(pageText).toContain('Student');
    expect(pageText).toContain('Instructor');
    expect(pageText).not.toContain('Return to main page');
  });

  it('should prompt users with no roles to return to the main page', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(authInfoFor({})));

    fixture.detectChanges();

    const pageText = fixture.nativeElement.textContent;
    expect(pageText).toContain('You do not currently have access to any instructor or student pages in TEAMMATES.');
    expect(pageText).toContain('Return to main page');
  });
});
