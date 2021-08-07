import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestLogDetailsComponent } from './request-log-details.component';

describe('RequestLogDetailsComponent', () => {
  let component: RequestLogDetailsComponent;
  let fixture: ComponentFixture<RequestLogDetailsComponent>;

  beforeEach(async(() => {
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
