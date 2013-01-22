package com.emo.mango.spring.web.support;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.emo.mango.cqs.DuplicateCommandException;
import com.emo.mango.spring.cqs.support.MangoCQS;

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
			commands.add(mapper.readValue(commandHolder.command, commandType));
		}
		
		for(final Object command : commands) {
			validator.validate(command);
		}
		
		for(final Object command : commands) {
			cqs.bus().send(command);
		}

		return "ok";
	}

	
	@RequestMapping(value = "/{commandName}", method = RequestMethod.GET)
	@ResponseBody
	public final List<String> displayCommand(final @PathVariable("commandName") String commandName) throws DuplicateCommandException {
		final Class<?> commandType = cqs.system().command(commandName).clazz;

		final Method[] methods = commandType.getMethods();

		final List<String> methodNames = new LinkedList<String>(); 
		
		for (final Method method : methods) {
			if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
				methodNames.add(method.getName().replaceAll("^get", "") + " : " + method.getReturnType().getSimpleName());
			}
		}
		
		return methodNames;
	}
	
}
