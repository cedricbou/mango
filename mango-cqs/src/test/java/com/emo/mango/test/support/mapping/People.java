package com.emo.mango.test.support.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="people")
public class People {

	@Id
	private int id;
	
	@Column
	private String name;

	@Column
	private String foo;
	
	@Column
	private String bar;
	

	public String getName() {
		return name;
	}

	public String getFoo() {
		return foo;
	}

	public String getBar() {
		return bar;
	}

	@Deprecated
	protected int getId() {
		return id;
	}

	@Deprecated
	protected People() {
		
	}
}
