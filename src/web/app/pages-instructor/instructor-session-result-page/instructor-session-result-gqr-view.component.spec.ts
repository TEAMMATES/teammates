import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';

describe('InstructorSessionResultGqrViewComponent', () => {
  let component: InstructorSessionResultGqrViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultGqrViewComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultGqrViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
