import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit{

  constructor(private keycloackServie: KeycloakService) {
  }

  ngOnInit() {

  }

  openUserSettings(){
    this.keycloackServie.getKeycloakInstance().accountManagement();
  }

}
