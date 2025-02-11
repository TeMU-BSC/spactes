package de.unihd.dbs.uima.annotator.treetagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.ctakes.core.util.ListFactory;

//import de.unihd.dbs.uima.types.heideltime.Sentence;
//import de.unihd.dbs.uima.types.heideltime.Token;


import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.Lemma;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.ctakes.typesystem.type.textspan.Sentence;


public class TreeTaggerReader implements Runnable {
	private List<BaseToken> tokens;
	private BufferedReader reader;
	private JCas jcas;
	private Boolean annotate_sentences;
	
	private int i; // position in list

	// Possible End-of-Sentence Tags
	private static final HashSet<String> hsEndOfSentenceTag = new HashSet<String>(
			Arrays.asList(new String[] {
				"SENT",		// ENGLISH, FRENCH, GREEK, ...
				"$.",		// GERMAN, DUTCH
				"FS",		// SPANISH
				"_Z_Fst",	// ESTONIAN
				"_Z_Int",	// ESTONIAN
				"_Z_Exc",	// ESTONIAN
				"ew",		// CHINESE
			})
		);
	
	public TreeTaggerReader(List<BaseToken> tokens, BufferedReader reader, JCas jcas, Boolean annotate_sentences) {
		this.tokens = tokens;
		this.reader = reader;
		this.jcas = jcas;
		this.annotate_sentences = annotate_sentences;
	}
	
	@Override
	public void run() {
		i = 0;
		try {
			Boolean isStarted = false;
			Sentence sentence = null;
			String s = null;
			// wait for the starting token to arrive
			while(null != (s = reader.readLine()) && !isStarted) {
				if(TreeTaggerProperties.STARTOFTEXT.equals(s)) {
					isStarted = true;
					break;
				}
			}
			
			// iterate over all the output lines and tokens array (which have the same source and are hence symmetric)
			while(null != (s = reader.readLine()) && isStarted) {
				// check whether this is the finalizing token
				if(TreeTaggerProperties.ENDOFTEXT.equals(s)) {
					isStarted = false;
					break;
				}
				
				// do splitting
				//System.out.println(s.toString());
				String[] splits = s.split("\t", 3);
				String tokenStr = splits[0].trim(), pos = "", lemma = "";
				if(splits.length == 3) {
					pos = splits[1].trim();
					lemma = splits[2].trim();
				}
				
				// grab a token
				BaseToken token = tokens.get(i++);
				// Handle empty tokens (such as empty lines) in input file
//				while("".equals(token.getCoveredText())) {
//					// if part of the configuration, also add sentences to the jcas document
//					if("EMPTYLINE".equals(token.getPos()) && annotate_sentences) {
//						// Establish sentence structure
//						if(sentence == null) {
//							sentence = new Sentence(jcas);
//							sentence.setBegin(token.getBegin());
//						}
//		
//						// Finish current sentence if end-of-sentence pos was found or document ended
//						sentence.setEnd(token.getEnd());
//						if(sentence.getBegin() < sentence.getEnd()){
//							sentence.addToIndexes();
//						}
//						
//						// Make sure current sentence is not active anymore so that a new one might be created
//						sentence = null;
//					}
//					token.removeFromIndexes();
//					token = tokens.get(i++);
//				}
				// remove tokens, otherwise they are in the index twice
//				token.removeFromIndexes(); 
				// set part of speech tag and add to indexes again
				if(!"".equals(token.getCoveredText())) {
//					token.setPos(pos);
					token.setPartOfSpeech(pos);
					
//					Map<String, Set<String>> lemmaMap = null;
//					Collection<Lemma> lemmas = new ArrayList<>(1);
//					
//					Lemma lemmaa = new Lemma(jcas);
//					lemmaa.setKey(lemma);
//					lemmaa.setPosTag(pos);
//					lemmas.add(lemmaa);
//					
//					
//					Lemma[] lemmaArray = lemmas.toArray(new Lemma[lemmas.size()]);
//					FSList fsList = ListFactory.buildList(jcas, lemmaArray);
//					token.setLemmaEntries(fsList);
					
//					token.setLemma(lemma);
					token.addToIndexes();//System.out.println("added token " + token.getCoveredText() + " -> " + pos); // TODO
				}
				
				// if part of the configuration, also add sentences to the jcas document
//				if(annotate_sentences) {
//					// Establish sentence structure
//					if(sentence == null) {
//						sentence = new Sentence(jcas);
//						sentence.setBegin(token.getBegin());
//					}
//	
//					// Finish current sentence if end-of-sentence pos was found or document ended
//					if(hsEndOfSentenceTag.contains(pos) || i == tokens.size()) {
//						sentence.setEnd(token.getEnd());
//						sentence.addToIndexes();
//						
//						// Make sure current sentence is not active anymore so that a new one might be created
//						sentence = null;
//					}
//				}
			}
			
//			while(i < tokens.size()) {
//				Token currentToken = tokens.get(i++);
//				
//				if(sentence != null) {
//					sentence.setEnd(tokens.get(tokens.size() - 1).getEnd());
//					sentence.addToIndexes();
//				}
//				
//				if("EMPTYLINE".equals(currentToken.getPos())) {
//					currentToken.removeFromIndexes();
//				}
//			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
