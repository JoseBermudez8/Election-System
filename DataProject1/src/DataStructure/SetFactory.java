package DataStructure;

public interface SetFactory<E> {

	public Set<E> newInstance(int capacity);

}