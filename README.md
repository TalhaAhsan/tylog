# Tylog [![Build Status](https://travis-ci.org/ernestrc/tylog.svg?branch=master)](https://travis-ci.org/ernestrc/tylog)

# The problem
Server-side inhomogeneous logging can be frustrating to parse and analyze.

# The solution
Tylog provides a thin interface that abstracts over SLF4J's facade to provide compile time checks for your debug/info/error/warning messages and a type-safe [measure API](src/main/scala/build/unstable/tylog/TypedLogging.scala#L44).

# Contribute
If you would like to contribute to the project, please fork the project, include your changes and submit a pull request back to the main repository.

# TODO
- Compile time checks for message templates defined in the project's compilation unit

# License
MIT License 
