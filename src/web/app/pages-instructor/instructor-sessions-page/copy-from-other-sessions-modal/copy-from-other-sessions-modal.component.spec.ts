import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopyFromOtherSessionsModalComponent } from './copy-from-other-sessions-modal.component';

describe('CopyFromOtherSessionsModalComponent', () => {
  let component: CopyFromOtherSessionsModalComponent;
  let fixture: ComponentFixture<CopyFromOtherSessionsModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyFromOtherSessionsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
