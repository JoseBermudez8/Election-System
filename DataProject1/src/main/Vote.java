package main;

public class Vote {


	private int candidateID;
	private int voterPref;

	public Vote(String rac) {
		String[] itty = rac.split(":");
		this.candidateID = Integer.parseInt(itty[0]);
		this.voterPref = Integer.parseInt(itty[1]);
	}
	public int getCandidateID() {

		return candidateID;
	}
	public void setCandidateID(int candidateID) {
		this.candidateID = candidateID;
	}
	public int getVoterPref() {
		return voterPref;
	}
	public void setVoterPref(int voterPref) {
		this.voterPref = voterPref;
	}



}
