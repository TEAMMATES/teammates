import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionSubmissionPageComponent } from './session-submission-page.component';

describe('SessionSubmissionPageComponent', () => {
  let component: SessionSubmissionPageComponent;
  let fixture: ComponentFixture<SessionSubmissionPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SessionSubmissionPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionSubmissionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
