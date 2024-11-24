import { Component } from '@angular/core';
import {MatAnchor, MatButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
//import {FeaturesComponent} from "../../../../shared/ui/features/features.component";

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    //FeaturesComponent,
    MatAnchor
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent {

}
