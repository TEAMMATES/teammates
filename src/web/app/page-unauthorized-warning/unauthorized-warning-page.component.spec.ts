import { ActivatedRoute, Params } from '@angular/router';
import { UnauthorizedWarningPageComponent } from './unauthorized-warning-page.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';

describe('UnauthorizedWarningPageComponent', () => {
  let component: UnauthorizedWarningPageComponent;
  let fixture: ComponentFixture<UnauthorizedWarningPageComponent>;
  let queryParams$: Subject<Params>;

  beforeEach(() => {
    queryParams$ = new Subject<Params>();
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParams$.asObservable(),
          },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(UnauthorizedWarningPageComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return correct reason for instructor role', () => {
    queryParams$.next({ role: 'instructor' });

    expect(component.role()).toBe('instructor');
    expect(component.reason()).toBe('You are not an instructor of any course.');
  });

  it('should return correct reason for student role', () => {
    queryParams$.next({ role: 'student' });

    expect(component.role()).toBe('student');
    expect(component.reason()).toBe('You are not enrolled as a student in any course.');
  });
});

describe('UnauthorizedWarningPageComponent snapshot', () => {
  let fixture: ComponentFixture<UnauthorizedWarningPageComponent>;
  let queryParams$: Subject<Params>;

  beforeEach(() => {
    queryParams$ = new Subject<Params>();
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: queryParams$.asObservable(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UnauthorizedWarningPageComponent);
  });

  it('should match snapshot when student role is set', () => {
    queryParams$.next({ role: 'student' });
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should match snapshot when admin role is set', () => {
    queryParams$.next({ role: 'admin' });
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
