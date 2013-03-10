package com.emo.mango.reflect;

import static org.junit.Assert.*;

import org.junit.Test;

import com.emo.mango.log.LogParam;
import com.google.common.base.Joiner;

public class ObjectAnnotationScannerTest {

	@Test
	public void test() {
		final DeposerPourUnUsage d = new DeposerPourUnUsage("ccc", 456, 4, new String[] {"rrr", "ttt"}, new Utilisateur("ced", "bou"));
	
		final ObjectAnnotationScanner oas = new ObjectAnnotationScanner(LogParam.class);
		final String[] props = oas.scan(d);
				
		assertEquals("numeroCompte, montant, utilisateur, validiteEnMois, services", Joiner.on(", ").join(props));
	}

}
