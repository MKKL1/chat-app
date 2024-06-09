import { Component } from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatFormField} from "@angular/material/form-field";
import {MatInput, MatInputModule} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {FormsModule, NgForm} from "@angular/forms";
import {MatCheckbox} from "@angular/material/checkbox";
import {NgOptimizedImage} from "@angular/common";
import {LoginForm} from "./LoginForm";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatCard,
    MatCardTitle,
    MatCardContent,
    MatCardHeader,
    MatIcon,
    MatFormField,
    MatInput,
    MatInputModule,
    MatButton,
    RouterLink,
    FormsModule,
    MatCheckbox,
    NgOptimizedImage
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  model = new LoginForm('','');

  submitLogin(form: NgForm){
    console.log("Jebać javascript");
    console.log(form.form.value);
  }

}
