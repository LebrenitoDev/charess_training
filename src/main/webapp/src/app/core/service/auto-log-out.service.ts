import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})

export class AutoLogOutService {

  private readonly MINUTES_UNITL_AUTO_LOGOUT = 1;
  private readonly CHECK_INTERVAL = 1000;
  private readonly STORE_KEY = 'lastAction';

   constructor(private router: Router) {
    this.check();
    this.initListener();
    this.initInterval();
    localStorage.setItem(this.STORE_KEY, Date.now().toString());
  }

  public getLastAction() {
    return parseInt(localStorage.getItem(this.STORE_KEY));
  }

  public setLastAction(lastAction: number) {
    localStorage.setItem(this.STORE_KEY, lastAction.toString());
  }

  private initListener() {
    document.body.addEventListener('click', () => this.reset());
    document.body.addEventListener('mousemove', () => this.reset());
    document.body.addEventListener('keydown', () => this.reset());
    document.body.addEventListener('keyup', () => this.reset());
    document.body.addEventListener('keypress', () => this.reset());
    window.addEventListener("load", () => this.reset());

  }

  private reset() {
    // console.log('date got by using events', Date.now());
    this.setLastAction(Date.now());
    // console.log('store key', localStorage.getItem(this.STORE_KEY));

  }

  private initInterval() {
    setInterval(() => {
      this.check();
    }, this.CHECK_INTERVAL);
  }

  private check() {
    const now = Date.now();
    const timeleft = this.getLastAction() + this.MINUTES_UNITL_AUTO_LOGOUT * 60 * 1000;
    const diff = timeleft - now;
    // console.log('difference', diff)
    const isTimeout = diff < 0;

    if (isTimeout) {
      localStorage.clear();
      this.router.navigate(['/authentication/signin']);
    }
  }
}
