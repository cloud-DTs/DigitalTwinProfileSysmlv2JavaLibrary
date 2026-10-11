package org.example;

import Main.ResultConverter;
import org.junit.jupiter.api.BeforeEach;


public abstract class AbstractDTLibraryTest {

	private static final String MODELS = "src/test/resources/models/";

	protected DTLibraryParser parser;

	@BeforeEach
	public void setUp() throws Exception {
		parser = new DTLibraryParser();
	}

	protected ResultConverter parse(String model) throws Exception {
		return parser.parse(MODELS + model);
	}
}
