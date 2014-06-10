package fh.mdp.chipazee;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GooglePlayActivity extends BaseGameActivity {

	TurnbasedGame game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		game = new GooglePlayTurnbasedGame(this, new TurnHandler() {

			@Override
			public void handleTurn(boolean firstTurn) {
				doTurn(firstTurn);
			}
		});

		TurnbasedGameSingleton.setGame(game);
		game.GameActivityCreated();
	}

	@Override
	protected void onStart() {
		super.onStart();
		game.GameActivityStarted();

		if (game.isSignedIn()) {
			findViewById(R.id.sign_in_button).setVisibility(View.GONE);
			findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
			findViewById(R.id.sign_out_button).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		game.GameActivityStopped();
	}

	public void goNewGame(View view) {
		game.startNewGame(8);
	}

	public void goCheckGames(View view) {
		game.checkGames();
	}

	public void signIn(View view) {
		game.beginUserInitiatedSignIn();
		findViewById(R.id.sign_in_button).setVisibility(View.GONE);
		findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
	}

	public void signOut(View view) {
		game.signOut();
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_out_button).setVisibility(View.GONE);
	}

	public void doTurn(boolean firstTurn) {
		Turn myTurn = game.getmTurn();

		if (firstTurn) {
			setContentView(R.layout.play);
		} else {
			myTurn.turnNumber += 1;
		}
	}

	public void goDone(View view) {
		game.takeTurn(game.getmTurn());
	}
	

}
