package io.wolff.jedn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

import io.wolff.jedn.edn.EDNLexer;
import io.wolff.jedn.edn.EDNParser;

public class EDN {
	public static Object deserializeEDN(InputStream is) throws IOException {
		StackListener l = new StackListener();
		EDNParser p = new EDNParser(new CommonTokenStream(new EDNLexer(CharStreams.fromStream(is))));
		p.addParseListener(l);
		p.addErrorListener(ConsoleErrorListener.INSTANCE);
		p.element();
		return l.getLastElement();
	}
	
	public static void serializeEDN(OutputStream os, Object o) throws IOException {
		if(o==null) {
			os.write("nil".getBytes());
		} else if(o instanceof Number) {
			os.write(o.toString().getBytes());
		} else if(o instanceof String) {
			os.write(("\""+o.toString()+"\"").getBytes());
		} else if(o instanceof Character) {
			String s;
			switch((Character) o) {
			case ' ':
				s = "space";
				break;
			case '\n':
				s="newline";
				break;
			case '\r':
				s="return";
				break;
			case '\t':
				s="tab";
				break;
			default:
				s = o.toString();
			}
			os.write(("\\"+s).getBytes());
		} // TODO: others
		else {
			throw new IllegalArgumentException("Do not know how to handle type "+o.getClass().getCanonicalName());
		}
	}
}
