import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UsermapPageComponent } from './usermap-page.component';

describe('UsermapPageComponent', () => {
  let component: UsermapPageComponent;
  let fixture: ComponentFixture<UsermapPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [UsermapPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UsermapPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
