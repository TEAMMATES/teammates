import {
  ComponentFactory,
  ComponentFactoryResolver,
  ComponentRef,
  Directive,
  Input,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { LoadingSpinnerComponent } from './loading-spinner.component';

/**
 * Directive for loading spinner component
 */
@Directive({
  selector: '[tmIsLoading]',
})
export class LoadingSpinnerDirective {
  loadingComponent: ComponentRef<LoadingSpinnerComponent>;
  loadingFactory: ComponentFactory<LoadingSpinnerComponent>;

  constructor(private templateRef: TemplateRef<any>,
              private viewContainer: ViewContainerRef,
              private componentFactoryResolver: ComponentFactoryResolver) {
    this.loadingFactory = this.componentFactoryResolver.resolveComponentFactory(LoadingSpinnerComponent);
    this.loadingComponent = this.viewContainer.createComponent(this.loadingFactory);
  }

  @Input() set tmIsLoading(loading: boolean) {
    this.viewContainer.clear();

    if (loading) {
      this.loadingComponent = this.viewContainer.createComponent(this.loadingFactory);
    } else {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
