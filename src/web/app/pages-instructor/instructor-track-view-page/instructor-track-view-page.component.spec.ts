import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorTrackViewPageComponent } from './instructor-track-view-page.component';

describe('InstructorTrackViewPageComponent', () => {
  let component: InstructorTrackViewPageComponent;
  let fixture: ComponentFixture<InstructorTrackViewPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorTrackViewPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorTrackViewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
