package fh.mdp.chipazee;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import android.app.AlertDialog;
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
			// TODO Since declaring them in XML was not
			// working, I got desparate, no idea if this
			// will work.
			addButtonListeners();
		} else {
			myTurn.turnNumber += 1;
		}
	}

	public void goDone(View view) {
		Iterator<Integer> itAttempt = attempt.iterator();
		Iterator<Integer> itLast = game.getmTurn().colours.iterator();

		boolean ok = false;
		if (attempt.size() == game.getmTurn().colours.size() + 1) {
			int count = 0;
			while (itLast.hasNext()) {
				if (itAttempt.next() != itLast.next())
					break;

				count += 1;
			}
			if (count == game.getmTurn().colours.size())
				ok = true;
		}

		if (ok) {
			game.getmTurn().colours.add(attempt.peekLast());
			showWarning("Success!", "Your attempt was fine, this time.");
		} else {
			showWarning("Failure!", "Your attempt was futile. You lost.");
		}

		attempt.clear();
		game.takeTurn(game.getmTurn()); // TODO End game if not OK? We could let next player try.
	}

	private Deque<Integer> attempt = new ArrayDeque<Integer>();

	private void addColour(int i) {
		attempt.add(i);
	}

	private void addButtonListeners() {
		Button b1 = (Button) findViewById(R.id.b1);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(1);
			}
		});
		Button b2 = (Button) findViewById(R.id.b2);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(2);
			}
		});
		Button b3 = (Button) findViewById(R.id.b3);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(3);
			}
		});
		Button b4 = (Button) findViewById(R.id.b4);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(4);
			}
		});
		Button b5 = (Button) findViewById(R.id.b5);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(5);
			}
		});
		Button b6 = (Button) findViewById(R.id.b6);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(6);
			}
		});
		Button b7 = (Button) findViewById(R.id.b7);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(7);
			}
		});
		Button b8 = (Button) findViewById(R.id.b8);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(8);
			}
		});
		Button b9 = (Button) findViewById(R.id.b9);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addColour(9);
			}
		});
	}

	private void showWarning(String title, String message) { // TODO Copied from
																// welcomeActivity.
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(title).setMessage(message);

		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", null);

		// create alert dialog
		AlertDialog mAlertDialog = alertDialogBuilder.create();

		// show it
		mAlertDialog.show();
	}
}
