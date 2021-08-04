import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExceptionLogDetailsComponent } from './exception-log-details.component';

describe('ExceptionLogDetailsComponent', () => {
  let component: ExceptionLogDetailsComponent;
  let fixture: ComponentFixture<ExceptionLogDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ExceptionLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
