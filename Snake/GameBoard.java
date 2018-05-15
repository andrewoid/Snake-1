import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;




/**
 * Represents the environment where the Snake moves a food spawns. <br/>
 * There are some special rules as to how the Snake can move. If the Snake's
 * size is 1, it can move in any direction. If the Snake's size is greater than
 * 1, it cannot move 180 degrees. Example: if the Snake is moving right, it
 * cannot immediately change its direction to left because it would run into
 * itself.
 */

class GameBoard  {

    private Square food;
    private Square poison;
    private Snake snake;
    private Square[] rock = new Square[5];
    private int score = 0;
	private BufferedImage left;
	private BufferedImage right;
	private BufferedImage up;
	private BufferedImage down;
	
	
    /**
     * Keep track of the last move so that the Snake cannot do 180 degree turns,
     * only 90 degree turns.
     */
    private Direction movement = Direction.DOWN;
    private Direction lastMove = movement;

    /**
     * Constructs the board.
     */
    GameBoard () {
        this.snake = new Snake();
    	addEyeMovement();

        newFood();
        newPoison();
        createRocks();
        update();
    }

    /**
     * Move the Snake.
     */
    void update () {
        moveSnake();
        
    }

    /**
     * Creates food at a random location. Only one piece of food can be spawned at a time.
     */
    private void newFood () {
        Random rX = new Random();
        Random rY = new Random();
        food = new Square(
                Square.Entity.Food,
                rX.nextInt(Properties.BOARD_COLUMNS),
                rY.nextInt(Properties.BOARD_ROWS));

        // If food is spawned inside the snake, try spawning it elsewhere.
        if (snake.contains(food)) {
            newFood();
        }
    }
    

	/**
	 * Creates poison at a random location. Only one piece of poison can be spawned at 
	 * a time. 
	 */
	private void newPoison() {
		Random rX = new Random();
		Random rY = new Random();
		poison = new Square(Square.Entity.Poison, rX.nextInt(Properties.BOARD_COLUMNS), rY.nextInt(Properties.BOARD_ROWS));
		
		//If poison is spawned inside the snake, try spawning elsewhere.
		if (snake.contains(poison)) {
			newPoison();
		}
	}

    /**
	 * Creates rocks at random locations.
	 */
	private void createRocks() {
		Random rand = new Random();
		Square sq;
		for (int i = 0; i < 5; i++) {
			do {
				sq = new Square(Square.Entity.Rock, rand.nextInt(Properties.BOARD_COLUMNS),
						rand.nextInt(Properties.BOARD_ROWS));
			} while (snake.contains(sq));

			rock[i] = sq;
		}

	}

    /**
     * Sets the direction of the Snake to go left.
     */
    void directionLeft () {
        if (lastMove != Direction.RIGHT || getSnakeSize() == 1) {
            movement = Direction.LEFT;
            
        }
    }

    /**
     * Sets the direction of the Snake to go right.
     */
    void directionRight () {
        if (lastMove != Direction.LEFT || getSnakeSize() == 1) {
            movement = Direction.RIGHT;
        }
    }

    /**
     * Sets the direction of the Snake to go up.
     */
    void directionUp () {
        if (lastMove != Direction.DOWN || getSnakeSize() == 1) {
            movement = Direction.UP;
            
        }
    }

    /**
     * Sets the direction of the Snake to go down.
     */
    void directionDown () {
        if (lastMove != Direction.UP || getSnakeSize() == 1) {
            movement = Direction.DOWN;
        }
    }

    /**
     * Moves the Snake one square, according to its direction.
     */
    private void moveSnake () {

        if (movement == Direction.LEFT) {
            moveSnakeLeft();
        } else if (movement == Direction.RIGHT) {
            moveSnakeRight();
        } else if (movement == Direction.UP) {
            moveSnakeUp();
        } else if (movement == Direction.DOWN) {
            moveSnakeDown();
        }

        lastMove = movement;
    }

    private void moveSnakeLeft () {
        if (!snake.moveLeft()) { // Check to see if the Snake has run into itself.
            exit();
        }
        checkBounds();
        checkRock();
        checkIfAteFood();
        checkIfAtePoison();
        movement = Direction.LEFT;
    }

    private void moveSnakeRight () {
        if (!snake.moveRight()) { // Check to see if the Snake has run into itself.
            exit();
        }
        checkBounds();
        checkRock();
        checkIfAteFood();
        checkIfAtePoison();
        movement = Direction.RIGHT;
    }

    private void moveSnakeUp () {
        if (!snake.moveUp()) { // Check to see if the Snake has run into itself.
            exit();
        }
        checkBounds();
        checkRock();
        checkIfAteFood();
        checkIfAtePoison();
        movement = Direction.UP;
    }

    private void moveSnakeDown () {
        if (!snake.moveDown()) { // Check to see if the Snake has run into itself.
            exit();
        }
        checkBounds();
        checkRock();
        checkIfAteFood();
        checkIfAtePoison();
        movement = Direction.DOWN;
    }

