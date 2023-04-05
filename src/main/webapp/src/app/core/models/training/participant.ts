import {Institution} from "../security/institution";
import {Training} from "./training";
import {Person} from "../security/person";

export class Participant {
    id: number;
    person: Person;
    partner: Institution;
    training: Training;
    status: string;
    logistic: string;
    transport: string;
    identifiertype: string;

    constructor(t) {
        {
            this.id = t.id;
            this.person = t.person;
            this.partner = t.partner;
            this.training = t.training;
            this.status = t.status;
            this.logistic = t.logistic;
            this.transport = t.transport;
        }
    }
}
