import {Component, OnInit, signal, ViewChild} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatListItem, MatNavList} from "@angular/material/list";
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from "@angular/material/sidenav";
import {MatToolbar} from "@angular/material/toolbar";
import {BreakpointObserver} from "@angular/cdk/layout";
import {CommunityService} from "../../../features/services/community.service";
import {CommunityQuery} from "../../../features/store/community/community.query";

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatListItem,
    MatNavList,
    MatSidenav,
    MatSidenavContainer,
    MatSidenavContent,
    MatToolbar
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  @ViewChild(MatSidenav) sidenav!: MatSidenav;
  isMobile= signal<boolean>(true);
  isCollapsed = signal<boolean>(true);

  title = signal<string>('');

  constructor(
    private observer: BreakpointObserver,
    private communityQuery: CommunityQuery) {}

  ngOnInit() {
    this.observer.observe(['(max-width: 800px)']).subscribe((screenSize) => {
      if(screenSize.matches){
        this.isMobile.set(true);
      } else {
        this.isMobile.set(false);
      }
    });

    this.communityQuery.selectActive().subscribe(community => {
      if(community === undefined){
        return;
      }

      this.title.set(community.name);
    });
  }

  toggleMenu() {
    if(this.isMobile()){
      this.sidenav.toggle();
      this.isCollapsed.set(false);
    } else {
      this.sidenav.open();
      this.isCollapsed.update(value => !value);
    }
  }
}
