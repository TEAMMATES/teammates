import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { RegenerateLinksConfirmModalComponent } from './regenerate-links-confirm-modal.component';

describe('RegenerateLinksModalComponent', () => {
  let component: RegenerateLinksConfirmModalComponent;
  let fixture: ComponentFixture<RegenerateLinksConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RegenerateLinksConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegenerateLinksConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
