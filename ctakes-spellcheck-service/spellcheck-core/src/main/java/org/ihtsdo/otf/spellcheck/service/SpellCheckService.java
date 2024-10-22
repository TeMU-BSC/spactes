package org.ihtsdo.otf.spellcheck.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.RAMDirectory;
import org.apache.log4j.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellCheckService {

	private SpellChecker spellChecker;

	private static final String CHECK_ERROR = "Problem checking and suggesting words with spell checker.";
	private static final int MIN_WORD_LENGTH = 3; // The Lucene SpellChecker does not deal with words shorter than this
	private static final Pattern LOOSE_NUMBER_PATTERN = Pattern.compile("^[\\d,.]+$");
	public static final Pattern MEASUREMENT_PATTERN = Pattern.compile("[,.\\d]+([^\\d]+)");

//	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Logger logger = Logger.getLogger(getClass());

	public SpellCheckService() throws IOException {
		clearIndex();
	}

	public void clearIndex() throws IOException {
		spellChecker = new SpellChecker(new RAMDirectory());
	}

	public void loadDictionary(Reader reader) throws IOException {
		spellChecker.indexDictionary(new PlainTextDictionary(reader), new IndexWriterConfig(null, new StandardAnalyzer(null)), true);
	}

	
	public void loadDirectoryOfDictionaries(InputStream DirectoryPath, String filename) throws IOException {
		
		Reader reader = new InputStreamReader(DirectoryPath);
		loadDictionary(reader);
		logger.info(filename + " dictionary loaded");

		

	}

	public void loadDirectoryOfDictionaries(String dictionariesDirectoryPath) throws IOException {
				

		File dictionariesDir = new File(dictionariesDirectoryPath);
		if (!dictionariesDir.isDirectory()) {
			logger.error("Specified dictionaries directory is not a directory: " + dictionariesDir.getAbsolutePath());
			return;
		}

		long dictionariesLoaded = 0;
		final File[] files = dictionariesDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".txt")) {
					logger.info("Loading dictionary "+ file.getName());
					FileReader reader = new FileReader(file);
					loadDictionary(reader);
					dictionariesLoaded++;
				}
			}
		}

		if (dictionariesLoaded > 0) {
			logger.info(dictionariesLoaded + " dictionaries loaded");
		} else {
			logger.error("No dictionaries loaded.");
		}
	}

	public Map<String, List<String>> checkWordsReturnErrorSuggestions(Collection<String> words, float accuracy) {
		Map<String, List<String>> errorSuggestions = new HashMap<>();
		try {
			for (String word : words) {
				Matcher matcher = MEASUREMENT_PATTERN.matcher(word);
				if (matcher.matches()) {
					word = matcher.group(1);
				}
				if (word.length() >= MIN_WORD_LENGTH && !LOOSE_NUMBER_PATTERN.matcher(word).matches() && !spellChecker.exist(word)) {
					errorSuggestions.put(word, Arrays.asList(spellChecker.suggestSimilar(word, 5, accuracy)));
				}
			}
		} catch (IOException e) {
			logger.error(CHECK_ERROR, e);
			throw new InternalError(CHECK_ERROR);
		}
		return errorSuggestions;
	}
	
	public static void main( final String... args ) throws IOException {
		SpellCheckService spellchecker = new SpellCheckService();
		spellchecker.loadDirectoryOfDictionaries("src/test/resources/dictionaries");
		Map<String, List<String>> suggestions = spellchecker.checkWordsReturnErrorSuggestions(
				Arrays.asList("Lesion" ),0.8f);
		for (String key :suggestions.keySet()) {
			System.out.println(key + ":" + suggestions.get(key));

		}

		
	}


}
