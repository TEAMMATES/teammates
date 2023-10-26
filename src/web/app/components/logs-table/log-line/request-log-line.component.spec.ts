import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RequestLogLineComponent } from './request-log-line.component';

describe('RequestLogLineComponent', () => {
  let component: RequestLogLineComponent;
  let fixture: ComponentFixture<RequestLogLineComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogLineComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestLogLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
    // Test purpose: Verify that the component's properties are correctly set when the log setter is called with a REQUEST_LOG event.
    it('should set properties correctly when log setter is called with REQUEST_LOG event', () => {
      const mockLog = {
        details: {
          event: LogEvent.REQUEST_LOG,
          responseStatus: 200,
          responseTime: 100,
          actionClass: 'TestActionClass',
          requestMethod: 'GET',
          requestUrl: '/test-url'
        }
      };
      component.log = mockLog as any;
      expect(component.httpStatus).toBe(200);
      expect(component.responseTime).toBe(100);
      expect(component.actionClass).toBe('TestActionClass');
      expect(component.summary).toBe('GET /test-url');
    });

    // Test purpose: Ensure that the method returns the correct CSS class based on the provided HTTP status.
    it('should return correct class for given HTTP status', () => {
      expect(component.getClassForStatus(200)).toBe('green-font');
      expect(component.getClassForStatus(404)).toBe('orange-font');
      expect(component.getClassForStatus(500)).toBe('red-font');
      expect(component.getClassForStatus(100)).toBe('');
    });

    // Test purpose: Check if the addActionClassEvent is emitted when the addActionClassToFilter method is called.
    it('should emit addActionClassEvent when addActionClassToFilter is called', () => {
      spyOn(component.addActionClassEvent, 'emit');
      component.addActionClassToFilter('TestActionClass');
      expect(component.addActionClassEvent.emit).toHaveBeenCalledWith('TestActionClass');
    });
});
