# MedleyRaceSimulaxion

## Overview
This project is a multithreaded Java simulation of a 4x100 medley relay swimming race. Ten teams of four swimmers each compete in a relay, with a focus on fixing and enhancing code to meet synchronization constraints. The simulation ensures safety and liveness using advanced concurrency mechanisms in Java.

## Features
- **Start and Quit Buttons**: Start or terminate the race simulation anytime.
- **Grid-Based Stadium**: The stadium includes a pool, entrance door, starting line, and free areas represented on a grid.
- **Relay Race Dynamics**: Swimmers enter one at a time, line up, and swim their strokes in the correct order (backstroke, breaststroke, butterfly, freestyle). The race begins once all first-leg swimmers are in position.
- **Concurrency Control**: Swimmers are synchronized to follow rules and avoid conflicts, ensuring safe movement and race progression.

## Classes and Code Structure
- **MedleySimulaxion.java**: Main class to set up and manage the simulation, including the GUI.
- **CounterDisplay.java**: Manages the display and updates in the simulation's GUI.
- **FinishCounter.java**: Tracks the status of teams, determining the winner.
- **GridBlock.java**: Defines the grid blocks in the stadium.
- **StadiumGrid.java**: Manages and updates the stadium grid during the race.
- **PeopleLocation.java**: Stores and tracks swimmer positions on the grid.
- **StadiumView.java**: Visualizes the race through a threaded JPanel.
- **SwimTeam.java**: Represents a team of swimmers in the simulation.
- **Swimmer.java**: Represents each swimmer as an individual thread with synchronized behavior.

## Running the Simulation
To run the simulation on Linux or macOS:

1. Clone this repository:

```bash
git clone https://github.com/NtandoMgo/MedleyRaceSimulaxion.git
cd MedleyRaceSimulaxion
```

2. Run the simulation:

```bash
make run
```

## Synchronization Mechanisms
This simulation uses concurrency mechanisms to ensure:

- Swimmers follow rules for entering and racing in a synchronized manner.
- No two swimmers occupy the same space on the grid simultaneously.
- Team coordination, ensuring each swimmer starts only after their teammate finishes.

## License
This project is for educational purposes, designed as part of the CSC2002S course at the University of Cape Town.
