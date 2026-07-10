import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PageComponent } from './page.component';

describe('PageComponent', () => {
  let component: PageComponent;
  let fixture: ComponentFixture<PageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(PageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show a generic sign-in link before login', () => {
    component.isFetchingAuthDetails = false;
    component.accountEmail = '';
    fixture.detectChanges();

    const loginLink: HTMLAnchorElement = fixture.nativeElement.querySelector('#login-btn');

    expect(loginLink).not.toBeNull();
    expect(loginLink.textContent?.trim()).toBe('Sign in');
    expect(fixture.nativeElement.querySelector('#student-login-btn')).toBeNull();
    expect(fixture.nativeElement.querySelector('#instructor-login-btn')).toBeNull();
  });
});
