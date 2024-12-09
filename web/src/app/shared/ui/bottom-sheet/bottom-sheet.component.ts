import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import {NgClass, NgStyle} from "@angular/common";

@Component({
  selector: 'app-bottom-sheet',
  standalone: true,
  imports: [
    NgClass,
    NgStyle
  ],
  templateUrl: './bottom-sheet.component.html',
  styleUrl: './bottom-sheet.component.scss'
})
export class BottomSheetComponent implements AfterViewInit, OnDestroy{
  @Input() opened: boolean;
  @Input() backgroundColor: string;
  @Input() color: string;
  @Input() addOverlay: boolean = false;

  @Output() close = new EventEmitter<void>();

  constructor(private el: ElementRef) {}

  ngAfterViewInit(): void {
    document.addEventListener('click', (event) => this.handleClickOutside(event));
  }

  ngOnDestroy(): void {
    document.removeEventListener('click', this.handleClickOutside);
  }

  handleClickOutside(event: MouseEvent){
    const targetElement = this.el.nativeElement;
    if (!targetElement.contains(event.target)) {
      this.opened = false;
    }
  }
}
