import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseSoftDeletionConfirmModalComponent } from './course-soft-deletion-confirm-modal.component';

describe('CourseSoftDeletionConfirmModalComponent', () => {
  let component: CourseSoftDeletionConfirmModalComponent;
  let fixture: ComponentFixture<CourseSoftDeletionConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CourseSoftDeletionConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseSoftDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
