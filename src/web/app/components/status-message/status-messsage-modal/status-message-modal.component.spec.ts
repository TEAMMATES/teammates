import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StatusMessageModalComponent } from './status-message-modal.component';

describe('StatusMessageModalComponent', () => {
  let component: StatusMessageModalComponent;
  let fixture: ComponentFixture<StatusMessageModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StatusMessageModalComponent],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatusMessageModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
