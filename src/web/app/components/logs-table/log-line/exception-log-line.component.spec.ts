import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExceptionLogLineComponent } from './exception-log-line.component';

describe('ExceptionLogLineComponent', () => {
  let component: ExceptionLogLineComponent;
  let fixture: ComponentFixture<ExceptionLogLineComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExceptionLogLineComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionLogLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should update logValue, exceptionClass, and summary based on log input', () => {
      const mockLog: GeneralLogEntry = {
        // ... mock data for log
        details: {
          event: LogEvent.EXCEPTION_LOG,
          exceptionClass: 'SomeException',
          message: 'Some error message'
        }
      };
      component.log = mockLog;
      expect(component.logValue).toEqual(mockLog);
      expect(component.exceptionClass).toBe('SomeException');
      expect(component.summary).toBe('Some error message');
    });
    it('should not update exceptionClass and summary if log details are not of EXCEPTION_LOG event', () => {
        const mockLog: GeneralLogEntry = {
        };
        component.log = mockLog;
        expect(component.exceptionClass).toBe('');
        expect(component.summary).toBe('');
        });

      it('should emit addExceptionClassEvent when addExceptionClassToFilter is called', () => {
          spyOn(component.addExceptionClassEvent, 'emit');
          component.addExceptionClassToFilter('SomeException');
          expect(component.addExceptionClassEvent.emit).toHaveBeenCalledWith('SomeException');
        });
});
