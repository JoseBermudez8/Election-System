package main;
import DataStructure.DynamicSet;
import DataStructure.Set;

public class Candidates {

	private String name;
	private int id;
	private Set<Ballot> candy = new DynamicSet<Ballot>(10);
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Set<Ballot> getCandy() {
		return candy;
	}
	public void setCandy(Set<Ballot> candy) {
		this.candy = candy;
	}
}