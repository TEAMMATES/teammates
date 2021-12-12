import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SavingCompleteModalComponent } from './saving-complete-modal.component';

describe('SavingCompleteModalComponent', () => {
  let component: SavingCompleteModalComponent;
  let fixture: ComponentFixture<SavingCompleteModalComponent>;

  beforeEach(waitForAsync(() => {
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
