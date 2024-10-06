import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersListComponent } from './users-list.component';
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

describe('UsersListComponent', () => {
  let component: UsersListComponent;
  let fixture: ComponentFixture<UsersListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersListComponent, NoopAnimationsModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
