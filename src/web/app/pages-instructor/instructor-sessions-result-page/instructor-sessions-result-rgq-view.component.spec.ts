import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionsResultRgqViewComponent } from './instructor-sessions-result-rgq-view.component';

describe('InstructorSessionsResultRgqViewComponent', () => {
  let component: InstructorSessionsResultRgqViewComponent;
  let fixture: ComponentFixture<InstructorSessionsResultRgqViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionsResultRgqViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsResultRgqViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
