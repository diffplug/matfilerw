JMatio
======

JMatio is a really great piece of software for dealing with MATLAB's IO format in java. From the original project:

> Matlab's MAT-file I/O API in JAVA. Supports Matlab 5 MAT-flie format reading and writing. Written in pure JAVA. 

This is a branch of the software [originally written](https://sourceforge.net/projects/jmatio/) by [Craig Watcham](https://sourceforge.net/users/cbwatcham), [Tim Kutz](https://sourceforge.net/users/tkutz) and [Wojciech Gradkowski](https://sourceforge.net/users/wgradkowski). The original code is very tidy and the API is really great to use. I made this branch to make some quite minor changes to uses of java's IOStreams which result in massive speedups.

For example: in MatFileWriter.java I have cut down the use of needless ByteArrayOutstream instances (writing directly to the compression IO Stream) and where ByteArrayOutstream are entirely necessary I've written a ByteArrayOutstream2 which exposes the underlying byte stream which I index into instead of using toByteArray (which copies, bad!)

These changes are completely irrelevant with small matlab files (sub 100Mb) but when the data being written is in the gigabytes these changes resulted in substantial memory usage improvements

 