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
package io.wolff.jedn;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;

import io.wolff.jedn.edn.EDNBaseListener;
import io.wolff.jedn.edn.EDNParser.BoolContext;
import io.wolff.jedn.edn.EDNParser.FloatlitContext;
import io.wolff.jedn.edn.EDNParser.IntlitContext;
import io.wolff.jedn.edn.EDNParser.KeywordContext;
import io.wolff.jedn.edn.EDNParser.ListContext;
import io.wolff.jedn.edn.EDNParser.MapContext;
import io.wolff.jedn.edn.EDNParser.NilContext;
import io.wolff.jedn.edn.EDNParser.SetContext;
import io.wolff.jedn.edn.EDNParser.StringlitContext;
import io.wolff.jedn.edn.EDNParser.SymbolContext;
import io.wolff.jedn.edn.EDNParser.TagContext;
import io.wolff.jedn.edn.EDNParser.VectorContext;

final class Sentinel {
	static Sentinel INSTANCE = new Sentinel();
	private Sentinel() {}
}

public class StackListener extends EDNBaseListener {
	private Stack<Object> stack = new Stack<>();
	private Map<String, Function<Object, Object>> tags = new HashMap<>();
	
	public StackListener() {
		tags.put("uuid", obj -> UUID.fromString((String) obj));
		tags.put("inst", obj -> Instant.parse((String) obj));
	}
	
	public Object getLastElement(){
		Object element = stack.pop();
		if(!stack.isEmpty()) {
			throw new RuntimeException("Illegal parse: stack was not cleared");
		}
		return element;
	}
	
	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		stack.add(Sentinel.INSTANCE);
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		Object top = stack.pop();
		if(!Sentinel.INSTANCE.equals(top)) {
			stack.pop();
			stack.add(top);
		}
	}

	@Override
	public void exitNil(NilContext ctx) {
		stack.add(null);
	}

	@Override
	public void exitBool(BoolContext ctx) {
		stack.add("true".equals(ctx.getText()));
	}

	@Override
	public void exitStringlit(StringlitContext ctx) {
		String text = ctx.getText();
		stack.add(text.substring(1, text.length()-1));
	}

	@Override
	public void exitSymbol(SymbolContext ctx) {
		stack.add(new EDNSymbol(ctx.getText()));
	}

	@Override
	public void exitSet(SetContext ctx) {
		HashSet<Object> val = new HashSet<>();
		populateCollection(val);
	}
	
	@Override
	public void exitList(ListContext ctx) {
		LinkedList<Object> val = new LinkedList<>();
		populateCollection(val);
	}

	@Override
	public void exitVector(VectorContext ctx) {
		ArrayList<Object> val = new ArrayList<>();
		populateCollection(val);
	}

	private void populateCollection(Collection<Object> val) {
		Object tip;
		do {
			tip = stack.pop();
			if(!Sentinel.INSTANCE.equals(tip)) {
				val.add(tip);
			}
		} while(!Sentinel.INSTANCE.equals(tip));
		stack.add(Sentinel.INSTANCE);
		stack.add(val);
	}

	@Override
	public void exitMap(MapContext ctx) {
		HashMap<Object, Object> map = new HashMap<>();
		Object tip;
		do {
			tip = stack.pop();
			if(!Sentinel.INSTANCE.equals(tip)) {
				Object key = stack.pop();
				map.put(key, tip);
			}
		} while(!Sentinel.INSTANCE.equals(tip));
		stack.add(Sentinel.INSTANCE);
		stack.add(map);
	}

	@Override
	public void exitKeyword(KeywordContext ctx) {
		stack.add(new EDNKeyword(ctx.getText().substring(1)));
	}

	@Override
	public void exitFloatlit(FloatlitContext ctx) {
		stack.add(Double.parseDouble(ctx.getText()));
	}

	@Override
	public void exitIntlit(IntlitContext ctx) {
		stack.add(Integer.parseInt(ctx.getText()));
	}

	@Override
	public void exitTag(TagContext ctx) {
		Object val = stack.pop();
		EDNSymbol symbol = (EDNSymbol) stack.pop();
		Function<Object, Object> processor = this.tags.get(symbol.name);
		if(processor == null) {
			stack.add(new UnknownTag(symbol.name, val));
		} else {
			stack.add(processor.apply(val));
		}
	}
	
	
	
	
}
