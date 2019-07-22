import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { HttpRequestService } from '../../../services/http-request.service';
import { ErrorReportComponent } from './error-report.component';

describe('ErrorReportComponent', () => {
  let component: ErrorReportComponent;
  let fixture: ComponentFixture<ErrorReportComponent>;
  let httpRequestService: HttpRequestService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorReportComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
      ],
      providers: [HttpRequestService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorReportComponent);
    component = fixture.componentInstance;
    httpRequestService = TestBed.get(HttpRequestService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user input from Subject form', () => {
    const input: any = fixture.nativeElement.querySelectorAll('input');

    expect(component.subject).toEqual('User-submitted Error Report');
    input[1].value = 'testInput';
    input[1].dispatchEvent((new Event('input')));
    expect(component.subject).toEqual('testInput');
  });

  it('should disable send report button and set errorReportSubmitted to true after successfully sending report', () => {
    expect(component.sendButtonEnabled).toBeTruthy();
    expect(component.errorReportSubmitted).toBeFalsy();

    spyOn(httpRequestService, 'post').and.returnValue(of(''));
    fixture.nativeElement.querySelector('button').click();
    fixture.detectChanges();

    expect(component.sendButtonEnabled).toBeFalsy();
    expect(component.errorReportSubmitted).toBeTruthy();
    expect(fixture.nativeElement.querySelector('button')).toBeFalsy();
  });

  it('should not disable send report button after unsuccessfully sending report', () => {
    expect(component.sendButtonEnabled).toBeTruthy();
    expect(component.errorReportSubmitted).toBeFalsy();

    spyOn(httpRequestService, 'post').and.returnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));
    fixture.nativeElement.querySelector('button').click();
    fixture.detectChanges();

    expect(component.sendButtonEnabled).toBeTruthy();
    expect(component.errorReportSubmitted).toBeFalsy();
    expect(fixture.nativeElement.querySelector('button')).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

});
