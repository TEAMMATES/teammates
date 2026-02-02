import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RequestPageComponent } from './request-page.component';

describe('RequestPageComponent', () => {
  let component: RequestPageComponent;
  let fixture: ComponentFixture<RequestPageComponent>;
  let mathRandomSpy;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    // ngx-captcha assigns random ID based on Date and Math.random
    // Here, both of them are mocked to ensure consistent results
    jest.useFakeTimers();
    jest.setSystemTime(new Date('2026-01-01T00:00:00Z'));
    mathRandomSpy = jest.spyOn(global.Math, 'random');
    mathRandomSpy.mockReturnValue(0.12345);

    fixture = TestBed.createComponent(RequestPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.useRealTimers();
    mathRandomSpy.mockRestore();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render correctly before instructor declaration is done', () => {
    component.isDeclarationDone = false;
    component.submittedFormData = null;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should render correctly after instructor declaration is done', () => {
    component.isDeclarationDone = true;
    component.submittedFormData = null;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should render correctly after form is submitted', () => {
    component.submittedFormData = {
      name: 'Jane Smith',
      institution: 'University of Example',
      country: 'Example Republic',
      email: 'js@exampleu.edu',
      comments: '',
    };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
