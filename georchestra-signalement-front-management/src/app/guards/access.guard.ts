import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate,
  Router, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AccessService} from '../services/access.service';

@Injectable({
  providedIn: 'root',
})
/**
* Guard to restrict the access to the administration routes
*/
export class IsSignalementAdmin implements CanActivate {
  /**
  * Construtor of the guard
  * @param {AccessService} accessService
  * @param {Router} router
  */
  constructor(private accessService: AccessService,
    private router: Router) {
  }

  /**
  * Is the user allowed to access the route
  * @param {ActivatedRouteSnapshot} route
  * @param {RouterStateSnapshot} state
  * @return {Observable<boolean>}
  */
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot)
    : Observable<boolean> {
    return this.accessService.isAllowed().pipe(
        catchError((err, caught)=>{
          return of(false);
        }),
    );
  }
}
