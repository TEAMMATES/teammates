import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditRequestModalComponent } from './admin-edit-request-modal.component';

describe('RejectWithReasonModal', () => {
  let fixture: ComponentFixture<EditRequestModalComponent>;
  let component: EditRequestModalComponent;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [],
      imports: [
        HttpClientTestingModule,
      ],
      providers: [NgbActiveModal],
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
    component.accountRequestComments = 'Comments';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should close modal with data', () => {
    const spyActiveModal = jest.spyOn(component.activeModal, 'close');
    component.accountRequestName = 'John Doe';
    component.accountRequestEmail = 'johndoe@email.com';
    component.accountRequestInstitution = 'NUS';
    component.accountRequestComments = 'Comments';
    fixture.detectChanges();
    component.edit();
    expect(spyActiveModal).toHaveBeenCalled();
    expect(spyActiveModal).toHaveBeenCalledWith({
      accountRequestName: 'John Doe',
      accountRequestEmail: 'johndoe@email.com',
      accountRequestInstitution: 'NUS',
      accountRequestComment: 'Comments',
    });
  });
});
