# Compilers

## About

This repository contains the assignments for the course Compilers (k31) of DIT-NKUA.

## Project 1 - LL(1) Calculator Parser - Translator to Java

Project 1 consists of two parts. 

### Part 1
In this part of this project I have created a recursive descend parser written in Java that implements a simple LL(1) grammar. This grammar is:

```
goal  --> exp
exp   --> term exp2

exp2  --> + term exp2
        | - term exp2
        | ε

term  --> factor term2

term2 --> * factor term2
        | / factor term2
        | ε

factor--> num
        | ( exp )  
        | ε    

num   --> 0
        | 1
        | 2
        | 3
        | 4
        | 5
        | 6
        | 7
        | 8
        | 9
```

Briefly, this program is a simple calculator that accepts expressions with addition, subtraction, multiplication & division operators, as well as parenthesses.

#### Compile & Execution

To build this project just run `make` and to clean run `make clean`. To execute you can simply run it by `java Main` or `java Main < [input_file]`.

### Part 2

In the second part Ι implemented a parser and translator for a language supporting string operations. The language supports the concatenation operator over strings, function definitions and calls, conditionals (if-else i.e, every "if" must be followed by an "else"), and the following logical expressions:

* string equality (string1 = string2): Whether string1 is equal to string2.
* is-substring-of (string1 in string2): Whether string1 is a substring of/is contained in string2.

The parser, based on a context-free grammar, will translate the input language into Java.

#### Examples of the input language

```javascript
name()  {
    "John"
}

surname() {
    "Doe"
}

fullname(first_name, sep, last_name) {
    first_name + sep + last_name
}

name()
surname()
fullname(name(), " ", surname())
```

```
name() {
    "John"
}

repeat(x) {
    x + x
}

condRepeat(c, x) {
    if (c = "yes")
        repeat(x)
    else
        x
}

condRepeat("yes", name())
condRepeat("no", "Jane")
```

```
findLangType(langName) {
    if (langName = "Java")
        "Static"
    else
        if ("script" in langName)
            if ("Java" in langName)
                "Dynamic"
            else
                "Probably Dynamic"
        else
            "Unknown"
}

findLangType("Java")
findLangType("Javascript")
findLangType("Typescript")
```

#### Tools & Frameworks used
* [JFlex](http://jflex.de/): scanner generator for Java.
* [JavaCUP](http://www2.cs.tum.edu/projects/cup/index.php): parser generator.

#### Compile & Execution

To build this project just run `make` and to clean run `make clean`. To execute you can simply run it by `make execute < [input_file]`.

## Project 2 - MiniJava Static Checking (Semantic Analysis)

In this project I had to built the 1st part of a compiler for MiniJava. 

MiniJava is a subset of Java, fully oriented language but it does not allow global functions. Only classes, fields and methods are allowed and the basic types are int, boolean and int[] (array of int). The MiniJava in BNF form can be found [here](https://github.com/VangelisTsiatouras/compilers-di/blob/master/project_2/documentation/BNF%20for%20minijava.jj) and in JavaCC form can be found [here](https://github.com/VangelisTsiatouras/compilers-di/blob/master/project_2/src/mini-java.jj).

The type checker is written in Java. To implement this I followed the [visitor pattern](https://en.wikipedia.org/wiki/Visitor_pattern) and more specific the type check visitors are subclasses of the visitors that generated by JTB Framework. The type checking process is split in two stages. The first stage consists of the initialization of the symbol table (store class names, fields, method names, parameters & variables inside methods) of the input MiniJava program and to catch some easy errors such as duplicate declarations. In the second stage, all the expressions & statements inside the methods of the input program are checked for their legality. Also the type checker produces a [v-table](https://en.wikipedia.org/wiki/Virtual_method_table) for the syntactical correct input programs.

#### Tools & Frameworks used
* [JavaCC](https://javacc.org/): Java parser generator.
* [JTB](https://github.com/VangelisTsiatouras/compilers-di/tree/master/project_2/documentation/jtb-javacc-2017): Java Tree Builder.  It takes a plain JavaCC grammar file as input and automatically generates the following: 

   * A set of syntax tree classes based on the productions in the grammar, utilizing the Visitor design pattern.
   * Two interfaces: Visitor and GJVisitor.  Two depth-first visitors: DepthFirstVisitor and GJDepthFirst, whose default methods simply visit the children of the current node.
   * A JavaCC grammar jtb.out.jj with the proper annotations to build the syntax tree during parsing.


#### Compile & Execution

To build this project just run `make` and to clean run `make clean`. To execute you can simply run it by `java Main [file1] , [file2],...,[fileN] `.


## Project 3 - Generating intermediate code (MiniJava -> LLVM)

This project completes the compiler that I was assigned to build. The implementation to this code generator is similar with the Project 2 (visitor pattern impl.). Every line of the input program is translated to LLVM code in sequence, choosing the equivalent instruction in LLVM-IR. Finally, the compiler can parse, typecheck and produce IR code in [LLVM](https://llvm.org/docs/LangRef.html#instruction-reference). To run the produced LLVM code files you must have installed [Clang](https://clang.llvm.org/) with version >= 4.0.0

#### Compile & Execution

To build this project just run `make` and to clean run `make clean`. To execute you can simply run it by `java Main [file1] , [file2],...,[fileN] `.
