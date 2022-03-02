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
});
