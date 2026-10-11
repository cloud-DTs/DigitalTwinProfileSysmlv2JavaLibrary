package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidModelTest extends AbstractDTLibraryTest {

	private void assertFails(Class<? extends Exception> type, String model, String message) {
		Exception exception = assertThrows(type, () -> parse(model));
		assertTrue(exception.getMessage().contains(message), exception.getMessage());
	}

	@Test
	public void testConstAttributeHasNoStringValue(){
		assertFails(IllegalArgumentException.class, "constAttributeHasNoStringValue", "expected LiteralUsage");	}

	@Test
	public void plainAttributeTakesNoReference() {
		assertFails(IllegalArgumentException.class, "referenceInPlainAttribute", "expected LiteralUsage");
	}

	@Test
	public void constValueMustBePlainString() {
		assertFails(IllegalArgumentException.class, "constValueCalculation", "mapped as nothing");
	}

	@Test
	public void operandMustBePlainReference() {
		assertFails(IllegalArgumentException.class, "operandCalculation", "mapped as nothing");
	}

	@Test
	public void pathToCodeIsRequired() {
		assertFails(IllegalStateException.class, "missingPathToCode", "slot 'pathToCode' is required exactly once");
	}

	@Test
	public void outputParameterMustBeReference() {
		assertFails(IllegalStateException.class, "outputParameterLiteral", "Bound features should have conforming types");
	}

	@Test
	public void constAttributeIsNoMeasurement() {
		assertFails(IllegalArgumentException.class, "constAttributeInComponent", "'limit' does not fit any slot");
	}

	@Test
	public void featureWithoutSlotIsRejected() {
		assertFails(IllegalArgumentException.class, "measureAttributeInEntity", "'temperature' does not fit any slot");
	}
}
