# SimulationApp
An application project for a proof of concept algorithm development use case.

## Algorithm Development in a DevSecOps Software Factory
The primary objective of this project is to demonstrate one approach to tailoring and applying DevSecOps practices within our domain.  The use case we're exploring is algorithm development.  In the past, we often see algorithm reference implementations created in a "heavy math" environment using simulations and monte-carlo analysis to evaluate changes and assess performance.  It is very common to see heavy Matlab use in this context which is almost always executed as a single threaded monolithic application.  The evaluation scenarios can often run for a very long time, sometimes days or weeks.  Here we want to explore
applying DoD CIO DevSecOps Reference Design guidance to this use case.  To that end, we're going to build pipelines to automate the build and packaging of our algorithm into a container.  We will then run that container within a Kubernetes environment.  Most commercial use cases for Kubernetes are focused on availability and scaling.  In this instance, we need to focus on job execution and load balancing across nodes within a Kubernetes cluster.  Obviously this is starting small and focusing on learning.  This should evolve over time as we experiment, learn, and improve.

## The Algorithm - Line Runner
We need a representative algorithm to support the proof of concept.  We're going to use a basic physics calculation for this purpose.  The algorithm will start at origin and accelerate/decellerate along a 1-dimensional path.  Scenarios are defined in separate projects (to support pipelining and container layering) and contain time ordered acceleration inputs in a CSV file.  The algorithm applies the acceleration to the current state and calculates position and velocity to establish a new state for each time step.  This algorithm utilizes instantaneous calculations, not accounting for ramping or other factors.


