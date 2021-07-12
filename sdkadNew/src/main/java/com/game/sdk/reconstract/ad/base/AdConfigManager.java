package com.game.sdk.reconstract.ad.base;

import android.content.Context;


import com.game.sdk.reconstract.ad.callback.AdCallBack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AdConfigManager {
    private static AdConfigManager instance;

    private static final String CONFIG_NAME = "GMConfig.xml";
    private Element mRootElement;

    private AdCallBack adCallBack;

    public static AdConfigManager getInstance() {
        if (instance == null) {
            synchronized (AdConfigManager.class) {
                if (instance == null) {
                    instance = new AdConfigManager();
                }
            }
        }
        return instance;
    }

    public AdCallBack getAdCallBack() {
        return adCallBack;
    }

    public void setAdCallBack(AdCallBack adCallBack) {
        this.adCallBack = adCallBack;
    }

    public String getAttrsValue(Node node, String attrsName) {
        NamedNodeMap map = node.getAttributes();
        Node valus = map.getNamedItem(attrsName);
        if (valus != null) {
            return valus.getNodeValue();
        }
        return "";
    }

    public Node getNodeByName(Context context, String path) {
        Element root = getRootElement(context);
        String[] nodeNameArr = path.split("/");
        if (nodeNameArr == null || nodeNameArr.length == 0) {
            return null;
        }
        NodeList nodes = root.getElementsByTagName(nodeNameArr[0]);
        if (nodes == null || nodes.getLength() <= 0) {
            return null;
        }
        Node tempNode = nodes.item(0);
        for (int i = 1; i < nodeNameArr.length; i++) {
            NodeList _nodes = tempNode.getChildNodes();
            if (_nodes == null || _nodes.getLength() <= 0) {
                return null;
            }
            for (int j = 0; j < _nodes.getLength(); j++) {
                Node node = _nodes.item(j);
                if (node.getNodeName().equals(nodeNameArr[i])) {
                    tempNode = node;
                    break;
                }
            }
        }
        return tempNode;
    }


    /**
     * 获取RootElement
     */
    private Element getRootElement(Context context) {
        if (mRootElement != null) {
            return mRootElement;
        }
        try {
            InputStream mXmlResourceParser = context.getAssets().open(CONFIG_NAME);
            DocumentBuilder builder = null;
            DocumentBuilderFactory factory = null;
            factory = DocumentBuilderFactory.newInstance();
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException ignored) {
            }
            try {
                Document document = builder.parse(mXmlResourceParser);
                mRootElement = document.getDocumentElement();
            } catch (SAXException ignored) {
            }
        } catch (IOException ignored) {
        }
        return mRootElement;
    }

}
