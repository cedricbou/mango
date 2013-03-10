package com.emo.mango.log.params;

import java.util.Collection;

import com.emo.mango.log.LogParam;
import com.emo.mango.reflect.ObjectAnnotationScanner;
import com.emo.mango.reflect.ObjectReflectGetter;
import com.google.common.base.Optional;

abstract class Arg {
	protected abstract String simple();

	protected abstract String detailed();

	@SuppressWarnings("rawtypes")
	protected static Arg buildArg(final String name, final Object arg) {
		if (arg == null) {
			return new SimpleArg(name, "null");
		}
		if (arg instanceof Number || arg instanceof String) {
			return new SimpleArg(name, arg.toString());
		} else if (arg instanceof Collection) {
			final Arg[] args = new Arg[((Collection) arg).size()];

			int i = 0;
			for (final Object item : (Collection) arg) {
				args[i++] = buildArg(Integer.toString(i - 1), item);
			}

			return new ArrayArg(name, args);
		} else if (arg instanceof Object[]) {
			final Arg[] args = new Arg[((Object[]) arg).length];

			int i = 0;
			for (final Object item : (Object[]) arg) {
				args[i++] = buildArg(Integer.toString(i), item);
			}

			return new ArrayArg(name, args);
		} else {
			final ObjectAnnotationScanner oas = new ObjectAnnotationScanner(
					LogParam.class);
			final ObjectReflectGetter org = new ObjectReflectGetter(arg);

			final String[] props = oas.scan(arg);
			final Arg[] args = new Arg[props.length];

			int i = 0;
			for (final String prop : props) {
				final Optional<Object> value = org.get(prop);

				if (value.isPresent()) {
					args[i] = buildArg(prop, value.get());
				}

				++i;
			}

			return new ArrayArg(name, arg.getClass().getSimpleName(), args);
		}
	}

}