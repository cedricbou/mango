package com.emo.mango.spring.web.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.bytecode.opencsv.CSVWriter;

import com.emo.mango.cqs.DuplicateException;
import com.emo.mango.cqs.PropertyBasedSearchValue;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.cqs.QueryItem;
import com.emo.mango.cqs.SearchValue;
import com.emo.mango.spring.cqs.support.MangoCQS;

@Controller
@RequestMapping("queries")
public class ProcessQueryController {

	@Inject
	private MangoCQS cqs;

	private static class Paging {
		public final int page;
		public final int perPage;

		public Paging(final int page, final int perPage) {
			this.page = page;
			this.perPage = perPage;
		}
	}

	private SearchValue readValueFromRequest(final QueryExecutor<?> executor,
			final HttpServletRequest request) {
		PropertyBasedSearchValue props = new PropertyBasedSearchValue();

		for (final String criteria : executor.getSearchCriteria().getCriteria()) {
			final Object value = (Object) request.getParameter(criteria);
			props = props.withProperty(criteria, value);
		}

		return props;
	}

	private Paging readPagingFromRequest(final HttpServletRequest request) {
		final String sPage = request.getParameter("_page");
		final String sPerPage = request.getParameter("_perPage");

		int page = 1;
		int perPage = 20;

		if (sPage == null && sPerPage == null) {
			return null;
		}

		if (sPage != null) {
			page = Integer.parseInt(sPage);
		}

		if (sPerPage != null) {
			perPage = Integer.parseInt(sPerPage);
		}

		return new Paging(page, perPage);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public final List<Object> query(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws DuplicateException {
		QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		final SearchValue props = readValueFromRequest(executor, request);
		final Paging paging = readPagingFromRequest(request);

		props.assertMatch(executor.getSearchCriteria());

		if (paging != null) {
			return (List<Object>) executor.pagedQuery(paging.page,
					paging.perPage, props);
		} else {
			return (List<Object>) executor.query(props);
		}
	}

	@RequestMapping(value = "/{queryName}/count", method = RequestMethod.GET)
	@ResponseBody
	public final long countItems(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws DuplicateException {
		QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		final SearchValue props = readValueFromRequest(executor, request);
		props.assertMatch(executor.getSearchCriteria());

		return executor.countItems(props);
	}

	@RequestMapping(value = "/{queryName}/pages", method = RequestMethod.GET)
	@ResponseBody
	public final int countPages(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws DuplicateException {
		QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		final SearchValue props = readValueFromRequest(executor, request);
		props.assertMatch(executor.getSearchCriteria());

		final int perPage = Integer.parseInt(request.getParameter("_perPage"));

		return executor.countPages(perPage, props);
	}

	@RequestMapping(value = "/{queryName}/export/csv", method = RequestMethod.GET)
	@ResponseBody
	public final void exportQueryResult(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, DuplicateException {
		
		QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		final SearchValue props = readValueFromRequest(executor, request);
		props.assertMatch(executor.getSearchCriteria());

		final File exportFile = new File(new File(
				System.getProperty("java.io.tmpdir")), "export-"
				+ UUID.randomUUID().toString() + ".csv");

		final QueryItem queryItem = cqs.system().query(queryName);

		final CSVWriter writer = new CSVWriter(new FileWriter(exportFile), ';');
		writer.writeNext(queryItem.columns());

		final int perPage = 1000;

		final long pages = executor.countPages(perPage, props);

		for (int i = 1; i <= pages; ++i) {
			final List<?> items = executor.pagedQuery(i, perPage, props);
			for (final Object item : items) {
				final String[] itemProps = queryItem.valuesAsStringFor(item);
				writer.writeNext(itemProps);
			}
		}

		writer.close();

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "inline; filename=\""
				+ exportFile.getName() + "\"");

		final InputStream is = new FileInputStream(exportFile);

		IOUtils.copy(is, response.getOutputStream());

		is.close();

		response.flushBuffer();
	}

	@RequestMapping(value = "/{queryName}/view", method = RequestMethod.GET, produces = "text/html")
	@ResponseBody
	public final String displayQueryResult(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws IOException,
			DuplicateException {

		final List<Object> results = query(queryName, request);
		final Paging paging = readPagingFromRequest(request);

		final QueryItem queryItem = cqs.system().query(queryName);

		String header = "<thead><tr>";
		for (final String field : queryItem.columns()) {
			header += "<th>" + field + "</th>";
		}
		header += "</tr></thead>";

		final InputStream is = this.getClass().getResourceAsStream(
				"queryui.html");
		final String rawHtml = IOUtils.toString(is);
		is.close();

		String modifiedHtml = rawHtml.replaceAll("##header##", header);

		String lines = "<tbody>";
		for (final Object obj : results) {
			String line = "<tr>";
			for (final String field : queryItem.valuesAsStringFor(obj)) {
				line += "<td>" + field + "</td>";
			}
			line += "</tr>";
			lines += line + "\n";
		}
		lines += "</tbody>";
		modifiedHtml = modifiedHtml.replaceAll("##lines##", lines);
		modifiedHtml = modifiedHtml.replaceAll("##query##", queryName);

		final UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(request.getRequestURL().toString() + "?" + request.getQueryString());

		int previousPage = (paging == null)?0:paging.page - 1;
		int nextPage = (paging == null)?2:paging.page + 1;
		
		final String previousUrl = builder.replaceQueryParam("_page",
				previousPage).build().toUri().toString();
		final String nextUrl = builder.replaceQueryParam("_page",
				nextPage).build().toUri().toString();

		final String pager = "<ul class=\"pager\"><li "
				+ ((previousPage <= 0) ? "class=\"disabled\"" : "")
				+ "><a href=\"" + ((previousPage > 0)?previousUrl:"#")
				+ "\">Previous</a></li><li><a href=\"" + nextUrl
				+ "\">Next</a></li></ul>";

		modifiedHtml = modifiedHtml.replaceAll("##pager##", pager);

		return modifiedHtml;
	}
}
