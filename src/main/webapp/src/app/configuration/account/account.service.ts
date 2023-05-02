import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { UnsubscribeOnDestroyAdapter } from 'src/app/shared/UnsubscribeOnDestroyAdapter';
import {environment} from "../../../environments/environment";
import {User} from "../../core/models/security/user";
import {Profile} from "../../core/models/security/profile";
import {Institution} from "../../core/models/security/institution";

@Injectable()
export class AccountService extends UnsubscribeOnDestroyAdapter {

    api = `${environment.api}/user`;

    isTblLoading = true;
    dataChange: BehaviorSubject<User[]> = new BehaviorSubject<User[]>([]);
    dialogData: any;

    constructor(private http: HttpClient) {
        super();
    }

    getInstitutions(){
        return this.http.get<Institution[]>(`${environment.api}/place`);
    }

    getProfiles(){
        return this.http.get<Profile[]>(`${this.api}/profile`);
    }

    get data(): User[] {
        return this.dataChange.value;
    }

    setDialogData(usr) {
        this.dialogData = usr;
    }


    getDialogData() {
        return this.dialogData;
    }

    getUsers(): void {
        this.subs.sink = this.http.get<User[]>(this.api).subscribe(
            (data) => {
                this.isTblLoading = false;
                this.dataChange.next(data);
            },
            (error: HttpErrorResponse) => {
                this.isTblLoading = false;
                console.log(error.name + ' ' + error.message);
            }
        );
    }

    create(usr: User){
        return this.http.post(this.api, usr);
    }

    update(users: User[]){
        return this.http.post(`${this.api}/update`, users);
    }

    delete(id:number){
        return this.http.delete(`${this.api}/delete/${id}`);
    }

    forgotPassword(email: String){
        return this.http.post(`${this.api}/password/forgot`, email);
    }

    resetPassword(id: number, password: string){
        return this.http.post(`${this.api}/password/reset`, {id, password});
    }
}
