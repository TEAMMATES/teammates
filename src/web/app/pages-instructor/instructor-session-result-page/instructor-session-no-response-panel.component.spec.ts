import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';

describe('InstructorSessionNoResponsePanelComponent', () => {
  let component: InstructorSessionNoResponsePanelComponent;
  let fixture: ComponentFixture<InstructorSessionNoResponsePanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionNoResponsePanelComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionNoResponsePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
