import {Component} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';

import {TranslateService} from '@ngx-translate/core';
import {LanguageSelectionDialog}
  from './language-selection/language-selection';
import {MatDialog} from '@angular/material/dialog';
import {AccessService} from './services/access.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})

/**
 * The global component for our App
 */
export class AppComponent {
  username:string = '';
  connected:boolean = false;
  allowed:boolean|undefined|null;

  /**
   * Constructor of our App
   * @param {BreakpointObserver} breakpointObserver Breakpoint observer
   * @param {TranslateService} translateService Translate service by ngx
   * @param {MatDialog} dialog The matDialog instance to open dialog windows
   * @param {AccessService} accessService Allow to know if access is granted
   */
  constructor(private breakpointObserver: BreakpointObserver,
    public translateService: TranslateService,
    public dialog: MatDialog,
    private accessService:AccessService) {
    this.accessService
        .isAllowed()
        .subscribe(
            (result)=>{
              this.allowed = result;
            },
            (err)=>{
              this.allowed=null;
            });
    // Set availables languages
    // TODO Automatisation
    translateService.setDefaultLang('fr-FR');
    translateService.addLangs(['fr-FR']);
    translateService.addLangs(['en-US']);

    this.accessService.getUser().subscribe((user)=>{
      if (user && user.login) {
        this.connected=true;
        this.username = user.login;
      }
    },
    (err)=>{});

    // Get the user name to show it
    const storedLanguage = localStorage.getItem('language');
    if (storedLanguage != null) {
      this.setCurrentLanguage(storedLanguage);
    } else {
      this.setCurrentLanguage(translateService.getDefaultLang());
    }
  }

  /**
   * Udpate the language based on the IETF code only if it is available
   * @param {string} code the IETF code of the language to applied
   */
  setCurrentLanguage(code:string): void {
    if (this.translateService.getLangs().indexOf(code) !== -1) {
      this.translateService.use(code);
      localStorage.setItem('language', code);
    }
  }

  /**
   * Return the current lang used in the app
   * @return {string} The current lang used in the app
   */
  getCurrentLang(): string {
    return this.translateService.currentLang;
  }

  /**
   * Return the available langs in the app
   * @return {string[]} The available langs in the app
   */
  getLangs(): string[] {
    return this.translateService.getLangs();
  }

  /**
   * On click on the langage button, open the langage dialog window
   */
  handleOpenLanguageSelector(): void {
    this.dialog.open(LanguageSelectionDialog, {
      data: this,
    });
  }

  /**
   * Verify if the user is allowed to access the app
   * @return {boolean|null} The user is allowed to access the app
   */
  isAllowed() : boolean|undefined|null {
    return this.allowed;
  }

  // eslint-disable-next-line no-invalid-this
  isHandset$: Observable<boolean> = this.breakpointObserver
      .observe(Breakpoints.Handset)
      .pipe(
          map((result) => result.matches),
          shareReplay(),
      );


  title = 'front-management';

  menuElements = [
    {
      name: 'menu.items.home',
      path: '',
    },
    {
      name: 'menu.items.workflows',
      path: 'workflows',
    },
    {
      name: 'menu.items.context',
      path: 'contexts',
    },
    {
      name: 'menu.items.users',
      path: 'users',
    },

  ]
}
