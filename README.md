# <img align="left" src="jmatio.png"> JMatIO: Read and write MATLAB MAT-files from Java

<!---freshmark shields
output = [
	link(shield('Maven artifact', 'mavenCentral', '{{group}}:{{name}}', 'blue'), 'https://bintray.com/{{org}}/opensource/{{name}}/view'),
	link(shield('Latest version', 'latest', '{{stable}}', 'blue'), 'https://github.com/{{org}}/{{name}}/releases/latest'),
	link(shield('Javadoc', 'javadoc', 'OK', 'blue'), 'https://{{org}}.github.io/{{name}}/javadoc/{{stable}}/'),
	link(shield('License Apache', 'license', 'BSD', 'blue'), 'https://tldrlegal.com/license/bsd-3-clause-license-(revised)'),
	'',
	link(shield('Changelog', 'changelog', '{{version}}', 'brightgreen'), 'CHANGES.md'),
	link(image('Travis CI', 'https://travis-ci.org/{{org}}/{{name}}.svg?branch=master'), 'https://travis-ci.org/{{org}}/{{name}}')
	].join('\n');
-->
[![Maven artifact](https://img.shields.io/badge/mavenCentral-com.diffplug.matsim%3Ajmatio-blue.svg)](https://bintray.com/diffplug/opensource/jmatio/view)
[![Latest version](https://img.shields.io/badge/latest-3.2.0-blue.svg)](https://github.com/diffplug/jmatio/releases/latest)
[![Javadoc](https://img.shields.io/badge/javadoc-OK-blue.svg)](https://diffplug.github.io/jmatio/javadoc/3.2.0/)
[![License Apache](https://img.shields.io/badge/license-BSD-blue.svg)](https://tldrlegal.com/license/bsd-3-clause-license-(revised))

[![Changelog](https://img.shields.io/badge/changelog-3.3.0--SNAPSHOT-brightgreen.svg)](CHANGES.md)
[![Travis CI](https://travis-ci.org/diffplug/jmatio.svg?branch=master)](https://travis-ci.org/diffplug/jmatio)
<!---freshmark /shields -->

JMatIO is a library which allows reading and writing MAT files.  Have a look at [MatIOTest.java](src/test/java/com/jmatio/test/MatIOTest.java?ts=4) to see each part in use.

As far as compatibility, the TL;DR is that it will work with any MAT-File with default savings.  The dirty details are that this library works with `v6` and `v7`, but not `v4` or `v7.3`.

* v4 is the default format before R8
* v6 is the default format from R8 to R13
* v7 is the default format from R14 to present (every R20XXX release)
* MATLAB does not export to v7.3 by default.
* The [Mathworks website](http://www.mathworks.com/help/matlab/import_export/mat-file-versions.html?refresh=true) has more details.

## Acknowledgements

This has been forked from the project originally maintained on [SourceForge](http://sourceforge.net/projects/jmatio/), now maintained by the original author on [GitHub](https://github.com/gradusnikov/jmatio).

We have made some improvements (see the [changelog](CHANGES.md)), and will maintain this library into the future.  Although this fork is in no way associated with or endorsed by any authors of the original project, we very much appreciate their work!
