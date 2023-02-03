import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IndexPageComponent } from './index-page.component';
//import { BackToTopButtonComponent } from '../../components/back-to-top-button/back-to-top-button';


describe('IndexPageComponent', () => {
  let component: IndexPageComponent;
  let fixture: ComponentFixture<IndexPageComponent>;
  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [IndexPageComponent],
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

// describe('BackToTopButtonComponent', () => {
//   let component: BackToTopButtonComponent;
//   let fixture: ComponentFixture<BackToTopButtonComponent>;
//   component= new BackToTopButtonComponent;
//   let x = component;
//   x.scrollToTop;
//   beforeEach(() => {
//     TestBed.configureTestingModule({
//       declarations: [ BackToTopButtonComponent ]
//     })
//     .compileComponents();

//     fixture = TestBed.createComponent(BackToTopButtonComponent);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });

//   it('scrolls to the top of the page when clicked', () => {
//     spyOn(window, 'scrollTo').and.callFake(() => {});

//     const buttonElement = fixture.debugElement.nativeElement.querySelector('button');
//     buttonElement.click();

//     expect(window.scrollTo).toHaveBeenCalledWith({
//       top: 0,
//       left: 0,
//       behavior: 'smooth'
//     });
//   });
// });



// import { render, fireEvent} from '@angular/core/testing';
// import { BackToTopButton } from './BackToTopButton';

// describe('BackToTopButton', () => {
//   it('scrolls to the top of the page when clicked', () => {
//     jest.spyOn(window, 'scrollTo').mockImplementation(() => {});

//     const { getByTestId } = render(<BackToTopButton />);
//     const button = getByTestId('back-to-top-button');

//     fireEvent.click(button);

//     expect(window.scrollTo).toHaveBeenCalledWith({
//       top: 0,
//       left: 0,
//       behavior: 'smooth',
//     });
//   });
// });

