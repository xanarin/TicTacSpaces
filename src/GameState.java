import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameState {

    private byte[][] state;
    private ArrayDeque<GameState> children;
    private boolean finished;
    private byte winner;

    enum Space {
        EMPTY(' '), PLAYER('X'), OPPONENT('O');

        private final byte code;

        Space(char code) {
            this.code = (byte) code;
        }

        public byte getCode() {
            return this.code;
        }

        public static Space valueOf(char c) {
            switch (c) {
                case ' ':
                    return EMPTY;
                case 'X':
                    return PLAYER;
                case 'O':
                    return OPPONENT;
                default:
                    return null;
            }
        }

    }

    public GameState() {
        this.state = new byte[3][3];
        this.children = new ArrayDeque<GameState>();

    }

    public void transferState(GameState donor) {
        byte[][] donorData = donor.getState();
        for (int i = 0; i < donorData.length; i++) {
            for (int j = 0; j < donorData[0].length; j++) {
                this.state[i][j] = donorData[i][j];
            }
        }
    }

    public void addChild(GameState child) {
        this.children.add(child);
    }

    public boolean removeChild(GameState child) {
        assert this.children.contains(child);

        return this.children.remove(child);
    }

    public GameState[] getChildren() {
        return this.children.toArray(new GameState[0]);
    }

    public byte[][] getState() {
        return this.state.clone();
    }

    public void setState(byte[][] newState) {
        this.state = newState.clone();
    }

    public void setPosition(int yCoor, int xCoor, byte value) {
        this.state[yCoor][xCoor] = value;
    }

    /**
     *
     * @param triplet
     *            Triplet to be evaluated
     * @param finished
     *            Whether this game state is finished
     *
     * @replace finished
     * @return
     */
    private static int evalTriplet(byte[] triplet, AtomicBoolean finished) {
        assert triplet.length == 3;

        final int two = 10;
        final int one = 1;

        int eval = 0;

        if ((triplet[0] == triplet[1]) && (triplet[1] == triplet[2])
                && (triplet[0] != GameState.Space.EMPTY.getCode())) {
            if (triplet[0] == GameState.Space.PLAYER.getCode()) {
                eval = Integer.MAX_VALUE;
            } else {
                eval = Integer.MIN_VALUE;
            }
            finished.set(true);

        } else if ((triplet[0] == triplet[1])
                && (triplet[0] != GameState.Space.EMPTY.getCode())) {
            if (triplet[0] == GameState.Space.PLAYER.getCode()) {
                eval += two;
                eval -= one;
            } else {
                eval -= two;
                eval += one;
            }
        } else if ((triplet[1] == triplet[2])
                && (triplet[1] != GameState.Space.EMPTY.getCode())) {
            if (triplet[1] == GameState.Space.PLAYER.getCode()) {
                eval += two;
                eval -= one;
            } else {
                eval -= two;
                eval += one;
            }
        } else {
            for (int i = 0; i < triplet.length; i++) {
                if (triplet[i] == GameState.Space.PLAYER.getCode()) {
                    eval += one;
                } else if (triplet[i] == GameState.Space.OPPONENT.getCode()) {
                    eval -= one;
                }
            }
        }

        return eval;
    }

    public int getScore(AtomicBoolean finished) {
        int score = 0;
        //Horizontal Summation
        for (int i = 0; i < this.state.length; i++) {
            score += evalTriplet(this.state[i], finished);
        }

        //Vertical Summation
        for (int j = 0; j < this.state[0].length; j++) {
            byte[] asTrip = new byte[3];
            for (int i = 0; i < this.state.length; i++) {
                asTrip[i] = this.state[i][j];
            }
            score += evalTriplet(asTrip, finished);
        }

        //Diagonal Summation
        byte[] asTrip = new byte[3];
        for (int x = 0; x < this.state.length; x++) {
            asTrip[x] = this.state[x][x];
        }
        score += evalTriplet(asTrip, finished);

        asTrip = new byte[3];
        for (int x = 0; x < this.state.length; x++) {
            asTrip[x] = this.state[x][2 - x];
        }
        score += evalTriplet(asTrip, finished);

        //Check if all spaces are full
        boolean allFull = true;
        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[0].length; j++) {
                if (this.state[i][j] == GameState.Space.EMPTY.getCode()) {
                    allFull = false;
                }
            }
        }
        finished.set(finished.get() || allFull);

        return score;
    }

    public int getScore() {
        AtomicBoolean throwaway = new AtomicBoolean();
        int score = this.getScore(throwaway);
        return score;
    }

    @Override
    public String toString() {
        String toString = "";

        for (int i = 0; i < this.state.length; i++) {
            for (int j = 0; j < this.state[0].length; j++) {
                toString += (char) this.state[i][j];
                if (j + 1 < this.state[0].length) {
                    toString += " , ";
                }
            }
            toString += "\n";
        }

        return toString;
    }

    public boolean isFinished() {
        AtomicBoolean isFinished = new AtomicBoolean();
        isFinished.set(false);
        int score = this.getScore(isFinished);

        if (isFinished.get()) {
            if (score > Integer.MAX_VALUE / 2) {
                //Player wins
                this.winner = GameState.Space.PLAYER.getCode();
            } else if (score < Integer.MIN_VALUE / 2) {
                //Opponent wins
                this.winner = GameState.Space.OPPONENT.getCode();
            } else {
                //Cat-game
                this.winner = GameState.Space.EMPTY.getCode();
            }
        }

        this.finished = isFinished.get();

        return this.finished;
    }

    public Space getWinner() {
        assert this.finished;

        return GameState.Space.valueOf((char) this.winner);
    }
}
