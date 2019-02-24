import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CoursesPermanentDeletionConfirmModalComponent } from './courses-permanent-deletion-confirm-modal.component';

describe('CoursesPermanentDeletionConfirmModalComponent', () => {
  let component: CoursesPermanentDeletionConfirmModalComponent;
  let fixture: ComponentFixture<CoursesPermanentDeletionConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CoursesPermanentDeletionConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CoursesPermanentDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
