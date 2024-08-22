import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersListVoiceComponent } from './users-list-voice.component';

describe('UsersListVoiceComponent', () => {
  let component: UsersListVoiceComponent;
  let fixture: ComponentFixture<UsersListVoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersListVoiceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UsersListVoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
