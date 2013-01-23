package com.emo.mango.spring.web.support;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.emo.mango.cqs.DuplicateCommandException;
import com.emo.mango.spring.cqs.support.MangoCQS;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping("/commands")
public class ProcessCommandController {
	@Inject
	private MangoCQS cqs;
	
	@Inject
	private ProcessCommandValidator validator;
		
	@RequestMapping(value = "/{commandName}", method = RequestMethod.POST)
	@ResponseBody
	public final String processCommand(final @PathVariable("commandName") String commandName, final HttpServletRequest req) throws JsonParseException, JsonMappingException, IOException, DuplicateCommandException {
		final Class<?> commandType = cqs.system().command(commandName).clazz;

		final ObjectMapper mapper = new ObjectMapper();
		final Object command = mapper.readValue(req.getInputStream(), commandType);

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
			throws IOException, DuplicateCommandException {
		
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
	public final String displayCommand(final @PathVariable("commandName") String commandName, UriComponentsBuilder builder) throws IOException, DuplicateCommandException {
		final Class<?> commandType = cqs.system().command(commandName).clazz;

		final InputStream is = this.getClass().getResourceAsStream("commandui.html");  
		final String rawHtml = IOUtils.toString(is);
		is.close();

		final String modifiedHtml = 
			rawHtml.replaceAll("##command##", commandType.getName())
				.replaceAll("##command_url##", builder.path("/commands/{commandName}").buildAndExpand(commandName).toUri().toString())
				.replaceAll("##schema_url##", builder.path("/schema").buildAndExpand(commandName).toUri().toString());
		
		return modifiedHtml;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public String handleException1(Exception ex) {
	    return ex.getMessage();
	}


	@RequestMapping(value = "/{commandName}/schema", method = RequestMethod.GET)
	@ResponseBody
	public final String schemaCommand(final @PathVariable("commandName") String commandName) throws JsonMappingException, DuplicateCommandException {

		final Class<?> commandType = cqs.system().command(commandName).clazz;
		
		final ObjectMapper mapper = new ObjectMapper();
		final JsonSchema schema = mapper.generateJsonSchema(commandType);

		final JsonNode node = schema.getSchemaNode();
		schemaAddTitle(null, node, commandType);

		return node.toString();
	}

	
	private <T extends Annotation> T findFieldOrGetterAnnotation(final Class<?> type, final String fieldName, final Class<T> annotationClass) {
		if(fieldName == null) {
			return type.getAnnotation(annotationClass);
		}
		
		final T get = findMethodAnnotation(type, "get" + StringUtils.capitalize(fieldName), annotationClass);
		if(get == null) {
			final T is = findMethodAnnotation(type, "is" + StringUtils.capitalize(fieldName), annotationClass);
			if(is == null) {
				final T field = findFieldAnnotation(type, fieldName, annotationClass);
				if(field == null) {
					final Class<?> childType = findFieldOrGetterType(type, fieldName);
					if(childType != null) {
						return childType.getAnnotation(annotationClass);
					}
					else {
						return null;
					}
				}
				else {
					return field;
				}
			}
			else {
				return is;
			}
		}
		else {
			return get;
		}
	}

	private <T extends Annotation> T findFieldAnnotation(final Class<?> type, final String fieldName, final Class<T> annotationClass) {
		try {
			final Field field = type.getDeclaredField(fieldName);
			return field.getAnnotation(annotationClass);
		}
		catch(NoSuchFieldException e) {
			return null;
		}
	}

	private <T extends Annotation> T findMethodAnnotation(final Class<?> type, final String methodName, final Class<T> annotationClass) {
		try {
			final Method method = type.getMethod(methodName);
			return method.getAnnotation(annotationClass);
		}
		catch(NoSuchMethodException e1) {
			return null;
		}
	}

	
	private Class<?> findFieldOrGetterType(final Class<?> type, final String fieldName) {
		if(fieldName == null) {
			return null;
		}
		
		final Class<?> get = findMethod(type, "get" + StringUtils.capitalize(fieldName));
		if(get == null) {
			final Class<?> is = findMethod(type, "is" + StringUtils.capitalize(fieldName));
			if(is == null) {
				final Class<?> field = findField(type, fieldName);
				return field;
			}
			else {
				return is;
			}
		}
		else {
			return get;
		}
	}

	private Class<?> findField(final Class<?> type, final String fieldName) {
		try {
			final Field field = type.getDeclaredField(fieldName);
			return field.getType();
		}
		catch(NoSuchFieldException e) {
			return null;
		}
	}

	private Class<?> findMethod(final Class<?> type, final String methodName) {
		try {
			final Method method = type.getMethod(methodName);
			return method.getReturnType();
		}
		catch(NoSuchMethodException e1) {
			return null;
		}
	}

	
	private void schemaAddTitle(final String name, final JsonNode node,
			final Class<?> type) {
		
		final Doc doc = findFieldOrGetterAnnotation(type, name, Doc.class);
		final Class<?> foundtype = findFieldOrGetterType(type, name);
		final Class<?> subtype = (foundtype == null)?type:foundtype;
				
		if (node.isObject() && node.has("type")) {
			final ObjectNode obj = (ObjectNode) node;
						
			if (null != doc) {
				obj.put("description", doc.value());
			}

			if( null != name) {
				obj.put("title", name);
			}
		}

		Iterator<String> nodes = node.fieldNames();

		while (nodes.hasNext()) {
			final String childName = nodes.next();
			schemaAddTitle(childName, node.get(childName), subtype);
		}

	}
	
}
