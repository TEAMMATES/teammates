import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminSearchPageComponent } from './admin-search-page.component';
import { SearchService } from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';

describe('AdminSearchPageComponent', () => {
  let component: AdminSearchPageComponent;
  let fixture: ComponentFixture<AdminSearchPageComponent>;
  let searchService: SearchService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSearchPageComponent);
    component = fixture.componentInstance;
    searchService = TestBed.inject(SearchService);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a search key', () => {
    component.searchQuery = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display error message for invalid input', () => {
    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message',
        },
      })),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display warning message for no results', () => {
    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(
      of({
        students: [],
        instructors: [],
        accountRequests: [],
      }),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showWarningToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('No results found.');
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });
});
