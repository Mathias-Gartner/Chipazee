package fh.mdp.chipazee;

import android.content.Intent;

public interface TurnbasedGame {
	void GameActivityCreated();
	void GameActivityStarted();
	void GameActivityStopped();
	
	void startNewGame(int maxPlayers);
	void checkGames();
	
	void takeTurn(Turn turn);
	void finishMatch();
	void cancelMatch();
	void leaveMatch();
	void unlockAchievement(String achievementID);
	
	void signOut();
	void beginUserInitiatedSignIn();
	boolean isSignedIn();
	
	void onActivityResult(int request, int response, Intent data);
}
