import { provideRouter } from '@angular/router';
import { UnauthorizedWarningPageComponent } from './unauthorized-warning-page.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';

describe('UnauthorizedWarningPageComponent', () => {
  let component: UnauthorizedWarningPageComponent;
  let fixture: ComponentFixture<UnauthorizedWarningPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(UnauthorizedWarningPageComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return correct reason for instructor role', () => {
    fixture.componentRef.setInput('role', 'instructor');
    fixture.detectChanges();

    expect(component.role).toBe('instructor');
    expect(component.reason).toBe('You are not an instructor of any course.');
  });

  it('should return correct reason for student role', () => {
    fixture.componentRef.setInput('role', 'student');
    fixture.detectChanges();

    expect(component.role).toBe('student');
    expect(component.reason).toBe('You are not enrolled as a student in any course.');
  });
});

describe('UnauthorizedWarningPageComponent snapshot', () => {
  let fixture: ComponentFixture<UnauthorizedWarningPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(UnauthorizedWarningPageComponent);
  });

  it('should match snapshot when student role is set', () => {
    fixture.componentRef.setInput('role', 'student');
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should match snapshot when admin role is set', () => {
    fixture.componentRef.setInput('role', 'admin');
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
