import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})

/**
* A class to display messages to the user
*/
export class ToasterUtil {
  /**
    * Constructor of our Toaster
    * @param {MatSnackBar} snackBar The SnackBar used to display messges
    */
  constructor(private snackBar:MatSnackBar,
              private translateService: TranslateService) {}

  /**
   * Display an error message
   * @param {string} message The message to display
   * @param {number} duration The duration of the message
   */
  private displayError(message: string, duration: number = 3000) : void {
    this.snackBar.open(message, '', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: duration,
      panelClass: ['error-snackbar', 'snackbar'],
    });
  }

  /**
   * Public method to display an error message
   * @param {string} key Translation key of the message
   * @param {string | undefined} reason Facultative reason
   */
  sendErrorMessage(key : string, reason?: string) : void {
    let message: string = this.translateService.instant(key);
    if (reason) {
      message = message + ' (' + reason + ')';
    }

    this.displayError(message);
  }

  /**
   * Display a success message
   * @param {string} message The message to display
   * @param {number} duration The duration of the message
   */
  private displaySuccess(message: string, duration: number = 3000) : void {
    this.snackBar.open(message, '', {
      horizontalPosition: 'center',
      verticalPosition: 'top',
      duration: duration,
      panelClass: ['success-snackbar', 'snackbar'],
    });
  }

  /**
   * Public method to display an error message
   * @param {string} key Translation key of the message
   */
  sendSuccessMessage(key: string) : void {
    const message: string = this.translateService.instant(key);
    this.displaySuccess(message);
  }
}
