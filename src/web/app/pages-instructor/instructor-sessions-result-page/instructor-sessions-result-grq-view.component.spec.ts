import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionsResultGrqViewComponent } from './instructor-sessions-result-grq-view.component';

describe('InstructorSessionsResultGrqViewComponent', () => {
  let component: InstructorSessionsResultGrqViewComponent;
  let fixture: ComponentFixture<InstructorSessionsResultGrqViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionsResultGrqViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsResultGrqViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
