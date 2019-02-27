import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionsResultGqrViewComponent } from './instructor-sessions-result-gqr-view.component';

describe('InstructorSessionsResultGqrViewComponent', () => {
  let component: InstructorSessionsResultGqrViewComponent;
  let fixture: ComponentFixture<InstructorSessionsResultGqrViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionsResultGqrViewComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsResultGqrViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
