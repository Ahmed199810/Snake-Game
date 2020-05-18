package my.snake.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
/**
 *
 * @author Ahmed
 */
public class SnakeGame extends Application{
    
    static int speed = 150;
    static int food_color = 0;
    static int width = 20;
    static int height = 20;
    static int food_x = 0;
    static int food_y = 0;
    static int cell_size = 25;
    static List<CellPoint> snake_cells = new ArrayList<>();
    static Dir dir = Dir.left;
    static boolean game_over = false;
    static Random random = new Random();
    static Label score = new Label("Score : " + 0);
    
    public enum Dir {
        left, right, up, down
    }

	
    public static class CellPoint {
        int x;
        int y;
        
        public CellPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
        

	
    public void start(Stage primaryStage) {
        try {
            locate_new_food();
            VBox root = new VBox();
            Canvas c = new Canvas(width * cell_size, height * cell_size);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);
            
            
            score.setFont(new Font("", 25));
            score.setPadding(new Insets(10));
            HBox score_box = new HBox(score);
            root.getChildren().add(score_box);
            
            new AnimationTimer() {
                public void handle(long now) {
                    update_ui(gc);
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SnakeGame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
            
            Scene scene = new Scene(root, width * cell_size, height * cell_size + 50);
            
            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.W) {
                    dir = Dir.up;
                }
                if (key.getCode() == KeyCode.A) {
                    dir = Dir.left;
                }
                if (key.getCode() == KeyCode.S) {
                    dir = Dir.down;
                }
                if (key.getCode() == KeyCode.D) {
                    dir = Dir.right;
                }
            });
            
            // initialize snake parts
            snake_cells.add(new CellPoint((width * cell_size)/2, height/2));
            snake_cells.add(new CellPoint((width * cell_size)/2 + 1 * cell_size, height/2));
            snake_cells.add(new CellPoint((width * cell_size)/2 - 1 * cell_size, height/2));
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("MY SNAKE GAME");
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    // tick
    public static void update_ui(GraphicsContext gc) {
        if (game_over) {
            gameOver(gc);
            return;
        }
        
        // move snake
        for(int i = snake_cells.size() - 1; i >= 1; i--){
            snake_cells.get(i).x = snake_cells.get(i-1).x;
            snake_cells.get(i).y = snake_cells.get(i-1).y;
        }
        
        // control snake directions
        switch(dir){
            case right:
                snake_cells.get(0).x +=cell_size;
                break;
            case left:
                snake_cells.get(0).x -=cell_size;
                break;
            case up:
                snake_cells.get(0).y--;
                break;
            case down:
                snake_cells.get(0).y++;
                break;
        }
        
        // walls collisions
        chck_walls_collisions();
        
        // eat foods
        if(food_x == snake_cells.get(0).x/cell_size && food_y == snake_cells.get(0).y){
            snake_cells.add(new CellPoint(-1, -1));
            locate_new_food();
        }
        score.setText("Score : " + (snake_cells.size() - 3));
        // destroy sanke by self collision
        for(int i = 1; i < snake_cells.size(); i++){
            if(snake_cells.get(0).x == snake_cells.get(i).x && snake_cells.get(0).y == snake_cells.get(i).y){
                game_over = true;
            }
        }
        
        // draw game shapes
        draw_game_shapes(gc);
	
    }

    // food
    public static void locate_new_food() {
        food_x = random.nextInt(width);
        food_y = random.nextInt(height);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private static void draw_game_shapes(GraphicsContext gc) {
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cell_size, height * cell_size);
        
        // draw food
        gc.setFill(Color.RED);
        gc.fillOval(food_x * cell_size, food_y * cell_size, cell_size, cell_size);
        
        // draw snake snake
        for (CellPoint c : snake_cells) {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(c.x, c.y * cell_size, cell_size - 1, cell_size - 1);
            gc.setFill(Color.BLUE);
            gc.fillRect(c.x, c.y * cell_size, cell_size - 2, cell_size - 2);
        } 
    }
    
    private static void chck_walls_collisions() {
        if(snake_cells.get(0).x < 0 || snake_cells.get(0).x > width * cell_size - cell_size || snake_cells.get(0).y < 0 || snake_cells.get(0).y > height - 1){
            game_over = true;
        }
    }
    
    
    private static void gameOver(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cell_size, height * cell_size);
        gc.setFill(Color.RED);
        gc.setFont(new Font("", 50));
        gc.fillText("Game Over", 120, 250); 
    }
    

}