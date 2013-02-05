package com.emo.mango.cqs;

public class Command<O> {

	public final String name;
	public final String namespace;
	public final Class<O> clazz;

	public Command(Class<O> clazz) {
		this.name = clazz.getSimpleName();
		this.namespace = clazz.getPackage().getName();
		this.clazz = clazz;
	}

	@Override
	public int hashCode() {
		return (this.name.toLowerCase())
				.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Command<?>) {
			final Command<?> c = (Command<?>) o;
			return this.name.equalsIgnoreCase(c.name);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "{" + namespace + "}" + name;
	}
}
