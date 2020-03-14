package main;
import DataStructure.ArrayList;



public class Ballot {
	private int BallotNum;

	private ArrayList<Vote> Votes= new ArrayList<>(5);

	public Ballot(String rac) {
		String[] littleBits = rac.split(",");
		this.BallotNum = Integer.parseInt(littleBits[0]);
		for(int i = 1;i<littleBits.length;i++) {
			Votes.add(new Vote(littleBits[i]));
		}

	}


	public ArrayList<Vote> getVotes() {
		return Votes;
	}


	public void setBallotNum(int ballotNum) {
		BallotNum = ballotNum;
	}

	public int getBallotNum() {// returns the ballot number
		return this.BallotNum;		 
	}
	
	public int getFirstChoice() {
		return getCandidateByRank(1);
	}

	public int getRankByCandidate(int candidateId) { // rank for that candidate
		for (Vote dang : Votes) {
		if(dang.getCandidateID()==candidateId) {
			
			return dang.getVoterPref();
			}
		}
		return -1;
	}

	public int getCandidateByRank(int rank) {// candidate with that rank
		for (Vote dang : Votes) {
			if(dang.getVoterPref()==rank) {
				
				return dang.getCandidateID();
				}
			}
			return -1;

	}
	
	public boolean eliminate(int candidateId) {// eliminates a candidate
		Vote die = null;
		boolean eliminated = false;
		for (Vote eliminee : Votes) {
			if(eliminee.getCandidateID()==candidateId) {
				die = eliminee;
				Votes.remove(eliminee);
				eliminated = true;
				}
		}
		for (Vote outted : Votes) {
			if(outted.getVoterPref()>die.getVoterPref()) {
				
				outted.setVoterPref(outted.getVoterPref()-1);
			}
		}
		return eliminated;
	}
}