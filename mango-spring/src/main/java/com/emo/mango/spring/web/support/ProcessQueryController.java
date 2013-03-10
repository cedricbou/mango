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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import au.com.bytecode.opencsv.CSVWriter;

import com.emo.mango.config.MangoConfig;
import com.emo.mango.cqs.DuplicateException;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.json.utils.JsonUtils;
import com.emo.mango.log.BusinessTransactionUtils;
import com.emo.mango.log.LogParam;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;

@Controller
@RequestMapping("queries")
public class ProcessQueryController {

	@Inject
	private MangoCQS cqs;
	
	@Inject
	private MangoConfig config;

	private static class Paging {
		public @LogParam final int page;
		public @LogParam final int perPage;

		public Paging(final int page, final int perPage) {
			this.page = page;
			this.perPage = perPage;
		}
	}

	private Object[] readValueFromRequest(final QueryExecutor executor,
			final HttpServletRequest request) {

		final String[] names = executor.getParamNames();
		final Object[] values = new Object[names.length];

		int i = 0;
		for (final String name : names) {
			if (request.getParameter(name) == null) {
				throw new IllegalArgumentException(
						"expected parameter for queries : "
								+ Joiner.on(", ").join(names));
			}
			values[i++] = (Object) request.getParameter(name);
		}

		return values;
	}

	/*
	 * private SearchValue readPostValueFromRequest(final QueryExecutor<?>
	 * executor, final HttpServletRequest request) throws JsonParseException,
	 * JsonMappingException, IOException { final String json =
	 * request.getParameter("json"); final Object queryForm; final ObjectMapper
	 * mapper = new ObjectMapper();
	 * 
	 * if(json == null) { queryForm = mapper.readValue(request.getInputStream(),
	 * ((ObjectBasedSearchCriteria)executor.getSearchCriteria()).clazz); } else
	 * { queryForm = mapper.readValue(json,
	 * ((ObjectBasedSearchCriteria)executor.getSearchCriteria()).clazz); }
	 * 
	 * final ObjectBasedSearchValue props = new
	 * ObjectBasedSearchValue(queryForm);
	 * props.assertMatch(executor.getSearchCriteria());
	 * 
	 * return props; }
	 */

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

		BusinessTransactionUtils.bindNew();
		
		final QueryExecutor executor = cqs.system().getQueryExecutor(queryName);