    private void checkBounds () {
        Square sq = snake.getHead();

        boolean tooFarLeft = sq.getX() < 0;
        boolean tooFarRight = sq.getX() >= Properties.BOARD_COLUMNS;
        boolean tooFarUp = sq.getY() < 0;
        boolean tooFarDown = sq.getY() >= Properties.BOARD_ROWS;

        boolean outOfBounds = tooFarLeft || tooFarRight || tooFarUp || tooFarDown;

        if (outOfBounds) {
            exit();
        }
    }
    
	private void checkRock() {
		Square sq = snake.getHead();
		boolean hitRock1 = sq.equals(rock[0]);
		boolean hitRock2 = sq.equals(rock[1]);
		boolean hitRock3 = sq.equals(rock[2]);
		boolean hitRock4 = sq.equals(rock[3]);
		boolean hitRock5 = sq.equals(rock[4]);

		if (hitRock1 || hitRock2 || hitRock3 || hitRock4 || hitRock5) {
			exit();
		}
	}

    private void checkIfAteFood() {
        if (isSnakeOnFood()) {
            growSnake();
            newFood();
        }
    }

	private void checkIfAtePoison() {
		if(isSnakeOnPoison()) {
			cutSnake();
			newPoison();
		}
	}
    
    private int getSnakeSize () {
        return snake.getSize();
    }

    private void exit () {
        System.out.println("Final Score: " + getScore());
        System.exit(0);
    }

    int getScore () {
        return score;
    }

    private boolean isSnakeOnFood () {
        return snake.getHead().equals(food);
    }

    private boolean isSnakeOnPoison() {
		return snake.getHead().equals(poison);
	}
    
    private void growSnake () {
        snake.grow();
        score += 10;
    }
    
	private void cutSnake() {
		snake.cutDown();
		score -= 10;
	}

    void paint (Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintSnake(g);
        paintFood(g);
        paintPoison(g);
        paintRocks(g);
		addEyes(g);
    }

    private void paintSnake (Graphics2D g) {
        int x, y;
        int corner = Properties.SQUARE_SIZE / 3;

        for (Square sq : snake) {

            x = sq.getX() * Properties.SQUARE_SIZE;
            y = sq.getY() * Properties.SQUARE_SIZE;

            g.setColor(Properties.snakeColor);
            g.fillRoundRect(x + 1, y + 1, Properties.SQUARE_SIZE - 2,
                    Properties.SQUARE_SIZE - 2, corner, corner);

        }
    }
    
    private void addEyeMovement() {
    	
    	try {    			
    		left = ImageIO.read(new File("gifs/eyes_left.png"));
    		right = ImageIO.read(new File("gifs/eyes_blink.png"));
    		up = ImageIO.read(new File("gifs/eyes_up.png"));
    		down = ImageIO.read(new File("gifs/eyes_down.png"));    		

           } catch (IOException e) {
    			e.printStackTrace();
           }
    
    }
   
    private BufferedImage getEyesImage () {
    	if (movement == Direction.LEFT){
    		return left;	
    	}
    	if(movement == Direction.RIGHT) {
    		return right;
    	}
    	if (movement == Direction.UP) {
    		return up;
    	}
    	if (movement == Direction.DOWN) {
    		return down;
    	}
    	return up;
    }
    
    
    private void addEyes(Graphics2D g) {
    	BufferedImage i = getEyesImage();
		int x = snake.getHead().getX() * Properties.SQUARE_SIZE + 3;
		int y = snake.getHead().getY() * Properties.SQUARE_SIZE + 3;
		g.drawImage(i, x, y, null);

	
	}	

    private void paintFood (Graphics2D g) {
        int x = food.getX() * Properties.SQUARE_SIZE;
        int y = food.getY() * Properties.SQUARE_SIZE;
        int corner = Properties.SQUARE_SIZE / 3;

        g.setColor(Properties.foodColor);
        g.fillRoundRect(x + 1, y + 1, Properties.SQUARE_SIZE - 2,
                Properties.SQUARE_SIZE - 2, corner, corner);
    }
    
	private void paintRocks(Graphics2D g) {
		for (int i = 0; i < 5; i++) {
			int x = rock[i].getX() * Properties.SQUARE_SIZE;
			int y = rock[i].getY() * Properties.SQUARE_SIZE;
			int corner = Properties.SQUARE_SIZE / 3;

			g.setColor(Properties.rockColor);
			g.fillRoundRect(x + 1, y + 1, Properties.SQUARE_SIZE - 2, Properties.SQUARE_SIZE - 2, corner, corner);
		}
	}
	
	private void paintPoison(Graphics2D g) {
		int x = poison.getX() * Properties.SQUARE_SIZE;
		int y = poison.getY() * Properties.SQUARE_SIZE;
		int corner = Properties.SQUARE_SIZE / 3;

		g.setColor(Properties.poisonColor);
		g.fillRoundRect(x + 1, y + 1, Properties.SQUARE_SIZE - 2, Properties.SQUARE_SIZE - 2, corner, corner);
	}
    
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (int y = 0; y < Properties.BOARD_ROWS; y++) {
			for (int x = 0; x < Properties.BOARD_COLUMNS; x++) {
				Square sq = new Square(x, y);

				if (snake.contains(sq)) {
					sb.append("S");
				} else if (food.equals(sq)) {
					sb.append("F");
				} else {
					sb.append("-");
				}

				sb.append(" ");

			}
			sb.append("\n");
		}

		return new String(sb);
	}

   

}