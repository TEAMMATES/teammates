import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmSessionMoveToRecycleBinModalComponent } from './confirm-session-move-to-recycle-bin-modal.component';

describe('ConfirmSessionMoveToRecycleBinModalComponent', () => {
  let component: ConfirmSessionMoveToRecycleBinModalComponent;
  let fixture: ComponentFixture<ConfirmSessionMoveToRecycleBinModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmSessionMoveToRecycleBinModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmSessionMoveToRecycleBinModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
