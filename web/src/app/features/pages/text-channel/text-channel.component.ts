import {Component, OnInit, ViewChild} from '@angular/core';
import {TextChatsListComponent} from "../../text-chat/text-chats-list/text-chats-list.component";
import {TextChatComponent} from "../../text-chat/text-chat/text-chat.component";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatSidenav, MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {BreakpointObserver} from "@angular/cdk/layout";
import {LayoutComponent} from "../../../core/components/layout/layout.component";

@Component({
  selector: 'app-text-channel',
  standalone: true,
  imports: [
    MatIconModule,
    MatButtonModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    LayoutComponent,
    TextChatsListComponent,
    TextChatComponent
  ],
  templateUrl: './text-channel.component.html',
  styleUrl: './text-channel.component.scss'
})
export class TextChannelComponent implements OnInit{
  @ViewChild(MatSidenav)
  sidenav!: MatSidenav;
  isMobile= true;
  isCollapsed = true;


  constructor(private observer: BreakpointObserver) {}

  ngOnInit() {
    this.observer.observe(['(max-width: 800px)']).subscribe((screenSize) => {
      if(screenSize.matches){
        this.isMobile = true;
      } else {
        this.isMobile = false;
      }
    });
  }

  toggleMenu() {
    if(this.isMobile){
      this.sidenav.toggle();
      this.isCollapsed = false; // On mobile, the menu can never be collapsed
    } else {
      this.sidenav.open(); // On desktop/tablet, the menu can never be fully closed
      this.isCollapsed = !this.isCollapsed;
    }
  }
}
