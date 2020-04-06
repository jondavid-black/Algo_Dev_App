package org.devopsfordefense.linerunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class LineRunnerTest {

	@Test
	void testOneStep() {

		System.out.println("Running testOneStep");

		LineRunner runner = new LineRunner();

		runner.step(10.0, 1.0);

		// ten seconds of constant 1m/s^2 acceleration
		// x = 0.5 * a * t^2 = 0.5 * 1.0 * 10.0^2 = 50.0
		// v = a * t = 1.0 * 10.0 = 10.0

		assertEquals(10.0, runner.getTimeSec());
		assertEquals(50.0, runner.getPosition());
		assertEquals(10.0, runner.getVelocity());
	}

	@Test
	void testMultipleSteps() {

		LineRunner runner = new LineRunner();

		for (int i=1; i<=10; i++) {
			
			double xInit = runner.getPosition();
			double vInit = runner.getVelocity();

			runner.step((double)i, 2.0);

			// check intermediate values
			// x = currPos + vInit * t + 0.5 * a * t^2 (note t == 1 for this test)
			double expX = xInit + vInit + 0.5 * 2.0;
			// v = vInit * a * t (note t == 1 for this test)
			double expV = vInit + 2.0;

			assertEquals(expX, runner.getPosition());
			assertEquals(expV, runner.getVelocity());

			System.out.println("Step " + i + ": x=" + runner.getPosition() + " v=" + runner.getVelocity());

		}

		// ten seconds of constant 2m/s^2 acceleration
		// x = 0.5 * a * t^2 = 0.5 * 2.0 * 10.0^2 = 100.0
		// v = a * t = 2.0 * 10.0 = 20.0

		assertEquals(10.0, runner.getTimeSec());
		assertEquals(100.0, runner.getPosition());
		assertEquals(20.0, runner.getVelocity());

	}

}
