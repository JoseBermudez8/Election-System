package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import DataStructure.ArrayList;
import DataStructure.DynamicSet;
import DataStructure.List;
import DataStructure.Set;

public class Election {

	public List<Candidates> regular; //List of Candidates
	public List<Candidates> losers = new ArrayList<Candidates>(1); //List of eliminated Candidates
	public Set<Ballot> allBallots;	//List of all valid ballots
	public int blanks = 0;
	public int invalids = 0;

	public static void main(String[] args) throws IOException {

		Election system = new Election();

		FileWriter writer = new FileWriter("results.txt");		

		system.regular = system.getCandidates(new File("candidates.csv")); //Receives the candidates from the CSV into the created list regular
		system.allBallots = system.getBallots(new File("ballots.csv")); //Receives the ballots from the CSV into the set allBallots

		for (Ballot currentBallot : system.allBallots) {
			system.sift(currentBallot.getCandidateByRank(1)).getCandy().add(currentBallot); //Adds candidates with rank 1
		}	

		int totalBallots = system.allBallots.size() + system.blanks + system.invalids; //Sum of all ballots

		writer.write("Number of ballots: " + totalBallots + "\n");
		writer.write("Number of blank ballots: " + system.blanks + "\n");
		writer.write("Number of invalid ballots: " +system.invalids + "\n");
		system.processing(writer); //Writes results into results.txt
		writer.close();
	}

	public void processing(FileWriter writer) throws IOException {	//Flushes out the tie-breaks and losers to determine the winner of the race
		int round = 1;
		Candidates winner = lead();
		while(winner.getCandy().size()<=allBallots.size()/2) { //Checks if the leading candidate is the winner
			fumigate(trail().getId(), round, writer); //Adjusts to find the current leader
			round++;
			winner = lead();
		}
		writer.write("Winner: " + winner.getName() + " wins with " + winner.getCandy().size() + " #1's");
	}

	public Candidates lead() {	//Determines who has the most rank 1's in the list of candidates
		Candidates leading = regular.get(0);
		for (Candidates currcand : regular) {
			if(leading.getCandy().size()<currcand.getCandy().size()) {
				leading = currcand;
			}
		}
		return leading;
	}

	public Candidates trail() {	//Determines who has the least rank 1's in the list of candidates
		Candidates trail = regular.get(0);
		for (Candidates currcand : regular) {
			if(trail.getCandy().size()>currcand.getCandy().size()) {
				trail = currcand;
			}
			if(currcand.getCandy().size() == trail.getCandy().size()) {
				trail = breaker(currcand,trail);
			}
		}
		return trail;
	}

	public Candidates sift(int id) { // Searches through regular for the target ID
		for (Candidates currCandi : regular) {
			if(currCandi.getId()==id) {
				return currCandi;	
			}
		}
		return null;
	}

	public void fumigate(int id, int round, FileWriter writer) throws IOException { //Eliminates Candidate from the race
		Candidates insect = sift(id);	
		for (Ballot parasite : insect.getCandy()) {	
			parasite.eliminate(parasite.getFirstChoice());
			boolean goodBallot = false;
			while(!goodBallot) {
				goodBallot = true;
				for (int i = 0; i < losers.size(); i++) { //Checks if candidate being eliminated has already been eliminated
					if(losers.get(i).getId() == parasite.getFirstChoice()) {
						parasite.eliminate(parasite.getFirstChoice()); //re-eliminates
						goodBallot = false;
					}
				}
			}
			sift(parasite.getFirstChoice()).getCandy().add(parasite);
		}
		writer.write("Round " + round + ": " + insect.getName() + " was eliminated with " + insect.getCandy().size() + " #1's\n");
		insect.getCandy().clear(); //Clears targeted candidates
		losers.add(insect);	//Adds candidate being eliminated to the list of people out
		regular.remove(insect); //removes targeted candidate from the list of candidates

	}

