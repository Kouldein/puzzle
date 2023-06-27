import { Injectable, ɵsetAllowDuplicateNgModuleIdsForTest } from '@angular/core';
import { PuzzlePiece } from './puzzle-piece';

@Injectable({
  providedIn: 'root'
})
export class PuzzleService {

  currentPuzzles:any[] = []; 
  x!:number;
  y!:number;
  solved:boolean = true;

  constructor() { 

  }

  sortPuzzles() {
    this.currentPuzzles.sort((a, b) => {
      if (a.currentPosition < b.currentPosition) {
        return -1;
      }
      if (a.currentPosition > b.currentPosition) {
        return 1;
      }
      return 0;
    });
  }

  shuffle(shuffleRotation:boolean){
    const indexes = Array.from({ length: this.currentPuzzles.length }, (_, index) => index);
    for (let i = indexes.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [indexes[i], indexes[j]] = [indexes[j], indexes[i]];
    }
    let temporary = []
    for (let i = 0; i < this.currentPuzzles.length; i++) {
      this.currentPuzzles[indexes[i]].currentPosition = i;
    }
    if(shuffleRotation){
      for (let i = 0; i < this.currentPuzzles.length; i++) {
        this.currentPuzzles[indexes[i]].rotation += Math.floor(Math.random() * (6));
      }
    }
    this.isValid()
    this.sortPuzzles()
  }

  isValid(){
    let isSolved = true;
    for(const piece of this.currentPuzzles){
      if(piece.rotation % 4 != 0 || piece.currentPosition != piece.position){
        isSolved = false;
        break;
      }
    }
    this.solved = isSolved;
  }

  setX(x:number){
    this.x = x
  }

  setY(y:number){
    this.y = y
  }

  getX():number{
    return this.x
  }

  getY():number{
    return this.y
  }

}
