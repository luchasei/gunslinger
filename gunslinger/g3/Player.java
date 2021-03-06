package gunslinger.g3;

import java.util.*;

// The base class of a player
// Extends the base class to start your player
// See dumb/Player.java for an example
//
public class Player extends gunslinger.sim.Player
{

    private int nplayers;
    private int[] friends;
    private int[] enemies;
	
	int[] expected_shots;
    
    // total versions of the same player
    private static int versions = 0;
    // my version no
    private int version = versions++;
    
    // name of the team
    //
    public String name()
    {
        return "Group3Player" + (versions > 1 ? " v" + version : "");
    }
    
    // Initialize the player
    //
    public void init(int nplayers, int[] friends, int enemies[])
    {
        this.nplayers = nplayers;
        this.friends = friends.clone();
        this.enemies = enemies.clone();  	
    }
	
	//Returns true if the game is in equilibrium (same shots by each player in the past three rounds) and false otherwise.
	public boolean equilibrium(int[] prevRound, boolean[] alive){
	
		//Temporary until histories is implemented
		int rounds = 5;
		int[][] history = new int[rounds-1][alive.length];
		
		for(int j= 0;j<alive.length;j++){
		
			//Player j's target in the most recent round.
			int enemyTarget = history[rounds-1][j];
		
			for(int i= 1;i<3;i++){
			
				//If a player shot differently in the past three rounds, the game is not in equilibrium so return false.
				if(history[rounds-1-i][j]!=enemyTarget)
					return false;
		
		
			}
		}
	
		//If all players have shot similarly in the past three rounds, return true.
		return true;
	
	
	
	}
	
	
	//Strategy when three players are left in equilibrium
	public int endGame(int[] prevRound, boolean[] alive){

		for(int i = 0; i < prevRound.length; i++){
		
			for(int j = 0; j < enemies.length; j++){
			
			
				//If one of the remaining players shot an enemy in equilibrium
				if(prevRound[i] == enemies[j]){
				
					System.out.println("[Me]: Shooting player " + enemies[j] + " who was shot by player " + i + ".");
				
					//Shoot the enemy being shot at in equilibrium
					return enemies[j];
				
				}
			
			}
		}
	
		return -1;
	
	}

	//Computes the expected number of shots each player will receive based on the history of shots
	public double[] expected_shots(boolean[] alive){
	
	
		//Temporary until histories is implemented
		int rounds = 5;
		int[][] history = new int[rounds-1][alive.length];
		double[] expected_shots= new double[alive.length];
	
		for(int i = 0;i<alive.length;i++){
		
			//If player i is alive
			if(alive[i] == true){
			
				//Use a weighted moving average of shots over the past rounds to calculate the expected 
				//number of shots for each player
				expected_shots[i]= .5 * history[rounds-1][i] + .3 * history[rounds-2][i] + .2 * history[rounds-3][i];
			
			}else{
			
				//If a player is dead, he cannot be shot
				expected_shots[i]= 0;
			
			}
		}
		
		return expected_shots;
	}

    // Pick a target to shoot
    // Parameters:
    //  prevRound - an array of previous shoots, prevRound[i] is the player that player i shot
    //              -1 if player i did not shoot
    //  alive - an array of player's status, true if the player is still alive in this round
    // Return:
    //  int - the player id to shoot, return -1 if do not shoot anyone
    //
    public int shoot(int[] prevRound, boolean[] alive){
    	    	
    	if (prevRound == null)
    	{
    		//First Round Strategy -> wait do nothing
    		System.err.println("[ME] First Round, I am " + id + " waiting...");
    	}
    	else
    	{
		
    		//Priority 1: Shoot person you shot at before if they are not dead
    		int lastPersonShotAt = prevRound[id];
	    		
	    	if( lastPersonShotAt != -1 && alive[lastPersonShotAt] )
	    	{
	    		return lastPersonShotAt;
	    	}

	    	//Priority 2: Shoot the person who shot you last round
	    	for(int i = 0;i < prevRound.length; i++)
	    	{
	    		if( (prevRound[i] == id) && alive[i] )
	    		{
	    			return i;
	    		}
	       	}
    		
			//Priority 3: Shoot at enemies that shot at friends
			for(int i = 0;i < prevRound.length; i++)
			{
				for(int j = 0;j < friends.length; j++)
				{
					// Did the player shoot a friend?
					if ( (friends[j] == prevRound[i]) && alive[i])
					{
						// Is the player an enemy
						for(int k = 0;k < enemies.length; k++)
						{
							if (enemies[k] == i)
							{
								return i;
							}
							//else keep a low profile by not killing neutral players
						}
					}
				}
			}		
    	}
    	    	
    	return -1;
    	
    }
    
}
