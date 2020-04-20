import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ConfirmDeleteCommentModalComponent } from './confirm-delete-comment-modal.component';

describe('ConfirmDeleteCommentModalComponent', () => {
  let component: ConfirmDeleteCommentModalComponent;
  let fixture: ComponentFixture<ConfirmDeleteCommentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [ConfirmDeleteCommentModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDeleteCommentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
