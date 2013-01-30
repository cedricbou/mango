package ${packageName}.domain.roshambo;

import javax.persistence.Embeddable;

@Embeddable
public class PlayerName {

	private /* should be final */ String name;
	
	public PlayerName(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PlayerName
				&& this.name.equalsIgnoreCase(((PlayerName)obj).name);
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode() + name.hashCode();
	}
	
	@Deprecated
	protected PlayerName() {
		
	}
}
