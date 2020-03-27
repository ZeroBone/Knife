# Knife
Knife is a tool that reads input grammar in BNF format and converts it to a few Java classes that can parse the given grammar through a simple interface.

Knife doesn't require any external libraries or dependencies. All generation is done ahead-of-time. After generating the parsing classes you can just copy them into your project.

## Features

* No runtime dependencies, knife generates pure Java code that can easily be ported on other JVM-based languages.
* Parsing is done using push-down automata without recursion.
* Knife uses an explicit API for accepting the token stream. It allows you to easily use knife with any (including your own) lexer. You can pause and resume parsing at any point. Parsing multiple token streams simultaneously is also possible.
* No complete parse-trees are being built during parsing. Reduction of the tree is done on-the-fly for performance.
* Knife uses Knife to read it's input grammar.

## Limitations

* Knife generates only **top-down** parsers for **LL(1)** grammars. In some cases knife can help you disambiguate your grammar to make it LL(1).

## Getting Started

TODO

## Support

Please [open an issue](https://github.com/ZeroBone/Knife/issues) if you found a bug in Knife.

Any contribution support is very appreciated.

## Copyright

Copyright (c) 2020 Alexander Mayorov.

This software is licensed under the terms of the GNU General Public License, Version 3.

See the LICENCE file for more details.