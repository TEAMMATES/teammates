import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminAccountVerificationRequestsPageComponent } from './admin-account-verification-requests-page.component';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';

describe('AdminAccountVerificationRequestsPageComponent', () => {
  let component: AdminAccountVerificationRequestsPageComponent;
  let fixture: ComponentFixture<AdminAccountVerificationRequestsPageComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminAccountVerificationRequestsPageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of({ accountVerificationRequests: [] }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch all requests on load with no search key', () => {
    expect(accountService.getAccountVerificationRequests).toHaveBeenCalledWith({ searchKey: undefined });
  });

  it('should fetch with search key when search is triggered', () => {
    component.searchQuery.set('john');
    component.search();
    fixture.detectChanges();
    expect(accountService.getAccountVerificationRequests).toHaveBeenCalledWith({ searchKey: 'john' });
  });

  it('should search on Enter key in search box', () => {
    component.searchQuery.set('test');
    const input: HTMLInputElement = fixture.debugElement.nativeElement.querySelector('#search-box');
    input.dispatchEvent(new KeyboardEvent('keyup', { key: 'Enter' }));
    fixture.detectChanges();
    expect(accountService.getAccountVerificationRequests).toHaveBeenCalledWith({ searchKey: 'test' });
  });

  it('should show error toast when fetch fails', () => {
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(
      throwError(() => ({ error: { message: 'Server error' } })),
    );
    component.searchQuery.set('fail-query');
    component.search();
    fixture.detectChanges();
    expect(errorSpy).toHaveBeenCalledWith('Server error');
  });
});
