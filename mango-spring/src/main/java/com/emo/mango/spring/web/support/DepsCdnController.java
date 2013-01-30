package com.emo.mango.spring.web.support;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DepsCdnController {

	private void serve(final String root, final String relPath, final HttpServletResponse response) throws IllegalAccessException, IOException {
		System.out.println("relPath : " + relPath);
		
		final String contentType;
		if(relPath.endsWith(".js")) {
			contentType = "application/javascript";
		}
		else if(relPath.endsWith(".css")) {
			contentType = "text/css";
		}
		else {
			contentType = "application/stream";
		}
		
		if (!relPath.matches("[a-zA-Z0-9\\/\\-\\.]+")) {
			throw new IllegalAccessException(
					"attempt to load cdn file with forbidden chars");
		}

		response.setContentType(contentType);
		
		final InputStream is = this.getClass().getResourceAsStream(root + "/" + relPath);
		if(is == null) {
			throw new IllegalAccessException("no such file " + root + "/" + relPath);
		}
		IOUtils.copy(is, response.getOutputStream());

		response.flushBuffer();
	}
	
	@RequestMapping(value = "/deps/**", method = RequestMethod.GET)
	@ResponseBody
	public final void getCdnFromDeps(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, IllegalAccessException {

		final String uri = request.getRequestURI();
		final String relPath = uri.substring(
				uri.indexOf("/deps/") + 6);
				
		serve("deps", relPath, response);
	}

	@RequestMapping(value = "/lib/**", method = RequestMethod.GET)
	@ResponseBody
	public final void getCdnFromLib(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, IllegalAccessException {

		final String uri = request.getRequestURI();
		final String relPath = uri.substring(
				uri.indexOf("/lib/") + 5);
				
		serve("lib", relPath, response);
	}	
}