	public Candidates breaker(Candidates c1, Candidates c2) { //Establishes the winner of the tie-breaker and every proceeding ties

		int checkRank = 2;
		int counter1 = 0;
		int counter2 = 0;
		while(checkRank<=regular.size()) {	//Ranks should never exceed over the amount of candidates
			for(int i = 0;i<regular.size();i++) {

				for(Ballot b : regular.get(i).getCandy()) {	
					if(c1.getId() == b.getCandidateByRank(checkRank)) { //Creates count of selected rank for first target candidate
						counter1++;
					}
					if(c2.getId() == b.getCandidateByRank(checkRank)) { //Creates count of selected rank for second target candidate
						counter2++;
					}
				}
			}
			if(counter1<counter2) { //Compares vote counts to determine if the first candidate wins
				return c1;
			}
			if(counter2<counter1) {	//Compares vote counts to determine if the second candidate wins
				return c2;
			}
			checkRank++; //Amount of votes are tied so the loop resets to compare with one rank higher
		}
		if(c1.getId()>c2.getId()) { //In the case that everybody ties, the ID values will be compared to determine a winner
			return c1;
		}else{
			return c2;
		}
	}

	public boolean invalidate(Ballot ballot) {	//Counts all of the invalid ballots in the system
		int count = 1; 
		for (int i = 0; i < ballot.getVotes().size(); i++) {	//Checks if all ranks are continuous by checking the rank order
			for (int j = i+1; j < ballot.getVotes().size(); j++) {	
				if(ballot.getVotes().get(i).getCandidateID()==ballot.getVotes().get(j).getCandidateID()) {
					invalids++;
					return true;
				}
			}	

			if(ballot.getVotes().get(i).getVoterPref()==count){ //Verifies the order by comparing to the IDs
				count++;
			}
			else {	//Adds to invalid count if votes aren't in order
				invalids++;
				return true;
			}

		}
		return false;
	}

	public ArrayList<Candidates> getCandidates(File file) throws FileNotFoundException{ //Fetches all the candidates from the selected csv file
		int index = 0;
		ArrayList<Candidates> candyList = new ArrayList<Candidates>(5);
		Scanner dataScanner = null;
		Scanner scanner = new Scanner(file);


		while (scanner.hasNextLine()) { 
			dataScanner = new Scanner(scanner.nextLine());
			dataScanner.useDelimiter(",");
			Candidates newCand = new Candidates();

			while (dataScanner.hasNext()) { //Checks if there are any more lines to scan
				String data = dataScanner.next(); 
				if (index == 0)				
					newCand.setName(data); //Sets those in the first column as candidate names
				else if (index == 1)
					newCand.setId(Integer.parseInt(data)); //Sets those in the second column as the ID for the row's respective candidate
				else
					System.out.println("invalid data::" + data);
				index++;
			}
			index = 0;
			candyList.add(newCand); //Adds selected candidate
		}
		scanner.close();
		return candyList;
	}

	public Set<Ballot> getBallots(File file) throws FileNotFoundException { //Fetches all the ballots from the selected csv file
		Scanner scanner = new Scanner(file);
		Set<Ballot> ballots = new DynamicSet<Ballot>(1);
		while (scanner.hasNextLine()) {	
			Ballot ballot = new Ballot(scanner.nextLine()); //Organizes every line in the CSV as a new ballot
			if(ballot.getVotes().size()!=0) { //Checks if ballot is blank
				if(invalidate(ballot)==false) { //Checks if ballot is valid
					invalidate(ballot); //Verifies the validity and adds to the invalids count if not valid
					ballots.add(ballot); //Adds verified ballot
					sift(ballot.getCandidateByRank(1)).getCandy().add(ballot); //Finds candidate with rank 1 in the ballot and adds them
				}
			}else {
				blanks++;
			}
		}
		scanner.close();
		return ballots;
	}
}
