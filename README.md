<!--

    Copyright (c) 2002-2012, the original author or authors.

    This software is distributable under the BSD license. See the terms of the
    BSD license in the documentation provided with this software.

    http://www.opensource.org/licenses/bsd-license.php

-->

# A fork of JLine for the Scala Compiler

[<img src="https://img.shields.io/travis/scala/scala-jline.svg"/>](https://travis-ci.org/scala/scala-jline)
[<img src="https://img.shields.io/maven-central/v/org.scala-lang.modules/scala-jline.svg"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.scala-lang.modules%20a%3Ascala-jline)

This repository contains a fork of [JLine](https://github.com/jline/jline2) for the Scala compiler.
The reason for using a fork is to avoid polluting the classpath of programs that embed the Scala compiler or REPL.
This fork therefore changes the package name for JLine to `scala.tools.jline`.

Releases of this fork are under the group id `"org.scala-lang.modules" % "scala-jline"`.

## Patches

The patches applied to this fork can be [inspected here](https://github.com/jline/jline2/compare/master...scala:scala-jline).
Take a look at the individual commit messages for commands that were used to create the patch.

Note that the `.java` source files are not moved to folders representing the new package name.
This simplifies integrating changes from the upstream repository.
Resource files (`src/main/resources`, `src/test/resources`) on the other hand needed to be moved so that they are copied to the right target directory.

## Branches

The `master` branch in this fork is always kept in synch with the upstream `master` branch.
The patches for re-packaging are in the `scala-jline` branch.

## Tags

The upstream repository uses tags of the form `jline-2.12.1`.
For our releases of scala-jline, we are using tags of the form `v2.12.1`.
These tags mark revisions in the `scala-jline` branch.

When building a v-shaped tag, the travis build script stages a release on sonatype.

# Upstream README

Description
-----------

JLine is a Java library for handling console input. It is similar in functionality to [BSD editline](http://www.thrysoee.dk/editline/) and [GNU readline](http://www.gnu.org/s/readline/). People familiar with the readline/editline capabilities for modern shells (such as bash and tcsh) will find most of the command editing features of JLine to be familiar.

JLine 2.x is an evolution of [JLine 1.x](https://github.com/jline/jline) which was previously maintained at [SourceForge](http://jline.sourceforge.net/).

License
-------

JLine is distributed under the [BSD License](http://www.opensource.org/licenses/bsd-license.php), meaning that you are completely free to redistribute, modify, or sell it with almost no restrictions.

Documentation
-------------

* [wiki](https://github.com/jline/jline2/wiki)

Forums
------

* [jline-users](https://groups.google.com/group/jline-users)
* [jline-dev](https://groups.google.com/group/jline-dev)

Maven Usage
-----------

Use the following definition to use JLine in your maven project:

    <dependency>
      <groupId>jline</groupId>
      <artifactId>jline</artifactId>
      <version>2.12</version>
    </dependency>

Building
--------

### Requirements

* Maven 2+
* Java 5+

Check out and build:

    git clone git://github.com/jline/jline2.git
    cd jline2
    mvn install

