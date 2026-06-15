import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { EditRequestModalComponent } from './admin-edit-request-modal.component';

describe('RejectWithReasonModal', () => {
  let fixture: ComponentFixture<EditRequestModalComponent>;
  let component: EditRequestModalComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(EditRequestModalComponent);
    fixture.detectChanges();
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show empty fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show populated data', () => {
    component.accountVerificationRequestName = 'John Doe';
    component.accountVerificationRequestEmail = 'johndoe@email.com';
    component.accountVerificationRequestInstitution = 'NUS';
    component.accountVerificationRequestComments = 'Comments';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should close modal with data', () => {
    const spyActiveModal = vi.spyOn(component.activeModal, 'close');
    component.accountVerificationRequestName = 'John Doe';
    component.accountVerificationRequestEmail = 'johndoe@email.com';
    component.accountVerificationRequestInstitution = 'NUS';
    component.accountVerificationRequestComments = 'Comments';
    fixture.detectChanges();
    component.edit();
    expect(spyActiveModal).toHaveBeenCalled();
    expect(spyActiveModal).toHaveBeenCalledWith({
      accountVerificationRequestName: 'John Doe',
      accountVerificationRequestEmail: 'johndoe@email.com',
      accountVerificationRequestInstitution: 'NUS',
      accountVerificationRequestCountry: '',
      accountVerificationRequestComment: 'Comments',
    });
  });
});
