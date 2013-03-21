package com.emo.mango.spring.web.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import au.com.bytecode.opencsv.CSVWriter;

import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.json.utils.JsonUtils;

public class QueryToCsv {

	private final QueryExecutor executor;
	private final Object[] values;
		
	public QueryToCsv(final QueryExecutor executor, final Object... values) {
		this.executor = executor;
		this.values = values;
	}
	
	public File generate() throws IOException {
		final File exportFile = new File(new File(
				System.getProperty("java.io.tmpdir")), "export-"
				+ UUID.randomUUID().toString() + ".csv");

		final CSVWriter writer = new CSVWriter(new FileWriter(exportFile), ';');

		boolean writeHeader = true;

		final int perPage = 1000;

		final long pages = executor.countPages(perPage, values);

		for (int i = 1; i <= pages; ++i) {
			final List<?> items = executor.pagedQuery(i, perPage, values);

			if (items != null && items.size() > 0) {
				if (writeHeader) {
					writer.writeNext(JsonUtils.getRootFieldNames(items.get(0)));
					writeHeader = false;
				}

				for (final Object item : items) {
					final String[] itemProps = JsonUtils
							.getRootValuesAsString(item);
					writer.writeNext(itemProps);
				}
			}
		}

		writer.close();
		
		return exportFile;
	}

}
