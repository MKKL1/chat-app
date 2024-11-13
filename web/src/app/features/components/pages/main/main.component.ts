import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {AppbarComponent} from "../../../../core/components/appbar/appbar.component";
import {UserService} from "../../../../core/services/user.service";
import {EventService} from "../../../../core/events/event.service";
import {RoleService} from "../../../services/role.service";

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

  constructor(
    private userService: UserService,
    private eventService: EventService,
    private roleService: RoleService) {
  }

  ngOnInit(): void {
    // after entering part of app available to authorized users
    // basic data about current user should be saved in service for further use
    this.userService.fetchUserData();

    // this method show your actual token
    // this.keycloakService.getToken().then(token => {
    //   console.log(token);
    // });

    this.eventService.init();
    this.roleService.init();
  }

}
