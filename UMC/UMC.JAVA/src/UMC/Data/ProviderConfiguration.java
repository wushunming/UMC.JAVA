package UMC.Data;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import java.awt.print.Book;
import java.io.*;
import java.util.*;

public class ProviderConfiguration {
    private List<Provider> _config;

    private static Map<String, ProviderConfiguration> configuration;

    static {
        configuration = new HashMap<>();
    }

    public ProviderConfiguration(InputStream inputStream) {
        List<String> names = new LinkedList<>();
        _config = new LinkedList<>();
        //将给定 URI 的内容解析为一个 XML 文档,并返回Document对象
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            inputStream.close();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        //按文档顺序返回包含在文档中且具有给定标记名称的所有 Element 的 NodeList
        NodeList bookList = document.getElementsByTagName("add");

        //遍历books
        for (int i = 0; i < bookList.getLength(); i++) {
            org.w3c.dom.Node node = bookList.item(i);
            Provider provider = new Provider(node);
            int index = names.indexOf(provider.name());
            if (index > -1) {
                _config.remove(index);

            }
            _config.add(provider);
        }


    }

    public int size() {
        return _config.size();
    }

    public Provider get(int index) {
        if (index > -1 && index < _config.size()) {
            return _config.get(index);
        }
        return null;
    }

    public Provider get(String name) {
        Iterator<Provider> iterator = _config.iterator();
        while (iterator.hasNext()) {
            Provider provider = iterator.next();
            if (provider.name().equals(name)) {
                return provider;
            }
        }
        return null;
    }

    public static void clear() {
        configuration.clear();
    }

    public static ProviderConfiguration configuration(String name) {
        if (configuration.containsKey(name)) {
            return configuration.get(name);
        } else {
            try {
                String filename = ProviderConfiguration.class.getClassLoader().getResource("../").getPath() + "App_Data/WebADNuke/" + name + ".xml";
                FileInputStream inputStream = new FileInputStream(filename);
                ProviderConfiguration providerConfiguration = new ProviderConfiguration(inputStream);
                configuration.put(name, providerConfiguration);
                return providerConfiguration;
            } catch (FileNotFoundException e) {
              //  e.printStackTrace();
            }
        }
        return null;
    }
}
