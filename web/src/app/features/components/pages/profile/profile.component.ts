import {MatIcon} from "@angular/material/icon";
import {MatFabButton} from "@angular/material/button";
import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {GifSearchComponent} from "../../../../shared/ui/gif-search/gif-search.component";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    GifSearchComponent,
    MatIcon,
    MatFabButton
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit{

  constructor(private keycloakService: KeycloakService) {
  }

  ngOnInit() {

  }

  openUserSettings(){
    this.keycloakService.getKeycloakInstance().accountManagement();
  }

  logout(){
    this.keycloakService.logout();
  }

}
