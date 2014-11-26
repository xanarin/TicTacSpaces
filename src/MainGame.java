import java.util.Scanner;

public class MainGame {

    public static void fillNodes(GameState root, boolean playerTurn) {
        byte[][] data = root.getState();

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (data[i][j] == GameState.Space.EMPTY.getCode()) {
                    GameState child = new GameState();
                    child.transferState(root);
                    root.addChild(child);
                    if (playerTurn) {
                        child.setPosition(i, j,
                                GameState.Space.PLAYER.getCode());
                    } else {
                        child.setPosition(i, j,
                                GameState.Space.OPPONENT.getCode());
                    }

                    if (!child.isFinished()) {
                        fillNodes(child, !playerTurn);
                    }
                }
            }
        }

    }

    public static void printChildren(GameState root) {
        if (root.getChildren().length == 0) {
            System.out.println(root.toString());
        } else {
            for (GameState waywardSon : root.getChildren()) {
                printChildren(waywardSon);
            }
        }
    }

    public static int evalMove(GameState root) {
        return 0;
    }

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        boolean gameFinished = false;

        //Generate Game Board
        GameState emptyState = new GameState();

        for (int i = 0; i < emptyState.getState().length; i++) {
            for (int j = 0; j < emptyState.getState()[0].length; j++) {
                emptyState.setPosition(i, j, GameState.Space.EMPTY.getCode());
            }
        }

        System.out.println("Loading...");
        //Generate state tree
        fillNodes(emptyState, true);

        //Aliased variable to keep track of current game state
        GameState currentState = emptyState;

        //Begin game
        System.out.println("I'll go first.");
        System.out.println(currentState);

        while (!gameFinished) { //Master game loop
            System.out.println("Thinking...");

            int myMove = evalMove(currentState);
            currentState = currentState.getChildren()[myMove];

            System.out.println(currentState);
            System.out.println("Score: " + currentState.getScore());

            if (currentState.isFinished()) { //Check if finished
                switch (currentState.getWinner()) {
                    case PLAYER:
                        System.out.println("I won! :)");
                        break;
                    case OPPONENT:
                        System.out.println("You won. :(");
                        break;
                    case EMPTY:
                        System.out.println("Nobody won! :o");
                        break;
                }
                gameFinished = true;
                continue;
            }

            System.out.print("Enter your move: ");
            int nextMove = 0;
            while (!(nextMove <= 9 && nextMove > 0)) {
                nextMove = reader.nextInt();
            }
            int y = (nextMove - 1) / 3;
            int x = (nextMove - 1) % 3;

            for (GameState child : currentState.getChildren()) {
                if (child.getState()[y][x] == GameState.Space.OPPONENT
                        .getCode()) {
                    currentState = child;
                    break;
                }
            }
            System.out.println(currentState);
            System.out.println("Score: " + currentState.getScore());

            if (currentState.isFinished()) { //Check if finished
                switch (currentState.getWinner()) {
                    case PLAYER:
                        System.out.println("I won! :)");
                        break;
                    case OPPONENT:
                        System.out.println("You won. :(");
                        break;
                    case EMPTY:
                        System.out.println("Nobody won! :o");
                        break;
                }
                gameFinished = true;
                continue;
            }
        }

        reader.close();
    }
}
