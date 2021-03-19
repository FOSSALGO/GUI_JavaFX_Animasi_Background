package fosalgo;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class VisualLoop extends Application implements Runnable {

    //Loop Parameters
    private final static int MAX_FPS = 60;
    private final static int MAX_FRAME_SKIPS = 10;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    //Thread
    private Thread thread;
    private volatile boolean running = true;

    //Canvas
    Canvas canvas = null;

    //KEYBOARD HANDLER
    ArrayList<String> inputKeyboard = new ArrayList<String>();

    //variabel untuk komponen-komponen canvas
    float xo = 212;
    float yo = 540;
    double d = 60.0 * Math.PI / 180.0;
    double delta_x = 2 * Math.cos(d);
    double delta_y = 2 * Math.sin(d);
    Image imageOto = new Image(this.getClass().getResourceAsStream("OTO.png"));
    Image imageBackground = new Image(this.getClass().getResourceAsStream("backgroundOTO.png"));
    
    public VisualLoop() {
        resume();
    }

    //THREAD
    private void resume() {
        reset();
        thread = new Thread(this);
        running = true;
        thread.start();
    }

    //THREAD
    private void pause() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //LOOP
    private void reset() {

    }

    //LOOP
    private void update() {
        if (inputKeyboard.contains("F")) {
            xo -= delta_x;
            yo -= delta_y;
        } else if (inputKeyboard.contains("B")) {
            xo += delta_x;
            yo += delta_y;
        }
    }

    //LOOP
    private void draw() {
        try {
            if (canvas != null) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(imageBackground, 0, 0);
                gc.drawImage(imageOto, xo, yo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long beginTime;
        long timeDiff;
        int sleepTime = 0;
        int framesSkipped;
        //LOOP WHILE running = true; 
        while (running) {
            try {
                synchronized (this) {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    update();
                    draw();
                }
                timeDiff = System.currentTimeMillis() - beginTime;
                sleepTime = (int) (FRAME_PERIOD - timeDiff);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                }
                while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    update();
                    sleepTime += FRAME_PERIOD;
                    framesSkipped++;
                }
            } finally {

            }
        }
    }

    public void setHandling(Scene scene) {
        //HANDLING KEYBOARD EVENT
        scene.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String code = e.getCode().toString();
                if (!inputKeyboard.contains(code)) {
                    inputKeyboard.add(code);
                    System.out.println(code);
                }
            }
        }
        );

        scene.setOnKeyReleased(
                new EventHandler<KeyEvent>() {
            public void handle(KeyEvent e) {
                String code = e.getCode().toString();
                inputKeyboard.remove(code);
            }
        }
        );

        //HANDLING MOUSE EVENT
        scene.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {

            }
        }
        );
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        setHandling(scene);

        canvas = new Canvas(496, 700);
        root.getChildren().add(canvas);
        
        Button btn = new Button();
        btn.setText("FOSALGO - Indonesia");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        root.getChildren().addAll(btn);
        scene.getStylesheets().addAll(this.getClass().getResource("CSSTheme.css").toExternalForm());
        primaryStage.setTitle("FOSALGO - Indonesia");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}