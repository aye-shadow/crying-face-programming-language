# Crying Face Programming Language

This project implements a compiler for the Crying Face Programming Language. It includes lexical analysis, syntax analysis, and semantic analysis components.

## Project Structure
```
```

### Directories and Files
- **InputFiles/**: Contains input files for testing.
- **Scanner/**: Contains the scanner (lexical analyzer) implementation.
  - `Scanner.java`: Implements the scanner that reads input and generates tokens.
- **States/**: Contains state-related classes for DFA and NFA.
  - `DFA.java`: Implements the Deterministic Finite Automaton (DFA).
  - `NFA.java`: Implements the Nondeterministic Finite Automaton (NFA).
  - `RegularExpression.java`: Handles regular expressions.
  - `State.java`: Represents a state in the automaton.
  - `transition_table.txt`: Contains the transition table for the DFA.
- **Symbols/**: Contains symbol-related classes.
  - `Symbol.java`: Represents a symbol.
  - `SymbolTable.java`: Implements the symbol table.
- **LexicalAnalyser.java**: Main class for lexical analysis.
- **Main.java**: Entry point of the application.
- **LICENSE**: License file.
- **README.md**: This file.

## Getting Started

### Prerequisites

- 

### Building the Project

To build the project, run the following command:

```sh
javac Main.java
```

To run the project, use the following command:

```sh
java Main
```

### Testing

Place your test input files in the `InputFiles/` directory and modify the `Main.java` to read from these files.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

### License

This project is licensed under the MIT License - see the `LICENSE` file for details.
