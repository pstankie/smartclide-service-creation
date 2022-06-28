/*******************************************************************************
 * Copyright (C) 2021-2022 University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package uom.smartclide.structurecreation.functionality.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class PipelineConfigXML {

	Document document;

	
	public PipelineConfigXML() throws DocumentException {
		SAXReader reader = new SAXReader();
		this.document = reader.read(getClass().getResource("/jenkins_pipeline_config_gitlab.xml"));
	}
	
    /**
     * Replaces the text of a Node
     * 
     * @param xPath The xPath to the Node that the change will happen.
     * @param newValue The new text value of he Node.
     * 
     * @throws NoSuchFieldException in case there is no Node with this xPath.
     * 
     */
	public void changeNode(String xPath, String newValue) throws NoSuchFieldException {
		List<Node> list = this.document.selectNodes(xPath);
		if (list.size() == 0) {
			throw new NoSuchFieldException("Could not find " + xPath + " in the XML template");
		}

		for (Node node : list) {
			node.setText(newValue);
		}
	}

    /**
     * Return the pipeline's configuration in String form
     * 
     * @return the XML in String form
     * 
     */
	public String getNewDoc() {		
		return document.asXML();
	}
}
