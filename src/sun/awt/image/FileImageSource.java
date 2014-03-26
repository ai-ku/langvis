/*
 * Copyright 1995-1996 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.awt.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileImageSource extends InputStreamImageSource {
	String imagefile;

	public FileImageSource(String filename) {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			security.checkRead(filename);
		}
		imagefile = filename;
	}

	final boolean checkSecurity(Object context, boolean quiet) {
		// File based images only ever need to be checked statically
		// when the image is retrieved from the cache.
		return true;
	}

	protected ImageDecoder getDecoder() {
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(imagefile));
		} catch (FileNotFoundException e) {
			return null;
		}
		// Don't believe the file suffix - many users don't know what
		// kind of image they have and guess wrong...
		/*
                int suffixpos = imagefile.lastIndexOf('.');
                if (suffixpos >= 0) {
                    String suffix = imagefile.substring(suffixpos+1).toLowerCase();
                    if (suffix.equals("gif")) {
                	return new GifImageDecoder(this, is);
                    } else if (suffix.equals("jpeg") || suffix.equals("jpg") ||
                	       suffix.equals("jpe") || suffix.equals("jfif")) {
                	return new JPEGImageDecoder(this, is);
                    } else if (suffix.equals("xbm")) {
                	return new XbmImageDecoder(this, is);
                    }
                }
		 */
		return getDecoder(is);
	}
}