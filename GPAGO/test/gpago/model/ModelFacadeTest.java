package gpago.model;

import gpago.model.entity.Greyhound;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for ModelFacade class.  This test exercises persistence of entity objects to the
 * database. 
 *
 */
public class ModelFacadeTest {

	@Test
	public void testGreyhoundPersistence() {
		ModelFacade facade = new ModelFacade();

		// Record the number of Greyhound records before this test.
		int greyhoundCountBeforeTest = facade.getAllGreyhounds().size();
		
		// Create a new greyhound instance and persist it.
		Greyhound greyhound = new Greyhound();
		greyhound.setName("Test Greyhound");
		facade.saveGreyhound(greyhound);
		
		List<Greyhound> greyhounds = facade.getAllGreyhounds();
		
		Assert.assertEquals("The expected number of greyhounds were not returned.", 1 + greyhoundCountBeforeTest, greyhounds.size());
		
		greyhound = facade.getGreyhound(greyhound.getId());
		
		Assert.assertNotNull("The test greyhound was not found in the database.", greyhounds);
		Assert.assertEquals("The test greyhound was not found in the database.", "Test Greyhound", greyhound.getName());
		
		facade.removeGreyhound(greyhound);
		
		greyhounds = facade.getAllGreyhounds();
		
		Assert.assertEquals("The test greyhound was not removed from the database.", greyhoundCountBeforeTest, greyhounds.size());
		greyhound = facade.getGreyhound(greyhound.getId());
		Assert.assertNull("The test greyhound was not removed from the database.", greyhound);
	}
}
