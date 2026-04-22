import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { AdminAccountSearchTableComponent } from './admin-account-search-table.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
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
    status: AccountRequestStatus.APPROVED,
    comments: 'comment',
    registeredAtText: 'registeredTime',
    createdAtText: 'Tue, 08 Feb 2022, 08:23 AM +00:00',
    showLinks: false,
    ...overrides,
  };
}

describe('AdminAccountSearchTableComponent', () => {
  let component: AdminAccountSearchTableComponent;
  let fixture: ComponentFixture<AdminAccountSearchTableComponent>;
  let accountService: AccountService;
  let simpleModalService: SimpleModalService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        provideNoopAnimations(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminAccountSearchTableComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    simpleModalService = TestBed.inject(SimpleModalService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render expansion chevron for account requests', () => {
    component.accountRequests = [buildAccountRequest()];
    fixture.detectChanges();

    expect(fixture.debugElement.nativeElement.querySelector('.fa-circle-chevron-down')).not.toBeNull();
  });

  it('should toggle expansion on row click', () => {
    component.accountRequests = [buildAccountRequest()];
    fixture.detectChanges();

    const row = fixture.debugElement.nativeElement.querySelector('tbody tr');
    row.click();
    fixture.detectChanges();

    expect(component.accountRequests[0].showLinks).toBe(true);
  });

  it('should expand all and collapse all account requests', () => {
    component.accountRequests = [
      buildAccountRequest({ id: 'id-1', showLinks: false }),
      buildAccountRequest({ id: 'id-2', showLinks: false }),
    ];
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#show-account-request-links').click();
    expect(component.accountRequests.every((request) => request.showLinks)).toBe(true);

    fixture.debugElement.nativeElement.querySelector('#hide-account-request-links').click();
    expect(component.accountRequests.every((request) => !request.showLinks)).toBe(true);
  });

  it('should clear registered timestamp after reset', async () => {
    component.accountRequests = [buildAccountRequest()];
    const mockModalRef = {
      componentInstance: {},
      result: Promise.resolve({}),
      dismissed: {
        subscribe: jest.fn(),
      },
    };
    jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef as any);
    jest.spyOn(accountService, 'resetAccountRequest').mockReturnValue(of({ joinLink: 'newLink' }));

    fixture.detectChanges();
    fixture.debugElement.nativeElement.querySelector('#reset-account-request-0').click();
    await Promise.resolve();
    fixture.detectChanges();

    expect(component.accountRequests[0].registeredAtText).toBe('');
  });
});
