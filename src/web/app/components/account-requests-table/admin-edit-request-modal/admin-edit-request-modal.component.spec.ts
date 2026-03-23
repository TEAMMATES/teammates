import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditRequestModalComponent } from './admin-edit-request-modal.component';
import { StatusMessageService } from '../../../../services/status-message.service';

describe('EditRequestModalComponent', () => {
  let fixture: ComponentFixture<EditRequestModalComponent>;
  let component: EditRequestModalComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        NgbActiveModal,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
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
    component.accountRequestName = 'John Doe';
    component.accountRequestEmail = 'johndoe@email.com';
    component.accountRequestInstitution = 'NUS';
    component.accountRequestCountry = 'Singapore';
    component.accountRequestComments = 'Comments';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should close modal with trimmed data', () => {
    const spyActiveModal = jest.spyOn(component.activeModal, 'close');
    component.accountRequestName = 'John Doe';
    component.accountRequestEmail = 'johndoe@email.com';
    component.accountRequestInstitution = 'NUS';
    component.accountRequestCountry = 'Singapore';
    component.accountRequestComments = 'Comments';
    fixture.detectChanges();
    component.edit();
    expect(spyActiveModal).toHaveBeenCalled();
    expect(spyActiveModal).toHaveBeenCalledWith({
      accountRequestName: 'John Doe',
      accountRequestEmail: 'johndoe@email.com',
      accountRequestInstitution: 'NUS',
      accountRequestCountry: 'Singapore',
      accountRequestComment: 'Comments',
    });
  });

  const expectedToastMessage = 'Please fill in name, email, institution, and country.';

  it.each([
    { field: 'name' as const, value: '', scenario: 'empty' },
    { field: 'name' as const, value: '   ', scenario: 'whitespace only' },
    { field: 'email' as const, value: '', scenario: 'empty' },
    { field: 'email' as const, value: '   ', scenario: 'whitespace only' },
    { field: 'institution' as const, value: '', scenario: 'empty' },
    { field: 'institution' as const, value: '   ', scenario: 'whitespace only' },
    { field: 'country' as const, value: '', scenario: 'empty' },
    { field: 'country' as const, value: '   ', scenario: 'whitespace only' },
  ])('should not close modal when $field is $scenario', ({ field, value }) => {
    const statusMessageService = TestBed.inject(StatusMessageService);
    const toastSpy = jest.spyOn(statusMessageService, 'showErrorToast');
    const closeSpy = jest.spyOn(component.activeModal, 'close');

    component.accountRequestName = 'John Doe';
    component.accountRequestEmail = 'johndoe@email.com';
    component.accountRequestInstitution = 'NUS';
    component.accountRequestCountry = 'Singapore';
    component.accountRequestComments = 'Comments';

    if (field === 'name') {
      component.accountRequestName = value;
    } else if (field === 'email') {
      component.accountRequestEmail = value;
    } else if (field === 'institution') {
      component.accountRequestInstitution = value;
    } else {
      component.accountRequestCountry = value;
    }

    fixture.detectChanges();
    component.edit();

    expect(toastSpy).toHaveBeenCalledWith(expectedToastMessage);
    expect(closeSpy).not.toHaveBeenCalled();
  });
});
