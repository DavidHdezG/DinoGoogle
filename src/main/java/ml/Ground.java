package ml;

import processing.core.PApplet;

public class Ground{
    float posX = Game.processing.width;
    float posY = Game.processing.height - PApplet.floor(Game.processing.random(Game.processing.groundHeight - 20, Game.processing.groundHeight + 30));
    int w = PApplet.floor(Game.processing.random(1, 10));

    Ground(){
    }

    void show(){
        Game.processing.stroke(0);
        Game.processing.strokeWeight(3);
        Game.processing.line(posX, posY, posX + w, posY);
    }

    void move(float speed){
        posX -= speed;
    }
}