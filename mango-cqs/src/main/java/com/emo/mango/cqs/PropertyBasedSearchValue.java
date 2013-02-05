package com.emo.mango.cqs;

public class PropertyBasedSearchValue implements SearchValue {

	private final Property[] props;
	
	private final Object[] values;
	
	private final String[] criteria;

	@Override
	public Object[] getValues() {
		return values;
	}

	@Override
	public String[] getCriteria() {
		return criteria;
	}
	
	@Override
	public Object get(final String criteria) {
		for(final Property prop : props) {
			if(prop.criteria.equals(criteria)) {
				return prop.value;
			}
		}
		
		return null;
	}
	
	public static class Property {
		public final String criteria;
		public final Object value;

		public Property(final String criteria, final Object value) {
			this.criteria = criteria;
			this.value = value;
		}
	}

	public static Property property(final String criteria, final Object value) {
		return new Property(criteria, value);
	}

	public PropertyBasedSearchValue(final Property... props) {
		this.props = props;

		this.criteria = new String[props.length];
		this.values = new String[props.length];

		for(int i = 0; i < props.length; ++i) {
			criteria[i] = props[i].criteria;
			values[i] = props[i].value;
		}
	}

	private PropertyBasedSearchValue(final Property[] props,
			final Property... otherProps) {
		this.props = new Property[props.length + otherProps.length];

		System.arraycopy(props, 0, this.props, 0, props.length);
		System.arraycopy(otherProps, 0, this.props, props.length,
				otherProps.length);

		this.criteria = new String[this.props.length];
		this.values = new String[this.props.length];

		for(int i = 0; i < this.props.length; ++i) {
			this.criteria[i] = this.props[i].criteria;
			this.values[i] = this.props[i].value;
		}
	}

	public void assertMatch(SearchCriteria criteria) {
/*		if (criteria instanceof PropertyBasedSearchCriteria) {
			if (criteria.getCriteria().length != props.length) {
				throw new IllegalArgumentException(
						"search values must match the number of search criteria");
			}
		} else {
			throw new IllegalArgumentException(
					"incompatible search value implementation for this search criteria. Expected "
							+ PropertyBasedSearchCriteria.class.getName()
							+ ", got " + criteria.getClass().getName());
		} */
		
		final String[] columns = criteria.getCriteria();
		
		if(columns.length != this.criteria.length) {
			throw new IllegalArgumentException("number of values criteria does not match declared criteria");
		}
		
		for(int i = 0; i < columns.length; ++i) {
			if(!columns[i].equals(this.criteria[i])) {
				throw new IllegalArgumentException("Expected criteria '" + columns[i] + "' but got '" + this.criteria[i] + "'.");
			}
		}

	}

	public PropertyBasedSearchValue withProperty(String criteria, Object value) {
		return new PropertyBasedSearchValue(this.props, property(criteria,
				value));
	}
}
