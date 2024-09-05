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

  constructor(private keycloakService: KeycloakService, private http: HttpClient, private userService: UserService) {
  }

  // Sending request to backend to create new user
  // It should be handled entirely by backend, but it's not ready yet
  ngOnInit(): void {
    this.keycloakService.getToken().then(token => {
      console.log(token);
    });

    // TODO store user data
    this.http.post(
      environment.api + "users",
      {username: this.keycloakService.getUsername()})
      .subscribe(res => {
        console.log(res);
      });

    // TODO call user service to get user data stored on client side
    // for now there is no data beside sub id ???
    this.http.get<User>(environment.api + "users/me").subscribe(
      (user: User) => {
        this.userService.setUser(user);
      }
    );
  }

}
