package com.emo.mango.spring.query.support;

import java.util.Collection;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.emo.mango.cqs.Queries;
import com.emo.mango.spring.cqs.support.MangoCQS;

public class MangoQueryScanner implements InitializingBean {

	private final static Logger logger = LoggerFactory.getLogger(MangoQueryScanner.class);
	
	@Autowired
	private MangoCQS cqs;

	private final String scanPackage;
	
	private final EntityManager em;
	
	private final DataSource ds;

	public MangoQueryScanner(final String scanPackage, final EntityManagerProvider emp) {
		this(scanPackage, null, emp);
	}
	
	public MangoQueryScanner(final String scanPackage, final DataSource ds) {
		this(scanPackage, ds, null);
	}
	
	public MangoQueryScanner(final String scanPackage) {
		this(scanPackage, null, null);
	}
	
	public MangoQueryScanner(final String scanPackage, final DataSource ds, final EntityManagerProvider emp) {
		this.scanPackage = scanPackage;
		this.ds = ds;
		this.em = (emp != null)?emp.get():null;
	}

	private void scanQueriesInterface() {
		logger.debug("will scan for queries interfaces in package {}", scanPackage);
		logger.debug("with datasource {}", ds);
		logger.debug("with entity manager {}", em);
		
		final Collection<Class<? extends Queries>> queriesItf = new QueriesInterfaceScanner().getComponentClasses(scanPackage);
		
		/*
		ComponentScanner scanner = new ComponentScanner();
		final Set<Class<?>> queriesItf = scanner.getClasses(new ComponentQuery() {
			
			@Override
			protected void query() {
				select()
					.from(MangoQueryScanner.this.scanPackage)
					.returning(allExtending(Queries.class));
			}
		});
			*/	
		logger.debug("found {} queries interfaces.", queriesItf.size());
		
		for(final Class<?> itf : queriesItf) {
			@SuppressWarnings("unchecked")
			final Class<? extends Queries> qItf = (Class<? extends Queries>)itf;
			logger.debug("will declare queries for interface {}", qItf.getName());
			cqs.system().declareQueries(qItf, ds, em);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scanQueriesInterface();
	}
}
