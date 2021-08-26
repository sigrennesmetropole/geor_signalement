import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss'],
})
/**
 * The component to use when the acces is not granted to the user
 */
export class ErrorComponent implements OnInit {
/**
 * Constructor of the component
 */
  constructor() { }

  /**
   * Actions after init
   */
  ngOnInit(): void {}
}
