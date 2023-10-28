package ml;

import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {

        String[] procArgs = {"Game"};
        Game app = new Game();
        PApplet.runSketch(procArgs, app);
    }
}