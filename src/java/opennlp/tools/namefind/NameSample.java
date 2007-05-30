package opennlp.tools.namefind;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.ngram.Token;

public class NameSample {
    
    public static final String START_TAG = "<START>";
    public static final String END_TAG = "<END>";
    
    private final Token sentence[];
    private final Name names[];
    private final String additionalContext[][];
    
    public NameSample(Token sentence[], Name names[], 
	    String additionalContext[][]) {
	
	if (sentence == null) {
	    throw new IllegalArgumentException();
	}
	
	if (names == null) {
	    names = new Name[0];
	}
	
	this.sentence = sentence;
	this.names = names;
	this.additionalContext = additionalContext;
    }
    
    public NameSample(Token sentence[], Name[] names) {
	this(sentence, names, null);
    }
    
    public NameSample(String taggedTokens, String type) {

	String[] parts = taggedTokens.split(" ");

	List tokenList = new ArrayList(parts.length);
	List nameList = new ArrayList();
	
	int startIndex = -1;

	for (int pi = 0; pi < parts.length; pi++) {
	    if (parts[pi].equals(START_TAG)) {
		startIndex = pi;
	    } else if (parts[pi].equals(END_TAG)) {
		// create name
		nameList.add(new Name(type, startIndex, pi));
	    }
	    else {
		tokenList.add(Token.create(parts[pi]));
	    }
	}
	
	sentence = (Token[]) tokenList.toArray(new Token[tokenList.size()]);

	names = (Name[]) nameList.toArray(new Name[nameList.size()]);
	
	this.additionalContext = null;
    }

    public Token[] sentence() {
	return sentence;
    }
    
    public Name[] names() {
	return names;
    }
    
    public String[][] additionalContext() {
	return additionalContext;
    }
    
    public String toString() {
	
	Token sentence[] = sentence();

	StringBuilder result = new StringBuilder();

	for (int tokenIndex = 0; tokenIndex < sentence.length; tokenIndex++) {
	    // token

	    for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
		if (names[nameIndex].getBegin() == tokenIndex) {
		    result.append(START_TAG + ' ');
		}
		
		if (names[nameIndex].getEnd() == tokenIndex) {
		    result.append(END_TAG + ' ');
		}
	    }

	    result.append(sentence[tokenIndex].getToken() + ' ');
	}

	for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
	    if (names[nameIndex].getEnd() == sentence.length) {
		result.append(END_TAG + ' ');
	    }
	}
	
	result.setLength(result.length() - 1);
	
	return result.toString();
    }
}