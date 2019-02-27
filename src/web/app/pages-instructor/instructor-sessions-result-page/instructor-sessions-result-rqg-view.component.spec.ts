import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionsResultRqgViewComponent } from './instructor-sessions-result-rqg-view.component';

describe('InstructorSessionsResultRqgViewComponent', () => {
  let component: InstructorSessionsResultRqgViewComponent;
  let fixture: ComponentFixture<InstructorSessionsResultRqgViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionsResultRqgViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsResultRqgViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
