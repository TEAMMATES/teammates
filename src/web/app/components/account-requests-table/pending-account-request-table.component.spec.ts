import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { PendingAccountRequestTableComponent } from './pending-account-request-table.component';
import { AccountService } from '../../../services/account.service';
import { AccountRequestStatus } from '../../../types/api-output';

/**
 * Builds a default account request row for table tests.
 */
function buildAccountRequest(overrides: Partial<AccountRequestTableRowModel> = {}): AccountRequestTableRowModel {
  return {
    id: 'id',
    email: 'email@example.com',
    name: 'name',
    instituteAndCountry: 'institute',
    registrationLink: 'registrationLink',
    status: AccountRequestStatus.PENDING,
    comments: 'comment',
    registeredAtText: '',
    createdAtText: 'Tue, 08 Feb 2022, 08:23 AM +00:00',
    showLinks: false,
    ...overrides,
  };
}

describe('PendingAccountRequestTableComponent', () => {
  let component: PendingAccountRequestTableComponent;
  let fixture: ComponentFixture<PendingAccountRequestTableComponent>;
  let accountService: AccountService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PendingAccountRequestTableComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render search expansion controls', () => {
    component.accountRequests = [buildAccountRequest()];
    fixture.detectChanges();

    expect(fixture.debugElement.nativeElement.querySelector('#show-account-request-links')).toBeNull();
    expect(fixture.debugElement.nativeElement.querySelector('.fa-chevron-circle-down')).toBeNull();
  });

  it('should update status when approval is successful', () => {
    component.accountRequests = [buildAccountRequest()];
    jest.spyOn(accountService, 'approveAccountRequest').mockReturnValue(of({
      id: 'id',
      comments: 'comment',
      email: 'email@example.com',
      institute: 'institute',
      registrationKey: 'registrationKey',
      name: 'name',
      createdAt: 1,
      status: AccountRequestStatus.APPROVED,
    }));

    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#approve-account-request-0').click();

    expect(component.accountRequests[0].status).toBe(AccountRequestStatus.APPROVED);
  });

  it('should update status when rejection is successful', () => {
    component.accountRequests = [buildAccountRequest()];
    jest.spyOn(accountService, 'rejectAccountRequest').mockReturnValue(of({
      id: 'id',
      comments: 'comment',
      email: 'email@example.com',
      institute: 'institute',
      registrationKey: 'registrationKey',
      name: 'name',
      createdAt: 1,
      status: AccountRequestStatus.REJECTED,
    }));

    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#reject-request-0').click();

    expect(component.accountRequests[0].status).toBe(AccountRequestStatus.REJECTED);
  });
});
