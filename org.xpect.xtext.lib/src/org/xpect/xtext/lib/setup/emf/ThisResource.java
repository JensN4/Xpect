/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.xtext.lib.setup.emf;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class ThisResource extends Resource {

	public ThisResource() {
		super(new org.xpect.xtext.lib.setup.generic.ThisFile());
	}

	public ThisResource(org.xpect.xtext.lib.setup.generic.ThisFile file) {
		super(file);
	}

	public ThisResource(String name) {
		super(name);
	}

}
