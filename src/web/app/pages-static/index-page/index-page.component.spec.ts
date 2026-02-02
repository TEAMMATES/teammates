import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { IndexPageComponent } from './index-page.component';

describe('IndexPageComponent', () => {
  let component: IndexPageComponent;
  let fixture: ComponentFixture<IndexPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
