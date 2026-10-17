package dev.kodysimpson.freezetag;

// The phase the game is in. It's always exactly one of these, so there's no way to be
// "counting down" and "running" at the same time, as there could be with two booleans.
public enum GameState {
    IDLE,      // No round. /freezetag start is allowed.
    COUNTDOWN, // Everyone is held in place until the countdown ends.
    RUNNING    // The tagger is chasing the runners.
}
