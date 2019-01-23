import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSessionNotOpenModalComponent } from './feedback-session-not-open-modal.component';

describe('FeedbackSessionNotOpenModalComponent', () => {
  let component: FeedbackSessionNotOpenModalComponent;
  let fixture: ComponentFixture<FeedbackSessionNotOpenModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FeedbackSessionNotOpenModalComponent],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackSessionNotOpenModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
