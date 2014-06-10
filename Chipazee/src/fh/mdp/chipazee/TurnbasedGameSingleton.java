package fh.mdp.chipazee;

public class TurnbasedGameSingleton {

	private static TurnbasedGame game;
	
	public static TurnbasedGame getGame()
	{
		return game;
	}
	
	public static void setGame(TurnbasedGame game)
	{
		TurnbasedGameSingleton.game = game;
	}
	
}
