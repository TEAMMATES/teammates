import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SavingCompleteModalComponent } from './saving-complete-modal.component';

describe('SavingCompleteModalComponent', () => {
  let component: SavingCompleteModalComponent;
  let fixture: ComponentFixture<SavingCompleteModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SavingCompleteModalComponent],
      imports: [HttpClientTestingModule],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SavingCompleteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
