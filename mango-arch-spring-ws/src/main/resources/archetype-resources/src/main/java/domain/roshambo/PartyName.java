package ${packageName}.domain.roshambo;

import javax.persistence.Embeddable;

@Embeddable
public class PartyName {

	private /* should be final */ String name;
	
	public PartyName(final String name) {
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
		return obj instanceof PartyName
				&& this.name.equalsIgnoreCase(((PartyName)obj).name);
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode() + name.hashCode();
	}
	
	@Deprecated
	protected PartyName() {
		
	}
}
