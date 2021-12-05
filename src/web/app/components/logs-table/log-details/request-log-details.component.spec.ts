import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RequestLogDetailsComponent } from './request-log-details.component';

describe('RequestLogDetailsComponent', () => {
  let component: RequestLogDetailsComponent;
  let fixture: ComponentFixture<RequestLogDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RequestLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
