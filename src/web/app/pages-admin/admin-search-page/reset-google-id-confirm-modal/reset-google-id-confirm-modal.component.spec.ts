import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResetGoogleIdConfirmModalComponent } from './reset-google-id-confirm-modal.component';

describe('ResetGoogleIdConfirmModalComponent', () => {
  let component: ResetGoogleIdConfirmModalComponent;
  let fixture: ComponentFixture<ResetGoogleIdConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ResetGoogleIdConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetGoogleIdConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
