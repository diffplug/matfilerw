/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.io;

import java.io.IOException;

/**
 * MAT-file reader/writer exception
 * 
 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 */
@SuppressWarnings("serial")
public class MatlabIOException extends IOException {
	public MatlabIOException(String s) {
		super(s);
	}
}
