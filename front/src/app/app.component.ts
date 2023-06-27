import { Component, OnInit, Renderer2 } from '@angular/core';
import { FileHandle } from './file-handle';
import { DomSanitizer } from '@angular/platform-browser';
import { ImageService } from './API/image.service';
import { PuzzlePiece } from './puzzle-piece';
import { PuzzleService } from './puzzle.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

  shuffleRotation:boolean = false;

  solve:any = [];

  showSolution:boolean = false;

  showText = "Show solution by algorithm";

  constructor(private sanitizer:DomSanitizer, private imageService:ImageService, private puzzleService:PuzzleService){
    
  }

  ngOnInit(){
    this.getImage(4);
    this.getSolve();
  }

  getIsSolved(){
    return this.puzzleService.solved;
  }

  toggleShowing(){
    this.showSolution = !this.showSolution;
    if(this.showText == "Show solution by algorithm"){
      this.showText = "Hide solution by algorithm"
    }else{
      this.showText = "Show solution by algorithm"
    }
  }

  shufflePuzzle(){
    if(this.shuffleRotation){
      const elements = document.getElementsByClassName('drag-item');
      for (let i = 0; i < elements.length; i++) {
        const div:any = elements[i].children[0].children[0].classList.remove('fancy-rotation')
      }
    }
    this.puzzleService.shuffle(this.shuffleRotation);
    setTimeout(() => {
      this.prepareData();
      if(this.shuffleRotation){
        const elements = document.getElementsByClassName('drag-item');
        for (let i = 0; i < elements.length; i++) {
          const div:any = elements[i].children[0].children[0].classList.add('fancy-rotation')
        }
      }
    }, 200)
  }

  prepareData(){
    var firstElement:any = document.querySelector('.drag-item')
    this.puzzleService.setX(firstElement.getBoundingClientRect().x)
    this.puzzleService.setY(firstElement.getBoundingClientRect().y)
  }

  dragStart(event:any) {
    event.dataTransfer.setData('startX', event.x);
    event.dataTransfer.setData('startY', event.y);
    event.target.classList.add('dragging');
  }
  
  dragOver(event:any) {
    event.preventDefault();
    event.target.classList.add('drag-over');
  }
  
  dragEnter(event:any) {
    event.preventDefault();
    event.target.classList.add('drag-over');
  }
  
  dragLeave(event:any) {
    event.target.classList.remove('drag-over');
  }
  
  drop(event:any) {
    event.preventDefault();
    var x:number = event.dataTransfer.getData('startX');
    var y:number = event.dataTransfer.getData('startY');
    this.swapPieces(x,y,event.x,event.y);
    event.target.classList.remove('drag-over');
    event.target.classList.remove('dragging');
  }

  dragEnd(event: any) {
    event.target.classList.remove('drag-over');
    event.target.classList.remove('dragging');
  }

  swapPieces(x:number,y:number,gettedX:number,gettedY:number):void{
    var oldX = Math.floor((x - this.puzzleService.getX())/100);
    var oldY = Math.floor((y - this.puzzleService.getY())/100);
    var newX = Math.floor((gettedX - this.puzzleService.getX())/100);
    var newY = Math.floor((gettedY - this.puzzleService.getY())/100);
    var firstPiece = this.puzzleService.currentPuzzles.find(puzzle => puzzle.currentPosition == (oldY*4 + oldX));
    var secondPiece = this.puzzleService.currentPuzzles.find(puzzle => puzzle.currentPosition == (newY*4 + newX));
    var temporaryHolder = firstPiece.currentPosition;
    firstPiece.currentPosition = secondPiece.currentPosition;
    secondPiece.currentPosition = temporaryHolder;
    this.puzzleService.sortPuzzles();
    this.puzzleService.isValid();
  }

  imageDataChanged(event:any) {
    if(event.target.files){
      const file = event.target.files[0];

      const fileHandle: FileHandle = {
        file: file,
        url: this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(file))
      }
      
      this.fileDropped(fileHandle);
    }
  }

  fileDropped(fileHandle: FileHandle){
    this.imageService.saveImage(fileHandle.file).subscribe(
      () => {
        this.getImage(4);
        alert('Image uploaded successfully');
      },
      (error) => {
        console.error('Failed to upload image', error);
        alert('Failed to upload image');
      }
    );
  }

  getPuzzleRows(){
    return this.puzzleService.currentPuzzles;
  }

  processImages(piece:any){
          const contentType = piece.contentType;
          const imageBlob = piece.imageData;
          const imageData = atob(imageBlob);
          const bytes = new Uint8Array(imageData.length);

          for (let i = 0; i < imageData.length; ++i) {
            bytes[i] = imageData.charCodeAt(i);
          }

          const blob = new Blob([bytes], { type: contentType });
          const fileName = piece.name;
          const file = new File([blob], fileName, { type: contentType });

          const puzzlePiece: PuzzlePiece = {
            file: file,
            url: this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(file)),
            rotation:piece.rotation,
            contentType:piece.contentType,
            name:piece.name,
            position:piece.position,
            currentPosition:piece.position
          }
          return puzzlePiece;
  }

  getImage(cuts:number){
    this.imageService.getFirstImage(cuts).subscribe((data:any) => {
      this.puzzleService.currentPuzzles = [];
      let counter = 0
      for(let piece of data){
        this.puzzleService.currentPuzzles.push(this.processImages(piece))
      }
    }, (error: any) => {
      console.error(error);
    });
    setTimeout(() => {
      this.prepareData();
    }, 1000)
  }

  processSolve(piece:any, name:string, contentType:string){
    const imageData = atob(piece);
    const bytes = new Uint8Array(imageData.length);

    for (let i = 0; i < imageData.length; ++i) {
      bytes[i] = imageData.charCodeAt(i);
    }

    const blob = new Blob([bytes], { type: contentType });
    const fileName = name;
    const file = new File([blob], fileName, { type: contentType });

    const solvePiece: FileHandle = {
      file: file,
      url: this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(file))
    }
    return solvePiece;
}


  getSolve(){
    this.imageService.getSolve().subscribe((data:any) => {
      this.solve = [];
      let counter = 0
      for(let piece of data.puzzlePieces){
        this.solve.push(this.processSolve(piece, "solve" + counter, data.contentType));
        counter++;
      }
    }, (error: any) => {
      console.error(error);
    });
  }

  getFileExtension(contentType: string): string {
    switch (contentType) {
      case 'image/jpeg':
        return '.jpg';
      case 'image/png':
        return '.png';
      default:
        return '';
    }
  }
}
  