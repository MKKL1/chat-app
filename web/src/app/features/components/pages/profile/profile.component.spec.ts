import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { KeycloakService } from "keycloak-angular";
import { UserService } from "../../../../core/services/user.service";
import { MatDialog } from "@angular/material/dialog";
import { of } from "rxjs";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const userServiceMock = {
  editDescription: jest.fn().mockReturnValue(of({
    id: '1',
    username: 'testuser',
    imageUrl: 'testimageurl',
    description: 'Test description'
  })),
  getUser: jest.fn().mockReturnValue(of({
    id: '1',
    username: 'testuser',
    imageUrl: 'testimageurl',
    description: 'Test description'
  })),
  deleteAccount: jest.fn()
};

const keycloakMock = {
  getUsername: jest.fn().mockReturnValue(of('Username')),
  getKeycloakInstance: jest.fn(),
  logout: jest.fn()
};

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let dialog: MatDialog;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileComponent, NoopAnimationsModule],
      providers: [
        { provide: KeycloakService, useValue: keycloakMock},
        MatDialog,
        { provide: UserService, useValue: userServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;

    dialog = TestBed.inject(MatDialog);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should retrieve user description on init', () => {

  });

  it('should call editDescription method from userService', () => {

  });

  it('should call deleteAccount method from userService', () => {

  });
});
