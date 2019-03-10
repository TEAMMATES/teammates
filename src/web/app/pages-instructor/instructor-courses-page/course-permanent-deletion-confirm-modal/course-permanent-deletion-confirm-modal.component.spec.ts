import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CoursePermanentDeletionConfirmModalComponent } from './course-permanent-deletion-confirm-modal.component';

describe('CoursePermanentDeletionConfirmModalComponent', () => {
  let component: CoursePermanentDeletionConfirmModalComponent;
  let fixture: ComponentFixture<CoursePermanentDeletionConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CoursePermanentDeletionConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CoursePermanentDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
