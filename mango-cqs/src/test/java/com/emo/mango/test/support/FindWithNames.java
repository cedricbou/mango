package com.emo.mango.test.support;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.emo.mango.cqs.Queries;
import com.emo.mango.cqs.queries.annotations.MangoJdbi;
import com.emo.mango.cqs.queries.annotations.MangoJpql;
import com.emo.mango.cqs.queries.annotations.MangoSql;
import com.emo.mango.cqs.queries.annotations.QueryBind;
import com.emo.mango.cqs.queries.annotations.QueryType;

public interface FindWithNames extends Queries {
	
	@MangoJpql("from People p where p.name = :name")
	public FooBar findExactName(final @QueryBind("name") String name);

	@MangoJpql("select new FooBar(fb.foo, fb.bar) from People p where p.name = :name")
	public FooBar findExactNameUnmanaged(final @QueryBind("name") String name);

	@MangoSql("from people p where p.name like :name")
	public List<FooBar> findNameStartingWith(final @QueryBind("name") String nameStartsWith);

	@MangoJdbi(value = "FindNameEndingWith", type = QueryType.COUNT)
 	@SqlQuery("select count(*) from people p where p.name like :name")
	public long countfindNameEndingWith(final @Bind("name") String nameEndsWith);

	@MangoJdbi(value = "FindNameEndingWith", type = QueryType.STANDARD)
 	@SqlQuery("select foo, bar from people p where p.name like :name")
  	@Mapper(FooBarMapper.class)
  	public List<FooBar> queryfindNameEndingWith(final @Bind("name") String nameEndsWith);

	@MangoJdbi(value = "FindNameEndingWith", type = QueryType.PAGED)
 	@SqlQuery("select foo, bar from people p where p.name like :name order by p.name asc limit :limit offset :offset")
  	@Mapper(FooBarMapper.class)
  	public List<FooBar> findNameEndingWith(final @Bind("name") String nameEndsWith, final @Bind("offset") int offset, final @Bind("limit") int limit);


}