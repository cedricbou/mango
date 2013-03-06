package com.emo.mango.spring.web.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.emo.mango.cqs.DuplicateException;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.common.io.ByteStreams;

@Controller
@RequestMapping("/commands")
public class ProcessCommandController {
	private final static Logger logger = LoggerFactory.getLogger(ProcessCommandController.class);
	
	@Inject
	private MangoCQS cqs;
	
	@Inject
	private ProcessCommandValidator validator;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/{commandName}", method = RequestMethod.POST)
	@ResponseBody
	public final String processCommand(final @PathVariable("commandName") String commandName, final HttpServletRequest req) throws JsonParseException, JsonMappingException, IOException, DuplicateException {
		final Class<?> commandType = cqs.system().command(commandName).clazz;

		final ObjectMapper mapper = new ObjectMapper();
		
		final String json = new String(ByteStreams.toByteArray(req.getInputStream()));
		
		LOG.debug("got json {} command : {}", commandName, json);
		
		final Object command = mapper.readValue(json, commandType);

		validator.validate(command);
		
		cqs.bus().send(command);

		return "ok";
	}

	
	private static class CommandHolder {
		public String type;
		public JsonNode command;
	}
	
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	@ResponseBody
	protected final String processCommands(HttpServletRequest req)
			throws IOException, DuplicateException {
		
		final ObjectMapper mapper = new ObjectMapper();
		final List<CommandHolder> holders = mapper.readValue(req.getInputStream(), new TypeReference<List<CommandHolder>>() {});
		
		final List<Object> commands = new LinkedList<Object>();
		
		final Iterator<CommandHolder> it = holders.iterator();
		
		while(it.hasNext()) {
			CommandHolder commandHolder = it.next();
			final Class<?> commandType = cqs.system().command(commandHolder.type).clazz;
			final Object command = mapper.readValue(
					commandHolder.command.toString(), commandType);
			commands.add(command);
		}
		
		for(final Object command : commands) {
			validator.validate(command);
		}
		
		for(final Object command : commands) {
			cqs.bus().send(command);
		}

		return "ok";
	}

	
	@RequestMapping(value = "/{commandName}", method = RequestMethod.GET, produces = "text/html")
	@ResponseBody
	public final String displayCommand(final @PathVariable("commandName") String commandName, UriComponentsBuilder builder) throws IOException, DuplicateException {
		final Class<?> commandType = cqs.system().command(commandName).clazz;

		final String baseURI = builder.path("").build().toUri().toString();
		
		final InputStream is = this.getClass().getResourceAsStream("commandui.html");  
		final String rawHtml = new String(ByteStreams.toByteArray(is));
		is.close();

		logger.debug("raw html : " + rawHtml);
		
		final String modifiedHtml = 
			rawHtml.replaceAll("##base##", baseURI)
				.replaceAll("##command##", commandType.getName())
				.replaceAll("##command_url##", builder.path("/commands/{commandName}").buildAndExpand(commandName).toUri().toString())
				.replaceAll("##schema_url##", builder.path("/schema").buildAndExpand(commandName).toUri().toString());
		
		return modifiedHtml;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public String handleException1(Exception ex) {
		LOG.error("will fail with 500 because of exception", ex);
	    return ex.getMessage();
	}


	@RequestMapping(value = "/{commandName}/schema", method = RequestMethod.GET)
	@ResponseBody
	public final String schemaCommand(final @PathVariable("commandName") String commandName) throws JsonMappingException, DuplicateException {

		final Class<?> commandType = cqs.system().command(commandName).clazz;
		
		final ObjectMapper mapper = new ObjectMapper();
		final JsonSchema schema = mapper.generateJsonSchema(commandType);

		final JsonNode node = schema.getSchemaNode();
		new JsonSchemaCompletion().schemaAddTitle(null, node, commandType);

		return node.toString();
	}

	
	
}
