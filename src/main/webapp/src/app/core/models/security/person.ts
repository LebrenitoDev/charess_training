import * as moment from 'moment';

export class Person {
    id: number;
    identifierType: string;
    identifier: string;
    firstName: string;
    lastName: string;
    gender: string;
    phone: string;
    email: string;
    birthDate: Date;
    locationAddress: Location;
    textAddress: string;
    fullname: string;
    alias: string;

    constructor(person) {
        {
            this.id = person?.id;
            this.identifierType = person?.identifierType;
            this.identifier =  person?.identifier;
            this.firstName = person?.firstName;
            this.lastName = person?.lastName;
            this.email = person?.email;
            this.phone = person?.phone;
            this.gender = person?.gender;
        }
    }
}