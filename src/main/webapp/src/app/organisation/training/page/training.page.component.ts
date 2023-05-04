import {Component, ElementRef, OnInit, ViewChild} from "@angular/core";
import {UnsubscribeOnDestroyAdapter} from "../../../shared/UnsubscribeOnDestroyAdapter";
import {DataSource} from "@angular/cdk/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {SelectionModel} from "@angular/cdk/collections";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Router} from "@angular/router";
import {MatMenuTrigger} from "@angular/material/menu";
import {BehaviorSubject, fromEvent, merge, Observable } from 'rxjs';
import {map} from 'rxjs/operators';
import {Training} from "../../../core/models/training/training";
import {TrainingService} from "../training.service";

@Component({
    selector: 'app-page',
    templateUrl: './training.page.component.html',
    styleUrls: ['./training.page.component.scss']
})

export class TrainingPageComponent extends UnsubscribeOnDestroyAdapter implements OnInit {

    displayedColumns = [
        'status',
        'topic',
        'startDate',
        'endDate',
        'capacity',
        'network',
        'location',
        'editor',
        'edited'
    ];

    database: TrainingService | null;
    datasource: Source | null;
    selection = new SelectionModel<Training>(true, []);
    Training: Training | null;
    profiles=[];
    dim = {
        width: '1200px',
        height: '420px'
    };

    constructor(public http: HttpClient, public dialog: MatDialog, private snack: MatSnackBar, private factory: TrainingService, private router: Router){
        super();
    }

    @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
    @ViewChild(MatSort, { static: true }) sort: MatSort;
    @ViewChild('filter', { static: true }) filter: ElementRef;
    @ViewChild(MatMenuTrigger)
    contextMenu: MatMenuTrigger;
    contextMenuPosition = { x: '0px', y: '0px' };

    public load() {
        this.database = new TrainingService(this.http);
        this.datasource = new Source(this.database, this.paginator, this.sort);
        console.log("*********{data}*********",this.datasource);

        this.subs.sink = fromEvent(this.filter.nativeElement, 'keyup').subscribe(
            () => {
                if (!this.datasource) {
                    return;
                }
                this.datasource.filter = this.filter.nativeElement.value;
            }
        );
    }

    ngOnInit() {
        this.load();
    }

    private toast(color, text) {
        this.snack.open(text, '', {
            duration: 2000, verticalPosition: 'top', horizontalPosition: 'right', panelClass: color,
        });
    }

    success(){
        this.router.navigateByUrl('/RefreshComponent', { skipLocationChange: true }).then(() => {
            this.router.navigate(['/organisation/training/page']);
        });
        this.toast('bg-green','That record has been successfully deleted');
    }

    private error(err:HttpErrorResponse){
        this.toast('bg-red','That record is in use somewhere, you have to delete its dependance first!');
        console.error(err);
    }

    displayManagers(managers) {
        let display = '';
        if (managers.length > 0) {
            if (managers.length == 1) {
                display = managers[0].fullname;
            } else if (managers.length == 2){
                display = managers[0].fullname + ' & ' + managers[1].fullname;
            } else {
                display = managers[0].fullname + ' & ' +  (managers.length-1) + ' autres';
            }
        }
        return display;
    }

    add(){
        this.router.navigate(['organisation/training/broadcast']);
    }

    details(rw){
        localStorage.setItem("training", JSON.stringify(rw));
        this.router.navigate(['organisation/training/details'],{state:{training: rw}});
    }
}


export class Source extends DataSource<Training> {

    filterChange = new BehaviorSubject('');

    get filter(): string {
        return this.filterChange.value;
    }

    set filter(filter: string) {
        this.filterChange.next(filter);
    }

    filteredData: Training[] = [];
    renderedData: Training[] = [];

    constructor(public service: TrainingService, public paginator: MatPaginator, public ms: MatSort){
        super();
        this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
    }

    connect(): Observable<Training[]>{
        const displayDataChanges = [
            this.service.dataChange,
            this.ms.sortChange,
            this.filterChange,
            this.paginator.page,
        ];
        this.service.getTrainings();
        return merge(...displayDataChanges).pipe(
            map(() => {
                this.filteredData = this.service.data
                    .slice()
                    .filter((training: Training) => {
                        const search = (
                            training.topic.title
                        ).toLowerCase();
                        return search.indexOf(this.filter.toLowerCase()) !== -1;
                    });
                const sortedData = this.sortData(this.filteredData.slice());
                const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
                this.renderedData = sortedData.splice(startIndex, this.paginator.pageSize);
                return this.renderedData;
            })
        );
    }

    disconnect() {}

    sortData(data: Training[]): Training[] {
        if (!this.ms.active || this.ms.direction === '') {
            return data;
        }
        return data.sort((a, b) => {
            let propertyA: number | string = '';
            let propertyB: number | string = '';
            switch (this.ms.active) {
                case 'id':
                    [propertyA, propertyB] = [a.id, b.id];
                    break;
                case 'name':
                    [propertyA, propertyB] = [a.topic.title, b.topic.title];
                    break;

            }
            const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
            const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
            return (
                (valueA < valueB ? -1 : 1) * (this.ms.direction === 'asc' ? 1 : -1)
            );
        });
    }

}
