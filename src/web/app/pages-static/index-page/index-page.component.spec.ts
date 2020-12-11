import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IndexPageComponent } from './index-page.component';
import { BannerComponent } from '../../components/banner/banner.component';

describe('IndexPageComponent', () => {
  let component: IndexPageComponent;
  let fixture: ComponentFixture<IndexPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IndexPageComponent, BannerComponent],
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
