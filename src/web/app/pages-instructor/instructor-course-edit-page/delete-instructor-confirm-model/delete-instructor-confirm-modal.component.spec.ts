import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteInstructorConfirmModalComponent } from './delete-instructor-confirm-modal.component';

describe('DeleteInstructorConfirmModalComponent', () => {
  let component: DeleteInstructorConfirmModalComponent;
  let fixture: ComponentFixture<DeleteInstructorConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DeleteInstructorConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteInstructorConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
