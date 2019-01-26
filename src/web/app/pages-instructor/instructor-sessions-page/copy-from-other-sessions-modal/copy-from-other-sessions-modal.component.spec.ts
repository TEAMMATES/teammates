import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopyFromOtherSessionsModalComponent } from './copy-from-other-sessions-modal.component';

describe('CopyFromOtherSessionsModalComponent', () => {
  let component: CopyFromOtherSessionsModalComponent;
  let fixture: ComponentFixture<CopyFromOtherSessionsModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopyFromOtherSessionsModalComponent],
      imports: [FormsModule],
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
