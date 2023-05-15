import {Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import {DatatableComponent } from '@swimlane/ngx-datatable';
import {MatSnackBar } from '@angular/material/snack-bar';
import {MatDialog} from "@angular/material/dialog";
import {UnsubscribeOnDestroyAdapter} from "../../../shared/UnsubscribeOnDestroyAdapter";
import {HttpErrorResponse} from "@angular/common/http";
import {DeleteComponent} from "../../delete/delete.confirm";
import {CategoryFormComponent} from "../form/category.form.component";
import {CategoryService} from "../category.service";
import {Category} from "../../../core/models/security/category";

@Component({
    selector: 'app-page',
    templateUrl: './category.page.component.html',
    styleUrls: ['./category.page.component.scss']
})

export class CategoryPageComponent extends UnsubscribeOnDestroyAdapter implements OnInit {

    @ViewChild('roleTemplate', { static: true }) roleTemplate: TemplateRef<any>;
    @ViewChild(DatatableComponent, { static: false }) table: DatatableComponent;
    rows = [];
    data = [];
    category: Category | null;

    filteredData = [];
    columns = [{name: 'name'}];

    dim = {
        width: '800px',
        height: '260px'
    };

    constructor(private service: CategoryService, private snack: MatSnackBar, public dialog: MatDialog) {
        super();
    }

    ngOnInit() {
        this.set();
    }

    set(){
        this.service.getCategories().subscribe(res => {
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
                if((item[cols[i].name] && item[cols[i].name].toString().toLowerCase().indexOf(val) !==-1) || !val)
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

    toast(color, text) {
        this.snack.open(text, '', {duration: 2000, verticalPosition: "top", horizontalPosition: "right", panelClass: color});
    }

    success(id){
        this.data = this.arrayRemove(this.data, id);
        this.toast('bg-green','That record has been successfully deleted');
    }

    private error(err:HttpErrorResponse){
        this.toast('bg-red','That record is in use somewhere, you have to delete its dependance first!');
        console.error(err);
    }

    add(){
        const dialogRef = this.dialog.open(CategoryFormComponent, {
            ...this.dim,
            data: {
                category: this.category,
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
        const dialogRef = this.dialog.open(CategoryFormComponent, {
            ...this.dim,
            data: {
                category: row,
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
                this.service.delete(row.id).subscribe(
                    res => this.success(row.id),
                    (err: HttpErrorResponse)=>this.error(err)
                );
                this.set();
            }
        });
    }
}
