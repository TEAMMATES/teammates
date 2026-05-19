import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionsRecycleBinTableComponent } from './sessions-recycle-bin-table.component';

describe('SessionsRecycleBinTableComponent', () => {
  let component: SessionsRecycleBinTableComponent;
  let fixture: ComponentFixture<SessionsRecycleBinTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsRecycleBinTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
