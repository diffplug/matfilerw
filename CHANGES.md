# MatFileRW releases

### Version 2.2.0-SNAPSHOT - TBD ([javadoc](http://diffplug.github.io/matfilerw/javadoc/snapshot/), [jcenter](https://oss.sonatype.org/content/repositories/snapshots/com/diffplug/matsim/matfilerw/))

* Added `MatFile.readFull(ByteBuffer buf, MatFileType type)`
* Added `MLArray.getIndex(int...)` and `MLNumericArray.set(T value, int...)` and `T get(int...)` for N-dimensional indices.

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
