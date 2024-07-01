import {Component, HostListener, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ScreenSizeService} from "./core/services/screen-size.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit{
  title = 'web';
  isMobileView = false;

  constructor(private screenSizeService: ScreenSizeService) {
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any){
    this.checkScreenSize();
  }

  ngOnInit() {
    this.checkScreenSize();
  }

  private checkScreenSize(){
    this.isMobileView = window.innerWidth <= 768;
    this.screenSizeService.setMobileView(this.isMobileView);
  }
}
