# MatFileRW releases

### Version 1.4.0-SNAPSHOT - TBD ([javadoc](http://diffplug.github.io/matfilerw/javadoc/snapshot/), [jcenter](https://oss.sonatype.org/content/repositories/snapshots/com/diffplug/matsim/matfilerw/))

### Version 1.3.1 - October 16th 2015 ([javadoc](http://diffplug.github.io/matfilerw/javadoc/1.3.1/), [jcenter](https://bintray.com/diffplug/opensource/matfilerw/1.3.1/view))

- Corrected the license in the maven metadata.

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
