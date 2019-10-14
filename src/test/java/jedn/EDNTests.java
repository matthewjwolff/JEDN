/*******************************************************************************
 * Copyright (C) 2019 mjw
 * 
 * JEDN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JEDN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package jedn;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.junit.Test;

import io.wolff.jedn.EDNSymbol;
import io.wolff.jedn.StackListener;
import io.wolff.jedn.edn.EDNLexer;
import io.wolff.jedn.edn.EDNParser;

public class EDNTests {
	
	private void verifyParse(Object expected, String edn) {
		// TODO: don't need to construct a new object
		StackListener l = new StackListener();
		EDNParser p = new EDNParser(new CommonTokenStream(new EDNLexer(CharStreams.fromString(edn))));
		p.addParseListener(l);
		p.addErrorListener(ConsoleErrorListener.INSTANCE);
		p.element();
		assertEquals(expected, l.getLastElement());
	}

	@Test
	public void testBuiltins() {
		verifyParse(null, "nil");
		verifyParse(true, "true");
		verifyParse(false, "false");
		verifyParse("Test", "\"Test\"");
		verifyParse(123, "123");
		verifyParse(12.5, "12.5");
		// TODO: char lit
		verifyParse(new EDNSymbol("symbol"), "symbol");
//		// TODO: more symbol cases
		verifyParse(new HashSet<EDNSymbol>(Arrays.asList(new EDNSymbol("a"), new EDNSymbol("b"))), "#{a b}");
		HashMap<String, Object> expected = new HashMap<>();
		expected.put("e", 4);
		verifyParse(expected, "{\"e\" 4}");
		verifyParse(UUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"), "#uuid \"f81d4fae-7dec-11d0-a765-00a0c91e6bf6\"");
	}

}
