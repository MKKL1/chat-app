import { Injectable } from '@angular/core';
import {User} from "../../features/models/user";
import {BehaviorSubject, Observable} from "rxjs";
import {KeycloakService} from "keycloak-angular";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../../environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly api: string = environment.api + "users";
  private readonly errorStatus: number = 419;

  //private userSubject: BehaviorSubject<User> = new BehaviorSubject<User>({description: "", id: 0, imageUrl: "", username: ""});
  private user: User = {
    id: '',
    username: "",
    description: "",
    imageUrl: "",
  };

  constructor(private http: HttpClient, private keycloakService: KeycloakService) {
  }

  public getUser(): User {
    return this.user;
  }

  public setUser(user: User): void {
    console.log(user);
    this.user = user;
  }

  // why it's fetching sub id instead of name??
  fetchUserData(){
      this.http.get<User>(this.api + "/me").subscribe({
        next: (user) => {
          this.setUser(user);
        },
        error: (err: HttpErrorResponse) => {
          // if backend returns error with status 419, it means user is not fully registered yet
          // to complete registration another request to backend must be sent
          if(err.status === this.errorStatus){
            this.registerUser().subscribe(res => {
              // after registering user, we are trying to
              // get user data from api once again
              this.fetchUserData();
            });
          }
        }
      });
  }

  registerUser(): Observable<any>{
    return this.http.post<User>(this.api,
      {username: this.keycloakService.getUsername()});
  }

  editDescription(description: string){
    return this.http.patch<User>(this.api, {description: description});
  }

  // todo implement when handling files on backend will be ready
  editAvatar(file: File){
    const formData = new FormData();
    formData.append('file', file, file.name);

    this.http.patch('http://localhost:8081/api/users/avatar', formData).subscribe({
      next: (response) => {
        console.log('Plik przesłany pomyślnie!', response);
      },
      error: (error) => {
        console.error('Wystąpił błąd przy przesyłaniu pliku:', error);
      },
    });
  }

  deleteAccount(){
    this.http.delete(this.api).subscribe(res => {
      this.keycloakService.logout();
    });
  }

}
