# MatFileRW releases

### Version 3.2.0-SNAPSHOT - TBD ([javadoc](http://diffplug.github.io/matfilerw/javadoc/snapshot/), [snapshot](https://oss.sonatype.org/content/repositories/snapshots/com/diffplug/matsim/matfilerw/))

* Fixed MatFileReader.read(File file) to allow multiple calls(see [#20](https://github.com/diffplug/matfilerw/issues/20)).

### Version 3.1.0 - November 13th 2018 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/3.1.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/3.1.0/view))

* Added support for Jigsaw and Java 9+ (see [#16](https://github.com/diffplug/matfilerw/issues/16)).

### Version 3.0.1 - June 28th 2017 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/3.0.1/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/3.0.1/view))

* Fixed second-level MCOS property dereferencing (see [#13](https://github.com/diffplug/matfilerw/issues/13)).

### Version 3.0.0 - November 22nd 2016 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/3.0.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/3.0.0/view))

* Fixed multidimensional array indexing (see [#10](https://github.com/diffplug/matfilerw/issues/10)).
	+ For arrays with dimension 1 or 2, there is no change.
	+ For arrays with dimension 3 and up, matfilerw 2.x did not order the dimensions in the column-major format used by MATLAB.  In 3.x forward, matfilerw uses the same column-major format as MATLAB.
* `getImaginary` will always return zero for real arrays.

### Version 2.3.0 - August 18th 2016 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/2.3.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/2.3.0/view))

* Fixed problem where a handle class is encoded within an MLObject.
* MCOS variables can now be parsed from anywhere within the MAT-File (previously could only be a root variable).
* Arrays of structs and fields are now enforced to have their fields be in a consistent order element to element.

### Version 2.2.0 - February 9th 2016 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/2.2.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/2.2.0/view))

* Added `MatFile.readFull(ByteBuffer buf, MatFileType type)`
* Added `MLArray.getIndex(int...)` and `MLNumericArray.set(T value, int...)` and `T get(int...)` for N-dimensional indices.
* Fixed the JRE6 download in the build file.

### Version 2.1.0 - December 10th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/2.1.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/2.1.0/view))

* Fixed support for reading UTF fields (closes [#2](https://github.com/diffplug/matfilerw/issues/2))

### Version 2.0.0 - October 25th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/2.0.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/2.0.0/view))

* Incorporated all changes from `codesourcery/JMatIO`.
	+ Adds support for MCOS Objects.
	+ Adds a Simulink decoder for reading files from Simulink MDL files.
	+ Improved performance.
* `MLObject` and `MLStructure` now share `MLStructureObjectBase` as a base class.  This caused a small breaking change to `MLObject`s API.
* `MatFileHeader` now wraps up the whole endianness mess, which also required a small breaking change.

### Version 2.0.0.TRANSITION - October 25th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/2.0.TRANSITION/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/2.0.TRANSITION/view))

Only pertinent for people who are migrating from a JMatIO fork whose packages were renamed to `ca.mjdsystems.jmatio`.

* `ca.mjdsystems.jmatio` code is included umodified, but marked as deprecated.
* `com.jmatio` code is identitical to `2.0.0`.

### Version 1.3.1 - October 16th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/1.3.1/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/1.3.1/view))

* Corrected the license in the maven metadata.

### Version 1.3.0 - October 16th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/1.3.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/1.3.0/view))

* OSGi header now imports its own packages, as recommended by OSGi gurus.

### Version 1.2.0 - October 15th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/1.2.0/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/1.2.0/view))

* Now available on Maven Central!
* Added a sanity check to `MLNumericArray.contentToString()`.
* `MatFileHeader.DEFAULT_VERSION` is now public.
* Added `MatFile`, a simpler API for parsing from `MatFileReader`.
* Added support for unsigned 16-bit ints.
* Parsing Java objects is now more robust.
* Fixed some inconsistent `equals()` and `hashCode()` implementations.
* Various minor performance improvements (thanks to FindBugs).

## Versions up to 1.1.0 are from the original author's [SourceForge](http://sourceforge.net/projects/jmatio/) and [GitHub](https://github.com/gradusnikov/jmatio) pages.
