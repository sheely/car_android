package com.sky.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SHModuleManager {
	private static SHModuleManager __instance;
	private HashMap<String, SHModule> mModule;
	private class SHModule{
		public String Name;
		public String Target = "";
		public String Pre_action = "";
		public String Need_pre_action = "";
	}
	public SHModuleManager() {
		this.readXML();
	}

	public static SHModuleManager getInstance() {
		if (__instance == null) {
			__instance = new SHModuleManager();
		}

		return __instance;
	}

	private void readXML() {
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(SHApplication.getInstance().getAssets()
					.open("module.xml"));
			Element root = doc.getDocumentElement();
		
			NodeList nodeList = root.getElementsByTagName("module");
			mModule = new HashMap<String, SHModule>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nd = nodeList.item(i);
				SHModule module = new SHModule();
				module.Name = nd.getAttributes().getNamedItem("name").getNodeValue();
				if(nd.getAttributes().getNamedItem("pre_action")!= null){
					module.Pre_action = nd.getAttributes().getNamedItem("pre_action").getNodeValue();
				}
				if(nd.getAttributes().getNamedItem("need_pre_action") != null){
					module.Need_pre_action = nd.getAttributes().getNamedItem("need_pre_action").getNodeValue();
				}
				module.Target = nd.getAttributes().getNamedItem("target").getNodeValue();
				mModule.put(nd.getAttributes().getNamedItem("name").getNodeValue(),module);
			}
		} catch (IOException e) {
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} finally {
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;
		}
	}

	public String getModuleName(String name) {
		String module = mModule.get(name).Target;
		// String dymodule = SHClass.getClassName(module);
		return module;
	}
	
	public String getNeed_Pre_Action_NameByTagert(String name) {
		Iterator<String> iterator = mModule.keySet().iterator();
		while (iterator.hasNext()) {
			SHModule module = mModule.get(iterator.next());
			if (module.Target.equalsIgnoreCase(name)) {
				return module.Need_pre_action;
			}
		}
		// String dymodule = SHClass.getClassName(module);
		return null;
	}
	public String getModuleNameByPreAction(String name) {
		Iterator<String> iterator = mModule.keySet().iterator();
		while (iterator.hasNext()) {
			SHModule module = mModule.get(iterator.next());
			if (module.Pre_action.equalsIgnoreCase(name)) {
				return module.Target;
			}
		}
		// String dymodule = SHClass.getClassName(module);
		return null;
	}
	
}
