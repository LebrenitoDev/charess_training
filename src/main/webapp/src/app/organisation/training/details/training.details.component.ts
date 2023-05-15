import { Component, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { Training } from "../../../core/models/training/training";
import { TrainingService } from "../training.service";
import { HttpErrorResponse } from "@angular/common/http";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Participant } from "../../../core/models/training/participant";
import { TrainingParticipants } from "../../../core/models/training/training.participants";
import { InstitutionService } from "../../../configuration/institution/institution.service";
import * as XLSX from "xlsx";


@Component({
    selector: 'app-form',
    templateUrl: './training.details.component.html',
    styleUrls: ['./training.details.component.scss']

})

export class TrainingDetailsComponent implements OnInit {
    fg: FormGroup;
    training: Training;
    startDate: Date;
    endDate: Date;
    partners = new FormControl();
    partnerArray = [];
    mask = '';
    data: Array<any>;
    excelData: any;


    constructor(private router: Router, private fb: FormBuilder, private service: TrainingService, private snack: MatSnackBar, private placeService: InstitutionService) {
        const state = this.router.getCurrentNavigation().extras.state;

        this.training = state ? state.training : JSON.parse(localStorage.getItem("training"));
        this.startDate = new Date(this.training?.startDate + "GMT-0500");
        this.endDate = new Date(this.training?.endDate + "GMT-0500");

        const trainingParticpants = new TrainingParticipants(
            {
                'training': this.training,
                'participants': this.fb.array(this.training.participants.length < 1 ? [this.format(new Participant({}))] : this.training.participants.map(
                    p => this.format(new Participant(p)))
                )
            });
        this.fg = this.fb.group(trainingParticpants);
    }

    ngOnInit() {
        this.placeService.getPlaces('').subscribe((res) => {
            this.partnerArray = res;
        });
    }

    get participants(): FormArray {
        return this.fg.get('participants') as FormArray;
    }

    compare(a, b): boolean {
        return a && b ? (a.id && b.id && a.id === b.id) : a === b;
    }

    format(p: Participant): FormGroup {
        return this.fb.group({
            id: [p?.id],
            partner: [p?.partner],
            logistic: [p.logistic],
            transport: [p.transport],
            person: this.fb.group({
                id: [p?.person?.id],
                identifierType: [p?.person?.identifierType, [Validators.required]],
                identifier: [p?.person?.identifier, [Validators.required]],
                firstName: [p?.person?.firstName, [Validators.required]],
                lastName: [p?.person?.lastName, [Validators.required]],
                phone: [p?.person?.phone, [Validators.required]],
                email: [p?.person?.email, [Validators.required]],
            })
        });
    }

    // readExcel(event):void {
    //     const file = event.target.files[0];
    //     // if (file.length) {
    //         let fileReader = new FileReader();
    //         console.log("****{file}****", file)
    //         fileReader.readAsBinaryString(file);
    //         fileReader.onload = (e) => {
    //             var workbook = XLSX.read(fileReader.result, { type: 'binary' });
    //             var sheet = workbook.SheetNames;
    //             if(sheet.length) {
    //                 this.data = XLSX.utils.sheet_to_json(workbook.Sheets[sheet[0]])
    //             this.data.map(res => {
    //                 console.log("****{Data}****", res)
    //                 this.excelData = {
    //                     id: null,
    //                     partner: res?.partner,
    //                     logistic: res?.logement,
    //                     transport: res?.transport,
    //                     person: {
    //                         id: null,
    //                         identifierType: res?.identifier_type,
    //                         identifier: res?.identifier,
    //                         firstName: res?.prenom,
    //                         lastName: res?.nom,
    //                         phone: res?.tel,
    //                         email: res?.email,
    //                     }
    //                 }
    //             })
    //             }
    //         }
    //     // }
    // }

    private toast(color, text) {
        this.snack.open(text, '', {
            duration: 2000, verticalPosition: 'top', horizontalPosition: 'right', panelClass: color,
        });
    }

    back() {
        this.router.navigate(['organisation/training/page']);
    }

    private chgSuccess() {
        this.toast('bg-green', 'Training status successfully changed...');
        // localStorage.setItem("training", JSON.stringify(this.training));
        // this.router.navigate(['organisation/training/details'],{state:{training: this.training}});
    }

    private success() {
        this.toast('bg-green', 'The usr has been successfully created');
        this.back();
    }

    private error(err: HttpErrorResponse) {
        this.toast('bg-red', 'Something went wrong the usr has not been created. Please, try again!');
        console.error(err);
    }

    private chgError(err: HttpErrorResponse) {
        this.toast('bg-red', 'Technical issues. Status cannot be changed!');
        console.error(err);
    }

    submit(ob: any): void {
        ob.id = this.training.id;
        ob.participants = this.participants.getRawValue();
        this.service.updateParticipants(ob).subscribe(
            (res) => this.success(),
            (err) => this.error(err)
        );
    }

    push() {
        this.participants.controls.unshift(this.format(new Participant({})));
    }

    pull(index: number): void {
        if (this.participants.length > 0) {
            this.participants.removeAt(index);
            if (this.participants.length == 0)
                this.push()
        }

    }
}