package com.emo.mango.spring.web.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.emo.mango.cqs.DuplicateException;
import com.emo.mango.cqs.QueryExecutor;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("queries")
public class ProcessQueryController {

	@Inject
	private MangoCQS cqs;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public final List<Object> query(final @PathVariable("queryName") String queryName, final HttpServletRequest request) throws DuplicateException {
		QueryExecutor<?> executor = cqs.system().queryExecutor(queryName);
		
		if(executor == null) {
			throw new IllegalArgumentException("no query with name " + queryName);
		}
		
		final List<Object> values = new LinkedList<Object>();
		
		for(final String param : executor.getParams()) {
			final Object value = (Object)request.getParameter(param);
			
			if(null == value) {
				throw new IllegalArgumentException("missing query parameter " + param);
			}
			
			values.add(value);
		}
		
		return (List<Object>)executor.query(values.toArray());
	}
	
	
	@RequestMapping(value = "/{queryName}/view", method = RequestMethod.GET, produces = "text/html")
	@ResponseBody
	public final String displayQueryResult(final @PathVariable("queryName") String queryName, final HttpServletRequest request, UriComponentsBuilder builder) throws IOException, DuplicateException {
		final List<Object> results = query(queryName, request);
		
		Object o = results.get(0);
		
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode n = mapper.valueToTree(o);

		final List<String> fields = new LinkedList<String>();
		final Iterator<String> it = n.fieldNames();

		while(it.hasNext()) {
			fields.add(it.next());
		}
		
		String header = "<thead><tr>";
		for(final String field : fields) {
			header += "<th>" + field + "</th>";
		}
		header += "</tr></thead>";
		
		final InputStream is = this.getClass().getResourceAsStream("queryui.html");  
		final String rawHtml = IOUtils.toString(is);
		is.close();

		String modifiedHtml = rawHtml.replaceAll("##header##", header);
		
		String lines = "<tbody>";
		for(final Object obj : results) {
			final JsonNode node = mapper.valueToTree(obj);
			String line = "<tr>";
			for(final String field : fields) {
				line += "<td>" + 
						((node.get(field) != null)?((node.isValueNode())?((node.get(field).isTextual())?node.get(field).textValue():node.get(field).asText()):node.get(field).toString()):"null") + "</td>";
			}
			line += "</tr>";
			lines += line + "\n";
		}
		lines += "</tbody>";
		modifiedHtml = modifiedHtml.replaceAll("##lines##", lines);
		modifiedHtml = modifiedHtml.replaceAll("##query##", queryName);
		return modifiedHtml;
	}


}
