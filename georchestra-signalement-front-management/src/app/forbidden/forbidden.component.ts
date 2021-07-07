import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-forbidden',
  templateUrl: './forbidden.component.html',
  styleUrls: ['./forbidden.component.scss'],
})
/**
 * The component to use when the acces is not granted to the user
 */
export class ForbiddenComponent implements OnInit {
/**
 * Constructor of the component
 */
  constructor() { }

  /**
   * Actions after init
   */
  ngOnInit(): void {}
}
