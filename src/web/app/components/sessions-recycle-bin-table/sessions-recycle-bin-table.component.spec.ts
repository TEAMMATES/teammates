import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { SessionsRecycleBinTableComponent } from './sessions-recycle-bin-table.component';
import { SessionsRecycleBinTableModule } from './sessions-recycle-bin-table.module';

describe('SessionsRecycleBinTableComponent', () => {
  let component: SessionsRecycleBinTableComponent;
  let fixture: ComponentFixture<SessionsRecycleBinTableComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [SessionsRecycleBinTableModule, HttpClientTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsRecycleBinTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
