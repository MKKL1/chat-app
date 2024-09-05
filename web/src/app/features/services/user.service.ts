import { Injectable } from '@angular/core';
import {User} from "../models/user";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  //private userSubject: BehaviorSubject<User> = new BehaviorSubject<User>({description: "", id: 0, imageUrl: "", username: ""});
  private user: User = {
    id: '',
    username: "",
    description: "",
    imageUrl: "",
  };

  public getUser(): User {
    return this.user;
  }

  public setUser(user: User): void {
    console.log(user);
    this.user = user;
  }

  constructor() { }
}
