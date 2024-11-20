import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton} from "@angular/material/button";
import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {KeycloakService} from "keycloak-angular";
import {GifSearchComponent} from "../../../../shared/ui/gif-search/gif-search.component";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {ButtonDangerComponent} from "../../../../shared/ui/button-danger/button-danger.component";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {EditAvatarComponent} from "../../../../shared/ui/edit-avatar/edit-avatar.component";
import {AvatarComponent} from "../../../../shared/ui/avatar/avatar.component";
import {UserService} from "../../../../core/services/user.service";
import {resetStores} from "@datorama/akita";
import {User} from "../../../models/user";
import {Subscription} from "rxjs";
import {MessageService} from "primeng/api";

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

export class ProfileComponent implements OnInit, OnDestroy{
  user = signal<User | null>(null);
  private userSubscription: Subscription;
  description: string = '';

  constructor(
    private keycloakService: KeycloakService,
    private userService: UserService,
    private dialog: MatDialog,
    private messageService: MessageService) {
  }

  ngOnInit() {
    this.userSubscription = this.userService.user$.subscribe(updatedUser => {
      this.user.set(updatedUser);
      this.description = updatedUser?.description || '';
    });
  }

  editAvatar(){
    this.dialog.open(EditAvatarComponent, {
      data: {imageUrl: this.user()?.imageUrl}
    });
  }

  openUserSettings(){
    this.keycloakService.getKeycloakInstance().accountManagement();
  }

  editDescription(){
    if(this.description.length === 0){
      this.messageService.add({severity: 'warn', summary: 'Description cannot be null'});
      return;
    }

    this.userService.editDescription(this.description).subscribe(user => {
      console.log(user);
    });
  }

  logout(){
    resetStores();
    this.keycloakService.logout();
  }

  deleteAccount(){
    this.userService.deleteAccount();
  }

  ngOnDestroy() {
    this.userSubscription.unsubscribe();
  }
}
