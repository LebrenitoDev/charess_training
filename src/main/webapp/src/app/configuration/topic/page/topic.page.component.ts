import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { DatatableComponent } from '@swimlane/ngx-datatable';
import { MatSnackBar } from '@angular/material/snack-bar';
import {MatDialog} from "@angular/material/dialog";
import {TopicService} from "../topic.service";
import {TopicFormComponent} from "../form/topic.form.component";
import {UnsubscribeOnDestroyAdapter} from "../../../shared/UnsubscribeOnDestroyAdapter";
import {HttpErrorResponse} from "@angular/common/http";
import {DeleteComponent} from "../../delete/delete.confirm";
import {Topic} from "../../../core/models/training/topic";

@Component({
    selector: 'app-page',
    templateUrl: './topic.page.component.html',
    styleUrls: ['./topic.page.component.scss']
})

export class TopicPageComponent extends UnsubscribeOnDestroyAdapter implements OnInit {

    @ViewChild('roleTemplate', { static: true }) roleTemplate: TemplateRef<any>;
    @ViewChild(DatatableComponent, { static: false }) table: DatatableComponent;
    rows = [];
    data = [];
    topic: Topic | null;

    filteredData = [];

    columns = [
        { title: 'title' },
        { theme: 'theme' }
    ];

    dim = {
        width: '1000px',
        height: '380px'
    };

    constructor(private service: TopicService, private _snackBar: MatSnackBar, public dialog: MatDialog) {
        super();
    }

    ngOnInit() {
        this.set();
    }

    set(){
        this.service.getAllTopics().subscribe(res => {
            this.data = res;
            this.filteredData = res;
        });
    }

    filterDatatable(event){
        const val = event.target.value.toLowerCase();
        const cols = this.columns;
        const keys = Object.keys(this.filteredData[0]);
        this.data = this.filteredData.filter(function (item) {
            for (let i = 0; i < cols.length; i++) {
                if((item[cols[i].title] && item[cols[i].title].toString().toLowerCase().indexOf(val) !==-1) || !val)
                   return true;
            }
        });
        this.table.offset = 0;
    }

    arrayRemove(array, id) {
        return array.filter(function (element) {
            return element.id != id;
        });
    }

    showNotification(colorName, text) {
        this._snackBar.open(text, '', {
            duration: 2000,
            verticalPosition: "top",
            horizontalPosition: "right",
            panelClass: colorName,
        });
    }

    success(id){
        this.data = this.arrayRemove(this.data, id);
        this.showNotification('bg-green','That record has been successfully deleted');
    }

    private error(err:HttpErrorResponse){
        this.showNotification('bg-red','That record is in use somewhere, you have to delete its dependance first!');
        console.error(err);
    }

    add(){
        const dialogRef = this.dialog.open(TopicFormComponent, {
            ...this.dim,
            data: {
                topic: this.topic,
                action: 'add',
            }
        });
        this.subs.sink = dialogRef.afterClosed().subscribe((result) => {
            if (result === 1) {
                this.set();
            }
        });
    }

    edit(row) {
        const dialogRef = this.dialog.open(TopicFormComponent, {
            ...this.dim,
            data: {
                topic: row,
                action: 'edit',
            }
        });
        this.subs.sink = dialogRef.afterClosed().subscribe((result) => {
            if (result === 1) {
                this.set();
            }
        });
    }

    delete(row): void {
        const dialogRef = this.dialog.open(DeleteComponent, {});
        this.subs.sink = dialogRef.afterClosed().subscribe((result) => {
            if (result === 1) {
                this.service.deleteTopic(row.id).subscribe(
                    res => this.success(row.id),
                    (err: HttpErrorResponse)=>this.error(err)
                );
                this.set();
            }
        });
    }
}
