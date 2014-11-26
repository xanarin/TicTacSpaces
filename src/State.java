public abstract interface State {

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

    public abstract void transferState(GameState donor);

    public abstract boolean removeChild(GameState child);

    public abstract GameState[] getChildren();

    public abstract byte[][] getState();

    public abstract void makeMove(int yCoor, int xCoor, byte value);

    public abstract int getScore();

    public abstract boolean isFinished();

    public abstract Space getWinner();

}
