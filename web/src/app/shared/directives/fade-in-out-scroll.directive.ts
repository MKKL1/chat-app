import {
  Directive,
  ElementRef,
  OnDestroy,
} from '@angular/core';

@Directive({
  selector: '[appFadeInOutScroll]',
  standalone: true
})
export class FadeInOutScrollDirective implements OnDestroy {
  private observer: IntersectionObserver;

  constructor(private element: ElementRef) {
    this.observer = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          this.element.nativeElement.classList.add('showOnScroll');
        } else {
          this.element.nativeElement.classList.remove('showOnScroll');
        }
      });
    });

    // Start observing when the directive is initialized
    this.observer.observe(this.element.nativeElement);
  }

  // Clean up observer when directive is destroyed
  ngOnDestroy() {
    this.observer.disconnect();
  }
}
