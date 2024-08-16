import {Component, OnInit, signal} from '@angular/core';
import {MatAccordion, MatExpansionModule} from '@angular/material/expansion';
import {AvatarComponent} from "../../../core/components/avatar/avatar.component";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {UserBasicInfoComponent} from "../../../core/components/user-basic-info/user-basic-info.component";
import {MatNavList} from "@angular/material/list";
import {KeycloakService} from "keycloak-angular";


@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    MatAccordion,
    MatExpansionModule,
    AvatarComponent,
    MatChipSet,
    MatChip,
    UserBasicInfoComponent,
    MatNavList
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent implements OnInit{
  readonly panelOpenState = signal(false);

  user = '';

  constructor(private keycloackService: KeycloakService) {
  }

  ngOnInit() {
    this.initUserOptions();
  }

  private initUserOptions(): void {
    this.user = this.keycloackService.getUsername();
  }

  logout(): void {
    this.keycloackService.logout('http://localhost:4200');
  }

  users: any[] = [
    {id: 1, name: "twoja stara", roles: ["Owner", "Administator"]},
    {id: 2, name: "twoj stary"},
    {id: 3, name: "twoje stare"}
  ];
}