		if (executor == null) { // TODO: check if this can happen!
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		final Object[] values = readValueFromRequest(executor, request);
		final Paging paging = readPagingFromRequest(request);

		config.loggers().access().log("rest.query", queryName, values,
				paging);
		
		try {
			if (paging != null) {
				return (List<Object>) executor.pagedQuery(paging.page,
						paging.perPage, values);
			} else {
				return (List<Object>) executor.query(values);
			}
		} finally {
			config.loggers().chrono().log("rest.query", queryName, values,
					paging);		}
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @RequestMapping(value = "/{queryName}", method = RequestMethod.POST)
	 * 
	 * @ResponseBody public final List<Object> queryPost( final
	 * 
	 * @PathVariable("queryName") String queryName, final HttpServletRequest
	 * request) throws DuplicateException, JsonParseException,
	 * JsonMappingException, IOException { QueryExecutor<?> executor =
	 * cqs.system().queryExecutor(queryName);
	 * 
	 * if (executor == null) { throw new
	 * IllegalArgumentException("no query with name " + queryName); }
	 * 
	 * final Paging paging = readPagingFromRequest(request);
	 * 
	 * final SearchValue props = readPostValueFromRequest(executor, request);
	 * 
	 * if (paging != null) { return (List<Object>)
	 * executor.pagedQuery(paging.page, paging.perPage, props); } else { return
	 * (List<Object>) executor.query(props); } }
	 */

	@RequestMapping(value = "/{queryName}/count", method = RequestMethod.GET)
	@ResponseBody
	public final long countItems(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws DuplicateException {
		QueryExecutor executor = cqs.system().getQueryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		return executor.countItems(readValueFromRequest(executor, request));
	}

	/*
	 * @RequestMapping(value = "/{queryName}/count", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public final long countItemsPost( final
	 * 
	 * @PathVariable("queryName") String queryName, final HttpServletRequest
	 * request) throws DuplicateException, JsonParseException,
	 * JsonMappingException, IOException { QueryExecutor<?> executor =
	 * cqs.system().queryExecutor(queryName);
	 * 
	 * if (executor == null) { throw new
	 * IllegalArgumentException("no query with name " + queryName); }
	 * 
	 * final SearchValue props = readPostValueFromRequest(executor, request);
	 * 
	 * return executor.countItems(props); }
	 */

	@RequestMapping(value = "/{queryName}/pages", method = RequestMethod.GET)
	@ResponseBody
	public final int countPages(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws DuplicateException {
		QueryExecutor executor = cqs.system().getQueryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		final int perPage = Integer.parseInt(request.getParameter("_perPage"));

		return executor.countPages(perPage,
				readValueFromRequest(executor, request));
	}

	/*
	 * @RequestMapping(value = "/{queryName}/pages", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public final int countPagesPost( final
	 * 
	 * @PathVariable("queryName") String queryName, final HttpServletRequest
	 * request) throws DuplicateException, JsonParseException,
	 * JsonMappingException, IOException { QueryExecutor<?> executor =
	 * cqs.system().queryExecutor(queryName);
	 * 
	 * if (executor == null) { throw new
	 * IllegalArgumentException("no query with name " + queryName); }
	 * 
	 * final SearchValue props = readPostValueFromRequest(executor, request);
	 * 
	 * final int perPage = Integer.parseInt(request.getParameter("_perPage"));
	 * 
	 * return executor.countPages(perPage, props); }
	 */

	private final void doExport(final QueryExecutor executor,
			final Object[] values, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
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

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "inline; filename=\""
				+ exportFile.getName() + "\"");

		final InputStream is = new FileInputStream(exportFile);

		ByteStreams.copy(is, response.getOutputStream());

		is.close();

		response.flushBuffer();
	}

	@RequestMapping(value = "/{queryName}/export/csv", method = RequestMethod.GET)
	@ResponseBody
	public final void exportQueryResult(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, DuplicateException {

		QueryExecutor executor = cqs.system().getQueryExecutor(queryName);

		if (executor == null) {
			throw new IllegalArgumentException("no query with name "
					+ queryName);
		}

		doExport(executor, readValueFromRequest(executor, request), request,
				response);

	}

	/*
	 * @RequestMapping(value = "/{queryName}/export/csv", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public final void exportQueryResultPost( final
	 * 
	 * @PathVariable("queryName") String queryName, final HttpServletRequest
	 * request, final HttpServletResponse response) throws IOException,
	 * DuplicateException {
	 * 
	 * QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);
	 * QueryItem queryItem = cqs.system().query(queryName);
	 * 
	 * if (executor == null) { throw new
	 * IllegalArgumentException("no query with name " + queryName); }
	 * 
	 * final SearchValue props = readPostValueFromRequest(executor, request);
	 * 
	 * doExport(executor, queryItem, props, request, response);
	 * 
	 * }
	 */

	/*
	 * @RequestMapping(value = "/{queryName}/form", method = RequestMethod.GET,
	 * produces = "text/html")
	 * 
	 * @ResponseBody public final String displayQuerySearchForm( final
	 * 
	 * @PathVariable("queryName") String queryName, final HttpServletRequest
	 * request, UriComponentsBuilder builder) throws IOException,
	 * DuplicateException {
	 * 
	 * final QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);
	 * final QueryItem item = cqs.system().query(queryName);
	 * 
	 * if (executor == null) { throw new
	 * IllegalArgumentException("no query with name " + queryName); }
	 * 
	 * if(!(executor.getSearchCriteria() instanceof ObjectBasedSearchCriteria))
	 * { throw new IllegalArgumentException(
	 * "form is only supported for query with object based search criteria"); }
	 * 
	 * final String baseURI = builder.path("").build().toUri().toString();
	 * 
	 * final InputStream is =
	 * this.getClass().getResourceAsStream("queryformui.html");
	 * 
	 * final String rawHtml = IOUtils.toString(is); is.close();
	 * 
	 * final String queryUrl =
	 * builder.path("/queries/{queryName}/view").buildAndExpand
	 * (queryName).toUri().toString(); final String schemaUrl =
	 * queryUrl.replace("/view", "/schema");
	 * 
	 * final String modifiedHtml = rawHtml.replaceAll("##base##", baseURI)
	 * .replaceAll("##query##", item.name) .replaceAll("##query_url##",
	 * queryUrl) .replaceAll("##schema_url##", schemaUrl);
	 * 
	 * return modifiedHtml; }
	 * 
	 * @RequestMapping(value = "/{queryName}/schema", method =
	 * RequestMethod.GET)
	 * 
	 * @ResponseBody public final String schemaCommand(final
	 * 
	 * @PathVariable("queryName") String queryName) throws JsonMappingException,
	 * DuplicateException {
	 * 
	 * final QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);
	 * 
	 * final ObjectBasedSearchCriteria criteria =
	 * (ObjectBasedSearchCriteria)executor.getSearchCriteria();
	 * 
	 * final ObjectMapper mapper = new ObjectMapper(); final JsonSchema schema =
	 * mapper.generateJsonSchema(criteria.clazz);
	 * 
	 * final JsonNode node = schema.getSchemaNode(); new
	 * JsonSchemaCompletion().schemaAddTitle(null, node, criteria.clazz);
	 * 
	 * return node.toString(); }
	 */

	private String renderQueryResult(final List<Object> results,
			final String queryName, final HttpServletRequest request)
			throws IOException {

		final InputStream is = this.getClass().getResourceAsStream(
				"queryui.html");
		final String rawHtml = new String(ByteStreams.toByteArray(is));
		is.close();

		String modifiedHtml = rawHtml;
		modifiedHtml = modifiedHtml.replaceAll("##query##", queryName);

		final boolean hasResults = results != null && results.size() > 0;

		if (hasResults) {

			String header = "<thead><tr>";

			for (final String field : JsonUtils.getRootFieldNames(results
					.get(0))) {
				header += "<th>" + field + "</th>";
			}
			header += "</tr></thead>";

			modifiedHtml = modifiedHtml.replaceAll("##header##", header);

			String lines = "<tbody>";
			for (final Object obj : results) {
				String line = "<tr>";
				for (final String field : JsonUtils.getRootValuesAsString(obj)) {
					line += "<td>" + field + "</td>";
				}
				line += "</tr>";
				lines += line + "\n";
			}
			lines += "</tbody>";
			modifiedHtml = modifiedHtml.replaceAll("##lines##", lines);
		} else {
			modifiedHtml = modifiedHtml.replaceAll("##header##",
					"<thead><tr><th>Warning</th></tr></thead>");
			modifiedHtml = modifiedHtml.replaceAll("##lines##",
					"<tbody><tr><td>No more results</td></tr></tbody>");
		}

		final UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(request.getRequestURL().toString() + "?"
						+ request.getQueryString());

		final Paging paging = readPagingFromRequest(request);

		int previousPage = (paging == null) ? 0 : paging.page - 1;
		int nextPage = (paging == null) ? 2 : paging.page + 1;

		final String baseUrl = builder.replaceQueryParam("_page", paging.page)
				.build().toUri().toString();

		// FIXME: this is dangerous, find a better and safer way to rebuild URL.
		final String exportUrl = baseUrl.replace("/view", "/export/csv");

		final String previousUrl = builder
				.replaceQueryParam("_page", previousPage).build().toUri()
				.toString();

		final String nextUrl = builder.replaceQueryParam("_page", nextPage)
				.build().toUri().toString();

		final String pager = "<ul class=\"pager\"><li "
				+ ((previousPage <= 0) ? "class=\"disabled\"" : "")
				+ "><a href=\"" + ((previousPage > 0) ? previousUrl : "#")
				+ "\">Previous</a></li><li "
				+ ((!hasResults) ? "class=\"disabled\"" : "") + "><a href=\""
				+ ((!hasResults) ? "#" : nextUrl) + "\">Next</a></li></ul>";

		modifiedHtml = modifiedHtml.replaceAll("##pager##", pager);

		modifiedHtml = modifiedHtml.replaceAll("##exporturl##", exportUrl);

		return modifiedHtml;

	}

	@RequestMapping(value = "/{queryName}/view", method = RequestMethod.GET, produces = "text/html")
	@ResponseBody
	public final String displayQueryResultPost(
			final @PathVariable("queryName") String queryName,
			final HttpServletRequest request) throws IOException,
			DuplicateException {

		final List<Object> results = query(queryName, request);

		return renderQueryResult(results, queryName, request);
	}

}
