import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {AppbarComponent} from "../../../../core/components/appbar/appbar.component";
import {KeycloakService} from "keycloak-angular";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../../environment";
import {UserService} from "../../../services/user.service";
import {User} from "../../../models/user";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    AppbarComponent,
    RouterOutlet
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit {

  constructor(private userService: UserService, private keycloakService: KeycloakService) {
  }

  ngOnInit(): void {
    // after entering part of app available to authorized users
    // basic data about current user should be saved in service for further use
    this.userService.fetchUserData();

    this.keycloakService.getToken().then(token => {
      console.log(token);
    });

  }

}
