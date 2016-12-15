package de.fh;

public class Scoreboard {
	private int score;
	
	public Scoreboard(int initScore){
		this.score = initScore;
	}
	
	public void changeScore(int points){
		this.score+=points;
	}
	
	public int getScore(){
		return this.score;
	}
}
