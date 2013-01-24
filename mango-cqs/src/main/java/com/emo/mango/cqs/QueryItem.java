package com.emo.mango.cqs;

public class QueryItem<O> {

	public final String name;
	public final String namespace;
	public final Class<O> clazz;

	public QueryItem(Class<O> clazz) {
		this.name = clazz.getSimpleName();
		this.namespace = clazz.getPackage().getName();
		this.clazz = clazz;
	}

	@Override
	public int hashCode() {
		return (this.name.toLowerCase() + this.namespace.toLowerCase())
				.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryItem<?>) {
			final QueryItem<?> c = (QueryItem<?>) o;
			return this.name.equalsIgnoreCase(c.name)
					&& this.namespace.equals(c.namespace);
		}
		return false;
	}

}
