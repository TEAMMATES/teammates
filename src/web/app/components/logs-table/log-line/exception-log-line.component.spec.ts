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
});
