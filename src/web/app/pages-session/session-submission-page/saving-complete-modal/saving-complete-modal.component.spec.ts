import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { SavingCompleteModalComponent } from './saving-complete-modal.component';

describe('SavingCompleteModalComponent', () => {
  let component: SavingCompleteModalComponent;
  let fixture: ComponentFixture<SavingCompleteModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SavingCompleteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
