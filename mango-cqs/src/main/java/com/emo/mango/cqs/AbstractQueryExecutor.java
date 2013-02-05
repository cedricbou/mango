package com.emo.mango.cqs;

import java.util.List;

import com.emo.mango.cqs.PropertyBasedSearchValue.Property;

public abstract class AbstractQueryExecutor<Q> implements QueryExecutor<Q> {

	private PropertyBasedSearchValue values(Object... values) {
		final String[] criteria = getSearchCriteria().getCriteria();

		if (values.length != criteria.length) {
			throw new IllegalArgumentException(
					"number of values passed in parameter must match number of criteria of search criteria. "
							+ getSearchCriteria());
		}

		final Property[] props = new Property[criteria.length];

		int i = 0;
		for (final String criterion : criteria) {
			props[i] = PropertyBasedSearchValue.property(criterion, values[i]);
			++i;
		}

		return new PropertyBasedSearchValue(props);
	}

	private SearchValue objectValue(Object value) {
		return new ObjectBasedSearchValue(value);
	}

	@Override
	public final long simpleCountItems(Object... values) {
		return countItems(values(values));
	}

	@Override
	public final int simpleCountPages(int elementsParPage, Object... values) {
		return countPages(elementsParPage, values(values));
	}

	@Override
	public final java.util.List<Q> simplePagedQuery(int page,
			int elementsParPage, Object... values) {
		return pagedQuery(page, elementsParPage, values(values));
	};

	@Override
	public final java.util.List<Q> simpleQuery(Object... values) {
		return query(values(values));
	};

	@Override
	public final long objectCountItems(Object value) {
		return countItems(objectValue(value));
	}

	@Override
	public final int objectCountPages(int elementsParPage, Object value) {
		return countPages(elementsParPage, objectValue(value));
	}

	@Override
	public final List<Q> objectPagedQuery(int page, int elementsParPage,
			Object value) {
		return pagedQuery(page, elementsParPage, objectValue(value));
	}

	@Override
	public final List<Q> objectQuery(Object value) {
		return query(objectValue(value));
	}

}
