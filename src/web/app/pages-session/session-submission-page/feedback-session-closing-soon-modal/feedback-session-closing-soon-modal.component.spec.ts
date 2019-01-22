import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSessionClosingSoonModalComponent } from './feedback-session-closing-soon-modal.component';

describe('FeedbackSessionClosingSoonModalComponent', () => {
  let component: FeedbackSessionClosingSoonModalComponent;
  let fixture: ComponentFixture<FeedbackSessionClosingSoonModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FeedbackSessionClosingSoonModalComponent],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackSessionClosingSoonModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
