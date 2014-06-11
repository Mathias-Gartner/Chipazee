package fh.mdp.chipazee;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GooglePlayTurnbasedGame implements TurnbasedGame,
		GameHelperListener {
	public static final String TAG = "DrawingGooglePlayTurnBasedGames";

	// For our intents
	final static int RC_NEW_GAME = 10000;
	final static int RC_CHECK_GAMES = 10001;
	final static int REQUEST_ACHIEVEMENTS = 10002;

	protected GameHelper mHelper = null;
	protected TurnBasedMatch mMatch = null;
	protected BaseGameActivity mActivity = null;
	protected Turn mTurn = null;
	protected TurnHandler mTurnHandler = null;

	public GooglePlayTurnbasedGame(BaseGameActivity activity,
			TurnHandler turnHandler) {
		if (activity == null)
			throw new IllegalArgumentException("activity cannot be null");
		if (turnHandler == null)
			throw new IllegalArgumentException("turnHandler cannot be null");

		mActivity = activity;
		mTurnHandler = turnHandler;
	}

	public Turn getTurn() {
		return mTurn;
	}

	public GameHelper getGameHelper() {
		if (mHelper == null) {
			mHelper = new GameHelper(mActivity, GameHelper.CLIENT_GAMES);
			mHelper.enableDebugLog(true);
		}
		return mHelper;
	}

	@Override
	public void GameActivityCreated() {
		if (mHelper == null) {
			getGameHelper();
		}
		mHelper.setup(this);
	}

	@Override
	public void GameActivityStarted() {
		mHelper.onStart(mActivity);
	}

	@Override
	public void GameActivityStopped() {
		mHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		// It's VERY IMPORTANT for you to remember to call your superclass.

		// not needed: we're getting called from our activity
		// mActivity.onActivityResult(request, response, data);

		// BaseGameActivity will not work otherwise.
		mHelper.onActivityResult(request, response, data);

		// We are coming back from the match inbox UI.
		if (request == RC_CHECK_GAMES) {
			if (response != Activity.RESULT_OK) {
				// user canceled
				return;
			}

			TurnBasedMatch match = data
					.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

			if (match != null) {
				updateMatch(match);
			} else {
				// refreshTurnsPending();
			}

			Log.d(TAG, "Match = " + match);
		}

		// We are coming back from the player selection UI, in preparation to
		// start a match.
		if (request == RC_NEW_GAME) {
			if (response != Activity.RESULT_OK) {
				// user canceled
				// refreshTurnsPending();
				return;
			}

			// get the invitee list
			final ArrayList<String> invitees = data
					.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);// GamesClient.EXTRA_PLAYERS);

			// get automatch criteria
			Bundle autoMatchCriteria = null;

			int minAutoMatchPlayers = data.getIntExtra(
					Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
			int maxAutoMatchPlayers = data.getIntExtra(
					Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

			if (minAutoMatchPlayers > 0) {
				autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
						minAutoMatchPlayers, maxAutoMatchPlayers, 0);
			} else {
				autoMatchCriteria = null;
			}

			TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
					.addInvitedPlayers(invitees)
					.setAutoMatchCriteria(autoMatchCriteria).build();

			// Start the match
			Games.TurnBasedMultiplayer
					.createMatch(mHelper.getApiClient(), tbmc);

			mTurn = new Turn();
			mTurnHandler.handleTurn(true);
			// showSpinner();
		}
	}

	@Override
	public void startNewGame(int playerCount) {
		Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(
				mHelper.getApiClient(), 1, playerCount, true);
		mActivity.startActivityForResult(intent, RC_NEW_GAME);

	}

	@Override
	public void checkGames() {
		Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mHelper
				.getApiClient());
		mActivity.startActivityForResult(intent, RC_CHECK_GAMES);
	}

	public void updateMatch(TurnBasedMatch match) {
		mMatch = match;
		// Unpack the turn data

		byte[] data = mMatch.getData();
		if (data == null) // if first turn was interrupted and is now being
							// restarted
		{
			mTurn = new Turn();
		} else {
			try {
				mTurn = Turn.deserialize(mMatch.getData());
			} catch (IllegalArgumentException e) {
				showWarning("Game invalid",
						"The reveived data was not valid. Your game has been aborted.");
				cancelMatch();
				return;
			}
		}

		int status = match.getStatus();
		int turnStatus = match.getTurnStatus();

		switch (status) {
		case TurnBasedMatch.MATCH_STATUS_CANCELED:
			showWarning("Canceled!", "This game was canceled!");
			return;
		case TurnBasedMatch.MATCH_STATUS_EXPIRED:
			showWarning("Expired!", "This game is expired.  So sad!");
			return;
		case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
			showWarning("Waiting for auto-match...",
					"We're still waiting for an automatch partner.");
			return;
		case TurnBasedMatch.MATCH_STATUS_COMPLETE:
			if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
				showWarning(
						"Complete!",
						"This game is over; someone finished it, and so did you!  There is nothing to be done.");
				break;
			}

			// Note that in this state, you must still call "Finish" yourself,
			// so we allow this to continue.
			showWarning("Complete!",
					"This game is over; someone finished it!  You can only finish it now.");

			// Show the replay of them guessing, and then you're done.
			/*
			 * if (mTurnData.guessingTurn != null) {
			 * 
			 * // Move the guessed turn to the next turn mTurnData.replayTurn =
			 * mTurnData.guessingTurn; // Need to see your replay mStateManager
			 * .transitionState(StateManager.STATE_REPLAY_METADATA); }
			 */
			return;

		}

		// OK, it's active. Check on turn status.
		switch (turnStatus) {
		case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
			// Should return results.
			showWarning("Alas...", "It's not your turn.");
			break;
		case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
			showWarning("Good inititative!",
					"Still waiting for invitations.\n\nBe patient!");

		case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
			mTurnHandler.handleTurn(false);

			/*
			 * if (mTurnData.replayTurn != null &&
			 * mTurnData.guessingTurn.guessedWord == -1) { // Need to see your
			 * replay mStateManager
			 * .transitionState(StateManager.STATE_REPLAY_METADATA); return; }
			 * 
			 * // Should only hit this on turn 1 if (mTurnData.guessingTurn !=
			 * null && mTurnData.guessingTurn.guessedWord == -1) { // Need to
			 * see your guess and you haven't guessed yet mStateManager
			 * .transitionState(StateManager.STATE_GUESSING_METADATA); return; }
			 * 
			 * // The game needs to be initiated, because A) it is a rematch, or
			 * // B) something crashed during startup and we have a null
			 * starting situation. if (mTurnData != null && mTurnData.artistTurn
			 * == null) { startMatch(match); return; }
			 */

			// Otherwise, it must be turn 1
			if (mTurn.turnNumber < 2) {
				Games.Achievements.unlock(
						mHelper.getApiClient(),
						mActivity.getResources().getString(
								R.string.achievement_started_a_game));
			}

			/*
			 * if (!skipTakeTurnUpdate) { mStateManager
			 * .transitionState(StateManager.STATE_NEW_TURN_METADATA); } else {
			 * skipTakeTurnUpdate = false; }
			 */
			return;

		}

		mTurn = null;
		// mCurrentStroke = null;
	}

	@Override
	public void takeTurn(Turn turn) {
		byte[] data = null;

		try {
			data = mTurn.serialize();
		} catch (IOException e) {
			showWarning("Game invalid",
					"Your data could not be sent. Your game has been aborted.");
			cancelMatch();
			return;
		}

		if (null == data)
			showWarning("Serialization failed",
					"An error occoured and your turn can not be finished.");
		else
			Games.TurnBasedMultiplayer.takeTurn(mHelper.getApiClient(), mMatch
					.getMatchId(), data, getNextParticipant()
					.getParticipantId());
	}

	@Override
	public void finishMatch() {
		Games.TurnBasedMultiplayer.finishMatch(mHelper.getApiClient(),
				mMatch.getMatchId());
	}

	@Override
	public void cancelMatch() {
		Games.TurnBasedMultiplayer.cancelMatch(mHelper.getApiClient(),
				mMatch.getMatchId());
	}

	@Override
	public void leaveMatch() {
		Games.TurnBasedMultiplayer.leaveMatchDuringTurn(mHelper.getApiClient(),
				mMatch.getMatchId(), getNextParticipant().getParticipantId());

	}

	@Override
	public void unlockAchievement(String achievementID) {
		Games.Achievements.unlock(mHelper.getApiClient(), achievementID);
	}

	@Override
	public boolean isSignedIn() {
		return mHelper.isSignedIn();
	}

	@Override
	public void beginUserInitiatedSignIn() {
		mHelper.beginUserInitiatedSignIn();
	}

	@Override
	public void signOut() {
		mHelper.signOut();
	}

	@Override
	public void onSignInFailed() {
		showWarning("Google+ login", "Sign in failed. Please try again.");
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub

	}

	public void showWarning(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mActivity);

		// set title
		alertDialogBuilder.setTitle(title).setMessage(message);

		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", null);

		// create alert dialog
		AlertDialog mAlertDialog = alertDialogBuilder.create();

		// show it
		mAlertDialog.show();
	}

	// Utility functions
	public Participant getParticipantForPlayerId(String playerId) {
		for (Participant part : mMatch.getParticipants()) {
			if (part.getPlayer() != null
					&& part.getPlayer().getPlayerId().equals(playerId)) {
				return part;
			}
		}

		return null;
	}

	public Participant getCurrentParticipant() {
		return getParticipantForPlayerId(Games.Players
				.getCurrentPlayerId(mHelper.getApiClient()));
	}

	public Participant getNextParticipant() {
		int myIndex;
		for (myIndex = 0; myIndex < mMatch.getParticipants().size(); myIndex++) {
			Participant p = mMatch.getParticipants().get(myIndex);
			if (p.getPlayer() != null
					&& p.getPlayer().getPlayerId() == Games.Players
							.getCurrentPlayerId(mHelper.getApiClient()))
				break;
		}

		myIndex++;
		if (myIndex >= mMatch.getParticipants().size())
			myIndex = 0;

		return mMatch.getParticipants().get(myIndex);
	}
}
