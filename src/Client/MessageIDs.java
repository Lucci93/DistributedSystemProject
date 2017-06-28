package Client;

public enum MessageIDs {
    // move player
    MOVE_PLAYER,
    // at start search a coordinates to place player
    FIND_COORDINATES,
    // bomb thrown alert
    THROWN_BOMB,
    // player added to list
    ADD_PLAYER,
    // pass the token to the next player
    MOVE_TOKEN,
    // alert players of die of one of them
    DEATH_PLAYER,
    // explosion of the bomb
    BOMB_EXPLOSION
}
