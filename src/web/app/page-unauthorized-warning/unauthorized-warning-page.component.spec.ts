import { ActivatedRoute } from '@angular/router';
import { UnauthorizedWarningPageComponent } from './unauthorized-warning-page.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Subject } from 'rxjs';

describe('UnauthorizedWarningPageComponent', () => {
  let component: UnauthorizedWarningPageComponent;
  let fixture: ComponentFixture<UnauthorizedWarningPageComponent>;
  let queryParams$: Subject<any>;

  beforeEach(() => {
    queryParams$ = new Subject<any>();
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
    component.role = 'instructor';
    expect(component['getUnauthorizedReason']()).toBe('You are not an instructor of any course.');
  });

  it('should return correct reason for student role', () => {
    component.role = 'student';
    expect(component['getUnauthorizedReason']()).toBe('You are not enrolled as a student in any course.');
  });

  it('should extract correct query parameters on initialization', () => {
    const mockQueryParams = { role: 'admin' };
    component.ngOnInit();
    queryParams$.next(mockQueryParams);
    expect(component.role).toBe('admin');
    expect(component.reason).toBe('You need to be an admin to access this page.');
  });
});

describe('Unauthorized Warning Page', () => {
  let component: UnauthorizedWarningPageComponent;
  let fixture: ComponentFixture<UnauthorizedWarningPageComponent>;
  let queryParams$: Subject<any>;

  beforeEach(() => {
    queryParams$ = new Subject<any>();
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

  it('should match snapshot when student role is set', () => {
    component.ngOnInit();
    queryParams$.next({ role: 'student' });
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should match snapshot when admin role is set', () => {
    component.ngOnInit();
    queryParams$.next({ role: 'admin' });
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
