package fh.mdp.chipazee;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public class BaseGameActivity extends FragmentActivity {
	
	@Override
	public void onActivityResult(int request, int response, Intent data)
	{
		super.onActivityResult(request, response, data);
		TurnbasedGameSingleton.getGame().onActivityResult(request, response, data);
		
	}
}
