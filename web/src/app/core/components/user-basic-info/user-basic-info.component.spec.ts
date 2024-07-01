import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserBasicInfoComponent } from './user-basic-info.component';

describe('UserBasicInfoComponent', () => {
  let component: UserBasicInfoComponent;
  let fixture: ComponentFixture<UserBasicInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserBasicInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserBasicInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
