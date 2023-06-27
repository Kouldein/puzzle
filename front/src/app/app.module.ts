import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { DragDirective } from './drag.directive';

import { MatGridListModule } from "@angular/material/grid-list";
import { ImageService } from './API/image.service';
import { HttpClientModule } from '@angular/common/http';
import { PuzzleComponent } from './puzzle/puzzle.component';
import { PuzzleService } from './puzzle.service';

import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    DragDirective,
    PuzzleComponent,
  ],
  imports: [
    BrowserModule,
    MatGridListModule,
    HttpClientModule,
    FormsModule 
  ],
  providers: [ImageService, PuzzleService],
  bootstrap: [AppComponent]
})
export class AppModule {
  
 }
