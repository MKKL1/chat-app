import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {AppbarComponent} from "../../../../core/components/appbar/appbar.component";
import {UserService} from "../../../../core/services/user.service";
import {EventService} from "../../../../core/events/event.service";
import {RoleService} from "../../../services/role.service";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {KeycloakEventType, KeycloakService} from "keycloak-angular";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    AppbarComponent,
    RouterOutlet,
    ConfirmDialogModule
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy {

  tokenExpireSub: Subscription;

  constructor(
    private userService: UserService,
    private eventService: EventService,
    private roleService: RoleService,
    private keycloak: KeycloakService) {
  }

  ngOnInit(): void {
    // after entering part of app available to authorized users
    // basic data about current user should be saved in service for further use
    this.userService.fetchUserData();
    this.eventService.init();
    this.roleService.init();

    this.tokenExpireSub = this.keycloak.keycloakEvents$
      .subscribe(event => {
        if(event.type === KeycloakEventType.OnTokenExpired){
          this.keycloak.updateToken(20);
        }
      }
    );
  }

  ngOnDestroy() {
    this.tokenExpireSub.unsubscribe();
  }

}
