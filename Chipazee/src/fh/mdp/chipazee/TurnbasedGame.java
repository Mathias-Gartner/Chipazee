package fh.mdp.chipazee;

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
}
