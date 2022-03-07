## Abstract
Since Craig Reynold's original formulation of Boids in 1986, these autonomous agents have been the subject of many studies in evolutionary computation that seek to perfect the weights of Boid characteristics relative to one another to display proper flocking behavior. This study adds a dimension to such work, using competitive co-evolution to evolve two sets of "predator" and "prey" Boids to not only display traits of flocking behavior but also to learn how to survive in their assigned predator/prey roles. 

This work creates, from scratch:<br>
**(1)** a Boid system with additional predation and avoidance characteristics for predator and prey Boids<br>
**(2)** an evolutionary computation framework<br>
**(3)** an elitist, co-evolutionary genetic algorithm to train predator and prey Boids<br>
**(4)** a co-evolutionary evolutionary strategies approach for Boid training<br>
**(5)** a non-co-evolutionary genetic algorithm for either prey or predator Boid training.<br>

By comparing the effectiveness of these approaches to one another,  it is possible to better understand the best framework and techniques for competitive co-evolution in artificial life, and how artificial life responds to such competitive co-evolution.

---
## Foundational EA

Firstly, Boid individuals are represented for evolutionary computation through a floating point vector of length 4. These floating points, which can take on values between 1 and 5 inclusive, represent the weights of separation, alignment, cohesion, and instinct (predation/escape) respectively. Importantly, these weights are relative to one another, meaning that a Boid with a genome of `[5,5,5,5]` is equivalent to a Boid with genome `[1,1,1,1]`. Limiting values to between 1 and 5 indicates that one weight may be favored up to 5 times as much as another. One such individual is used to create an entire flock of Boids with identical genotypes for simulation.
<br><br>
These fitness of these Boids may be tested through simulation using the Boid system explained in (1.D). Once initialized within a simulation, predator and prey Boids will run for a total of 500 frames. Upon the conclusion of a simulation, the fitness of both the predator and prey flock is recorded. Fitness is simply measured for predators as `n`, where `n` is the number of prey remaining after the simulation has concluded. For prey, fitness is measured using `k-n`, where `k` is the initial number of prey in the simulation and `n` is the number of prey at the simulation's conclusion. These fitness functions, though simple, allow both predator and prey flocks freedom to evolve without introducing arbitrary constraints, while still encouraging flocks to complete their species objective.
<br><br>
Critically, the non-deterministic nature of predator-prey simulations laid out in the Boid system (1.D) may tend to produce inconsistent fitnesses for a given genotype. To compensate for this, the evolutionary algorithms use rigorous retesting of each Boid individual to approximate their true fitness as accurately as possible. In order to do this, each generation of the evolutionary algorithms used involves testing individuals in a variety of trials, each of which consist of a number of simulations. Simply put, many simulations compose a trial, many trials compose a generation, and many generations compose a completed run.
<br><br>
Each generation begins by shuffling the population array of predator and prey individuals, so that each predator individual is paired up against a random prey flock. This indicates the beginning of a trial. During this trial, each predator flock competes against its corresponding prey flock in a single 500-frame simulation as described above. Once all predator and prey flocks have completed one simulation with their matched opponent, a single simulation has been completed. Many of these simulations are conducted in a single trial to generate an accurate average fitness of each individual (flock). Once all simulations have been completed, the predator and prey individuals are reshuffled so that they may be paired with a new opponent. The above described simulation process occurs, and this new trial is completed. While conducting multiple simulations helps average out the fitness of an individual, trials help determine their true fitness relative to all of their competitors. Once a number of trials has been conducted, the final average fitness of each individual is computed.
<br><br>
Using this fitness, parent selection, crossover, and mutation occurs using the implementation details discussed later. These children are then used in the subsequent generation, which follows the same process as above.
