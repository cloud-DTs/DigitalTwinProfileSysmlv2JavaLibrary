package org.example;

import Main.ResultConverter;
import org.example.Model.Attribute.InputParameterUsage;
import org.example.Model.Attribute.MeasureAttributeUsage;
import org.example.Model.Component.ComponentUsage;
import org.example.Model.Condition.ConditionUsage;
import org.example.Model.Entity.EntityUsage;
import org.example.Model.Strategy.StrategyActionUsage;
import org.example.Model.Twin.TwinDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidModelTest extends AbstractDTLibraryTest {

	private TwinDefinition twin(ResultConverter result) {
		List<TwinDefinition> twins = result.getByType(TwinDefinition.class).stream().filter(t -> !t.isLibrary()).toList();
		assertEquals(1, twins.size());
		return twins.getFirst();
	}

	private EntityUsage entity(TwinDefinition twin, String name) {
		return twin.getEntities().stream().filter(e -> name.equals(e.getName())).findFirst().orElseThrow();
	}

	@Test
	public void entitiesAndComponents() throws Exception {
		TwinDefinition twin = twin(parse("valid"));
		assertEquals(List.of("battery", "KebaP30", "SieVersicharge", "SieChargeCC", "pv", "SieChargeD", "SieCPC20"),
				twin.getEntities().stream().map(EntityUsage::getName).toList());

		EntityUsage keba = entity(twin, "KebaP30");
		assertEquals(List.of("p13"), keba.getComponents().stream().map(ComponentUsage::getName).toList());
		assertEquals(List.of("maxConsumption"), keba.getConstAttributes().stream().map(c -> c.getName()).toList());
		assertEquals(List.of("power"), keba.getComponents().getFirst().getMeasurements().stream().map(MeasureAttributeUsage::getName).toList());
	}

	@Test
	public void conditionReferencesResolveToMappedInstances() throws Exception {
		TwinDefinition twin = twin(parse("valid"));
		EntityUsage keba = entity(twin, "KebaP30");
		ComponentUsage p13 = keba.getComponents().getFirst();
		MeasureAttributeUsage<?, ?> power = p13.getMeasurements().getFirst();

		ConditionUsage condition = twin.getConditions().getFirst();
		assertEquals(power.getId(), condition.getLeftOperand().orElseThrow().getReference().orElseThrow().getId());
		assertEquals(keba.getConstAttributes().getFirst().getId(), condition.getRightOperand().orElseThrow().getReference().orElseThrow().getId());

		StrategyActionUsage action = condition.getStrategyActions().getFirst();
		assertEquals(p13.getId(), action.getComponentTopic().orElseThrow().getReference().orElseThrow().getId());
		assertEquals(2, action.getInputParameters().size());
		for (InputParameterUsage input : action.getInputParameters()) {
			assertEquals(power.getId(), input.getReference().orElseThrow().getId());
		}
	}

	@Test
	public void requiredStrategyActionSlots() throws Exception {
		StrategyActionUsage action = twin(parse("valid")).getConditions().getFirst().getStrategyActions().getFirst();
		assertNotNull(action.getActionName());
		assertNotNull(action.getPathToCode());
		assertNotNull(action.getExternal());
		assertEquals("LAMBDA", action.getStrategyType().getValue().orElseThrow().name());
		assertEquals("INTERN", action.getFeedbackTopicType().orElseThrow().getValue().orElseThrow().name());
	}

	@Test
	public void outputParametersReferenceMeasurements() throws Exception {
		TwinDefinition twin = twin(parse("valid"));
		EntityUsage keba = entity(twin, "KebaP30");
		StrategyActionUsage action = twin.getConditions().getFirst().getStrategyActions().getFirst();
		assertEquals(List.of(keba.getComponents().getFirst().getMeasurements().getFirst().getId(), keba.getConstAttributes().getFirst().getId()),
				action.getOutputParameters().stream().map(output -> output.getReference().orElseThrow().getId()).toList());
	}
}
