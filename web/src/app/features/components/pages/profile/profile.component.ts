import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton} from "@angular/material/button";
import {Component, OnInit} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {GifSearchComponent} from "../../../../shared/ui/gif-search/gif-search.component";
import {CommunityService} from "../../../services/community.service";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {ButtonDangerComponent} from "../../../../shared/ui/button-danger/button-danger.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {EditAvatarComponent} from "../../../../shared/ui/edit-avatar/edit-avatar.component";
import {AvatarComponent} from "../../../../shared/ui/avatar/avatar.component";
import {UserService} from "../../../../core/services/user.service";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    GifSearchComponent,
    MatIcon,
    MatFabButton,
    MatCard,
    MatButton,
    MatCardContent,
    MatCardHeader,
    ButtonDangerComponent,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    FormsModule,
    AvatarComponent
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit{
  userDescription: string = '';
  username: string = '';

  constructor(
    private keycloakService: KeycloakService,
    private userService: UserService,
    private dialog: MatDialog) {
  }

  ngOnInit() {
    this.username = this.keycloakService.getUsername();
    this.userDescription = this.userService.getUser().description!;
  }

  editAvatar(){
    this.dialog.open(EditAvatarComponent);
  }

  openUserSettings(){
    this.keycloakService.getKeycloakInstance().accountManagement();
  }

  editDescription(){
    this.userService.editDescription(this.userDescription).subscribe(user => {
      console.log(user);
    });
  }

  logout(){
    this.keycloakService.logout();
  }

  deleteAccount(){
    this.userService.deleteAccount();
  }
}
